
fun main() {
    forLinesIn("day02/input.txt") { input ->
        val ranges: List<LongRange> = input.iterator().next().split(",")
            .map { r ->
                val list = r.split("-").map { it.toLong() }.toList()
                LongRange(list.first(), list.last())
            }
            .toList()
        //println(ranges)

        var invalidIdsPart1 = 0L
        var invalidIdsPart2 = 0L
        val repeats = Regex("^([0-9]+)\\1+$")
        ranges.forEach { range ->
            for (nr in range) {
                val asStr = nr.toString()
                // Part 2
                if (repeats.matches(asStr)) {
                    //println("range $range: invalid $nr")
                    invalidIdsPart2 += nr

                }

                // Part 1
                if (asStr.length % 2 != 0) {
                    continue
                }
                val left = asStr.take(asStr.length / 2)
                val right = asStr.substring(asStr.length / 2)
                //println("range: $range, nr $nr, left: $left, right: $right")
                if (left == right) {
                    invalidIdsPart1 += nr
                }
            }
        }

        println("Part 1: $invalidIdsPart1")
        println("Part 2: $invalidIdsPart2")
    }
}