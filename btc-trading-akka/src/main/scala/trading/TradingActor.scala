package trading
import akka.actor.{ Props, Actor }
import akka.actor.ActorLogging
import com.typesafe.config.{ Config, ConfigFactory }
import marketdata.MarketDataTypes.MarketDataType
import marketdata.MarketDataTypes.TickDataType
import marketdata.MarketDataTypes.TradeDataType

import marketdata.{ Tick, Trade, SubscribeListenerRequest }

class TradingActor(val instrument: String, val url_marketdata: String) extends Actor with ActorLogging {

  def receive = {
    case tick: Tick => {
      log.info("trader received " + tick)
    }
    case trade: Trade => {
      log.info("trader received " + trade)
    }
    case x: AnyRef => log.error("unexpected message received " + x)
  }

  override def preStart(): Unit = {
    val selection = context.actorSelection(url_marketdata+"/user/*")
    selection ! SubscribeListenerRequest(self, instrument, Some(List(TickDataType,TradeDataType)))
    ()
  }
}