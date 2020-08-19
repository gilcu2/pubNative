package processing

import io.circe._
import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object Reader {

	case class OptionImpression(id: Option[String], app_id: Option[Int], country_code: Option[String], advertiser_id: Option[Int])

	case class OptionClick(impression_id: Option[String], revenue: Option[Double])

	//TODO Read as stream
	//TODO Parametrice

	def loadFile(path: String): String = {
		val file = scala.io.Source.fromFile(path)
		val buffer = file.mkString
		file.close()
		buffer
	}

	def parseImpressions(buffer: String): Either[String, Seq[Impression]] = {

		val either = decode[Seq[OptionImpression]](buffer)
		either match {
			case Right(seq) => Right(seq
				.filter(o => o.id.isDefined &&
					o.advertiser_id.isDefined &&
					o.app_id.isDefined &&
					o.country_code.isDefined &&
					o.country_code.get.nonEmpty
				)
				.map(o => Impression(o.id.get, o.app_id.get, o.country_code.get, o.advertiser_id.get))
			)
			case Left(_) => Left(s"Problem parsing impressions json")
		}
	}

	def loadClicks(path: String): Either[String, Seq[Click]] = {
		val buffer = loadFile(path)
		parseClicks(buffer)
	}

	def parseClicks(buffer: String): Either[String, Seq[Click]] = {

		val either = decode[Seq[OptionClick]](buffer)
		either match {
			case Right(seq) => Right(seq
				.filter(o => o.impression_id.isDefined && o.revenue.isDefined)
				.map(o => Click(o.impression_id.get, o.revenue.get))
			)
			case Left(_) => Left(s"Problem parsing clicks json")
		}
	}

	def loadImpressions(path: String): Either[String, Seq[Impression]] = {
		val buffer = loadFile(path)
		parseImpressions(buffer)
	}


}
