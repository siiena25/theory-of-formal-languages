import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import kotlin.math.pow

const val TEST_PATH = "src/examples/example1.txt"

fun parseRule(s: String): Rule? {
    var str = s.replace(" ", "")
    val rule = Rule()
    val nTerm = parseNTerm(str) ?: return null
    rule.nTerm = nTerm

    str = str.substring(nTerm.length)
    if (!str.startsWith("::=")) {
        return null
    }
    str = str.substring(3)
    var i = 0
    while (i < str.length) {
        val term = parseTerm(str.substring(i)) ?: return null
        rule.terms.add(term)
        i += term.length
    }
    return rule
}

fun parseTerm(s: String): String? {
    var res: String? = parseNTerm(s)
    if (res == null) {
        val at0 = s[0]
        if (at0 in 'a'..'z' ||
            at0 in '0'..'9' ||
            at0 == '_' ||
            at0 == '*' ||
            at0 == '+' ||
            at0 == '=' ||
            at0 == '(' ||
            at0 == ')' ||
            at0 == '$' ||
            at0 == ';' ||
            at0 == ':'
        ) {
            res = at0.toString()
        }
    }
    return res
}

fun parseNTerm(s: String): String? {
    if (!s.startsWith("[")) {
        return null
    }
    val result = StringBuilder("[")
    var i = 1
    while (i < s.length) {
        when {
            s[i] == ']' && i != 1 -> {
                result.append("]")
                return result.toString()
            }
            s[i] in 'A'..'z' -> {
                result.append(s[i])
            }
            else -> {
                return null
            }
        }
        i++
    }
    return null
}

fun parseFirstLine(s: String): Int {
    val str = s.replace(" ", "")
    return str.replace("\$EXTRACTIONS=", "").toInt()
}

