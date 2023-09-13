package hr.foi.pop.backend.controllers.user_controller

import hr.foi.pop.backend.controllers.store_controller.STORE_ROUTE
import hr.foi.pop.backend.controllers.store_controller.StoreControllerCreationTest
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.utils.JsonMockRequestGenerator
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerAssigningStoresToBuyerTest {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var authenticationService: AuthenticationService

    lateinit var mvc: MockMvc

    lateinit var mockActivatedBuyer: User

    lateinit var accessTokenBuyer: String

    lateinit var mockActivatedSeller: User

    lateinit var accessTokenSeller: String

    private val mockNewStoreName = "SuperStore"

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, StoreControllerCreationTest::class)

        val mockBuyerId = 2
        mockActivatedBuyer = userRepository.getReferenceById(mockBuyerId)
        val buyerTokenPair =
            authenticationService.authenticateAndGenerateTokenPair(mockActivatedBuyer.username, "test123")
        accessTokenBuyer = buyerTokenPair.accessToken

        val mockSellerId = 8
        mockActivatedSeller = userRepository.getReferenceById(mockSellerId)
        val sellerTokenPair =
            authenticationService.authenticateAndGenerateTokenPair(mockActivatedSeller.username, "test123")
        accessTokenSeller = sellerTokenPair.accessToken
    }

    @Test
    fun givenActivatedBuyerWithoutStore_whenPatchRequestSent_thenStatus200() {
        Assertions.assertTrue(mockActivatedBuyer.isAccepted)
        Assertions.assertEquals("buyer", mockActivatedBuyer.role.name)

        val requestBody = mapOf("store_name" to mockNewStoreName)
        val request = createAuthorizedPatchRequestWithBody(requestBody, accessTokenBuyer)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(true))
            .andExpect(
                MockMvcResultMatchers.jsonPath("message")
                    .value(Matchers.matchesPattern("Store \"$mockNewStoreName\" created with ID \\d+."))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("data[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("data[0].store_name").value(mockNewStoreName))
    }

    private fun createAuthorizedPatchRequestWithBody(
        requestBody: Any,
        accessToken: String
    ): MockHttpServletRequestBuilder {
        val request = JsonMockRequestGenerator(STORE_ROUTE, HttpMethod.PATCH).getRequestWithJsonBody(requestBody)
        request.header("Authorization", "Bearer $accessToken")
        return request
    }
}
