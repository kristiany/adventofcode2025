import kotlin.collections.any
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    forLinesIn("day09/input.txt") { input ->
        val pos = input.map { row ->
            val nrs = row.split(",")
                .map { it.toLong() }
                .toList()
            LPos(nrs[0], nrs[1])
        }
        .toList()

        val max = largest(pos)
        println("Part 1: $max")

        val edges = ArrayList<Edge>()
        for (i in 0..<pos.size) {
            edges.add(Edge(pos[i], pos[if (i + 1 < pos.size) i + 1 else 0]))
        }
        val boxes = boxes(pos)
        var largestWithin: Box? = null
        outer@ for (box in boxes) {
            //println("box $box")
            for (boxEdge in box.edges()) {
                if (edges.any { it.crossing(boxEdge) }) {
                    //println("  ${boxEdge} not inside")
                    continue@outer
                }
            }
            println("largest box $box")
            largestWithin = box
            break
        }
        println("Part 2: ${largestWithin?.size}")
    }
}

data class Box(val a: LPos, val b: LPos, val size: Long) {
    fun minX(): Long {
        return min(a.x, b.x)
    }

    fun maxX(): Long {
        return max(a.x, b.x)
    }

    fun minY(): Long {
        return min(a.y, b.y)
    }

    fun maxY(): Long {
        return max(a.y, b.y)
    }

    fun edges(): List<Edge> {
        val lines = listOf(
            Edge(LPos(minX() + 1, minY() + 1), LPos(maxX() - 1, minY() + 1)),
            Edge(LPos(maxX() - 1, minY() + 1), LPos(maxX() - 1, maxY() - 1)),
            Edge(LPos(minX() + 1, maxY() - 1), LPos(maxX() - 1, maxY() - 1)),
            Edge(LPos(minX() + 1, minY() + 1), LPos(minX() + 1, maxY() - 1)),
        )
        //println("making lines $lines")
        return lines
    }
}

private fun boxes(pos: List<LPos>): List<Box> {
    var result = ArrayList<Box>()
    for (i in 0..<pos.size - 1) {
        val a = pos[i]
        for (j in i + 1..<pos.size) {
            val b = pos[j]
            val size = sizeOf(a, b)
            result.add(Box(a, b, size))
        }
    }
    return result.sortedBy { it.size }.reversed()
}

private fun largest(pos: List<LPos>): Long {
    var max = 0L
    for (i in 0..<pos.size - 1) {
        //println("pos $i / ${pos.size - 2}")
        val a = pos[i]
        for (j in i + 1..<pos.size) {
            val b = pos[j]
            val size = sizeOf(a, b)
            //println("a $a - b $b: $size")
            if (size > max) {
                max = size
            }
        }
    }
    return max
}

fun sizeOf(a: LPos, b: LPos): Long {
    return abs((max(a.x, b.x) - min(a.x, b.x) + 1)
                * (max(a.y, b.y) - min(a.y, b.y) + 1))
}

data class LPos(val x: Long, val y: Long)

data class Edge(val a: LPos, val b: LPos) {

    fun minX(): Long {
        return min(a.x, b.x)
    }

    fun maxX(): Long {
        return max(a.x, b.x)
    }

    fun minY(): Long {
        return min(a.y, b.y)
    }

    fun maxY(): Long {
        return max(a.y, b.y)
    }

    fun within(p: LPos): Boolean {
        if (a.x == b.x) {
            return p.x == a.x && p.y >= minY() && p.y <= maxY()
        }
        return p.y == a.y && p.x >= minX() && p.x <= maxX()
    }

    fun crossing(e: Edge): Boolean {
        val crossPoint = getCrossPoint(e)
        return crossPoint?.let { within(it) && e.within(it) } ?: false
    }

    private fun getCrossPoint(e: Edge): LPos? {
        if (a.x == b.x && e.a.x != e.b.x) {
            return LPos(a.x, e.a.y)
        }
        else if (a.y == b.y && e.a.y != e.b.y) {
            return LPos(e.a.x, a.y)
        }
        return null
    }

}