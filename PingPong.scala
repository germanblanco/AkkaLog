import akka.actor._

import net.logstash.logback.marker.Markers._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

case object PingMessage
case object PongMessage
case object StartMessage
case object StopMessage
 
class Ping(pong: ActorRef) extends Actor {
  def receive = {
    case StartMessage =>
        val logger1: Logger = LoggerFactory.getLogger(this.getClass)
        logger1.info("Starting")
        pong ! PingMessage
    case PongMessage => 
        val logger2: Logger = LoggerFactory.getLogger(this.getClass)
        logger2.info("Finishing Ping")
        pong ! StopMessage
        exit()
  }
}
 
class Pong extends Actor {
  def receive = {
    case PingMessage =>
        val logger1: Logger = LoggerFactory.getLogger(this.getClass)
        logger1.info("sending pong")
        sender ! PongMessage
    case StopMessage =>
        val logger2: Logger = LoggerFactory.getLogger(this.getClass)
        logger2.info("Finishing Pong")
        exit()
  }
}
 
object PingPongTest extends App {
  val system = ActorSystem("PingPongSystem")
  val pong = system.actorOf(Props[Pong], name = "pong")
  val ping = system.actorOf(Props(new Ping(pong)), name = "ping")

  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val serviceInfo = Map(("name", "service-writer"), ("version", "1.0")).asJava
  val service  = Map("service" -> serviceInfo).asJava
  val json  = Map("json" -> service).asJava

  println(json)
  println(appendEntries(json))

  logger.info(appendEntries(json), "log message")

  // start them going
  ping ! StartMessage

  system.shutdown()
}
