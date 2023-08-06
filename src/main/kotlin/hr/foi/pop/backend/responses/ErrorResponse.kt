package hr.foi.pop.backend.responses

import hr.foi.pop.backend.definitions.ApplicationErrorType

class ErrorResponse(
    message: String, error: ApplicationErrorType
) : Response(false, message) {

    val errorCode: Int
    val errorMessage: String

    init {
        errorCode = error.code
        errorMessage = error.name
    }
}
