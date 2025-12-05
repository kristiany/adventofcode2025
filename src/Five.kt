
fun main() {
    forLinesIn("day05/input.txt") { input ->
        val ranges = ArrayList<LongRange>()
        val ids = ArrayList<Long>()
        input.forEach {
            if (it.contains("-")) {
                val rangeInput = it.split("-")
                val range = LongRange(rangeInput[0].trim().toLong(),
                    rangeInput[1].trim().toLong())
                ranges.add(range)
            }
            else if (it.isNotBlank()) {
                ids.add(it.trim().toLong())
            }
        }
        println(ranges)
        println(ids)

        val fresh = ids.filter { id -> ranges.any { it.contains(id) } }
        println("Part 1: ${fresh.count()}")

        var merged = ArrayList(ranges)
        var prevSize: Int
        do {
            prevSize = merged.size
            merged = mergeRanges(merged)

        } while (merged.size < prevSize)

        val freshRangeIds = merged
            .sumOf { it.last - it.first + 1 }
        println("Part 2: $freshRangeIds")
    }
}

private fun mergeRanges(ranges: List<LongRange>): ArrayList<LongRange> {
    val merged = ArrayList<LongRange>()
    val mergedIdx = HashSet<Int>()
    for (i in 0..<ranges.size - 1) {
        if (mergedIdx.contains(i)) {
            continue
        }
        val a = ranges[i]
        for (j in i + 1..<ranges.size) {
            if (mergedIdx.contains(j)) {
                continue
            }
            val b = ranges[j]
            val result = merge(a, b)
            if (result.size == 1) {
                merged.add(result.first())
                mergedIdx.add(i)
                mergedIdx.add(j)
                break
            }
        }
        if (!mergedIdx.contains(i)) {
            merged.add(a)
        }
    }
    if (!mergedIdx.contains(ranges.size - 1)) {
        merged.add(ranges.last())
    }
    //println(merged)
    return merged
}

private fun merge(a: LongRange, b: LongRange): List<LongRange> {
    //println("merging $a, $b")
    if (a.contains(b.first) && a.contains(b.last)) {
        //println("  b contained in a")
        return listOf(a)
    }
    if (b.contains(a.first) && b.contains(a.last)) {
        //println("  a contained in b")
        return listOf(b)
    }
    if (a.contains(b.first)) {
        //println("  new range ${a.first} .. ${b.last}")
        return listOf(LongRange(a.first, b.last))
    }
    if (a.contains(b.last)) {
        //println("  new range ${b.first} .. ${a.last}")
        return listOf(LongRange(b.first, a.last))
    }
    //println("  no merge")
    return listOf(a, b)
}