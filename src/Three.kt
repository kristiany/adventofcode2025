import kotlin.collections.joinToString

fun main() {
    forLinesIn("day03/input.txt") { input ->
        val banks = input.toList()
        val result = banks.sumOf { bank ->
            var maxJolt = 0
            for (i in 0..<bank.length) {
                val a = bank[i].toString()
                for (b in bank.substring(i + 1)) {
                    val jolt = (a + b).toInt()
                    if (jolt > maxJolt) {
                        maxJolt = jolt
                    }
                }
            }
            maxJolt
        }
        println("Part 1: $result")

        val result2 = banks.sumOf { bank ->
            maxJolt3(bank)
        }
        println("Part 2: $result2")
    }

}

// Works but is too slow
private fun maxJolt(acc: String, tail: String, max: Long, level: Int): Long {
    if (tail.isBlank() || level == 12) {
        return acc.toLong()
    }
    var result = max
    for (i in tail.indices) {
        val first = tail[i]
        val rest = tail.substring(i + 1)
        val r = maxJolt(acc + first, rest, result, level + 1)
        if (r > result) {
            result = r
        }
    }
    return result
}

// Inspired by https://www.reddit.com/r/adventofcode/comments/1pcvaj4/comment/ns4ori8/
private fun maxJolt3(bankStr: String): Long {
    val bank = bankStr.map { it.toString().toInt() }.toList()
    val result = ArrayList<Int>()
    var left = 0
    for (right in (bank.size - 12 + 1)..bank.size) {
        left += bank.subList(left, bank.size)
            .indexOf(bank.subList(left, right).max()) + 1
        result.add(bank[left - 1])
    }
    println("res ${result.joinToString("").toLong()}")
    return result.joinToString("").toLong()
}
