package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.invoice.Invoice
import org.springframework.data.jpa.repository.JpaRepository

interface InvoiceRepository : JpaRepository<Invoice, Int>
