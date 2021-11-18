class Transition(
    var state_1: String,
    var letter_1: String,
    var stack_s_1: String,
    var state_2: String,
    var stack_ss: MutableList<String>,
    var afterEndSign: Boolean
) {
    override fun toString(): String {
        val result = StringBuilder("<$state_1,$letter_1,$stack_s_1>-><$state_2,")
        for (stack_s in stack_ss) {
            result.append(stack_s)
        }
        result.append(">")
        return result.toString()
    }
}