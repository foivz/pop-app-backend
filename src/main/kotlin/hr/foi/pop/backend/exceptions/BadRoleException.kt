package hr.foi.pop.backend.exceptions

import hr.foi.pop.backend.definitions.ApplicationErrorType

class BadRoleException(message: String, val error: ApplicationErrorType) : RuntimeException(message)
