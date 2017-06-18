import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.flume.{FlumeUtils, SparkFlumeEvent}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import org.json.{JSONArray, JSONObject}

/**
  * Created by IZZ on 15/06/2017.
  * 程序的主入口
  *
  * @author IZZ
  *
  */
object Main {

  def getDataFromFlume(flumeData: SparkFlumeEvent): Iterable[String] = {
    val body = new String(flumeData.event.getBody.array())

    //由于获取的是javaMap<CharSequence,CharSequence>需要转换一下
    val headerMapChar = flumeData.event.getHeaders.asScala
    val headerMap = headerMapChar.map(x => (x._1.toString, x._2.toString))
    val pin_mac = headerMap.getOrElse("mmac", "00:00:00:00:00:00")

    //获取数组中每个设备的信息
    val device_array = new JSONArray(body)
    var device_jsons = new ArrayBuffer[JSONObject]()
    for (i <- 0 until device_array.length()) {
      device_jsons += device_array.getJSONObject(i)
    }

    //整理好需要的数据，使其与数据库结构一致
    var device_strings = new ArrayBuffer[String]()
    for (json <- device_jsons) {
      json.put("pin_mac", pin_mac)
      val essids = json.getJSONArray("essid")
      var moreThanThree = false
      for (i <- 0 until essids.length(); if !moreThanThree) {
        json.put("ssid" + (i + 1), essids.getString(i))
        if (i >= 3) {
          moreThanThree = true
        }
      }

      json.remove("essid")
      json.put("connect_mac", json.get("tmc"))
      json.remove("tmc")
      json.put("collect_time", System.currentTimeMillis())
      json.put("device_mac", json.get("mac"))
      json.remove("device_mac")
      json.put("current_wifi", json.get("ts"))
      json.remove("ts")
      json.remove("tc")
      device_strings += json.toString()
    }
    device_strings
  }


  def main(args: Array[String]): Unit = {
    val log = LogManager.getLogger("org")
    log.setLevel(Level.WARN) //把日志记录调整为WARN级别，以减少输出
    val conf = new SparkConf().setAppName("test")
    val ssc = new StreamingContext(conf, Seconds(3)) //默认三秒更新一次数据
    val spark = SparkSession
      .builder()
      .config("hive.metastore.uris", "thrift://master.com:9083")
      .enableHiveSupport()
      .getOrCreate()
    import spark.implicits._
    import spark.sqlContext
    sqlContext.sql("use wifi_pin")

    val flumeStream = FlumeUtils.createStream(ssc, "master.com", 7777, StorageLevel.MEMORY_ONLY) //从localhost:7777获取flume数据
    val streamData = flumeStream.flatMap(getDataFromFlume)
    streamData.print(10)
    streamData.foreachRDD { rdd =>

      if (!rdd.isEmpty()) {
        // Convert RDD[String] to DataFrame
        val currentDF = sqlContext.jsonRDD(rdd)
        currentDF.show()
        currentDF.createOrReplaceTempView("temp")
        sqlContext.sql("insert into person select * from temp")
      }
    }


    ssc.start()
    ssc.awaitTermination()
  }
}
