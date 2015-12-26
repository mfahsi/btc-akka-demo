package util.random
import marketdata._
object RandomGenerators {

  trait Generator[+T] {
    self =>
    def generate: T
    def foreach[U](f: T => U) {
      f(generate)
    }
    def map[S](f: T => S): Generator[S] = new Generator[S] {
      def generate = f(self.generate)
    }
    def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
      def generate = f(self.generate).generate
    }
  }

  val integers = new Generator[Int] {
    def generate = scala.util.Random.nextInt()
  }
  
   val doubles = new Generator[Double] {
    def generate = scala.util.Random.nextDouble()
  }

  val zeroOrOne = choose(0, 1)
  
  val booleans = integers.map(_ >= 0)
  
  val signe = integers.map(x => {if (x >=0) 1 else -1});
  

  def choose(from: Int, to: Int) = new Generator[Int] {
    def generate = if (from == to) from else scala.util.Random.nextInt(to - from) + from
  }
  
  def ticks(exchange:String, subject:String, averagePrice: BigDecimal, deviation: Double) = new Generator[Tick] {
    def generate = {
         new Tick(exchange,subject, averagePrice + signe.generate * deviation * doubles.generate );
    }
  }
  
  val tradesSize = choose(0,4)
  
   def trades(exchange:String, subject:String, averagePrice: BigDecimal, deviation: Double) = new Generator[List[Trade]] {
    def generate = {
      val size = tradesSize.generate
      val trades : Seq[Trade] = for(i <-  0 to size -1) yield new Trade(exchange,subject, averagePrice + signe.generate * deviation * doubles.generate, 2)
      trades.toList
    }
  }
}