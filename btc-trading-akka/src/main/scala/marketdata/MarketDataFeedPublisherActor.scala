package marketdata

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.MILLISECONDS
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import marketdata.MarketDataPollCommandEnum.TickPollCommand
import marketdata.MarketDataPollCommandEnum.TradePollCommand
import marketdata.MarketDataTypes.MarketDataType
import marketdata.MarketDataTypes.TickDataType
import marketdata.MarketDataTypes.TradeDataType


class MarketDataFeedPublisherActor(val exchange: String, val instrument: String, val topics: Iterable[MarketDataType]) extends Actor with SubscriptionManager with ActorLogging {
  implicit val sinceId: Long = 0
  var lastTickId: Long = 0
  var lastTradeTimestamp = System.currentTimeMillis() - 60 * 1000
  val feedService = new FeedServiceMock(exchange, instrument) //Mock source for demos

  implicit val ec = ExecutionContext.Implicits.global

  override def receive: Receive = {

    case TickPollCommand =>
      {
        val data: Option[Tick] = feedService.getLastTick(instrument)

        data match {
          case Some(tick) =>
            subscribtions.get(TickDataType).get.foreach { listener => listener ! tick }; log.info("tick received " + tick)
          case None => log.debug("No tick")

        }
      }

    case TradePollCommand =>
      {
        
        val data: List[Trade] = feedService.getTrades(instrument, lastTradeTimestamp)

        data match {
          case Nil => log.debug("No new trades")
          case trades: List[Trade] => {
            log.info("trades " + trades.head.instrument + " " + trades.map(x => x.price.toString()).reduce((x, y) => x + "," + y))
            log.info("subscribtions " + subscribtions)
            subscribtions.get(TradeDataType).get.foreach { listener => data.foreach(trade => listener ! trade) };

          }
        }
      }

    case SubscribeListenerRequest(actor, subject, datatypes) => {
      datatypes match {
        case Some(tp) => { tp.filter(x => subject == instrument).foreach(topic => subscribe(actor, topic)); log.info("subscribe " + subject) }
        case None     => { MarketDataTypes.values.filter(x => subject == instrument).foreach(topic => subscribe(actor, topic)); log.info("subscribe all topics " + subject) }
        case _        => log.info("Ignored subscribtion on instruments out of actor scope subject was " + subject)
      }
    }

    case message: AnyRef => throw new UnsupportedOperationException("unsupported message =" + message)

  }

  override def preStart(): Unit = {
    MarketDataTypes.values.foreach { topic =>
      {
        if (!subscribtions.keySet.contains(topic)) {
          subscribtions = subscribtions.+((topic, Set[ActorRef]()))
        }
      }
    }
    //startup polling of all topics - schedule polling every  800 ms
    val commands = topics.map { topic => MarketDataPollCommandEnum.withName(topic.toString()) }
    log.info("schedule these topics " + commands)
    commands.foreach(cmd => context.system.scheduler.schedule(FiniteDuration(0, MILLISECONDS), FiniteDuration(800, MILLISECONDS))(self ! cmd))

    ()
  }
}
