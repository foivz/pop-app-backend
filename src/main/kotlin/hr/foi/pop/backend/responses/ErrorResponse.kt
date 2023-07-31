package hr.foi.pop.backend.responses

import hr.foi.pop.backend.definitions.ApplicationErrorType

class ErrorResponse(
    success: Boolean, message: String, error: ApplicationErrorType
) : Response(success, message) {

    val errorCode: Int
    val errorMessage: String

    init {
        errorCode = error.code
        errorMessage = error.name
    }
}
