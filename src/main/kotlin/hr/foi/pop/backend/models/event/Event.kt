package hr.foi.pop.backend.models.event

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "events")
class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_event")
    var id: Int = 0

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "date_created")
    lateinit var dateCreated: LocalDateTime

    @Column(name = "is_active")
    var isActive: Boolean = false
}
