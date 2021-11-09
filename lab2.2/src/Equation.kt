import kotlin.system.exitProcess

class Equation(equation: String) {
    var name: Char? = null
    var equation: MutableMap<Char, String> = mutableMapOf()
    var coefficient: String = ""
    private var variables = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    init {
        var ind = 0
        var startInd: Int
        var endInd: Int

        equation.replace(Regex(" \n"), "")

        while (!variables.contains(equation[ind])) {
            ind += 1
        }
        name = equation[ind]
        ind += 2

        while (ind < equation.length) {
            startInd = ind
            while (ind != equation.length && !variables.contains(equation[ind])) {
                ind += 1
            }
            endInd = ind

            if (startInd == endInd) {
                println("Incorrect: all coefficients should not be empty.")
                exitProcess(2)
            }

            val coefficient = equation.substring(startInd, endInd)
            if (checkBrakes(coefficient)) {
                println("Incorrect: check brakes.")
                exitProcess(2)
            }

            if (ind == equation.length) {
                this.equation['ε'] = equation.substring(startInd, endInd)
                continue
            }

            if (equation[ind] == name) {
                this.coefficient = equation.substring(startInd, endInd)
                ind += 2
                continue
            }

            this.equation[equation[ind]] = equation.substring(startInd, endInd)
            ind += 2
        }
    }

    private fun getEquation(): String {
        var res = ""
        equation.entries.forEach { entry ->
            res +=
                if (entry.key == 'ε') {
                    entry.value
                } else {
                    entry.value + entry.key
                }
            res += "+"
        }
        return if (res == "") res
        else res.substring(0, res.length - 1)
    }

    fun getSolution(): String {
        var solution = kleeneIteration(coefficient)
        val eqStr = getEquation()
        if (eqStr != "") {
            solution += brakes(eqStr)
        }
        return solution
    }
}