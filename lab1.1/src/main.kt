import java.io.File
import kotlin.system.exitProcess

private var firstTermReplacement = ""
private var secondTermReplacement = ""

private var varNames: MutableSet<String>? = mutableSetOf()
private var constructorNames: MutableMap<String, Constructor>? = mutableMapOf()
private var constantNames: MutableSet<String>? = mutableSetOf()

class Variable(var name: String)
class Constant(var value: String)
class Constructor(name: String, args: MutableList<Any>) {
    var name: String? = name
    var arguments: MutableList<Any>? = args

    override fun toString(): String {
        var res = "$name("
        arguments?.forEach {
            when (it) {
                is Variable -> res += it.name
                is Constant -> res += it.value
                else -> res += it
            }
            if (arguments!!.indexOf(it) < arguments!!.size - 1) {
                res += ", "
            }
        }
        res += ")"
        return res
    }
}

private fun parseVariableNames(vars: String) {
    vars.split(',').forEach { varName ->
        varNames!!.add(varName)
    }
}

private fun parseConstructorNames(constructors: String) {
    constructors.split(',').forEach { str ->
        if (str[2] == '0') {
            constantNames!!.add(str[0].toString())
        } else {
            constructorNames?.set(
                str[0].toString(),
                Constructor(str[0].toString(), mutableListOf(str[2].toString()))
            )
        }
    }
}

private fun parseConstant(): Constant? {
    if (str.isNotEmpty()) {
        val const = str[0].toString()
        if (!constantNames!!.contains(const)) {
            throw Error("Unknown constant name: $const")
        }
        str = str.substring(1)
        return Constant(const)
    }
    return null
}

private fun parseVariable(): Variable? {
    if (str.isNotEmpty()) {
        val variable = str[0].toString()
        if (!varNames!!.contains(variable)) {
            throw Error("Unknown variable name: $variable")
        }
        str = str.substring(1)
        return Variable(variable)
    }
    return null
}

private fun parseConstructor(): Constructor? {
    if (str.isNotEmpty()) {
        val constructorName = str[0].toString()
        if (!constructorNames!!.contains(constructorName)) {
            throw Error("Unknown constructor name: $constructorName")
        }
        str = str.substring(1)

        if (str[0] != '(') {
            throw Error("Missing opening brackets: $constructorName")
        } else {
            str = str.substring(1)
        }

        val args = mutableListOf<Any>()
        var arg = parseTerm()
        args.add(arg!!)
        while (str[0] == ',') {
            str = str.substring(1)
            arg = parseTerm()
            args.add(arg!!)
        }

        if (str[0] != ')') {
            throw Error("Missing closing brackets: $constructorName")
        }
        if (args.size != (constructorNames!![constructorName]?.arguments!![0] as String).toInt()) {
            throw Error("Number of arguments doesn't match: $constructorName")
        }

        str = str.substring(1)
        return Constructor(constructorName, args)
    }
    return null
}

private fun parseTerm(): Any? {
    if (str.isNotEmpty()) {
        val elem = str[0].toString()
        return when {
            varNames!!.contains(elem) -> {
                parseVariable()
            }
            constantNames!!.contains(elem) -> {
                parseConstant()
            }
            constructorNames!!.contains(elem) -> {
                parseConstructor()
            }
            else -> {
                throw Error("Invalid expression.")
            }
        }
    }
    return null
}

private fun unify(term1: Any, term2: Any): Any {
    if (term1 is Variable &&
        term2 is Variable &&
        term1.name == term2.name
    ) {
        return term1
    }
    if (term1 is Variable) {
        firstTermReplacement += "${term1.name}=$term2, "
        return term2
    }
    if (term2 is Variable) {
        secondTermReplacement += "${term2.name}=$term1, "
        return term1
    }
    if (term1 is Constructor &&
        term2 is Constructor &&
        term1.name == term2.name &&
        term1.arguments!!.size == term2.arguments!!.size
    ) {
        val args: MutableList<Any> = mutableListOf()
        for (i in 0 until term1.arguments!!.size) {
            args.add(unify(term1.arguments!![i], term2.arguments!![i]))
        }
        return Constructor(term1.name!!, args)
    }
    if (term1 is Constant &&
        term2 is Constant &&
        term1.value == term2.value
    ) {
        return term1
    }
    throw Error("Unable to unity.")
}

private var str: String = ""

fun main() {
    val lineList = mutableListOf<String>()
    var inputConstructors = ""
    var inputVariables = ""
    var inputFirst = ""
    var inputSecond = ""

    File("examples/example1.txt").useLines { lines -> lines.forEach { lineList.add(it) }}

    lineList.forEach { case ->
        val ind = lineList.indexOf(case)
        val pos = case.indexOf('=')
        when (ind) {
            0 -> {
                inputConstructors = case.substring(pos + 1).replace(" ", "")
                println("Input constructors: $inputConstructors")
            }
            1 -> {
                inputVariables = case.substring(pos + 1).replace(" ", "")
                println("Input variabes: $inputVariables")
            }
            2 -> {
                inputFirst = case.substring(pos + 1).replace(" ", "")
                println("Input first term: $inputFirst")
            }
            3 -> {
                inputSecond = case.substring(pos + 1).replace(" ", "")
                println("Input second term: $inputSecond\n")
            }
            else -> exitProcess(1)
        }
    }

    parseVariableNames(inputVariables)
    parseConstructorNames(inputConstructors)

    println("Parsed variable names: $varNames")
    println("Parsed constructor names: $constructorNames")
    println("Parsed constants: $constantNames\n")

    val term1: Any?
    val term2: Any?

    try {
        str = inputFirst
        term1 = parseTerm()
    } catch (e: Error) {
        println("Unable to parse term1: " + e.message)
        exitProcess(1)
    }

    try {
        str = inputSecond
        term2 = parseTerm()
    } catch (e: Error) {
        println("Unable to parse term2: " + e.message)
        exitProcess(1)
    }

    try {
        val unifier = unify(term1!!, term2!!)
        println(
            "First term replacement: " +
                    firstTermReplacement.slice(0..firstTermReplacement.length - 3)
        )
        println(
            "Second term replacement: " +
                    secondTermReplacement.slice(0..secondTermReplacement.length - 3)
        )
        println("Result of replacement: $unifier")
    } catch (e: Error) {
        println(e.message)
    }
}