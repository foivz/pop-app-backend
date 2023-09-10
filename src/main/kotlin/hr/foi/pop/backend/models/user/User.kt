package hr.foi.pop.backend.models.user

import hr.foi.pop.backend.models.event.Event
import hr.foi.pop.backend.models.role.Role
import hr.foi.pop.backend.models.store.Store
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User : UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    var id: Int = 0

    @Column(name = "username")
    private lateinit var username: String

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

    @Column(name = "password_hash")
    lateinit var passwordHash: String

    @Column(name = "date_registered")
    lateinit var dateOfRegister: LocalDateTime

    @Column(name = "balance")
    var balance: Int = 0

    @Column(name = "is_accepted")
    var isAccepted: Boolean = false

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        var listOfGrantedAuthorities = mutableListOf<GrantedAuthority>()

        if (isAccepted) {
            listOfGrantedAuthorities.add(
                object : GrantedAuthority {
                    override fun getAuthority() = role.name
                }
            )
        }

        return listOfGrantedAuthorities
    }

    override fun getUsername() = username

    fun setUsername(username: String) {
        this.username = username
    }

    override fun getPassword() = passwordHash

    override fun isAccountNonExpired() = isAccepted

    override fun isAccountNonLocked() = isAccepted

    override fun isCredentialsNonExpired() = isAccepted

    override fun isEnabled() = isAccepted
}
