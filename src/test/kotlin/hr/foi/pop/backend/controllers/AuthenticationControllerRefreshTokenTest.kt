package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.models.refresh_token.RefreshTokenDto
import hr.foi.pop.backend.repositories.RefreshTokenRepository
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
import java.time.LocalDateTime

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
    lateinit var refreshTokenRepository: RefreshTokenRepository

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

    @Test
    fun givenInvalidRefreshToken_onRequestToRefreshToken_invalidTokenResponse() {
        val invalidToken = "bad refresh token"
        val body = RefreshTokenRequestBody(invalidToken)
        val request = jsonRequester.getRequestWithJsonBody(body)

        mvc.perform(request)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("message").value("Refresh token not in expected format!"))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_REFRESH_TOKEN_INVALID.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_REFRESH_TOKEN_INVALID.name))
    }

    @Test
    fun givenNonExistentRefreshToken_onRequestToRefreshToken_invalidTokenResponse() {
        val correctlyFormattedButMadeUpToken = "d3F5AOdPk89OntlYE7IbIAeCFeYxFSFvUwko3J6pFGq9zfB2SHsilPdyoKbjWNYp"
        val body = RefreshTokenRequestBody(correctlyFormattedButMadeUpToken)
        val request = jsonRequester.getRequestWithJsonBody(body)

        mvc.perform(request)
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("message").value("Provided refresh token not recognized!"))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_REFRESH_TOKEN_INVALID.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_REFRESH_TOKEN_INVALID.name))
    }

    @Test
    fun givenExpiredRefreshToken_onRequestToRefreshToken_expiredTokenResponse() {
        val validTokenPair = authenticationService.authenticateAndGenerateTokenPair(mockUser.username, mockPassword)
        expireRefreshToken(validTokenPair.refreshToken)
        val body = RefreshTokenRequestBody(validTokenPair.refreshToken.token)
        val request = jsonRequester.getRequestWithJsonBody(body)

        mvc.perform(request)
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("message").value("Refresh token expired!"))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_REFRESH_TOKEN_EXPIRED.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_REFRESH_TOKEN_EXPIRED.name))
    }

    private fun expireRefreshToken(refreshToken: RefreshTokenDto) {
        val token = refreshTokenRepository.getRefreshTokenByToken(refreshToken.token)
        Assertions.assertNotNull(token)
        token!!.expirationDate = LocalDateTime.now().minusMinutes(5)
        refreshTokenRepository.save(token)
    }
}
