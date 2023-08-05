package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.exceptions.UserBuilderException
import hr.foi.pop.backend.models.role.Role
import hr.foi.pop.backend.utils.encoder
import org.springframework.util.StringUtils
import java.time.LocalDateTime

class UserBuilder {
    private val user = User()

    fun setFirstName(firstName: String): UserBuilder {
        user.name = firstName
        return this
    }

    fun setLastName(lastName: String): UserBuilder {
        user.surname = lastName
        return this
    }

    fun setUsername(username: String): UserBuilder {
        user.username = username
        return this
    }

    fun setPassword(password: String): UserBuilder {
        user.passwordHash = encoder().encode(password)
        return this
    }

    fun setRole(role: Role): UserBuilder {
        user.role = role
        return this
    }

    fun build(): User {
        user.dateOfRegister = LocalDateTime.now()
        ensureUserValidity()
        return user
    }

    private fun ensureUserValidity() {
        var badProperties = emptyArray<String>()

        fun appendBadPropertyName(propertyName: String) {
            badProperties = badProperties.plus(propertyName)
        }

        fun checkIfBadStringProperty(value: String, propertyName: String) {
            if (!StringUtils.hasText(value)) {
                appendBadPropertyName(propertyName)
            }
        }

        try {
            checkIfBadStringProperty(user.name, "first name")
            checkIfBadStringProperty(user.surname, "last name")
            checkIfBadStringProperty(user.username, "username")
            checkIfBadStringProperty(user.passwordHash, "passwordHash")
            if (!user.isRoleInitialized) appendBadPropertyName("role")
        } catch (ex: UninitializedPropertyAccessException) {
            val message = ex.message!!
            val messageTrimmedAtStart = message.substringAfter("lateinit property ")
            val propertyName = messageTrimmedAtStart.substringBefore("has not been initialized")

            appendBadPropertyName(propertyName)
        }

        if (badProperties.isNotEmpty()) {
            throw UserBuilderException(*badProperties)
        }
    }
}
