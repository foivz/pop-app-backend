package hr.foi.pop.backend.models.store

import com.fasterxml.jackson.annotation.JsonIgnore
import hr.foi.pop.backend.models.event.Event

data class StoreDTO(
    val id: Int,
    @JsonIgnore
    val event: Event,
    val storeName: String,
    @JsonIgnore
    val balance: Int,
    val latitude: Double,
    val longitude: Double
)
