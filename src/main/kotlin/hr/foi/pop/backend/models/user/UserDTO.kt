package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.models.store.StoreAttributeDto
import java.time.LocalDateTime

data class UserDTO(
    val id: Int,
    val role: String,
    val store: StoreAttributeDto?,
    val event: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val dateOfRegister: LocalDateTime,
    val balance: Int,
    val isAccepted: Boolean
)
