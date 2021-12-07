class Automate {
    var states: ArrayList<State> = ArrayList()
    var splits: MutableMap<Pair<State, State>, String> = HashMap()

    override fun toString(): String {
        var res = "Automate{\n\tstates=\n"
        for (i in states) {
            res += "\t\t"
            res += i
            res += "\n"
        }
        res += "}"
        return res
    }
}