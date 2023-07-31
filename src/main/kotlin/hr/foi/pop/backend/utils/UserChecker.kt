package hr.foi.pop.backend.utils

import hr.foi.pop.backend.errors.ApplicationErrorType
import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.user.UserDTO
import hr.foi.pop.backend.repositories.UserRepository

open class UserChecker(protected var user: UserDTO, protected val userRepository: UserRepository) {
    fun validateUserProperties() {
        validateUsername()
        validatePassword()
        validateEmail()
        validateRole()
    }

    protected fun validateUsername() {
        if (isUsernameTooShort(4) || isUsernameWithoutCharacters()) {
            throw UserCheckException(ApplicationErrorType.ERR_USERNAME_INVALID)
        }
        if (userRepository.existsByUsername(user.username)) {
            throw UserCheckException(ApplicationErrorType.ERR_USERNAME_USED)
        }
    }

    private fun isUsernameWithoutCharacters() = !user.username.contains(Regex("[a-zA-Z]"))

    private fun isUsernameTooShort(minLength: Int) = user.username.length < minLength

    protected fun validatePassword() {
        TODO("Not yet implemented")
    }

    protected fun validateEmail() {
        TODO("Not yet implemented")
    }

    protected fun validateRole() {
        TODO("Not yet implemented")
    }
}
