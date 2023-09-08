package hr.foi.pop.backend.security

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.services.UserService
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import hr.foi.pop.backend.utils.MockObjectsHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class TokenFilterTest {
    val testRoute = "/api/v2/test/auth"

    private val templateRequestBodyForTesting =
        MockObjectsHelper.getMockRegisterRequestBody("token-tester", "test@token.com")

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Autowired
    lateinit var userRepository: UserRepository

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, TokenFilterTest::class)
    }

    @Test
    fun givenNoJWT_onLoginAttempt_401() {
        mvc.perform(MockMvcRequestBuilders.get(testRoute))
            .andExpect(status().isUnauthorized)
            .andExpect(header().string("WWW-Authenticate", "Bearer realm=\"Secure and personalized API access\""))
    }

    @Test
    fun givenInvalidJwt_onLoginAttempt_403() {
        val badJwt = "this=is=not=a=jwt"

        mvc.perform(
            MockMvcRequestBuilders
                .get(testRoute)
                .header("Authorization", "Bearer $badJwt")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(header().string("WWW-Authenticate", "Bearer realm=\"Secure and personalized API access\""))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_JWT_INVALID.name))
    }

    @Test
    fun givenExpiredJwt_onLoginAttempt_403() {
        val expiredJwt =
            "eyJhbGciOiJIUzI1NiJ9." +
                    "eyJzdWIiOiJpaG9ydmF0IiwiaWF0IjoxNjkyNzExMDU0LCJleHAiOjE2OTI3MTIyNTR9." +
                    "WTqKrTBU17yU5uakLnC_4pzr8d7sH7eZkNDNbXQGZk0"

        mvc.perform(
            MockMvcRequestBuilders
                .get(testRoute)
                .header("Authorization", "Bearer $expiredJwt")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(header().string("WWW-Authenticate", "Bearer realm=\"Secure and personalized API access\""))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_JWT_EXPIRED.name))
    }

    private fun procureValidJwt(): String {
        Assertions.assertFalse(userRepository.existsByUsername(templateRequestBodyForTesting.username))

        val acceptedUser = userService.registerUser(templateRequestBodyForTesting)
        acceptedUser.isAccepted = true
        userRepository.save(acceptedUser)

        val tokenPair = authenticationService.authenticateAndGenerateTokenPair(
            templateRequestBodyForTesting.username,
            templateRequestBodyForTesting.password
        )
        return tokenPair.accessToken
    }
}
