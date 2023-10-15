package hr.foi.pop.backend.exceptions

class UserHasStoreException(private val username: String, private val storeName: String) : RuntimeException() {
    override val message: String
        get() = "User \"$username\" already belongs to store \"$storeName\"!"
}
