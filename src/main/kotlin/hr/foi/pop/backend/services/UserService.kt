package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.UserAuthenticationException
import hr.foi.pop.backend.exceptions.UserNotAcceptedException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserBuilder
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.RoleRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.security.jwt.JwtUtils
import hr.foi.pop.backend.utils.UserChecker
import hr.foi.pop.backend.utils.passwordEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService : UserDetailsService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    private val passwordEncoder: PasswordEncoder = passwordEncoder()

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
            .setPassword(userInfo.password, passwordEncoder)
            .build()

        userRepository.save(user)
        return user
    }

    protected fun validateUser(userInfo: RegisterRequestBody) {
        UserChecker(userInfo, userRepository).validateUserProperties()
    }

    fun authenticateAndGenerateJWT(username: String, password: String): String {
        val user = authenticate(username, password)
        return generateJWT(user)
    }

    private fun authenticate(providedUsername: String, providedPassword: String): User {
        val user = userRepository.getUserByUsername(providedUsername)
            ?: throw UserAuthenticationException("Non-existent user $providedUsername tried to log in")

        val encoder = passwordEncoder

        val isAuthenticated = encoder.matches(providedPassword, user.passwordHash)

        if (isAuthenticated) {
            ensureUserIsAccepted(user)
            return user
        } else {
            throw UserAuthenticationException("A wrong password was provided for user $providedUsername")
        }
    }

    private fun ensureUserIsAccepted(user: User) {
        if (!user.isAccepted) {
            throw UserNotAcceptedException("User \"${user.username}\" is not accepted yet by the admin!")
        }
    }

    private fun generateJWT(user: User): String {
        return jwtUtils.generateJwtToken(user)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.getUserByUsername(username)
            ?: throw UsernameNotFoundException("User '$username' not found!")
    }
}
