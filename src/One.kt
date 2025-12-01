
fun main() {
    forLinesIn("day01/input.txt") { input ->
        var dial = 50
        var zeros = 0
        var zeros2 = 0
        input.forEach {
            //println("dial $dial")
            val startDial = dial
            val dir = it[0]
            val steps = it.substring(1).toInt()
            if (dir == 'L')
                dial -= steps
            else // R
                dial += steps
            dial = dial.mod(100)
            if (dial == 0) {
                ++zeros
            }

            // Part 2
            var d = startDial
            if (dir == 'R') {
                for (i in 0..<steps) {
                    ++d
                    if (d == 100)
                        d = 0
                    if (d == 0)
                        ++zeros2
                }
            } else {
                for (i in 0..<steps) {
                    --d
                    if (d == -1)
                        d = 99
                    if (d == 0)
                        ++zeros2
                }
            }

        }

        println("Part 1: $zeros")
        println("Part 2: $zeros2")
    }
}

