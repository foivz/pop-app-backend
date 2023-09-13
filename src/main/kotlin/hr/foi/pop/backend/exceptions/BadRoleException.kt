package hr.foi.pop.backend.exceptions

class BadRoleException(private val badRoleName: String) : RuntimeException() {
    override val message: String
        get() = "User with role \"$badRoleName\" cannot execute this operation!"
}
