package processing

import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

case class Impression(id: String, app_id: Integer, country_code: String, advertiser_id: String)

object Processing {

	def loadImpressions(path: String): Seq[Impression] = {
		val buffer = scala.io.Source.fromFile(path).mkString
		decode[Seq[Impression]](buffer).right.get
	}

}
