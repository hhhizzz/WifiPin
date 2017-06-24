import Main.{dbPasswd, dbUser, dbURL, spark}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.log4j.Logger

/**
  * Created by xunixhuang on 22/06/2017.
  */
class GetNewData {

  //  val spark: SparkSession = SparkSession
  //    .builder()
  //    .appName("getNewData")
  //    .getOrCreate()

  val log: Logger = Logger.getLogger(getClass.getName)

  def getClientDF: DataFrame = {
    val DF = spark.read
      .format("jdbc")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("url", dbURL)
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
      .option("url", dbURL)
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
      .option("url", dbURL)
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
      .option("url", dbURL)
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
      .option("url", dbURL)
      .option("dbtable", "sniffer.oui")
      .option("user", dbUser)
      .option("password", dbPasswd)
      .load()
    DF
  }

  def clearTable(): Unit = {

    import java.sql._

    var conn: Connection = null
    var stmt: Statement = null

    try {
      Class.forName("com.mysql.jdbc.Driver")
      log.info("Connecting to a selected database...")
      conn = DriverManager.getConnection(dbURL, dbUser, dbPasswd)
      log.info("Connected database successfully...")
      log.info("Deleting table in given database...")
      stmt = conn.createStatement()


      var sql: String = s"TRUNCATE TABLE sniffer.client"
      stmt.executeUpdate(sql)
      log.info(s"Table sniffer.client cleared in given database...")

      sql = s"TRUNCATE TABLE sniffer.connection"
      stmt.executeUpdate(sql)
      log.info(s"Table sniffer.connection cleared in given database...")

      sql = s"TRUNCATE TABLE sniffer.history"
      stmt.executeUpdate(sql)
      log.info(s"Table sniffer.history cleared in given database...")

      sql = s"TRUNCATE TABLE sniffer.power"
      stmt.executeUpdate(sql)
      log.info(s"Table sniffer.power cleared in given database...")

    } catch {
      case e: Exception => println("exception caught: " + e)
    }

  }
}
