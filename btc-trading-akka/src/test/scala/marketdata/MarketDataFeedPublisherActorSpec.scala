package marketdata

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.SECONDS
import org.scalatest.Matchers
import org.scalatest.WordSpecLike
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.testkit.ImplicitSender
import akka.testkit.TestKit
import akka.testkit.TestProbe
import marketdata.MarketDataTypes.TickDataType
import marketdata.MarketDataTypes.TradeDataType
import org.scalatest.BeforeAndAfterAll

class MarketDataFeedPublisherActorSpec extends TestKit(ActorSystem("marketdata-usd"))
  with ImplicitSender
  with Matchers
  with WordSpecLike 
  with BeforeAndAfterAll {
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  "Market data publisher" should {
    "send trade to subscribed listener on same topic Trade case" in {
      val actorRef = system.actorOf(Props(new MarketDataFeedPublisherActor("testExchange", "BTC_USD", List(TradeDataType))), "publisherTrades")
      val mock = TestProbe()
      actorRef ! SubscribeListenerRequest(mock.ref, "BTC_USD", Some(List(TradeDataType)))
      mock.expectMsgType[Trade](FiniteDuration(1, SECONDS))
     
    }
  }

  "Market data publisher" should {
    "send tick to subscribed listener on same topic Tick Case" in {
      val actorRef = system.actorOf(Props(new MarketDataFeedPublisherActor("testExchange", "BTC_USD", List(TickDataType))), "publisherTicks")
      val probe = TestProbe()
      actorRef ! SubscribeListenerRequest(probe.ref, "BTC_USD", Some(List(TickDataType)))
      probe.expectMsgType[Tick](FiniteDuration(1, SECONDS))
    }
  }

  "Market data publisher" should {
    "ignore subscribtions on incorrect instrument" in {
      val actorRef = system.actorOf(Props(new MarketDataFeedPublisherActor("testExchange", "BTC_USD", List(TickDataType, TradeDataType))), "publisherNoSubscriberOnInstrument")
      val mock = TestProbe()
      actorRef ! SubscribeListenerRequest(mock.ref, "BTC_EUR", None) //Wrong instrument BTC_EUR ... he does USD
      mock.expectNoMsg(FiniteDuration(1, SECONDS))

    }
  }

  "Market data publisher" should {
    "ignore subscribtions on incorrect topics" in {
      val actorRef = system.actorOf(Props(new MarketDataFeedPublisherActor("testExchange", "BTC_USD", List(TickDataType))), "publisherNoSubscriberOnTopic")
      val mock = TestProbe()
      actorRef ! SubscribeListenerRequest(mock.ref, "BTC_USD", Some(List(TradeDataType))) //server has only trades
      mock.expectNoMsg(FiniteDuration(1, SECONDS))

    }
  }

}