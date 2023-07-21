package hr.foi.pop.backend.models.products

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "products")
class Product {
    @Id
    @Column(name = "id_product")
    var id: Int = 0

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "description")
    lateinit var description: String

    @Column(name = "image")
    var imageUrl: String? = null

    @Column(name = "price")
    var price: Int = 0

    @Column(name = "quantity")
    var quantity: Int = 0
}
