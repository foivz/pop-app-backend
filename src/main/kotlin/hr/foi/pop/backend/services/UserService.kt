package hr.foi.pop.backend.services

import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserBuilder
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.RoleRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.utils.UserChecker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

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
}
