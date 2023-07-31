package hr.foi.pop.backend.request_bodies

data class RegisterRequestBody(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    val role: String,
)
