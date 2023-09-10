package hr.foi.pop.backend.services

import hr.foi.pop.backend.exceptions.BadRoleException
import hr.foi.pop.backend.exceptions.InvalidStoreNameException
import hr.foi.pop.backend.exceptions.UsedStoreNameException
import hr.foi.pop.backend.models.store.Store
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.StoreRepository
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

        val foundForbiddenRole = principal.authorities.find {
            checkForbiddenRoles(it)
        }

        if (foundForbiddenRole != null) {
            throw BadRoleException("User of type \"${foundForbiddenRole.authority}\" cannot create stores!")
        }
    }

    private fun checkForbiddenRoles(it: GrantedAuthority): Boolean {
        return it.authority == "buyer"
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
