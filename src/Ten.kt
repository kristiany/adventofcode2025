import java.util.Objects
import kotlin.math.min
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

fun main() {
    forLinesIn("day10/input.txt") { input ->
        val macs = input.map { row ->
            val lights = row.split("]")[0].drop(1)
            val bsplit = row.substring(row.indexOf(']') + 2, row.indexOf('{') - 1)
                .trim().split(") (")
            val buttons = bsplit
                .map { bs -> bs.replace(Regex("[()]"), "")
                            .split(",")
                            .map { it.toInt() }
                            .toList()
                }
                .toList()
            val reqs = row.split("{")[1].dropLast(1)
                .split(",")
                .map { it.toInt() }
                .toList()
            Mac(lights.map { if (it == '#') true else false }.toList(),
                buttons,
                reqs)
        }
        .toList()
        println("macs $macs")

        var i = 1
        val lowest = macs.sumOf { mac ->
            println("mac ${i++} $mac")
            val wantedIndices = mac.wanted.mapIndexed { i, b -> if (b) i else null }
                .filter { it != null }
                .map { it!! }
                .toSet()
            val res = comboRec(wantedIndices,
                mac.buttons.map { HashSet(it) }.toList(),
                HashSet(),
                0)
            if (res == null) throw IllegalStateException("Couldn't find solution for $mac")
            println("  $res")
            res
        }
        println("Part 1: $lowest")

        println("max buttons: ${macs.maxOf { it.buttons.size }}")
        var i2 = 1
        val elapsed = measureTimeMillis {
            val joltest = macs.sumOf { mac ->
                println("mac ${i2++} $mac")
                val equations = mac.joltReq.mapIndexed { i, nr ->
                    Equation(
                        mac.buttons.mapIndexedNotNull { bi, b ->
                            if (b.contains(i)) bi
                            else null
                        }.toIntArray(),
                        nr
                    )
                }.toList()
                println("eqs $equations")
                val solver = EquationDrivenSolver(equations, numVars = mac.buttons.size)
                val solution = solver.solve()

                println(solution?.joinToString())
                println("Sum = ${solution?.sum()}")
                solution?.sum() ?: 0
            }
            println("Part 2: ${joltest}")
        }
        println("time: ${elapsed.milliseconds.toString(DurationUnit.SECONDS, 1)}")

    }
}

// Manual op seems faster than union op on subtracted parts
fun xor(a: Set<Int>, b: Set<Int>):Set<Int> {
    val res = HashSet<Int>()
    for (nr in 0..9) {
        if (a.contains(nr) && b.contains(nr)) continue
        if (a.contains(nr) || b.contains(nr)) res.add(nr)
    }
    return res
}

fun comboRec(wanted: Set<Int>, buttons: List<Set<Int>>, acc: Set<Int>, clicks: Int): Int? {
    if (Objects.equals(wanted, acc)) {
        return clicks
    }
    if (buttons.isEmpty()) {
        return null
    }
    val first = buttons.first()
    val tail = buttons.drop(1)
    // No click on this one
    val res1 = comboRec(wanted, tail, acc, clicks)
    // Clicked the button
    val res2 = comboRec(wanted, tail, xor(first, acc), clicks + 1)
    if (res1 == null) {
        return res2
    }
    if (res2 == null) {
        return res1
    }
    return min(res1, res2)
}

data class Mac(val wanted: List<Boolean>, val buttons: List<List<Int>>, val joltReq: List<Int>)

data class Equation(
    val vars: IntArray,
    val rhs: Int
)

/*
    GPT 5 was here!
    Equation-driven solution for positive integers
 */
fun generateDistributions(
    k: Int,
    sum: Int,
    current: IntArray = IntArray(k),
    idx: Int = 0,
    out: MutableList<IntArray>
) {
    if (idx == k - 1) {
        current[idx] = sum
        out.add(current.clone())
        return
    }
    for (v in 0..sum) {
        current[idx] = v
        generateDistributions(k, sum - v, current, idx + 1, out)
    }
}

class EquationDrivenSolver(
    equationsInput: List<Equation>,
    private val numVars: Int
) {
    private val equations = equationsInput.map {
        Equation(it.vars.clone(), it.rhs)
    }

    private val current = IntArray(numVars)
    private val assigned = BooleanArray(numVars)

    private var bestSum = Int.MAX_VALUE
    private var best: IntArray? = null

    fun solve(): IntArray? {
        dfs(equations)
        return best
    }

    private fun dfs(eqList: List<Equation>) {
        val currentSum = current.sum()
        if (currentSum >= bestSum) return
        // All equaltions are solved
        if (eqList.isEmpty()) {
            bestSum = currentSum
            best = current.clone()
            return
        }
        // Choose the smallest eq (min vars)
        val eq = eqList.minBy { it.vars.size }
        val freeVars = eq.vars.filter { !assigned[it] }
        if (freeVars.isEmpty()) {
            if (eq.rhs == 0) {
                dfs(eqList - eq)
            }
            return
        }
        val distributions = mutableListOf<IntArray>()
        generateDistributions(freeVars.size, eq.rhs, out = distributions)
        for (dist in distributions) {
            var ok = true
            val modified = mutableListOf<Pair<Int, Int>>()
            for (i in freeVars.indices) {
                val v = freeVars[i]
                val value = dist[i]
                if (assigned[v] && current[v] != value) {
                    ok = false
                    break
                }
                if (!assigned[v]) {
                    assigned[v] = true
                    current[v] = value
                    modified.add(v to value)
                }
            }
            if (!ok) {
                rollback(modified)
                continue
            }
            // Update eqs
            val newEqs = mutableListOf<Equation>()
            for (e in eqList) {
                var newRhs = e.rhs
                val newVars = mutableListOf<Int>()
                for (v in e.vars) {
                    if (assigned[v]) {
                        newRhs -= current[v]
                    } else {
                        newVars.add(v)
                    }
                }
                if (newRhs < 0) {
                    ok = false
                    break
                }
                if (newVars.isNotEmpty()) {
                    newEqs.add(Equation(newVars.toIntArray(), newRhs))
                } else if (newRhs != 0) {
                    ok = false
                    break
                }
            }
            if (ok) {
                dfs(newEqs)
            }
            rollback(modified)
        }
    }

    private fun rollback(modified: List<Pair<Int, Int>>) {
        for ((v, _) in modified) {
            assigned[v] = false
            current[v] = 0
        }
    }
}
