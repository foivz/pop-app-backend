package hr.foi.pop.backend.request_bodies

data class CreateStoreRequestBody(
    val storeName: String?,
    val latitude: Double?,
    val longitude: Double?
)
