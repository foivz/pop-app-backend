package hr.foi.pop.backend.services

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceTest {
    @Autowired
    lateinit var productService: ProductService

    val sellerUsernameOfStore2 = "sbarry"

    @Test
    @WithMockUser(username = "sbarry", authorities = ["seller"])
    fun getMockProductsForMockUser() {
        val receivedProducts = productService.getProducts()

        Assertions.assertTrue(receivedProducts[0].name == "Product 9")
        Assertions.assertTrue(receivedProducts[5].name == "Product 14")
        Assertions.assertTrue(receivedProducts[7].name == "Product 16")
    }
}
