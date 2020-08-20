import com.typesafe.scalalogging.LazyLogging
import interfaces.{HadoopFS, Spark}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import processing._
import org.rogach.scallop.ScallopConf

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
	banner("Pass the paths for impressions, clicks, metrics and top advertiser as line parameter")

	val impressionsPath = trailArg[String]()
	val clicksPath = trailArg[String]()
	val metricsPath = trailArg[String]()
	val topAdvertiserPath = trailArg[String]()

	verify()
}

object PubNativeMain extends LazyLogging {

	def main(argv: Array[String]): Unit = {

		val lineParameters = new Conf(argv)

		val sparkConf = new SparkConf().setAppName("PubNative")
		implicit val spark = Spark.sparkSession(sparkConf)

		val possibleImpressions = Reader.loadImpressions(lineParameters.impressionsPath())
		val possibleClicks = Reader.loadClicks(lineParameters.clicksPath())

		(possibleImpressions, possibleClicks) match {
			case (Right(impressions), Right(clicks)) => process(impressions, clicks,
				lineParameters.metricsPath(), lineParameters.topAdvertiserPath())
			case _ => logger.error("Problem reading json files")
		}

	}

	def process(impressions: Seq[Impression], clicks: Seq[Click],
							pathMetrics: String, pathTopAdvertiser: String)(implicit spark: SparkSession): Unit = {
		computeAndSaveMetrics(impressions, clicks, pathMetrics)

		computeAndSaveTopAdvertisers(impressions, clicks, pathTopAdvertiser)

		logger.info("Processing finish")
	}


	private def computeAndSaveTopAdvertisers(impressions: Seq[Impression], clicks: Seq[Click],
																					 pathTopAdvertiser: String)(implicit spark: SparkSession): Unit = {
		val topAdvertisers = Computation.computeTopAdvertisers(impressions, clicks)
		val tmpPath = pathTopAdvertiser + ".tmp"
		HadoopFS.delete(tmpPath)
		topAdvertisers.write.json(tmpPath)
		HadoopFS.delete(pathTopAdvertiser)
		HadoopFS.merge(pathTopAdvertiser, tmpPath)
		HadoopFS.delete(tmpPath)
	}

	private def computeAndSaveMetrics(impressions: Seq[Impression], clicks: Seq[Click], pathMetrics: String) = {
		val metrics = Computation.computeMetrics(impressions, clicks)
		Writer.writeMetrics(metrics, pathMetrics)
	}
}
