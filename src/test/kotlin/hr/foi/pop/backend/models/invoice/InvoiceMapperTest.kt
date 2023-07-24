package hr.foi.pop.backend.models.invoice

import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class InvoiceMapperTest {

    val invoiceMapper = InvoiceMapper()

    @Test
    fun whenInvoiceDTOMapped_CheckInvoiceAttributes_AreEqual() {
        val invoice = MockEntitiesHelper.generateInvoiceEntity()

        val invoiceDTO = invoiceMapper.mapDto(invoice)

        Assertions.assertTrue(
            invoice.id == invoiceDTO.id &&
                    invoice.code == invoiceDTO.code &&
                    invoice.dateIssue == invoiceDTO.dateIssue
        )
    }
}
