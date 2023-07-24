package hr.foi.pop.backend.models.invoice

import hr.foi.pop.backend.utils.GenericMapper

class InvoiceMapper : GenericMapper<InvoiceDTO, Invoice> {
    override fun mapDto(e: Invoice): InvoiceDTO {
        return InvoiceDTO(e.id, e.store, e.user, e.code, e.dateIssue, e.discount, e.total)
    }

    override fun map(d: InvoiceDTO): Invoice {
        return Invoice().apply {
            id = d.id
            store = d.store
            user = d.user
            dateIssue = d.dateIssue
            discount = d.discount
            total = d.total
        }
    }

}
