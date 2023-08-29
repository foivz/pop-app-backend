package hr.foi.pop.backend.definitions

enum class ActivateUserDefinitions(val code: Boolean, val label: String) {
    ACTIVATE(true, "activated"),
    DEACTIVATE(false, "deactivated");

    companion object {
        fun getEnumByCode(code: Boolean): ActivateUserDefinitions {
            return if (code)
                ACTIVATE
            else
                DEACTIVATE
        }
    }
}
