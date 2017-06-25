import org.apache.log4j.Logger
import Main.{dbPasswd, dbUser, dbURL}

/**
  * Created by xunixhuang on 24/06/2017.
  */
case class Data(time: Long,
                username: String,
                clientNumber: Long,
                InputClient: Long,
                Period: Double,
                oldClient: Long,
                newClient: Long,
                holdTime: Double,
                jumpOut: Long,
                deepIn: Long,
                highActivity: Long,
                midActivity: Long,
                lowActivity: Long,
                sleepActivity: Long)

object SaveData {
  val log: Logger = Logger.getLogger(getClass.getName)

  def saveData(data: Data): Unit = {
    import java.sql._

    var conn: Connection = null
    var stmt: Statement = null

    try {
      Class.forName("com.mysql.jdbc.Driver")
      log.info("Connecting to a selected database...")
      conn = DriverManager.getConnection(dbURL, dbUser, dbPasswd)
      log.info("Connected database successfully...")
      log.info("Inserting table in given database...")
      stmt = conn.createStatement()

      val sql: String = s"Insert into sniffer.data value(${data.time},\'${data.username}\',${data.clientNumber},${data.InputClient},${data.Period},${data.oldClient},${data.newClient},${data.holdTime},${data.jumpOut},${data.deepIn},${data.highActivity},${data.midActivity},${data.lowActivity},${data.sleepActivity})"
      stmt.executeUpdate(sql)
    } catch {
      case e: Exception => println("exception caught: " + e)
    }
  }
}
