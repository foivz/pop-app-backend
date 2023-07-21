package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.products.Product
import hr.foi.pop.backend.repository.ProductRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    lateinit var productRepository: ProductRepository

    @Test
    fun getProductById_CheckProductName_IsCorrect() {
        val product: Product = productRepository.getProductById(3)

        Assertions.assertTrue(product.name == "Product 3")
    }

}
