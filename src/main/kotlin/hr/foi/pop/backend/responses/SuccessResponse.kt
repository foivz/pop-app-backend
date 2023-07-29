package hr.foi.pop.backend.responses

class SuccessResponse(
    success: Boolean, message: String, val data: List<*>
) : Response(success, message)
