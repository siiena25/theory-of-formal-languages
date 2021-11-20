class Transition(
    var state_1: String,
    var letter_1: String,
    var stack_s1: String,
    var state_2: String,
    var stack_s2: MutableList<String>,
    var afterEndSign: Boolean
) {
    override fun toString(): String {
        val result = StringBuilder("<$state_1,$letter_1,$stack_s1>-><$state_2,")
        for (stack_s in stack_s2) {
            result.append(stack_s)
        }
        result.append(">")
        return result.toString()
    }
}