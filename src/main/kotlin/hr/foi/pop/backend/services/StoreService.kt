package hr.foi.pop.backend.services

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.*
import hr.foi.pop.backend.models.store.Store
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.StoreRepository
import hr.foi.pop.backend.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class StoreService {
    @Autowired
    lateinit var storeRepository: StoreRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var userRepository: UserRepository

    fun createStore(newStoreName: String): Store {
        ensureUserIsNotForbiddenToCreateStores()
        ensureStoreNameIsProper(newStoreName)
        ensureStoreNameIsUnique(newStoreName)

        val newStore = Store().apply {
            storeName = newStoreName
            event = eventRepository.getEventByIsActiveTrue()
            balance = 0
        }

        return storeRepository.save(newStore)
    }

    private fun ensureUserIsNotForbiddenToCreateStores() {
        val principal = SecurityContextHolder.getContext().authentication.principal as UserDetails
        ensureUserDoesntHaveRoleWhichPermitsStoreCreation(principal)
        ensureUserDoesntAlreadyHaveStore(principal)
    }

    private fun ensureUserDoesntHaveRoleWhichPermitsStoreCreation(principal: UserDetails) {
        val foundForbiddenRole = principal.authorities.find {
            checkForbiddenRoles(it)
        }

        if (foundForbiddenRole != null) {
            throw BadRoleException(
                "User of type \"${foundForbiddenRole.authority}\" cannot create stores!",
                ApplicationErrorType.ERR_ROLE_NOT_APPLICABLE
            )
        }
    }

    private fun checkForbiddenRoles(it: GrantedAuthority): Boolean {
        return it.authority == "buyer"
    }

    private fun ensureUserDoesntAlreadyHaveStore(principal: UserDetails) {
        val userEntityObject = userRepository.getUserByUsername(principal.username)
        if (userEntityObject == null) {
            throw UserNotFoundException("User could not be determined! Couldn't proceed with creating a store.")
        }
        if (userEntityObject.store != null) {
            throw UserHasStoreException(userEntityObject.username, userEntityObject.store!!.storeName)
        }
    }

    private fun ensureStoreNameIsProper(storeName: String) {
        if (!StringUtils.hasText(storeName)) {
            throw InvalidStoreNameException()
        }
    }

    private fun ensureStoreNameIsUnique(newStoreName: String) {
        if (storeRepository.existsByStoreName(newStoreName)) {
            throw UsedStoreNameException()
        }
    }
}
