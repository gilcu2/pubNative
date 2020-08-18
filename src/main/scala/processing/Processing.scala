package processing

import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

case class Impression(id: String, app_id: Integer, country_code: String, advertiser_id: String)

case class Click(impression_id: String, revenue: Double)

object Processing {

	def loadClicks(path: String): Seq[Click] = {
		val buffer = scala.io.Source.fromFile(path).mkString
		val either = decode[Seq[Click]](buffer)
		either.right.get
	}

}
