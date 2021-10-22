import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


private var fsm: MutableMap<String, State> = mutableMapOf()
private var start: String = ""
private var accept: MutableList<String> = mutableListOf()
private var allStates: MutableList<String> = mutableListOf()
private const val filePath: String = "examples/example1.txt"

fun main() {
    val file = File(filePath)
    val br = BufferedReader(FileReader(file))
    start = br.readLine().split("=")[1]
    accept = mutableListOf(
        *br.readLine().split("=")[1].split(",".toRegex()).toTypedArray()
    )
    br.readLine()
    allStates = mutableListOf(
        *br.readLine().split("=".toRegex()).toTypedArray()[1].split(",".toRegex()).toTypedArray()
    )

    allStates.forEach {
        fsm[it] = State(it, accept.contains(it), it == start)
    }

    val s: String = br.readLine()

    while (s != "") {
        val temp: MutableList<String> = s.split(",").toMutableList()
        val temp2: MutableList<String> = temp[1].split("=").toMutableList()
        val from: String = temp[0]
        val value: String = temp2[0]
        val to: String = temp2[1]

        if (from == to) {
            fsm[from]!!.addSelfLoop(value)
        } else {
            fsm[from]!!.addOutTransition(to, value)
            fsm[to]!!.addInTransition(from, value)
        }

        println("\n------------------" + fsm.size + "-state DFA---------------------")
        showFSM()
        println("--------------------------------------------------")
        println("Adding new start state (qinit)")
        println("Adding new final state (qfin)")

        addNewAccept()
        addNewStart()

        while (fsm.size > 2) {
            println("-------------------" + fsm.size + "-state GNFA-------------------")
            showFSM()
            eliminateState()
        }
        println("-------------------2-state GNFA-------------------")
        showFSM()
        println("--------------------------------------------------")
        println("Regular Expression: " + fsm["qinit"]!!.outTransitions["qfin"]!!.value)
        println("--------------------------------------------------")
    }
}

private fun eliminateState() {
    removeDeadState()
    val removeState = pickState()
    val state = fsm[removeState]
    val removeIn: MutableList<String> = mutableListOf()
    val removeOut: MutableList<String> = mutableListOf()

    println("--------------------------------------------------")
    println("Removing " + state?.label + "...")

    state?.inTransitions?.values?.forEach { transIn ->
        state.outTransitions.values.forEach { transOut ->
            transIn.value = if (transIn.value == "€") "" else transIn.value
            transOut.value = if (transOut.value == "€") "" else transOut.value
            if (transIn.from == transOut.to) {
                if (fsm[state.label]!!.selfLoop == null) {
                    fsm[transIn.from]!!.addSelfLoop(transIn.value + transOut.value)
                } else {
                    val selfLoopValue =
                        if (state.selfLoop?.value!!.length == 1) {
                            state.selfLoop!!.value
                        } else {
                            "(" + state.selfLoop!!.value + ")"
                        }
                    fsm[transIn.from]?.addSelfLoop(transIn.value + selfLoopValue + "*" + transOut.value)
                }
            } else {
                if (fsm[state.label]!!.selfLoop == null) {
                    fsm[transIn.from]?.addOutTransition(transOut.to, transIn.value + transOut.value)
                    fsm[transOut.to]?.addInTransition(transIn.from, transIn.value + transOut.value)
                } else {
                    val selfLoopValue =
                        if (state.selfLoop?.value!!.length == 1) {
                            state.selfLoop!!.value
                        } else {
                            "(" + state.selfLoop!!.value + ")"
                        }
                    fsm[transIn.from]?.addOutTransition(transOut.to, transIn.value + selfLoopValue + "*" + transOut.value)
                    fsm[transOut.to]?.addInTransition(transIn.from, transIn.value + selfLoopValue + "*" + transOut.value)
                }
            }
            removeOut.add(transOut.to)
        }
        removeIn.add(transIn.from)
    }
    allStates.remove(removeState)
    fsm.remove(removeState)
    removeOut.forEach { str ->
        fsm[str]?.removeInTransition(removeState)
    }
    removeIn.forEach { str ->
        fsm[str]?.removeOutTransition(removeState)
    }
}

private fun removeDeadState() {
    var removed = true
    while (removed) {
        var label = ""
        removed = false
        fsm.values.forEach { state ->
            if (state.outTransitions.isEmpty() && !state.isAccept) {
                state.inTransitions.values.forEach { trans ->
                    fsm[trans.from]!!.outTransitions.remove(trans.to)
                    removed = true
                }
                label = state.label
                println("-------------------------------------------")
                println("Dead State $label is deleted.")
                println("-------------------------------------------")
            }
        }
        fsm.remove(label)
        allStates.remove(label)
    }
}

private fun pickState(): String {
    val queue: PriorityQueue<Map.Entry<String, Int>>? = null
    var sum: Int
    fsm.values.forEach { state ->
        sum = 0
        if (!state.isAccept && !state.isStart) {
            if (state.selfLoop != null) {
                sum += 1
            }
            sum += state.inTransitions.size + state.outTransitions.size
            queue?.offer(AbstractMap.SimpleEntry(state.label, sum))
        }
    }
    var minimumState = ""
    queue?.forEach { value ->
        minimumState = value.key
    }
    return minimumState
}

private fun showFSM() {
    println("Start -> $start")
    print("Accept -> ")
    accept.forEach { str -> print("$str ") }

    print("\nAll States -> ")
    allStates.forEach { str -> print("$str ") }

    println("\nTransitions: ")
    for (state in fsm.values) {
        if (state.selfLoop != null) {
            println("  " + state.selfLoop!!.from + "\t---" + state.selfLoop!!.value + "--->\t" + state.selfLoop!!.to)
        }
        for (tran in state.outTransitions.values) {
            println("  " + tran.from + "\t---" + tran.value + "--->\t" + tran.to)
        }
    }
}

private fun addNewStart() {
    val newStart = State("qinit", isAccept = false, isStart = true)
    newStart.addOutTransition(fsm[start]!!.label, "€")
    fsm[newStart.label] = newStart
    fsm[start]!!.isStart = false
    fsm[start]!!.addInTransition(newStart.label, "€")
    start = newStart.label
    allStates.add(0, newStart.label)
}

private fun addNewAccept() {
    val newAccept = State("qfin", isAccept = true, isStart = false)
    fsm.values.forEach { state ->
        if (accept.contains(state.label)) {
            newAccept.addInTransition(state.label, "€")
            state.addOutTransition(newAccept.label, "€")
            state.isAccept = false
            accept.remove(state.label)
        }
    }
    fsm[newAccept.label] = newAccept
    accept.add(newAccept.label)
    allStates.add(newAccept.label)
}