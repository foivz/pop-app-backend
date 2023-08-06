package hr.foi.pop.backend.models.user

import java.time.LocalDateTime

class UserDTO(
    val id: Int,
    val role: String,
    val store: Int?,
    val event: Int,
    val name: String,
    val surname: String,
    val email: String,
    val username: String,
    val dateOfRegister: LocalDateTime,
    val balance: Int,
    val isAccepted: Boolean
)
