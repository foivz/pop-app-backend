package hr.foi.pop.backend.exceptions

import hr.foi.pop.backend.definitions.ApplicationErrorType

class ActivateUserException(private val error: ApplicationErrorType) : RuntimeException() {
    override val message: String
        get() = "$error: Check incoming user object!"

    fun getApplicationErrorType(): ApplicationErrorType {
        return this.error
    }
}
