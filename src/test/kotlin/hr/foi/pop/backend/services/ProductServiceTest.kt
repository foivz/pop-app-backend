package hr.foi.pop.backend.services

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.BadRoleException
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceTest {
    @Autowired
    lateinit var productService: ProductService

    @Test
    @WithMockUser(username = "sbarry", authorities = ["seller"])
    fun givenValidSeller_whenGettingProducts_productsReturnedOnlyForThatUsername() {
        val receivedProducts = productService.getProducts()

        Assertions.assertTrue(receivedProducts[0].name == "Product 9")
        Assertions.assertTrue(receivedProducts[5].name == "Product 14")
        Assertions.assertTrue(receivedProducts[7].name == "Product 16")
        Assertions.assertEquals(8, receivedProducts.size)
    }

    @Test
    @WithMockUser(username = "testerBuyerUser", authorities = ["buyer"])
    fun givenBuyer_onAttemptToGetProducts_throwBadRoleException() {
        val exception = assertThrows<BadRoleException> { productService.getProducts() }
        Assertions.assertEquals(exception.error, ApplicationErrorType.ERR_ROLE_NOT_APPLICABLE)
    }
}
