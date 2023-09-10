package hr.foi.pop.backend.models.store

data class StoreAttributeDto(
    private val store: Store,
    val storeId: Int = store.id,
    val storeName: String = store.storeName
)
