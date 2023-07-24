package hr.foi.pop.backend.models.packages

import hr.foi.pop.backend.models.products.Product
import hr.foi.pop.backend.models.store.Store
import jakarta.persistence.*

@Entity
@Table(name = "packages")
class PackageEntity {
    @Id
    @Column(name = "id_package")
    var id: Int = 0

    @ManyToOne
    @JoinColumn(name = "stores_id_store")
    lateinit var store: Store

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "description")
    lateinit var description: String

    @Column(name = "image")
    var imageUrl: String? = null

    @Column(name = "discount_on_items")
    var discount: Int = 0

    @Column(name = "amount")
    var amount: Int? = null

    @ManyToMany(
            targetEntity = Product::class,
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "packages_has_products",
            joinColumns = [JoinColumn(name = "packages_id_package")],
            inverseJoinColumns = [JoinColumn(name = "products_id_product")]
    )
    lateinit var products: Set<Product>
}
