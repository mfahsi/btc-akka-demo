package marketdata

import scala.math.BigDecimal.int2bigDecimal

import util.random.RandomGenerators

/**
 * real service use xchange api with R/Replay or streaming flavors
 * this is a demo service : generating ramdom prices around a particular mean price
 */
class FeedServiceMock(val exchange: String, val instrument: String) extends FeedService {

  val ticksGenerator: RandomGenerators.Generator[Tick] = RandomGenerators.ticks(exchange, instrument, 10, 1)
  val tradesGenerator: RandomGenerators.Generator[List[Trade]] = RandomGenerators.trades(exchange, instrument, 10, 1)

  def getLastTick(subject: String): Option[Tick] = {
    Some(ticksGenerator.generate)
  }
  def getTrades(subject: String, sinceTimestamp: Long): List[Trade] = {
    tradesGenerator.generate
  }
}
