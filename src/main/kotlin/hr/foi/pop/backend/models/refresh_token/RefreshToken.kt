package hr.foi.pop.backend.models.refresh_token

import hr.foi.pop.backend.models.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_refresh_token")
    var id: Int = 0

    @OneToOne(targetEntity = User::class)
    @JoinColumn(name = "users_id_user")
    lateinit var owner: User

    @Column(length = 64)
    lateinit var token: String

    @Column(name = "date_created")
    lateinit var dateCreated: LocalDateTime

    @Column(name = "expiration_date")
    lateinit var expirationDate: LocalDateTime
}
