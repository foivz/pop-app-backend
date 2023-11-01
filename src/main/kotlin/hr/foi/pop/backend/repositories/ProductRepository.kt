package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.products.Product
import hr.foi.pop.backend.models.store.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Int> {
    fun getProductById(id: Int): Product

    fun getProductsByStore(store: Store): List<Product>
}
