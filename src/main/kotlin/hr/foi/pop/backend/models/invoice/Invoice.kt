package hr.foi.pop.backend.models.invoice

import hr.foi.pop.backend.models.store.Store
import hr.foi.pop.backend.models.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "invoices")
class Invoice {
    @Id
    @GeneratedValue
    @Column(name = "id_invoice")
    var id: Int = 0

    @ManyToOne
    @JoinColumn(name = "stores_id_store")
    lateinit var store: Store

    @ManyToOne
    @JoinColumn(name = "users_id_user")
    lateinit var user: User

    @Column(name = "code")
    lateinit var code: String

    @Column(name = "date_issued")
    lateinit var dateIssue: LocalDateTime

    @Column(name = "discount")
    var discount: Int? = null

    @Column(name = "total")
    var total: Int = 0

}
