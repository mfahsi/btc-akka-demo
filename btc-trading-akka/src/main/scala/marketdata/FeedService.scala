package marketdata

trait FeedService {
  def getLastTick(subject: String): Option[Tick]
  def getTrades(subject: String, sinceTimestamp: Long): List[Trade]

}