@Throws(IOException::class)
fun getGrammar(): Grammar? {
    try {
        BufferedReader(FileReader(TEST_PATH)).use { br ->
            val grammar = Grammar(N = parseFirstLine(br.readLine()))
            var line: String?
            var correct = true
            while (br.readLine().also { line = it } != null) {
                val r = parseRule(line!!)
                if (r == null) {
                    correct = false
                    break
                } else {
                    grammar.rules.add(r)
                }
            }
            return if (!correct) null else grammar
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun removeEpsilons(grammar: Grammar): Grammar {
    val resGrammar = Grammar(N = grammar.N)
    val nullableNTerms: MutableSet<String?> = HashSet()
    var oldSize = -1
    while (nullableNTerms.size != oldSize) {
        oldSize = nullableNTerms.size
        for (rule in grammar.rules) {
            var nullable = true
            for (term in rule.terms) {
                if (!nullableNTerms.contains(term)) {
                    nullable = false
                    break
                }
            }
            if (nullable) {
                nullableNTerms.add(rule.nTerm)
            }
        }
    }
    var additionRules = ArrayList<Rule>()
    for (rule in grammar.rules) {
        var nullableCount = 0
        rule.terms.forEach { term ->
            if (nullableNTerms.contains(term)) {
                nullableCount++
            }
        }
        var data = 0b0
        while (data < 2.0.pow(nullableCount.toDouble()).toInt()) {
            val newRule = Rule()
            newRule.nTerm = rule.nTerm
            val terms = ArrayList<String>()
            var i = 0
            rule.terms.forEach { term ->
                if (!nullableNTerms.contains(term)) terms.add(term) else {
                    if ((data shr i) % 2 == 1) {
                        terms.add(term)
                    }
                    i++
                }
            }
            newRule.terms = terms
            additionRules.add(newRule)
            data++
        }
    }
    var oldSizeR = -1
    while (additionRules.size != oldSizeR) {
        oldSizeR = additionRules.size
        val newAdditionRules = ArrayList<Rule>()
        additionRules.forEach { rule ->
            var needToAdd = rule.terms.size != 0
            repeat(
                rule.terms
                    .filter {
                        it.startsWith("[") && additionRules.stream().filter { rule1: Rule ->
                            rule1.nTerm == it
                        }.toArray().isEmpty()
                    }.size
            ) { needToAdd = false }
            if (needToAdd) {
                newAdditionRules.add(rule)
            }
        }
        additionRules = newAdditionRules
    }
    resGrammar.rules = additionRules
    return resGrammar
}

fun generateAutomate(grammar: Grammar): Automate {
    val automate = Automate()
    val firstState = State()
    recursiveAddNotTermToState(startSymbol, firstState, grammar)
    automate.states.add(firstState)
    addNewStatesFromState(firstState, automate, grammar)
    return automate
}

fun recursiveAddNotTermToState(notTerm: String?, state: State, grammar: Grammar) {
    for (rule in grammar.rules) if (rule.nTerm == notTerm) {
        val posRule = PosRule(rule = rule, posBeforeTerm = 0)
        if (state.posRules.contains(posRule)) {
            continue
        }
        state.posRules.add(posRule)
        if (posRule.rule!!.terms[0].startsWith("[")) {
            recursiveAddNotTermToState(posRule.rule!!.terms[0], state, grammar)
        }
    }
}

fun addNewStatesFromState(state: State?, automate: Automate, grammar: Grammar?) {
    val newStates: MutableMap<String, State?> = HashMap()
    state!!.posRules.forEach { posRule ->
        if (posRule.posBeforeTerm >= posRule.rule!!.terms.size) {
            return@forEach
        }
        if (newStates[posRule.rule!!.terms[posRule.posBeforeTerm]] == null) {
            newStates[posRule.rule!!.terms[posRule.posBeforeTerm]] = State()
        }
        val posRule1 = PosRule(
            rule = posRule.rule,
            posBeforeTerm = posRule.posBeforeTerm + 1
        )
        newStates[posRule.rule!!.terms[posRule.posBeforeTerm]]!!.posRules.add(posRule1)
    }
    newStates.forEach { (splitThrough, state1) ->
        val additionState = State()
        state1!!.posRules
            .filter { it.posBeforeTerm < it.rule!!.terms.size }
            .forEach { recursiveAddNotTermToState(it.rule!!.terms[it.posBeforeTerm], additionState, grammar!!) }
        state1.posRules.addAll(additionState.posRules)
        automate.splits[Pair(state, state1)] = splitThrough
        if (automate.states.contains(state1)) {
            return@forEach
        }
        automate.states.add(state1)
        addNewStatesFromState(state1, automate, grammar)
    }
}

fun findConflicts(automate: Automate): Set<String?> {
    val result: MutableSet<String?> = HashSet()
    for (state in automate.states) {
        val nTermTrue: MutableSet<String?> = HashSet()
        var reduce = false
        var shift = false
        state.posRules.forEach { posRule ->
            if (posRule.posBeforeTerm < posRule.rule!!.terms.size) {
                shift = true
                if (reduce) {
                    result.add(posRule.rule!!.nTerm)
                    result.addAll(nTermTrue)
                }
                nTermTrue.add(posRule.rule!!.nTerm)
            }
            if (posRule.posBeforeTerm == posRule.rule!!.terms.size) {
                if (reduce || shift) {
                    result.add(posRule.rule!!.nTerm)
                    result.addAll(nTermTrue)
                }
                reduce = true
                nTermTrue.add(posRule.rule!!.nTerm)
            }
        }
    }
    return result
}

fun attachmentRightContext(grammar: Grammar, conflictNTerms: Set<String?>): Grammar {
    val resGrammar = Grammar(N = grammar.N)
    val oldWithTerm: MutableMap<String?, ArrayList<String>?> = HashMap()
    for (rule in grammar.rules) {
        var i = 0
        val newRule = Rule(nTerm = rule.nTerm)
        while (i < rule.terms.size) {
            val curTerm = rule.terms[i]
            if (i + 1 >= rule.terms.size) {
                newRule.terms.add(curTerm)
                break
            }
            val nextTerm = rule.terms[i + 1]
            if (conflictNTerms.contains(curTerm) && !nextTerm.startsWith("[")) {
                newRule.terms.add("[" + curTerm.replace("[", "").replace("]", "") + nextTerm + "]")
                if (oldWithTerm[curTerm] == null) {
                    oldWithTerm[curTerm] = ArrayList()
                }
                oldWithTerm[curTerm]!!.add(nextTerm)
                i++
            } else {
                newRule.terms.add(curTerm)
            }
            i++
        }
        resGrammar.rules.add(newRule)
    }
    val resultRules = ArrayList<Rule>()
    resGrammar.rules.forEach { rule ->
        if (oldWithTerm.containsKey(rule.nTerm)) {
            oldWithTerm[rule.nTerm]!!.forEach { term ->
                val terms = rule.terms.clone() as ArrayList<String>
                terms.add(term)
                val rule1 = Rule(
                    nTerm = "[" + rule.nTerm!!.replace("[", "").replace("]", "") + term + "]",
                    terms = terms
                )
                resultRules.add(rule1)
            }
        }
        resultRules.add(rule)
    }
    resGrammar.rules = resultRules
    return resGrammar
}

fun clarificationRightContext(grammar: Grammar, conflictNTerms: Set<String?>?): Grammar {
    step2Bc.clear()
    val resGrammar = Grammar(N = grammar.N)
    val rulesFrom2: Set<Rule> = step2(grammar, conflictNTerms!!)
    if (step2Bc.isEmpty()) {
        return grammar
    }
    count++

    resGrammar.rules = ArrayList(rulesFrom2)
    val rulesFrom3: Set<Rule> = step3(resGrammar)

    resGrammar.rules = ArrayList(rulesFrom3)
    val rulesFrom4: Set<Rule> = step4(resGrammar)

    resGrammar.rules = ArrayList(rulesFrom4)
    count++

    return resGrammar
}

fun step2(grammar: Grammar, conflictNTerms: Set<String?>): Set<Rule> {
    val rulesFrom2: MutableSet<Rule> = HashSet()
    grammar.rules.forEach { rule ->
        var i = 0
        var templateRules: MutableSet<Rule> = HashSet()
        val startRule = Rule(nTerm = rule.nTerm)
        templateRules.add(startRule)
        while (i < rule.terms.size) {
            val curTerm = rule.terms[i]
            if (i + 1 >= rule.terms.size) {
                templateRules.forEach { ruleInSet ->
                    ruleInSet.terms.add(curTerm)
                }
                break
            }
            val nextTerm = rule.terms[i + 1]
            if (conflictNTerms.contains(curTerm) && nextTerm.startsWith("[")) {
                step2Bc[nextTerm] = ArrayList()
                val firstNextTerm: Set<String> = getFirstTerms(nextTerm, grammar)
                val newTemplateRules: MutableSet<Rule> = HashSet()
                firstNextTerm.forEach { term ->
                    step2Bc[nextTerm]?.add(term)
                    for (templateRule in templateRules) {
                        val terms = templateRule.terms.clone() as ArrayList<String>
                        terms.add(term)
                        terms.add("[" + term + "/" + curTerm.replace("[", "").replace("]", "") + "]")
                        terms.add(nextTerm)

                        val rule1 = Rule(
                            nTerm = templateRule.nTerm,
                            terms = terms
                        )
                        println(rule1)
                        newTemplateRules.add(rule1)
                    }
                }
                i++
                templateRules = newTemplateRules
            } else {
                templateRules.forEach { ruleInSet ->
                    ruleInSet.terms.add(curTerm)
                }
            }
            i++
        }
        rulesFrom2.addAll(templateRules)
    }
    return rulesFrom2
}

fun step3(grammar: Grammar): Set<Rule> {
    val resRules: MutableSet<Rule> = HashSet()
    grammar.rules.forEach { rule ->
        if (step2Bc.containsKey(rule.nTerm) && step2Bc[rule.nTerm]!!.contains(rule.terms[0])) {
            val newRule = Rule(
                nTerm = "[" + rule.terms[0] + "/" + rule.nTerm!!.replace("[", "").replace("]", "") + "]",
                terms = ArrayList()
            )
            for (i in 1 until rule.terms.size) {
                newRule.terms.add(rule.terms[i])
            }
            resRules.add(newRule)
        }
        resRules.add(rule)
    }
    return resRules
}

fun step4(grammar: Grammar): Set<Rule> {
    val resRules: MutableSet<Rule> = HashSet()
    grammar.rules.forEach { rule ->
        if (rule.terms.size > 0 && rule.terms[0].startsWith("[")) {
            val firstD: Set<String> = getFirstTerms(rule.terms[0], grammar)
            if (step2Bc[rule.nTerm] != null) {
                step2Bc[rule.nTerm]!!.forEach { c ->
                    if (firstD.contains(c)) {
                        val newRule = Rule()
                        newRule.nTerm = "[" + c + "/" + rule.nTerm!!.replace("[", "").replace("]", "") + "]"
                        newRule.terms = ArrayList()
                        newRule.terms.add("[" + c + "/" + rule.terms[0].replace("[", "").replace("]", "") + "]")
                        for (i in 1 until rule.terms.size) {
                            newRule.terms.add(rule.terms[i])
                        }
                        resRules.add(newRule)
                    }
                }
            }
        }
        resRules.add(rule)
    }
    return resRules
}

fun getFirstTerms(nTerm: String?, grammar: Grammar): Set<String> {
    val result: MutableSet<String> = HashSet()
    val firstNTerms = getFirstNTerms("[S]", grammar, HashSet())
    firstNTerms.add(nTerm)
    grammar.rules
        .filter { firstNTerms.contains(it.nTerm) && !it.terms[0].startsWith("[") }
        .mapTo(result) { it.terms[0] }
    return result
}

fun getFirstNTerms(nTerm: String?, grammar: Grammar, result: MutableSet<String?>): MutableSet<String?> {
    grammar.rules.forEach { rule ->
        if (rule.nTerm == nTerm) {
            if (rule.terms[0].startsWith("[")) {
                val oldSize = result.size
                result.add(rule.terms[0])
                if (result.size != oldSize) getFirstNTerms(rule.terms[0], grammar, result)
            }
        }
    }
    return result
}

var startSymbol = "[S]"
var count = 0
var step2Bc: MutableMap<String, ArrayList<String>> = HashMap()

@Throws(IOException::class)
fun main() {
    var grammar = getGrammar()
    if (grammar == null) {
        println("Uncorrect.")
    } else {
        var resAutomate: Automate? = null
        while (count < grammar!!.N!!) {
            grammar = removeEpsilons(grammar)
            val automate = generateAutomate(grammar)
            val conflictNTerms = findConflicts(automate)
            if (conflictNTerms.isNotEmpty()) {
                grammar = attachmentRightContext(grammar, conflictNTerms)
                grammar = clarificationRightContext(grammar, conflictNTerms)
            } else {
                resAutomate = automate
                break
            }
        }
        if (resAutomate == null) {
            println("Clarification of right context occured more than N times.")
        } else {
            println("$resAutomate\n")
            resAutomate.splits.forEach { (key, value) ->
                println("From: " + key.first + "\nTo: " + key.second + "\nTransition: $value\n")
            }
        }
    }
}