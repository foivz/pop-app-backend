package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.utils.GenericMapper

class UserMapper : GenericMapper<UserDTO, User> {
    override fun mapDto(e: User): UserDTO {
        return UserDTO(
            e.id, e.role, e.store, e.event, e.name, e.surname,
            e.email, e.username, e.dateOfRegister, e.balance, e.isAccepted
        )
    }

    override fun map(d: UserDTO): User {
        return User().apply {
            id = d.id
            role = d.role
            store = d.store
            event = d.event
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
