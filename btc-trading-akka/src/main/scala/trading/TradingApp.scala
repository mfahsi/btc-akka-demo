package trading

import scala.collection.JavaConverters.asScalaBufferConverter

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.{ConfigObject, ConfigValue, ConfigFactory, Config}  
import scala.collection.JavaConverters._  
import java.net.URI  
import java.util.Map.Entry


//runs bitcoin market data feed nodes on a given configured server instance
//all feeds in a server are supervised by a localFeedManager
//remote trading actor may contact LocalFeedManager to know about service status or discover available feed
object TradingApp extends App {
  val sysName: String = "trading-fx" //mytradingSystem-amazon-us, will trade btc usd
  val config = ConfigFactory.load()
  val tradingConfig = config.getConfig(sysName).withFallback(config) //get config for a given system
 
  lazy val actorSystems : Map[String, String] = {
    val list : Iterable[ConfigObject] = config.getObjectList("actorSystems").asScala
    (for {
      item : ConfigObject <- list
      entry : Entry[String, ConfigValue] <- item.entrySet().asScala
      key = entry.getKey
      uri = entry.getValue.unwrapped().toString()
    } yield (key, uri)).toMap
  }
  
   val marketdata_system_url = actorSystems.get("marketdata-usd").get

  lazy val actorSystem: ActorSystem = ActorSystem(sysName, tradingConfig)
  val instruments : List[String] =  tradingConfig.getStringList("instruments").asScala.toList
  instruments.foreach { instrument => actorSystem.actorOf(Props(new TradingActor(instrument, marketdata_system_url)), sysName) }
  actorSystem.awaitTermination();   
}



