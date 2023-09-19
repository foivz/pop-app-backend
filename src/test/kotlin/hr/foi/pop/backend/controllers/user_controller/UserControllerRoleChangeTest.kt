package hr.foi.pop.backend.controllers.user_controller

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.services.UserService
import hr.foi.pop.backend.utils.JsonMockRequestGenerator
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserControllerRoleChangeTest {
    companion object {
        fun getRouteForUser(userId: Int): String {
            return "/api/v2/users/${userId}/role"
        }
    }

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Autowired
    lateinit var userRepository: UserRepository

    val mockSellerId = 8

    lateinit var mockSellerAccessToken: String

    val mockBuyerId = 2

    lateinit var mockBuyerAccessToken: String

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, UserControllerActivationTest::class)

        val mockUserPredefinedPassword = "test123"

        val mockSeller = userRepository.getReferenceById(mockSellerId)
        mockSellerAccessToken =
            authenticationService.authenticateAndGenerateTokenPair(
                mockSeller.username,
                mockUserPredefinedPassword
            ).accessToken

        val mockBuyer = userRepository.getReferenceById(mockBuyerId)
        mockBuyerAccessToken =
            authenticationService.authenticateAndGenerateTokenPair(
                mockBuyer.username,
                mockUserPredefinedPassword
            ).accessToken
    }

    @Test
    fun givenValidJsonBodyAndSellerObject_whenChangeRoleRequestSent_status200() {
        val body = mapOf("role" to "buyer")

        val request = JsonMockRequestGenerator(
            getRouteForUser(mockSellerId),
            HttpMethod.PATCH
        ).getRequestWithJsonBody(body)
        request.header("Authorization", "Bearer $mockSellerAccessToken")

        val changedUser = userRepository.getReferenceById(mockSellerId)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(true))
            .andExpect(
                MockMvcResultMatchers.jsonPath("message")
                    .value("User \"${changedUser.username}\" switched to the new role: \"${changedUser.role.name}\".")
            )
    }

    @Test
    fun givenOneUser_whenChangeRoleRequestSentForAnotherUser_status403() {
        val body = mapOf("role" to "buyer")

        val request = generateRequestForSellerUsingBuyersAuth(body)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
            .andExpect(
                MockMvcResultMatchers.jsonPath("message")
                    .value("You are not permitted to edit selected user!")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("error_message")
                    .value(ApplicationErrorType.ERR_AUTHORIZATION_NOT_SUFFICIENT.name)
            )
    }

    private fun generateRequestForSellerUsingBuyersAuth(body: Map<String, String>): MockHttpServletRequestBuilder {
        val request = JsonMockRequestGenerator(
            getRouteForUser(mockSellerId),
            HttpMethod.PATCH
        ).getRequestWithJsonBody(body)
        request.header("Authorization", "Bearer $mockBuyerAccessToken")
        return request
    }
}
