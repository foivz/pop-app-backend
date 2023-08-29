package hr.foi.pop.backend.response_bodies

import hr.foi.pop.backend.models.user.User

class ActivateUserResponseBody(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val role: String,
    val activated: Boolean
) {
    companion object {
        fun mapResponse(user: User): ActivateUserResponseBody {
            return ActivateUserResponseBody(
                user.id,
                user.firstName,
                user.lastName,
                user.username,
                user.email,
                user.role.name,
                user.isAccepted
            )
        }
    }
}
