import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.net.{URL, HttpURLConnection}
import scala.io

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

/**
  * Created by xunixhuang on 25/06/2017.
  */
class CustomReceiver(url: String)
  extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) {

  def onStart() {
    // Start the thread that receives data over a connection
    new Thread("Socket Receiver") {
      override def run() {
        receive()
      }
    }.start()
  }

  def onStop() {
    // There is nothing much to do as the thread calling receive()
    // is designed to stop by itself if isStopped() returns false
  }

  /** Create a socket connection and receive data until receiver is stopped */
  private def receive() {
    try {
      while(true){

      }
      val connection = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(2000)
      connection.setReadTimeout(2000)
      connection.setRequestMethod("GET")
      val inputStream = connection.getInputStream
      val content = io.Source.fromInputStream(inputStream).mkString
      if (inputStream != null) inputStream.close()
      store(content)
      Thread.sleep(30000)
      if (!isStopped()) {
        restart("Trying to connect again")
      }
    }
    catch {
      case e: java.net.ConnectException =>
        // restart if could not connect to server
        restart("Error connecting to " + url, e)
      case t: Throwable =>
        // restart if there is any other error
        restart("Error receiving data", t)
    }
    finally {
      onStop()
    }
  }
}
