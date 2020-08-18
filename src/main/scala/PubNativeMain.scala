import processing.{Click, Processing}

object PubNativeMain {

	def main(argv: Array[String]): Unit = {
		val impressions = Processing.loadClicks("data/clicks.json")
		println(impressions.head)
	}

}
