package processing

import org.apache.spark.sql.functions.expr
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import testutils.SparkSessionTestWrapper

class ComputationTest extends AnyFlatSpec with Matchers with SparkSessionTestWrapper {

	"Computation" should "compute the metrics" in {

		val impressionsText =
			"""
				|[
				|{
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "UK",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b5"
				|    },
				|    {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "UK",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b7"
				|    },
				| {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "DE",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b6"
				|    },
				|    {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "DE",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b9"
				|    }
				|]
				|""".stripMargin
		val impressions = Reader.parseImpressions(impressionsText).right.get

		val clicksText =
			"""
				|[{
				|        "impression_id": "a39747e8-9c58-41db-8f9f-27963bc248b5",
				|        "revenue": 2.0
				|    },
				|    {
				|        "impression_id": "a39747e8-9c58-41db-8f9f-27963bc248b7",
				|        "revenue": 2.4
				|    },
				|    {
				|    	"impression_id": "a39747e8-9c58-41db-8f9f-27963bc248b6",
				|     "revenue": 1.1
				|    }
				|]
				|""".stripMargin

		val clicks = Reader.parseClicks(clicksText).right.get

		val metrics = Computation.computeMetrics(impressions, clicks)

		metrics.toSet should be(Set(
			Metric(32, "UK", 2, 2, 4.4),
			Metric(32, "DE", 2, 1, 1.1)
		))

	}

	"Computation" should "compute top advertiser, advertisers without revenue must not be included" in {

		val impressionsText =
			"""
				|[
				|{
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "UK",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b5"
				|    },
				|    {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "UK",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b7"
				|    },
				|    {
				|        "app_id": 32,
				|        "advertiser_id": 9,
				|        "country_code": "UK",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc24810"
				|    },
				| {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "DE",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b6"
				|    },
				|    {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "DE",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b9"
				|    },
				|    {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "US",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248c9"
				|    }
				|]
				|""".stripMargin
		val impressions = Reader.parseImpressions(impressionsText).right.get

		val clicksText =
			"""
				|[{
				|        "impression_id": "a39747e8-9c58-41db-8f9f-27963bc248b5",
				|        "revenue": 2.0
				|    },
				|    {
				|        "impression_id": "a39747e8-9c58-41db-8f9f-27963bc248b7",
				|        "revenue": 2.4
				|    },
				|    {
				|        "impression_id": "a39747e8-9c58-41db-8f9f-27963bc24810",
				|        "revenue": 5.3
				|    },
				|    {
				|    	"impression_id": "a39747e8-9c58-41db-8f9f-27963bc248b6",
				|     "revenue": 1.1
				|    }
				|]
				|""".stripMargin

		val clicks = Reader.parseClicks(clicksText).right.get

		val topAdvertizers = Computation.computeTopAdvertisers(impressions, clicks)

		topAdvertizers.collect.toSet should be(Set(
			TopAdvertisers(32, "UK", Seq(9, 8)),
			TopAdvertisers(32, "DE", Seq(8))
		))

	}

}
