package hr.foi.pop.backend.controllers.user_controller

import hr.foi.pop.backend.controllers.store_controller.StoreControllerCreationTest
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.utils.JsonMockRequestGenerator
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
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

    private val mockNewStoreName = "Store 1"

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
        val requestBody = mapOf("store_name" to mockNewStoreName)
        val request = createAuthorizedPatchRequestWithBodyForBuyer(requestBody)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(true))
            .andExpect(
                MockMvcResultMatchers.jsonPath("message")
                    .value("User \"${mockActivatedBuyer.username}\" assigned to store \"Store 1\".")
            )
    }

    @Test
    fun givenActivatedBuyerWithoutStore_whenPatchRequestSentWithNonexistentStoreName_thenStatus404() {
        val nonExistentStoreName = "Nonexistent test store name"
        val requestBody = mapOf("store_name" to nonExistentStoreName)
        val request = createAuthorizedPatchRequestWithBodyForBuyer(requestBody)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
            .andExpect(
                MockMvcResultMatchers.jsonPath("error_message")
                    .value(ApplicationErrorType.ERR_STORE_NOT_AVAILABLE.name)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("error_code")
                    .value(ApplicationErrorType.ERR_STORE_NOT_AVAILABLE.code)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("message")
                    .value("Store \"$nonExistentStoreName\" not found!")
            )
    }

    private fun createAuthorizedPatchRequestWithBodyForBuyer(requestBody: Any): RequestBuilder {
        Assertions.assertTrue(mockActivatedBuyer.isAccepted)
        Assertions.assertEquals("buyer", mockActivatedBuyer.role.name)

        val request =
            JsonMockRequestGenerator("${getRouteForUser(mockActivatedBuyer.id)}/store", HttpMethod.PATCH)
                .getRequestWithJsonBody(requestBody)
        request.header("Authorization", "Bearer $accessTokenBuyer")
        return request
    }

    @Test
    fun givenActivatedBuyerWithStore_whenPatchRequestSent_thenStatus403() {
        val requestBody = mapOf("store_name" to mockNewStoreName)
        val request = createAuthorizedPatchRequestWithBodyForBuyer(requestBody)

        mvc.perform(request)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
            .andExpect(
                MockMvcResultMatchers.jsonPath("error_message")
                    .value(ApplicationErrorType.ERR_BUYER_ALREADY_HAS_STORE.name)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("error_code")
                    .value(ApplicationErrorType.ERR_BUYER_ALREADY_HAS_STORE.code)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("message")
                    .value("User \"dhuff\" already belongs to store \"Store 1\"!")
            )
    }

    @Test
    fun givenActivatedSeller_whenPatchRequestSent_thenStatus403() {
        val requestBody = mapOf("store_name" to mockNewStoreName)
        val request = createAuthorizedPatchRequestWithBodyForSeller(requestBody)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
            .andExpect(
                MockMvcResultMatchers.jsonPath("error_message")
                    .value(ApplicationErrorType.ERR_ROLE_NOT_APPLICABLE.name)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("error_code")
                    .value(ApplicationErrorType.ERR_ROLE_NOT_APPLICABLE.code)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("message")
                    .value("User with role \"seller\" cannot execute this operation!")
            )
    }

    private fun createAuthorizedPatchRequestWithBodyForSeller(requestBody: Any): RequestBuilder {
        Assertions.assertTrue(mockActivatedSeller.isAccepted)
        Assertions.assertEquals("seller", mockActivatedSeller.role.name)

        val request =
            JsonMockRequestGenerator("${getRouteForUser(mockActivatedSeller.id)}/store", HttpMethod.PATCH)
                .getRequestWithJsonBody(requestBody)
        request.header("Authorization", "Bearer $accessTokenSeller")
        return request
    }
}
