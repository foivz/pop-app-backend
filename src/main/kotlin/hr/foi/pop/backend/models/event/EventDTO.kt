package hr.foi.pop.backend.models.event

import java.time.LocalDateTime

data class EventDTO(
    val id: Int,
    val name: String,
    val dateCreated: LocalDateTime,
    val isActive: Boolean
)
