package hr.foi.pop.backend.services

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.ChangeUserStatusException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserBuilder
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.RoleRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.utils.UserChecker
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

    fun activateUser(userId: Int): User {
        val user: User = userRepository.getReferenceById(userId)

        if (user.isAccepted)
            throw ChangeUserStatusException(ApplicationErrorType.ERR_ALREADY_ACTIVATED)

        user.isAccepted = true

        return userRepository.save(user)
    }

    fun deactivateUser(userId: Int): User {
        val user: User = userRepository.getReferenceById(userId)

        if (!user.isAccepted)
            throw ChangeUserStatusException(ApplicationErrorType.ERR_ALREADY_DEACTIVATED)

        user.isAccepted = false

        return userRepository.save(user)
    }

    protected fun validateUser(userInfo: RegisterRequestBody) {
        UserChecker(userInfo, userRepository).validateUserProperties()
    }

}
