import java.util.Properties

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
  * Created by IZZ on 15/06/2017.
  * 程序的主入口
  *
  * @author IZZ
  *
  */
object Main {

  val dbUser = "root"
  val dbPasswd = "123456"

  def main(args: Array[String]): Unit = {
    val log = LogManager.getLogger("org")
    log.setLevel(Level.WARN) //把日志记录调整为WARN级别，以减少输出
    val conf = new SparkConf().setAppName("Simple Application")
    val sc = new SparkContext(conf)
    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .getOrCreate()

    val clientDF = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://slave2.com/jsp_db?useUnicode=true&characterEncoding=utf-8&useSSL=false")
      .option("dbtable", "sniffer.client")
      .option("user", dbUser)
      .option("password", dbPasswd)
      .load()

    clientDF.show()
  }
}
