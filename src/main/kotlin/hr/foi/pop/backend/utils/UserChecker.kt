package hr.foi.pop.backend.utils

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import org.springframework.util.StringUtils

open class UserChecker(protected var user: RegisterRequestBody, protected val userRepository: UserRepository) {
    fun validateUserProperties() {
        validateFirstName()
        validateLastName()
        validateUsername()
        validatePassword()
        validateEmail()
        validateRole()
    }

    protected fun validateFirstName() {
        if (isTooShort(user.firstName, 2) || !hasTextAndNoDigits(user.firstName)) {
            throw UserCheckException(ApplicationErrorType.ERR_FIRSTNAME_INVALID)
        }
    }

    protected fun validateLastName() {
        if (isTooShort(user.lastName, 2) || !hasTextAndNoDigits(user.lastName)) {
            throw UserCheckException(ApplicationErrorType.ERR_LASTNAME_INVALID)
        }
    }

    private fun isTooShort(string: String, minLength: Int) = string.length < minLength
    private fun hasTextAndNoDigits(string: String) = StringUtils.hasText(string) && !containsNumbers(string)

    protected fun validateUsername() {
        if (isUsernameTooShort(4) || isUsernameWithoutLetters()) {
            throw UserCheckException(ApplicationErrorType.ERR_USERNAME_INVALID)
        }
        if (isUsernameUsed()) {
            throw UserCheckException(ApplicationErrorType.ERR_USERNAME_USED)
        }
    }

    private fun isUsernameTooShort(minLength: Int) = user.username.length < minLength
    private fun isUsernameWithoutLetters() = !user.username.contains(Regex("[a-zA-Z]"))
    private fun isUsernameUsed() = userRepository.existsByUsername(user.username)


    protected fun validatePassword() {
        if (isPasswordTooShort(7) || isPasswordWithoutNumbers()) {
            throw UserCheckException(ApplicationErrorType.ERR_PASSWORD_INVALID)
        }
    }

    private fun isPasswordTooShort(minLength: Int) = user.password.length < minLength
    private fun isPasswordWithoutNumbers() = !containsNumbers(user.password)

    private fun containsNumbers(string: String) = string.contains(Regex("[0-9]"))

    protected fun validateEmail() {
        if (isEmailInvalid()) {
            throw UserCheckException(ApplicationErrorType.ERR_EMAIL_INVALID)
        }
        if (isEmailUsed()) {
            throw UserCheckException(ApplicationErrorType.ERR_EMAIL_USED)
        }
    }

    private fun isEmailInvalid() = !"^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex().matches(user.email)
    private fun isEmailUsed() = userRepository.existsByEmail(user.email)


    protected fun validateRole() {
        if (isRoleInvalid()) {
            throw UserCheckException(ApplicationErrorType.ERR_ROLE_INVALID)
        }
    }

    private fun isRoleInvalid() = user.roleValue == null
}
