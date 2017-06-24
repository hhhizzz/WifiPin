import org.apache.log4j.{Level, LogManager}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

/**
  * Created by IZZ on 15/06/2017.
  * 程序的主入口
  *
  * @author IZZ
  *
  */
object Main {

  //从mysql数据库读取探针基础数据
  val dbURL = "jdbc:mysql://slave2.com/?useUnicode=true&characterEncoding=utf-8&useSSL=false"
  val dbUser = "root"
  val dbPasswd = "123456"
  val spark = SparkSession
    .builder()
    .appName("Spark SQL wifiPin")
    .enableHiveSupport()
    .getOrCreate()

  def main(args: Array[String]): Unit = {
    val log = LogManager.getLogger("org")
    log.setLevel(Level.WARN) //把日志记录调整为WARN级别，以减少输出
    //val sc = spark.sparkContext


    import spark.implicits._
    import spark.sql
    sql("use sniffer")
    val newDataObject = new GetNewData()


    val clientDF = newDataObject.getClientDF
    val powerDF = newDataObject.getPowerDF
    clientDF.createOrReplaceTempView("RowClient")
    powerDF.createOrReplaceTempView("RowPower")
    sql("insert into client select * from RowClient")
    sql("insert into power select * from RowPower")


//    //从power表中获取这段时间内被搜集到了用户
//    val clientPowerDF = spark.sql("SELECT power.*\nFROM client\n  LEFT JOIN power ON client.client = power.client")
//    clientPowerDF.createOrReplaceTempView("clientPower")
//    //    +-------+-----------------+-----+----------+
//    //    |sniffer|           client|power|      time|
//    //    +-------+-----------------+-----+----------+
//    //    |      1|9C:B7:0D:52:11:19|  -88|1495364161|
//    //    |      1|9C:B7:0D:52:11:19|  -89|1495364510|
//
//    //计算用户停留时间和这段时间的平均信号强度
//    val clientHoldDF = spark.sql("SELECT\n  client,\n  MAX(time) - MIN(time) hold,\n  AVG(power) average_power\nFROM clientPower GROUP BY client")
//    clientHoldDF.createOrReplaceTempView("clientHold")
//    //    +-----------------+----+-------------------+
//    //    |           client|hold|      average_power|
//    //    +-----------------+----+-------------------+
//    //    |9C:B7:0D:52:11:19| 769| -79.66666666666667|
//    //    |DA:A1:19:5E:9C:E1| 441|-58.541666666666664|
//
//    //对用户按时间进行排序，用于计算来访周期
//    val clientNumberDF = spark.sql(" SELECT\n    a.client,\n    a.time,\n    count(*) row_num\n  FROM clientPower a\n    JOIN clientPower b ON a.client = b.client AND a.time >= b.time\n  GROUP BY a.client, a.time")
//    clientNumberDF.createOrReplaceTempView("clientNumber")
//    //    +-----------------+----------+-------+
//    //    |           client|      time|row_num|
//    //    +-----------------+----------+-------+
//    //    |9C:B7:0D:52:11:19|1495364161|      1|
//    //    |9C:B7:0D:52:11:19|1495364510|      2|
//    //    |9C:B7:0D:52:11:19|1495364930|      3|
//
//    //计算来访周期，就是最近两个时间最长时间差
//    val clientPeriodDF = spark.sql("SELECT\n  a.client,\n  max(b.time - a.time) time_diff\nFROM clientNumber a\n  JOIN clientNumber b ON a.client = b.client AND a.row_num + 1 = b.row_num\nGROUP BY a.client")
//    clientPeriodDF.createOrReplaceTempView("clientPeriod")
//    //    +-----------------+---------+
//    //    |           client|time_diff|
//    //    +-----------------+---------+
//    //    |9C:B7:0D:52:11:19|      420|
//    //    |DA:A1:19:5E:9C:E1|       24|
//
//
//    //计算客流量
//    val clientNumber = clientDF.count()
//    println("The clientNumber is " + clientNumber)
//
//    //计算入店量
//    val inputClient = clientHoldDF.filter(row => row.getDouble(2) > -65)
//    val inputClientNumber = inputClient.count()
//    println("The InputClient is " + inputClientNumber)
//
//    //计算来访周期，-1表示没有老客户
//    val periodNeedDF = clientPeriodDF.filter(row => row.getInt(1) > 3600)
//    periodNeedDF.createOrReplaceTempView("periodNeed")
//    val periodSumDF = spark.sql("SELECT AVG(time_diff) FROM periodNeed")
//    var period = -1.0
//    if (!periodSumDF.collect()(0).isNullAt(0)) {
//      period = periodSumDF.collect()(0).getDouble(0)
//    }
//    println("The Period is " + period)
//
//    //计算老新顾客数量
//    val oldClientDF = clientPeriodDF.filter(row => row.getInt(1) > 3600)
//    val oldClientNumber = oldClientDF.count()
//    println("The old client is " + oldClientNumber)
//    val newClient = inputClientNumber - oldClientNumber
//    println("The new client is " + newClient)
//
//    //计算用户驻店时间
//    val clientHoldNumberDF = spark.sql("SELECT AVG(hold) FROM clientHOLD")
//    var hold = 0.0
//    if (!clientHoldNumberDF.collect()(0).isNullAt(0)) {
//      hold = clientHoldNumberDF.collect()(0).getDouble(0)
//    }
//    println("The hold time is " + hold)
//
//    //计算跳出率
//    val jumpOutDF = clientHoldDF.filter(row => row.getInt(1) <= 120)
//    val jumpOutNumber = jumpOutDF.count()
//    println("The jump out number is " + jumpOutNumber)
//
//    //计算深访率
//    val deepInDF = clientHoldDF.filter(row => row.getInt(1) >= 1800)
//    val deepInNumber = deepInDF.count()
//    println("The deep in number is " + deepInNumber)
//
//    //计算高中低沉睡活跃度用户
//    val clientPeriodHigh = oldClientDF.filter(row => row.getInt(1) <= 129600).count()
//    val clientPeriodMid = oldClientDF.filter(row => row.getInt(1) > 129600 && row.getInt(1) <= 691200).count()
//    val clientPeriodLow = oldClientDF.filter(row => row.getInt(1) > 691200 && row.getInt(1) <= 2678400).count()
//    val clientPeriodSleep = oldClientNumber - clientPeriodLow - clientPeriodMid - clientPeriodHigh
//    println("The high activity client is " + clientPeriodHigh)
//    println("The Mid activity client is " + clientPeriodMid)
//    println("The Low activity client is " + clientPeriodLow)
//    println("The sleep activity client is " + clientPeriodSleep)
//
//    newDataObject.clearTable()
  }
}
