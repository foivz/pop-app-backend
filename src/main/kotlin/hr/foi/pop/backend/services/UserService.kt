package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.UserAuthenticationException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserBuilder
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.RoleRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.utils.UserChecker
import hr.foi.pop.backend.utils.encoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    fun registerUser(userInfo: RegisterRequestBody): User {
        validateUser(userInfo)

        val desiredRole = roleRepository.getRoleByName(userInfo.roleValue!!.name.lowercase())
        val currentEvent = eventRepository.getEventByIsActiveTrue()

        val user: User = UserBuilder()
            .setUsername(userInfo.username)
            .setFirstName(userInfo.firstName)
            .setLastName(userInfo.lastName)
            .setEmail(userInfo.email)
            .setRole(desiredRole)
            .setCurrentEvent(currentEvent)
            .setPassword(userInfo.password)
            .build()

        userRepository.save(user)
        return user
    }

    protected fun validateUser(userInfo: RegisterRequestBody) {
        UserChecker(userInfo, userRepository).validateUserProperties()
    }

    fun authenticateAndGenerateJWT(username: String, password: String): String {
        val user = authenticate(username, password)
        return generateJWT(user.id)
    }

    private fun authenticate(providedUsername: String, providedPassword: String): User {
        val user = userRepository.getUserByUsername(providedUsername) ?: throw UserAuthenticationException()
        val encoder = encoder()

        val isAuthenticated = encoder.matches(providedPassword, user.passwordHash)

        if (isAuthenticated) {
            return user
        } else {
            throw UserAuthenticationException()
        }
    }

    private fun generateJWT(userId: Int): String {
        // TODO Generate and return a valid JWT
        return ""
    }
}
