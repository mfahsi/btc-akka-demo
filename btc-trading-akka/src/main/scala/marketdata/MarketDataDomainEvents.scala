package marketdata
import akka.actor.ActorRef
import scala.{ Enumeration }
import marketdata.MarketDataTypes.{MarketDataType}
import marketdata.MarketDataTypes.{TradeDataType,TickDataType,DepthDataType,OrderBookDataType}

/** market data events : technical events **/
abstract trait MarketEvent {
  def exchange: String
  val timestamp: Long = System.currentTimeMillis();
}

case class Connect(val exchange: String) extends MarketEvent
case class Disconnect(val exchange: String) extends MarketEvent
case class SystemMessage(val exchange: String, val message: Exception) extends MarketEvent

/** market data events**/
object MarketDataTypes extends Enumeration {
  type MarketDataType = Value
  val TickDataType = Value("Tick")
  val TradeDataType = Value("Trade")
  val DepthDataType = Value("Depth")
  val OrderBookDataType = Value("OrderBook")
}

abstract trait MarketDataEvent extends MarketEvent {
  def instrument: String
}

case class Tick(val exchange: String, val instrument: String, val price: BigDecimal) extends MarketDataEvent
case class Trade(val exchange: String, val instrument: String, val price: BigDecimal, val quantity: BigDecimal) extends MarketDataEvent
//case class Depth
//case class OrderBook

/**subscription to market data gateways**/
abstract trait Command
case class SubscribeListenerRequest(val subscriber: ActorRef, val subject: String, val topics: Option[Iterable[MarketDataType]]) extends Command
case class SubscribeListenerResponse(val subject: String, val subscribed: Boolean, val publisher: ActorRef) extends Command

/**Internal events to polling mechanism**/
abstract trait MarketDataPollCommand
object MarketDataPollCommandEnum extends Enumeration {
  case class EnumVal(val dataType: MarketDataType) extends super.Val(nextId, dataType.toString()) with MarketDataPollCommand 
  val TickPollCommand =  EnumVal(TickDataType)
  val TradePollCommand = EnumVal(TradeDataType)
  val DepthPollCommand = EnumVal(DepthDataType)
  val OrderBookDataPollCommand = EnumVal(OrderBookDataType)
}


