import java.io.File

var equations: MutableList<Equation> = mutableListOf()

fun main() {
    val lineList = mutableListOf<String>()
    File("examples/example5.txt").useLines { lines -> lines.forEach { lineList.add(it.replace(" ", "")) }}

    lineList.forEach { line ->
        equations.add(Equation(line))
    }

    for (i in 0 until equations.size) {
        for (j in i + 1 until equations.size) {
            substitution(i, j)
        }
    }

    for (i in equations.size - 1 downTo 0) {
        for (j in i - 1 downTo 0) {
            substitution(i, j)
        }
    }

    equations.forEach { equation ->
        println(equation.getSolution())
    }
}

fun substitution(i: Int, j: Int) {
    val x = equations[i]
    val y = equations[j]

    if (y.equation.containsKey(x.name)) {
        val coefficientXinY = y.equation[x.name]
        val coefficient = coefficientXinY + kleeneIteration(x.coefficient)
        if (x.equation.entries.isEmpty()) {
            if (y.equation.containsKey('ε')) {
                y.equation['ε'] = y.equation['ε'] + "+" + coefficient
            } else {
                y.equation['ε'] = coefficient
            }
        }
        for (entryX in x.equation.entries) {
            if (entryX.key == y.name) {
                if (y.coefficient != "") {
                    y.coefficient += "+" + coefficient + brakes(entryX.value)
                } else {
                    y.coefficient = coefficient + brakes(entryX.value)
                }
                continue
            }
            if (y.equation.containsKey(entryX.key)) {
                val newCoefficient = y.equation[entryX.key]
                y.equation[entryX.key] = newCoefficient + "+" + coefficient + brakes(entryX.value)
            } else {
                y.equation[entryX.key] = coefficient + brakes(entryX.value)
            }
        }
        y.equation.remove(x.name)
        equations[j] = y
    }
    return
}
