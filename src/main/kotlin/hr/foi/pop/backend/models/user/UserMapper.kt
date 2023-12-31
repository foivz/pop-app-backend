package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.models.store.StoreAttributeDto
import hr.foi.pop.backend.repositories.EventRepository
import hr.foi.pop.backend.repositories.RoleRepository
import hr.foi.pop.backend.repositories.StoreRepository
import hr.foi.pop.backend.utils.GenericMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserMapper : GenericMapper<UserDTO, User> {
    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var storeRepository: StoreRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    override fun mapDto(e: User): UserDTO {
        val retrievedStore = when (e.store) {
            null -> null
            else -> StoreAttributeDto(e.store!!)
        }

        return UserDTO(
            e.id, e.role.name, retrievedStore, e.event.id, e.firstName, e.lastName,
            e.email, e.username, e.dateOfRegister, e.balance, e.isAccepted
        )
    }

    override fun map(d: UserDTO): User {
        val foundStore = when (d.store) {
            null -> null
            else -> storeRepository.getStoreById(d.store.storeId)
        }
        return User().apply {
            id = d.id
            role = roleRepository.getRoleByName(d.role)
            store = foundStore
            event = eventRepository.getEventById(d.event)
            firstName = d.firstName
            lastName = d.lastName
            email = d.email
            username = d.username
            dateOfRegister = d.dateOfRegister
            balance = d.balance
            isAccepted = d.isAccepted
        }
    }
}
