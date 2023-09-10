package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.models.store.StoreAttributeDto

data class UserLoginResponseDTO(
    private val userDto: UserDTO,
    val id: Int = userDto.id,
    val firstName: String = userDto.firstName,
    val lastName: String = userDto.lastName,
    val username: String = userDto.username,
    val email: String = userDto.email,
    val role: String = userDto.role,
    val isAccepted: Boolean = userDto.isAccepted,
    val store: StoreAttributeDto? = userDto.store,
)
