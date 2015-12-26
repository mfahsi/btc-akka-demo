package marketdata
import akka.actor.{Props, Actor}
import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import scala.collection.JavaConverters._
import marketdata.MarketDataTypes.{MarketDataType,TickDataType,TradeDataType}

object MarketDataApp extends App {
    val sysName : String = "marketdata-usd"
    val config = ConfigFactory.load()
    val marketdataConfig = config.getConfig(sysName).withFallback(config)
    lazy val actorSystem: ActorSystem = ActorSystem(sysName, marketdataConfig)

    val actors : List[String] =  marketdataConfig.getStringList("actors").asScala.toList
    val instrument : String =  marketdataConfig.getString("instrument")
    actors.foreach { actorName => actorSystem.actorOf(Props(new MarketDataFeedPublisherActor(actorName, instrument, List[MarketDataType] (TickDataType,TradeDataType))), actorName) }
    actorSystem.awaitTermination();
}