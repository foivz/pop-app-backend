package hr.foi.pop.backend.request_bodies

import hr.foi.pop.backend.definitions.RegisterAllowedRole

data class RegisterRequestBody(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    private val role: String
) {

    val roleValue: RegisterAllowedRole?

    init {
        val lookedUpRole = try {
            RegisterAllowedRole.valueOf(role.uppercase())
        } catch (ex: IllegalArgumentException) {
            null
        }
        this.roleValue = lookedUpRole
    }
}
