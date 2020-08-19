package processing

case class Impression(id: String, app_id: Int, country_code: String, advertiser_id: Int)

case class Click(impression_id: String, revenue: Double)

case class Metric(app_id: Int, country_code: String, impressions: Int, clicks: Int, revenue: Double)

case class TopAdvertisers(app_id: Int, country_code: String, recommended_advertiser_ids: Seq[Int])

object FieldNames {
	val idF = "id"
	val app_idF = "app_id"
	val country_codeF = "country_code"
	val advertiser_idF = "advertiser_id"
	val impression_idF = "impression_id"
	val revenueF = "revenue"
	val recommended_advertiser_idsF = "recommended_advertiser_ids"

}