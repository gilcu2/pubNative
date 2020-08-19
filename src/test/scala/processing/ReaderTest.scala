package processing

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReaderTest extends AnyFlatSpec with Matchers {

	"Processing" should "convert from impression json text list to seq class" in {
		val text =
			"""
				|[{
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "UK",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b5"
				|    },
				| {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "DE",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b6"
				|    }
				|]
				|""".stripMargin
		val either = Reader.parseImpressions(text)
		val impression = either.right.get

		impression should be(Seq(
			Impression("a39747e8-9c58-41db-8f9f-27963bc248b5", 32, "UK", 8),
			Impression("a39747e8-9c58-41db-8f9f-27963bc248b6", 32, "DE", 8)
		))
	}

	"Processing" should "convert from impression json text list to seq class when fields are null or missing " in {
		val text =
			"""
				|[{
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": "UK",
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b5"
				|    },
				| {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "country_code": null,
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b6"
				|    },
				|    {
				|        "app_id": 32,
				|        "advertiser_id": 8,
				|        "id": "a39747e8-9c58-41db-8f9f-27963bc248b6"
				|    }
				|
				|]
				|""".stripMargin
		val either = Reader.parseImpressions(text)
		val impression = either.right.get

		impression should be(Seq(
			Impression("a39747e8-9c58-41db-8f9f-27963bc248b5", 32, "UK", 8),
		))
	}

	"Processing" should "convert from cliks json text list to seq class when fields are null or missing " in {
		val text =
			"""
				|[{
				|        "impression_id": "97dd2a0f-6d42-4c63-8cd6-5270c19f20d6",
				|        "revenue": 2.091225600111518
				|    },
				|    {
				|        "impression_id": "43bd7feb-3fea-40b4-a140-d01a35ec1f73",
				|        "revenue": 2.4794577548980876
				|    },
				|    {
				|    	"impression_id": "43bd7feb-3fea-40b4-a140-d01a35ec1f73",
				|     "revenue": null
				|    }
				|]
				|""".stripMargin

		val either = Reader.parseClicks(text)
		val clicks = either.right.get

		clicks should be(Seq(
			Click("97dd2a0f-6d42-4c63-8cd6-5270c19f20d6", 2.091225600111518),
			Click("43bd7feb-3fea-40b4-a140-d01a35ec1f73", 2.4794577548980876)
		))
	}


}
