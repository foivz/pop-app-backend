package hr.foi.pop.backend.controllers.user_controller

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserControllerSetBalanceTest {
    companion object {
        fun getRouteForUser(userId: Int): String {
            return "/api/v2/users/${userId}/balance"
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

    lateinit var mockAdminAccessToken: String

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, UserControllerActivationTest::class)

        val mockAdminUserId = 1
        val user = userRepository.getReferenceById(mockAdminUserId)
        val mockUserPredefinedPassword = "test123"

        mockAdminAccessToken =
            authenticationService.authenticateAndGenerateTokenPair(
                user.username,
                mockUserPredefinedPassword
            ).accessToken
    }

    @Test
    fun onSetBalanceRequest_withValidBody_status200() {
        val body = mapOf("amount" to 4591)
        val mockBuyerId = 2

        val request = JsonMockRequestGenerator(
            getRouteForUser(mockBuyerId),
            HttpMethod.PATCH
        ).getRequestWithJsonBody(body)
        request.header("Authorization", "Bearer $mockAdminAccessToken")

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("message").value("New balance set for buyer \"dhuff\": 45.91"))
    }
}
