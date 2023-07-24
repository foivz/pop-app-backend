package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.invoice.Invoice
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class InvoicesRepositoryTest {
    @Autowired
    lateinit var invoicesRepository: InvoiceRepository

    @Test
    fun findAllAndLast_shouldReturnInvoiceWithCodeINV005() {
        val invoice: Invoice = invoicesRepository.findAll()
            .last()

        Assertions.assertEquals("INV005", invoice.code)
    }
}
