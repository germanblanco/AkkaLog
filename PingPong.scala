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
        val test  = Map("service" -> "ping-start").asJava
        logger1.info(appendEntries(test), "hello")
        pong ! PingMessage
    case PongMessage => 
        val logger2: Logger = LoggerFactory.getLogger(this.getClass)
        val test  = Map("service" -> "ping-pong").asJava
        logger2.info(appendEntries(test), "hello")
        pong ! StopMessage
        exit()
  }
}
 
class Pong extends Actor {
  def receive = {
    case PingMessage =>
        val logger1: Logger = LoggerFactory.getLogger(this.getClass)
        val test  = Map("service" -> "pong-ping").asJava
        logger1.info(appendEntries(test), "hello")
        sender ! PongMessage
    case StopMessage =>
        val logger2: Logger = LoggerFactory.getLogger(this.getClass)
        val test  = Map("service" -> "stop").asJava
        logger2.info(appendEntries(test), "hello")
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

  logger.info(appendEntries(json), "hello")

  // start them going
  ping ! StartMessage

  system.shutdown()
}
