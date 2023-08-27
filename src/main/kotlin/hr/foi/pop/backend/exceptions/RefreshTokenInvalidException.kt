package hr.foi.pop.backend.exceptions

import hr.foi.pop.backend.definitions.ApplicationErrorType

class RefreshTokenInvalidException(message: String, val errorType: ApplicationErrorType) : RuntimeException(message)
