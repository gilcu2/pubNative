package processing

import java.io._

import io.circe._
import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object Writer {

	def writeMetrics(metrics: Seq[Metric], path: String): Unit = {
		val writer = new BufferedWriter(new FileWriter(path))
		val s = metrics.asJson.toString()
		writer.write(s)
		writer.close()
	}

}
