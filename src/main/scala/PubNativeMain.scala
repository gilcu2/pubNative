import processing.Processing

object PubNativeMain {

	def main(argv: Array[String]): Unit = {
		val impressions = Processing.loadImpressions("data/impressions.json")
		println(impressions.head)
	}

}
