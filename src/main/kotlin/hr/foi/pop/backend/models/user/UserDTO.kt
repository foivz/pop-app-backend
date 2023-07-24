package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.models.event.Event
import hr.foi.pop.backend.models.role.Role
import hr.foi.pop.backend.models.store.Store
import java.time.LocalDateTime

class UserDTO(
    val id: Int,
    val role: Role,
    val store: Store?,
    val event: Event?,
    val name: String,
    val surname: String,
    val email: String,
    val username: String,
    val dateOfRegister: LocalDateTime,
    val balance: Int,
    val isAccepted: Boolean
)
