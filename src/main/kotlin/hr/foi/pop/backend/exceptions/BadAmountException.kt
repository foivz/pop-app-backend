package hr.foi.pop.backend.exceptions

import hr.foi.pop.backend.definitions.ApplicationErrorType

class BadAmountException(val error: ApplicationErrorType) : RuntimeException()
