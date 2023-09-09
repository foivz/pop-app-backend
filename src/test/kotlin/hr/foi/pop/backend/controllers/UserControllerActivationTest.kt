package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.ActivateUserRequestBody
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.services.UserService
import hr.foi.pop.backend.utils.JsonMockRequestGenerator
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserControllerActivationTest {
    companion object {
        fun getRouteForUser(userId: Int): String {
            return "/api/v2/users/${userId}/activate"
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

    lateinit var mockAccessToken: String

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, UserControllerActivationTest::class)

        val mockAdminUserId = 1
        val user = userRepository.getReferenceById(mockAdminUserId)
        val mockUserPredefinedPassword = "test123"

        mockAccessToken =
            authenticationService.authenticateAndGenerateTokenPair(
                user.username,
                mockUserPredefinedPassword
            ).accessToken
    }

    @Test
    fun onActivateRequest_whenInvalidHttpMethod_status405() {
        val requestBody = ActivateUserRequestBody(false)
        val request = JsonMockRequestGenerator(getRouteForUser(1), HttpMethod.GET).getRequestWithJsonBody(requestBody)
        request.header("Authorization", "Bearer $mockAccessToken")

        mvc.perform(request).andExpect(status().isMethodNotAllowed)
    }

    @Test
    fun givenUserWhoIsNotAdmin_whenTriedToActivateAnotherUser_status403() {
        val body = mapOf("is_accepted" to false)
        val idOfNonAdminMockUser = 2
        val mockNonAdminUserPassword = "test123"

        val user = userRepository.getReferenceById(idOfNonAdminMockUser)

        val mockTokenForNonAdmin =
            authenticationService.authenticateAndGenerateTokenPair(user.username, mockNonAdminUserPassword).accessToken

        val request = JsonMockRequestGenerator(getRouteForUser(1), HttpMethod.PATCH).getRequestWithJsonBody(body)
        request.header("Authorization", "Bearer $mockTokenForNonAdmin")

        mvc.perform(request).andExpect(status().isForbidden)
    }

    @Test
    fun onActivateRequest_whenInvalidRequestBody_status400() {
        val body = mapOf("random" to "object")

        val request = JsonMockRequestGenerator(getRouteForUser(1), HttpMethod.PATCH).getRequestWithJsonBody(body)
        request.header("Authorization", "Bearer $mockAccessToken")

        mvc.perform(request).andExpect(status().isBadRequest)
    }

    @Test
    fun onActivateRequest_withValidBody_status200() {
        val body = mapOf("is_accepted" to true)
        val mockUserIdOfNotActivatedUser = 4

        val request = JsonMockRequestGenerator(
            getRouteForUser(mockUserIdOfNotActivatedUser),
            HttpMethod.PATCH
        ).getRequestWithJsonBody(body)
        request.header("Authorization", "Bearer $mockAccessToken")

        mvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("success").value(true))
            .andExpect(jsonPath("message").value(Matchers.matchesPattern("User 'bhartman' activated.")))
            .andExpect(jsonPath("data[0].id").value(4))
            .andExpect(jsonPath("data[0].role").value("buyer"))
            .andExpect(jsonPath("data[0].first_name").value("Bruno"))
            .andExpect(jsonPath("data[0].last_name").value("Hartman"))
            .andExpect(jsonPath("data[0].email").value("bhartman@pop.app"))
            .andExpect(jsonPath("data[0].username").value("bhartman"))
            .andExpect(jsonPath("data[0].is_accepted").value(true))
    }

    @Test
    fun onDeactivateRequest_whenAlreadyDeactivated_status400() {
        val body = mapOf("is_accepted" to false)
        val mockUserIdOfNotActivatedUser = 4

        val request = JsonMockRequestGenerator(
            getRouteForUser(mockUserIdOfNotActivatedUser),
            HttpMethod.PATCH
        ).getRequestWithJsonBody(body)
        request.header("Authorization", "Bearer $mockAccessToken")

        mvc.perform(request)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("message").value(Matchers.matchesPattern("Invalid new activation status.")))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_ALREADY_DEACTIVATED.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_ALREADY_DEACTIVATED.name))
    }
}
