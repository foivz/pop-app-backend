package hr.foi.pop.backend.services

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.*
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserBuilder
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.RoleRepository
import hr.foi.pop.backend.repositories.StoreRepository
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.utils.UserChecker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
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

    @Autowired
    private lateinit var storeRepository: StoreRepository

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

    fun activateUser(userId: Int): User {
        val user: User = tryToGetUserById(userId)

        if (user.isAccepted)
            throw ChangeUserStatusException(ApplicationErrorType.ERR_ALREADY_ACTIVATED)

        user.isAccepted = true

        return userRepository.save(user)
    }

    fun deactivateUser(userId: Int): User {
        val user: User = tryToGetUserById(userId)

        if (!user.isAccepted)
            throw ChangeUserStatusException(ApplicationErrorType.ERR_ALREADY_DEACTIVATED)

        user.isAccepted = false

        return userRepository.save(user)
    }

    private fun tryToGetUserById(userId: Int): User {
        try {
            val user: User = userRepository.getReferenceById(userId)
            return user
        } catch (ex: JpaObjectRetrievalFailureException) {
            throw UserNotFoundException("User with ID ${userId} not found.")
        }
    }

    protected fun validateUser(userInfo: RegisterRequestBody) {
        UserChecker(userInfo, userRepository).validateUserProperties()
    }

    fun assignStore(buyerId: Int, storeName: String): User {
        val user = tryToGetUserById(buyerId)
        ensureUserIsAccepted(user)
        ensureUserIsNotSeller(user)
        ensureStoreExistsByName(storeName)
        ensureUserHasNoStore(user)

        val storeFoundByName = storeRepository.getStoreByStoreName(storeName)
        user.store = storeFoundByName
        userRepository.save(user)

        return user
    }

    fun setBalance(buyerId: Int, newBalance: Int): User {
        val user: User = tryToGetUserById(buyerId)

        ensureNewBalanceIsOk(newBalance)

        user.balance = newBalance
        userRepository.save(user)

        return user
    }

    private fun ensureUserIsNotSeller(user: User) {
        if (user.role.name == "seller") {
            throw BadRoleException("seller")
        }
    }

    private fun ensureStoreExistsByName(storeName: String) {
        if (!storeRepository.existsByStoreName(storeName)) {
            throw StoreNotFoundException(storeName)
        }
    }

    private fun ensureUserIsAccepted(user: User) {
        if (!user.isAccepted) {
            throw UserNotAcceptedException(user.username)
        }
    }

    private fun ensureUserHasNoStore(user: User) {
        if (user.store != null) {
            throw UserHasStoreException(user.username, user.store!!.storeName)
        }
    }

    private fun ensureNewBalanceIsOk(amount: Int) {
        ensureAmountNotTooLarge(amount)
        ensureAmountNotNegative(amount)
    }

    private fun ensureAmountNotTooLarge(amount: Int) {
        if (amount > 999999) {
            throw BadAmountException(ApplicationErrorType.ERR_AMOUNT_TOO_LARGE)
        }
    }

    private fun ensureAmountNotNegative(amount: Int) {
        if (amount < 0) {
            throw BadAmountException(ApplicationErrorType.ERR_AMOUNT_NEGATIVE)
        }
    }

}
