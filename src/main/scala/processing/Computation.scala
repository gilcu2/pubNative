package processing

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Computation {

	def computeMetrics(impressions: Seq[Impression], clicks: Seq[Click]): Seq[Metric] = {
		val clicksMap = clicks.map(c => (c.impression_id, c.revenue)).toMap

		impressions
			.groupBy(i => (i.app_id, i.country_code))
			.map { case ((app_id, country_code), impressions_related) => {
				val impressionsCount = impressions_related.length
				val clicksCount = impressions_related.map(i => if (clicksMap.contains(i.id)) 1 else 0).sum
				val revenue = impressions_related.map(i => clicksMap.getOrElse(i.id, 0.0)).sum
				Metric(app_id, country_code, impressionsCount, clicksCount, revenue)
			}
			}
			.toSeq
	}

	def computeTopAdvertizers(impressions: Seq[Impression], clicks: Seq[Click]): TopAdvertisers = {

		val sparkConf = new SparkConf().setAppName("PubNative")
		implicit val spark = SparkSession
			.builder()
			.config(sparkConf)
			.getOrCreate()

		import spark.implicits._

		val impressionsDS = impressions.toDS.repartition(8)
		val clicksDS = clicks.toDS.repartition(8)

	}

}
