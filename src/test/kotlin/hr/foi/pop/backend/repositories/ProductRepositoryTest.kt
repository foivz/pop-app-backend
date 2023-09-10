package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.products.Product
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    lateinit var productRepository: ProductRepository

    @Test
    fun getProductById_checkProductName_isCorrect() {
        val product: Product = productRepository.getProductById(3)

        Assertions.assertTrue(product.name == "Product 3")
    }

    @Test
    fun getProductsById_checkProductStore_isCorrect() {
        val productFromStore1: Product = productRepository.getProductById(1)
        val productFromStore2: Product = productRepository.getProductById(9)
        val productFromStore3: Product = productRepository.getProductById(17)

        Assertions.assertTrue(productFromStore1.store.id == 1)
        Assertions.assertTrue(productFromStore2.store.id == 2)
        Assertions.assertTrue(productFromStore3.store.id == 3)
    }
}
