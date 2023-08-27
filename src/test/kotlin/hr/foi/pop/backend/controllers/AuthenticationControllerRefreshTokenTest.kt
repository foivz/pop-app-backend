package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.RefreshTokenRequestBody
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.utils.JsonMockRequestGenerator
import hr.foi.pop.backend.utils.MockEntitiesHelper
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AuthenticationControllerRefreshTokenTest {
    companion object {
        const val refreshTokenRoute = "/api/v2/auth/refresh-token"
    }

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var context: WebApplicationContext

    private final val mockPassword = "test123"
    val mockUser = MockEntitiesHelper.generateUserEntityWithStore(
        this::class,
        mockPassword
    )

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, AuthenticationControllerRefreshTokenTest::class)
        Assertions.assertFalse(userRepository.existsByUsername(mockUser.username))
        userRepository.save(mockUser)
    }

    private val jsonRequester = JsonMockRequestGenerator(refreshTokenRoute)

    @Test
    fun givenValidRefreshToken_onRequestToRefreshToken_accessTokenIsRefreshed() {
        val validTokenPair = authenticationService.authenticateAndGenerateTokenPair(mockUser.username, mockPassword)
        val body = RefreshTokenRequestBody(validTokenPair.refreshToken.token)
        val request = jsonRequester.getRequestWithJsonBody(body)

        mvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("access_token").isString)
            .andExpect(jsonPath("refresh_token.token").isString)
            .andExpect(jsonPath("refresh_token.valid_for.time_unit").isString)
            .andExpect(jsonPath("refresh_token.valid_for.time_amount").isNumber)
    }
}
