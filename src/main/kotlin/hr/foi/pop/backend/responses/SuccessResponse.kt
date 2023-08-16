package hr.foi.pop.backend.responses

open class SuccessResponse(
    message: String, private vararg val params: Any
) : Response(true, message) {
    val data = if (parametersConsistOfASingleArray()) {
        params[0]
    } else {
        params
    }

    private fun parametersConsistOfASingleArray(): Boolean {
        return params.size == 1 && params[0] is Array<*>
    }
}
