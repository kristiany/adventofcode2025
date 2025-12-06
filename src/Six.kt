
fun main() {
    forLinesIn("day06/input.txt") { input ->
        val inputProblems = input.toList()
        val problems = inputProblems
            .map { it.trim().split(Regex(" +")).toList() }
            .toList()
        //println(problems)
        val nrs = problems.subList(0, problems.size - 1)
            .map { inputList -> inputList.map { it.toLong() }.toList() }
            .toList()
        val ops = problems.last()
        var total = 0L
        for (i in 0..<nrs[0].size) {
            val subtotal = ArrayList<Long>()
            for (j in nrs.indices) {
                subtotal.add(nrs[j][i])
            }
            total += if (ops[i] == "+") subtotal.sum()
                     else subtotal.reduce { a, b -> a * b }
        }
        println("Part 1: $total")

        val nrsStartIndex = inputProblems.last()
            .mapIndexedNotNull { i, c -> if (c != ' ') i else null }
            .toList()
        println(nrsStartIndex)
        val w = inputProblems.maxOf { it.length }
        val problemStrings = inputProblems.subList(0, inputProblems.size - 1)
        var total2 = 0L
        for (i in nrsStartIndex.indices) {
            //println("ops $ops, i $i, nrsStartI $nrsStartIndex")
            val op = ops[i]
            val start = nrsStartIndex[i]
            val end = if (i == nrsStartIndex.size - 1) w else nrsStartIndex[i + 1]
            val sub = ArrayList<Long>()
            for (j in start..<end) {
                var nrStr = ""
                for (row in problemStrings.indices) {
                    if (j < problemStrings[row].length) {
                        nrStr += problemStrings[row][j]
                    }
                }
                if (nrStr.isNotBlank()) {
                    sub.add(nrStr.trim().toLong())
                }
            }
            //println(sub)
            val subTotal = if (op == "+") sub.sum()
                           else sub.reduce { a, b -> a * b }
            //println("  $subTotal")
            total2 += subTotal
        }
        println("Part 2: $total2")

    }
}

