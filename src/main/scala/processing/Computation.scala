package processing

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Dataset, SparkSession}
import processing.FieldNames._

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

	def computeTopAdvertisers(impressions: Seq[Impression], clicks: Seq[Click])(implicit spark: SparkSession): Dataset[TopAdvertisers] = {

		import spark.implicits._

		val impressionsDS = impressions.toDS.repartition(8)
		val clicksDS = clicks.toDS.repartition(8)

		val join = impressionsDS.join(broadcast(clicksDS), impressionsDS("id") === clicksDS("impression_id"), "left")

		//		join.show()

		val withAvg = join.groupBy(app_idF, country_codeF, advertiser_idF).agg(avg(revenueF))
		//		withAvg.show()

		val groupingAdvertizers = withAvg.withColumn("combined", array("avg(revenue)", advertiser_idF))
			.groupBy(app_idF, country_codeF)
			.agg(sort_array(collect_list("combined"), false))
			.withColumnRenamed("sort_array(collect_list(combined), false)", "sorted")

		//		groupingAdvertizers.show()

		val topAdvertizer = groupingAdvertizers
			.withColumn("First5", slice(col("sorted"), 1, 5))
			.withColumn(recommended_advertiser_idsF, expr("transform(First5,x -> cast(x[1] as int))"))
			.select(app_idF, country_codeF, recommended_advertiser_idsF)
			.as[TopAdvertisers]
			.sort(country_codeF, app_idF)

		//		topAdvertizer.printSchema()
		//		topAdvertizer.show()

		topAdvertizer
	}

}
