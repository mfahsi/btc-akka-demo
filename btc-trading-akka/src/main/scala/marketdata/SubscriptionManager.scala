package marketdata

import marketdata.MarketDataTypes.{MarketDataType}
import akka.actor.ActorRef

trait SubscriptionManager {

  var subscribtions: Map[MarketDataType, Set[ActorRef]] = Map[MarketDataTypes.MarketDataType, Set[ActorRef]]()
 
  def subscribe(subscriber: ActorRef, topic: MarketDataType): Unit = {
    def addIfSameTopic(subscriber: ActorRef, topic: MarketDataType, entry: (MarketDataTypes.MarketDataType, Set[ActorRef])): (MarketDataTypes.MarketDataType, Set[ActorRef]) =
      {
        if (entry._1 == topic) { (entry._1, entry._2 + subscriber) } else { entry }

      }
    subscribtions = subscribtions.map(mapEntry => addIfSameTopic(subscriber, topic, mapEntry))
    ()

  }
  
   def unsubscibe(subscriber: ActorRef, topic: MarketDataType): Unit = {
    def removeIfSameTopic(subscriber: ActorRef, topic: MarketDataTypes.MarketDataType, entry: (MarketDataType, Set[ActorRef])): (MarketDataTypes.MarketDataType, Set[ActorRef]) =
      {
        if (entry._1 == topic) (entry._1, entry._2 - subscriber)
        else
          entry
      }
    subscribtions = subscribtions.map(mapEntry => removeIfSameTopic(subscriber, topic, mapEntry))
    ()
  }
}