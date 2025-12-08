import java.util.Objects
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    forLinesIn("day08/input.txt") { input ->
        val pos = input.map { row ->
            val nrs = row.split(",")
                .map { it.toInt() }
                .toList()
            Pos3(nrs[0], nrs[1], nrs[2])
        }
        .toList()
        println(pos)

        val dists = HashMap<PKey, Double>()
        for (i in 0..pos.size - 2) {
            for (j in i + 1..<pos.size) {
                val a = pos[i]
                val b = pos[j]
                val dist = sqrt(
                    (b.x - a.x).toDouble().pow(2.0)
                            + (b.y - a.y).toDouble().pow(2.0)
                            + (b.z - a.z).toDouble().pow(2.0)
                )
                //println("a $a, b $b -> $dist")
                dists[PKey(a, b)] = dist
            }
        }
        val res = part1(dists, 1000)
        println("Part 1: $res")

        val lastTwo = part2(dists, pos)
        println("Part 2: ${lastTwo[0].x.toLong() * lastTwo[1].x}")
    }
}

private fun part2(dists: HashMap<PKey, Double>, pos: List<Pos3>): List<Pos3> {
    val circuits = HashSet<HashSet<Pos3>>(pos.map { HashSet(setOf(it)) }.toSet())
    val usedKeys = HashSet<PKey>()
    val sorted = dists.toList().sortedBy { it.second }
    var lastTwo: List<Pos3>? = null
    for (c in 1..10000) {
        //println("Run $c, circuits size ${circuits.size}")
        val minEntry = sorted.first { !usedKeys.contains(it.first) }
        val minKey = minEntry.first
        findCircuit(circuits, minKey).addAll(setOf(minKey.a, minKey.b))
        if (circuits.size == 1) {
            lastTwo = listOf(minKey.a, minKey.b)
            break
        }
        usedKeys.add(minKey)
    }
    lastTwo!!
    println("last two $lastTwo")
    return lastTwo
}

private fun part1(
    dists: HashMap<PKey, Double>,
    iterations: Int
): Int {
    val circuits = HashSet<HashSet<Pos3>>()
    val usedKeys = HashSet<PKey>()
    val sorted = dists.toList().sortedBy { it.second }
    for (c in 1..iterations) {
        //println("Run $c")
        val minEntry = sorted.first { !usedKeys.contains(it.first) }
        val minKey = minEntry.first
        findCircuit(circuits, minKey).addAll(setOf(minKey.a, minKey.b))
        usedKeys.add(minKey)
    }
    return circuits.map { it.size }.sorted().reversed().take(3)
        .reduce { a, b -> a * b }
}

data class PKey(val a: Pos3, val b: Pos3)

private fun findCircuit(
    circuits: HashSet<HashSet<Pos3>>,
    key: PKey
): HashSet<Pos3> {
    val found = circuits.filter { it.contains(key.a) || it.contains(key.b) }.toList()
    if (found.isEmpty()) {
        val newCircuit = HashSet<Pos3>()
        circuits.add(newCircuit)
        return newCircuit
    }
    if (found.size > 1) {
        // Merging
        val sorted = found.sortedBy { it.size }.reversed()
        val first = sorted.first()
        for (f in sorted.subList(1, sorted.size)) {
            first.addAll(f)
            circuits.removeIf { Objects.equals(it, f) }
        }
        return first
    }
    return found.first()
}

data class Pos3(val x: Int, val y: Int, val z: Int)

