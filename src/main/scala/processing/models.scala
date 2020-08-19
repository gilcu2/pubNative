package processing

case class Impression(id: String, app_id: Int, country_code: String, advertiser_id: Int)

case class Click(impression_id: String, revenue: Double)

case class Metric(app_id: Int, country_code: String, impressions: Int, clicks: Int, revenue: Double)

case class TopAdvertisers(app_id: Int, country_code: String, recommended_advertiser_ids: Seq[Int])
