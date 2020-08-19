import interfaces.{HadoopFS, Spark}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import processing._


object PubNativeMain {
	// TODO Option to avoid duplicates
	def main(argv: Array[String]): Unit = {

		val sparkConf = new SparkConf().setAppName("PubNative")
		implicit val spark = Spark.sparkSession(sparkConf)


		println("Pass the paths for impressions, clicks, metrics and top advertiser as line parameter")

		val possibleImpressions = Reader.loadImpressions(argv(0))
		val possibleClicks = Reader.loadClicks(argv(1))

		(possibleImpressions, possibleClicks) match {
			case (Right(impressions), Right(clicks)) => process(impressions, clicks, argv(2), argv(3))
			case _ => println("Problems readings json files")
		}

	}

	def process(impressions: Seq[Impression], clicks: Seq[Click],
							pathMetrics: String, pathTopAdvertiser: String)(implicit spark: SparkSession): Unit = {
		val metrics = Computation.computeMetrics(impressions, clicks)
		Writer.writeMetrics(metrics, pathMetrics)

		val topAdvertisers = Computation.computeTopAdvertisers(impressions, clicks)
		//TODO convert output to one file
		val tmpPath = pathTopAdvertiser + ".tmp"
		topAdvertisers.write.json(tmpPath)
		HadoopFS.merge(pathTopAdvertiser, tmpPath)
		HadoopFS.delete(tmpPath)

		println("Processing finish")
	}


}
