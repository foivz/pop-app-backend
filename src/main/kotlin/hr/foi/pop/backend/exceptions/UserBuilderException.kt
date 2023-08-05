package hr.foi.pop.backend.exceptions

class UserBuilderException(vararg undefinedProperties: String) : RuntimeException() {
    private val cleanStringOfUndefinedProperties: String
    override val message: String
        get() = "User has no $cleanStringOfUndefinedProperties set!"

    init {
        val listOfUndefinedProperties = undefinedProperties.asList()
        val rawStringOfUndefinedProperties = listOfUndefinedProperties.toString()
        cleanStringOfUndefinedProperties = rawStringOfUndefinedProperties.removeSurrounding("[", "]")
    }
}
