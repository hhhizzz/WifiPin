import Main.{dbPasswd, dbUser}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Created by xunixhuang on 22/06/2017.
  */
class getNewData {
  //    val spark = SparkSession
  //      .builder()
  //      .config("spark.sql.warehouse.dir", "hdfs://master.com:8020/apps/hive/warehouse")
  //      .enableHiveSupport()
  //      .appName("Spark SQL basic example")
  //      .getOrCreate()
  val spark: SparkSession = SparkSession
    .builder()
    .appName("getNewData")
    .getOrCreate()

  def getClientDF: DataFrame = {
    val DF = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://slave2.com/?useUnicode=true&characterEncoding=utf-8&useSSL=false")
      .option("dbtable", "sniffer.client")
      .option("user", dbUser)
      .option("password", dbPasswd)
      .load()
    DF
  }

  def getPowerDF: DataFrame = {
    val DF = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://slave2.com/?useUnicode=true&characterEncoding=utf-8&useSSL=false")
      .option("dbtable", "sniffer.power")
      .option("user", dbUser)
      .option("password", dbPasswd)
      .load()
    DF
  }

  def getConnectionDF: DataFrame = {
    val DF = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://slave2.com/?useUnicode=true&characterEncoding=utf-8&useSSL=false")
      .option("dbtable", "sniffer.connection")
      .option("user", dbUser)
      .option("password", dbPasswd)
      .load()
    DF
  }

  def getHistoryDF: DataFrame = {
    val DF = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://slave2.com/?useUnicode=true&characterEncoding=utf-8&useSSL=false")
      .option("dbtable", "sniffer.history")
      .option("user", dbUser)
      .option("password", dbPasswd)
      .load()
    DF
  }

  def getOuiDF: DataFrame = {
    val DF = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", "jdbc:mysql://slave2.com/?useUnicode=true&characterEncoding=utf-8&useSSL=false")
      .option("dbtable", "sniffer.oui")
      .option("user", dbUser)
      .option("password", dbPasswd)
      .load()
    DF
  }
}
