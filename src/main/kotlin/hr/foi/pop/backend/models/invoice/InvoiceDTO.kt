package hr.foi.pop.backend.models.invoice

import hr.foi.pop.backend.models.store.Store
import hr.foi.pop.backend.models.user.User
import java.time.LocalDateTime

data class InvoiceDTO(
    val id: Int,
    val store: Store,
    val user: User,
    val code: String,
    val dateIssue: LocalDateTime,
    val discount: Int?,
    val total: Int
)
