import processing.{Click, Computation, Impression, Reader, Writer}


object PubNativeMain {
	// TODO Use line parameter
	def main(argv: Array[String]): Unit = {

		println("Pass the paths for impressions, clicks, metrics as line parameter")

		val possibleImpressions = Reader.loadImpressions(argv(0))
		val possibleClicks = Reader.loadClicks(argv(1))

		(possibleImpressions, possibleClicks) match {
			case (Right(impressions), Right(clicks)) => process(impressions, clicks, argv(2))
			case _ => println("Problems readings json files")
		}

	}

	def process(impressions: Seq[Impression], clicks: Seq[Click], path: String): Unit = {
		val metrics = Computation.computeMetrics(impressions, clicks)
		Writer.writeMetrics(metrics, path)
	}


}
