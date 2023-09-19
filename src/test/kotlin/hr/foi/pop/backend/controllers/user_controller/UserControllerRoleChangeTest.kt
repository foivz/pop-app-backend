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

    val mockSeller by lazy {
        userRepository.getReferenceById(8)
    }

    lateinit var mockSellerAccessToken: String

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, UserControllerActivationTest::class)

        val mockUserPredefinedPassword = "test123"

        mockSellerAccessToken =
            authenticationService.authenticateAndGenerateTokenPair(
                mockSeller.username,
                mockUserPredefinedPassword
            ).accessToken
    }

    @Test
    fun givenValidJsonBodyAndSellerObject_whenChangeRoleRequestSent_status200() {
        val body = mapOf("role" to "buyer")

        val request = JsonMockRequestGenerator(
            getRouteForUser(mockSeller.id),
            HttpMethod.PATCH
        ).getRequestWithJsonBody(body)
        request.header("Authorization", "Bearer $mockSellerAccessToken")

        val changedUser = userRepository.getReferenceById(mockSeller.id)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(true))
            .andExpect(
                MockMvcResultMatchers.jsonPath("message")
                    .value("User \"${changedUser.username}\" switched to the new role: \"${changedUser.role.name}\".")
            )
    }
}
