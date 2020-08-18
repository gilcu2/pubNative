package processing

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

class ProcessingTest extends AnyFlatSpec with Matchers {

	"Processing" should "convert from json text to class" in {
		val text =
			"""
				|{
				|  "impression_id":"one",
				|  "app_id":1
				|  }
				|""".stripMargin
		val either = decode[Click](text)
		val click = either.right.get

		click should be(Click("one", 1))
	}

	"Processing" should "convert from json text to seq class" in {
		val text =
			"""
				|[
				| {
				|  "impression_id":"one",
				|  "app_id":1
				|  },
				|  {
				|  "impression_id":"two",
				|  "app_id":3
				|  }
				|]
				|""".stripMargin
		val either = decode[Seq[Click]](text)
		val click = either.right.get

		click should be(Seq(Click("one", 1), Click("two", 3)))
	}

}
