package hr.foi.pop.backend.exceptions

class UserNotAcceptedException(private val username: String) : RuntimeException() {
    override val message: String
        get() = "User \"${username}\" is not accepted by the admin!"
}
