package hr.foi.pop.backend.responses

class SuccessResponse(
    message: String, vararg val data: Any
) : Response(true, message)
