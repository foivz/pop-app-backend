package hr.foi.pop.backend.models.store

import hr.foi.pop.backend.models.event.Event
import jakarta.persistence.*

@Entity
@Table(name = "stores")
class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_store")
    var id: Int = 0

    @ManyToOne
    @JoinColumn(name = "events_id_event")
    lateinit var event: Event

    @Column(name = "store_name")
    lateinit var storeName: String

    @Column
    var balance: Int = 0

}
