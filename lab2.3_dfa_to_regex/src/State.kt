class State(var label: String, var isAccept: Boolean, var isStart: Boolean) {
    class Transition(public var from: String, public var to: String, public var value: String)

    var inTransitions: MutableMap<String, Transition> = mutableMapOf()
    var outTransitions: MutableMap<String, Transition> = mutableMapOf()

    var selfLoop: Transition? = null

    fun addSelfLoop(value: String) {
        if (selfLoop == null) {
            selfLoop = Transition(label, label, value)
        } else {
            selfLoop!!.value = "(" + selfLoop!!.value + "+" + value + ")"
        }
    }

    fun addInTransition(from: String, value: String) {
        if (!inTransitions.containsKey(from)) {
            inTransitions[from] = Transition(from, label, value)
        } else {
            inTransitions[from]!!.value = "(" + inTransitions[from]!!.value + "+" + value + ")"
        }
    }

    fun addOutTransition(to: String, value: String) {
        if (!outTransitions.containsKey(to)) {
            outTransitions[to] = Transition(label, to, value)
        } else {
            outTransitions[to]!!.value = "(" + value + "+" + outTransitions[to]!!.value + ")"
        }
    }

    fun removeInTransition(dest: String) {
        inTransitions.remove(dest)
    }

    fun removeOutTransition(dest: String) {
        outTransitions.remove(dest)
    }
}