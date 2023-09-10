package hr.foi.pop.backend.responses

import hr.foi.pop.backend.definitions.ApplicationErrorType

class WarningResponse(
    warning: ApplicationErrorType,
    message: String,
    vararg data: Any
) : SuccessResponse(message, data) {
    val errorCode = warning.code
    val errorMessage = warning.name
}
