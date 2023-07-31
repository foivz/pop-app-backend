package hr.foi.pop.backend.exceptions

import hr.foi.pop.backend.errors.ApplicationErrorType

class UserCheckException(private val error: ApplicationErrorType) : RuntimeException() {
    override val message: String
        get() = "$error: Check incoming user object!"
}
