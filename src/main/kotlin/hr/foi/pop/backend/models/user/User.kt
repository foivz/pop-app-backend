package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.models.event.Event
import hr.foi.pop.backend.models.role.Role
import hr.foi.pop.backend.models.store.Store
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    var id: Int = 0

    @ManyToOne
    @JoinColumn(name = "roles_id_role")
    lateinit var role: Role
    val isRoleInitialized get() = ::role.isInitialized

    @ManyToOne
    @JoinColumn(name = "stores_id_store")
    var store: Store? = null

    @ManyToOne
    @JoinColumn(name = "events_id_event")
    lateinit var event: Event
    val isEventInitialized get() = ::event.isInitialized

    @Column(name = "first_name")
    lateinit var firstName: String

    @Column(name = "last_name")
    lateinit var lastName: String

    @Column(name = "email")
    lateinit var email: String

    @Column(name = "username")
    lateinit var username: String

    @Column(name = "password_hash")
    lateinit var passwordHash: String

    @Column(name = "date_registered")
    lateinit var dateOfRegister: LocalDateTime

    @Column(name = "balance")
    var balance: Int = 0

    @Column(name = "is_accepted")
    var isAccepted: Boolean = false

}
