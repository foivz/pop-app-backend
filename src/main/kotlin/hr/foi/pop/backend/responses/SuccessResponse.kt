package hr.foi.pop.backend.responses

open class SuccessResponse(
    message: String, vararg val data: Any
) : Response(true, message)
