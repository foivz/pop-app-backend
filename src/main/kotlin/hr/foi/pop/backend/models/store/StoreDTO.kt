package hr.foi.pop.backend.models.store

import hr.foi.pop.backend.models.event.Event

data class StoreDTO(
    val id: Int,
    val event: Event,
    val storeName: String,
    val balance: Int
)
