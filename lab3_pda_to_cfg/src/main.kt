import java.io.BufferedReader
import java.io.FileReader


var stack_ss: MutableSet<String> = HashSet()
var states: MutableSet<String> = HashSet()
var terminals: MutableMap<Term, String> = HashMap()
var counter = 0
const val TEST_PATH = "src/examples/example2.txt"

fun main() {
    BufferedReader(FileReader(TEST_PATH)).use { br ->
        var s = StringBuilder()
        var k: String?
        while (br.readLine().also { k = it } != null) {
            s.append(k)
        }
        s = StringBuilder(s.toString().replace(" ", ""))
        val pda = parsePDA(s.toString())
        makeGraphviz(pda, testPath = TEST_PATH)
        createTerminals()
        val rules = ArrayList<Rule>()
        for (state in states) {
            rules.add(Rule("S", null, arrayListOf(terminals[Term(pda.state, pda.bottom, state)])))
        }
        for (tr in pda.transitions) {
            if (tr.stack_ss.size == 0) {
                val from = terminals[Term(tr.state_1, tr.stack_s_1, tr.state_2)]
                rules.add(Rule(from!!, tr.letter_1, ArrayList()))
            } else {
                val res = getStatePermutations(ArrayList(states), tr.stack_ss.size)
                for (per in res) {
                    val from = terminals[Term(tr.state_1, tr.stack_s_1, per[tr.stack_ss.size - 1])]
                    val terms = arrayListOf<String?>()
                    terms.add(terminals[Term(tr.state_2, tr.stack_ss[0], per[0])])
                    for (j in 1 until per.size) {
                        terms.add(terminals[Term(per[j - 1], tr.stack_ss[j], per[j])])
                    }
                    rules.add(Rule(from!!, tr.letter_1, terms))
                }
            }
        }
        val productive = ArrayList<String>()
        while (true) {
            var wasNewNoProductive = false
            for (rule in rules) {
                if (productive.contains(rule.from)) break
                var isProductive = true
                for (toNotTerminal in rule.nterms) {
                    if (!productive.contains(toNotTerminal)) {
                        isProductive = false
                        break
                    }
                }
                if (isProductive) {
                    wasNewNoProductive = true
                    productive.add(rule.from)
                }
            }
            if (!wasNewNoProductive) break
        }
        val reachable = goInRules("S", rules, ArrayList())
        val resultRules = ArrayList<Rule>()
        for (rule in rules) {
            var needToAdd = productive.contains(rule.from) && reachable.contains(rule.from)
            for (term in rule.nterms) {
                if (!productive.contains(term) || !reachable.contains(term)) {
                    needToAdd = false
                    break
                }
            }
            if (needToAdd) {
                resultRules.add(rule)
            }
        }
        for (i in resultRules.indices) {
            val rule = StringBuilder(resultRules[i].toString())
            for (j in i + 1 until resultRules.size) {
                if (resultRules[j].from == resultRules[i].from) {
                    rule.append("|")
                    if (resultRules[j].term != null) {
                        rule.append(resultRules[j].term)
                    }
                    for (nt in resultRules[j].nterms) {
                        rule.append(nt)
                    }
                    resultRules.remove(resultRules[j])
                }
            }
            println(rule)
        }
        if (resultRules.size == 0) {
            println("Language from PDA is empty.")
        }
    }
}