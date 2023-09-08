package hr.foi.pop.backend.exceptions

import hr.foi.pop.backend.definitions.ApplicationErrorType

class ChangeUserStatusException(val error: ApplicationErrorType) : RuntimeException() {
    override val message: String
        get() = "$error: Check incoming user object!"
}
