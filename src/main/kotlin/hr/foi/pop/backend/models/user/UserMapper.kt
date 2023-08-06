package hr.foi.pop.backend.models.user

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
        return UserDTO(
            e.id, e.role.id, e.store?.id, e.event.id, e.name, e.surname,
            e.email, e.username, e.dateOfRegister, e.balance, e.isAccepted
        )
    }

    override fun map(d: UserDTO): User {
        val foundStore = when (d.store) {
            null -> null
            else -> storeRepository.getStoreById(d.store)
        }
        return User().apply {
            id = d.id
            role = roleRepository.getRoleById(d.role)
            store = foundStore
            event = eventRepository.getEventById(d.event)
            name = d.name
            surname = d.surname
            email = d.email
            username = d.username
            dateOfRegister = d.dateOfRegister
            balance = d.balance
            isAccepted = d.isAccepted
        }
    }
}
