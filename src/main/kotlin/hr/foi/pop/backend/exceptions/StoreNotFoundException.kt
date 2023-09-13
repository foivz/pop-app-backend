package hr.foi.pop.backend.exceptions

class StoreNotFoundException(private val storeName: String) : RuntimeException() {
    override val message: String
        get() = "Store \"$storeName\" not found!"
}
