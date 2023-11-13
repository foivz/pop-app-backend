package hr.foi.pop.backend.models.store

import hr.foi.pop.backend.models.event.Event
import jakarta.persistence.*

private const val FOI_LATITUDE = 46.307679
private const val FOI_LONGITUDE = 16.338106

@Entity
@Table(name = "stores")
class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_store")
    var id: Int = 0

    @ManyToOne
    @JoinColumn(name = "events_id_event")
    lateinit var event: Event

    @Column(name = "store_name")
    lateinit var storeName: String

    @Column
    var balance: Int = 0

    @Column
    var longitude: Double = FOI_LONGITUDE

    @Column
    var latitude: Double = FOI_LATITUDE
}
