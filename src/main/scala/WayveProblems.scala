object WayveProblems extends App {

	def getDistanceMetrics(arr: Array[Int]): Array[Long] = {
		val withIndex = arr.zipWithIndex
		withIndex.map(x => {
			val others = withIndex.filter(y => y._1 == x._1 && y._2 != x._2)
			others.map(y => math.abs(y._2 - x._2)).sum.toLong
		})
	}

	def horseMinMoves(n: Int, startRow: Int, startCol: Int, endRow: Int, endCol: Int): Int = {
		case class Vector(col: Int, row: Int) {
			def +(other: Vector) = Vector(col + other.col, row + other.row)
		}

		case class PositionAndDistance(pos: Vector, distance: Int)

		val movements = Seq(Vector(-2, -1), Vector(-2, 1), Vector(2, -1), Vector(2, 1),
			Vector(1, -2), Vector(1, 2), Vector(-1, -2), Vector(-1, 2))

		def isValid(m: Vector): Boolean = m.col >= 1 && m.col <= n && m.row >= 1 && m.row <= n

		val start = Vector(startCol, startRow)
		val end = Vector(endCol, endRow)

		val queue = scala.collection.mutable.Queue(PositionAndDistance(start, 0))
		val visited = scala.collection.mutable.Set[PositionAndDistance]()
		val found = false

		while (true) {

			val node = queue.dequeue()

			if (node.pos == end)
				return node.distance

			if (!visited.contains(node)) {

				visited.add(node)

				movements.foreach(m => if (isValid(node.pos + m))
					queue += (PositionAndDistance(node.pos + m, node.distance + 1)))

			}


		}

		-1

	}

	println(horseMinMoves(8, 1, 1, 2, 2))

}
