package hr.foi.pop.backend.repository

import hr.foi.pop.backend.models.products.Product
import hr.foi.pop.backend.models.role.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Int> {
    fun getProductById(id: Int): Product
}