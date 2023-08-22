package hr.foi.pop.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import hr.foi.pop.backend.controllers.AuthenticationControllerRegistrationTest.Companion.mockRegisterBodyAsObject
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.LoginRequestBody
import hr.foi.pop.backend.services.UserService
import hr.foi.pop.backend.utils.MockEntitiesHelper
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerLoginTest {
    companion object {
        const val loginRoute = "/api/v2/auth/login"
    }

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .alwaysDo<DefaultMockMvcBuilder> {
                LoggerFactory.getLogger(AuthenticationControllerLoginTest::class.java).info(it.response.contentAsString)
            }
            .build()
    }

    private val mockLoginBodyAsObject = LoginRequestBody(
        mockRegisterBodyAsObject.username,
        mockRegisterBodyAsObject.password
    )

    private val badJSON = "{\"random\": \"Object\"}"

    private fun getRequestObjectWithJSONBody(jsonBody: String) = MockMvcRequestBuilders
        .post(loginRoute)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)

    @Test
    fun givenInvalidBodyJSON_whenLoginRouteHit_returnError400() {
        val request = getRequestObjectWithJSONBody(badJSON)

        mvc.perform(request).andExpect(status().isBadRequest)
    }

    @Test
    fun givenNonExistentUser_whenLoginRouteHit_returnErrorMessage() {
        val body = getJsonFromObject(LoginRequestBody("dont exist", "invalid"))
        val request = getRequestObjectWithJSONBody(body)

        mvc.perform(request)
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_USER_INVALID.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_USER_INVALID.name))
    }

    @Test
    @Transactional
    fun givenNonAcceptedCorrectUser_whenLoginRouteHit_returnError() {
        val body = getJsonFromObject(mockLoginBodyAsObject)
        val request = getRequestObjectWithJSONBody(body)

        Assertions.assertNotNull(userService.registerUser(mockRegisterBodyAsObject))

        mvc.perform(request)
            .andExpect(status().isForbidden)
            .andExpect(
                jsonPath("message").value(
                    Matchers.matchesPattern(
                        "User \"${mockRegisterBodyAsObject.username}\" is not accepted yet by the admin!"
                    )
                )
            )
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_NOT_ACTIVATED.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_NOT_ACTIVATED.name))
    }

    @Test
    @Transactional
    fun givenAcceptedCorrectUserWithoutAStore_whenLoginRouteHit_returnValidJWTAndWarningMessage() {
        val body = getJsonFromObject(mockLoginBodyAsObject)
        val request = getRequestObjectWithJSONBody(body)

        val storedUser = getStoredAcceptedUserWithoutStore()

        mvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("message").value(
                    Matchers.matchesPattern(
                        "User \"${mockRegisterBodyAsObject.username}\" logged in with warnings."
                    )
                )
            )
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.WARN_STORE_NOT_SET.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.WARN_STORE_NOT_SET.name))
            .andExpect(jsonPath("data[0].id").isNumber)
            .andExpect(jsonPath("data[0].role").value(storedUser.role.name))
            .andExpect(jsonPath("data[0].store").isEmpty)
            .andExpect(jsonPath("data[0].first_name").value(storedUser.firstName))
            .andExpect(jsonPath("data[0].last_name").value(storedUser.lastName))
            .andExpect(jsonPath("data[0].email").value(storedUser.email))
            .andExpect(jsonPath("data[0].username").value(storedUser.username))
            .andExpect(jsonPath("data[0].is_accepted").value(true))
    }

    private fun getStoredAcceptedUserWithoutStore(): User {
        var storedUser: User? = userService.registerUser(mockRegisterBodyAsObject)
        Assertions.assertNotNull(storedUser)

        storedUser!!.isAccepted = true
        userRepository.save(storedUser)
        storedUser = userRepository.getUserByUsername(storedUser.username)
        Assertions.assertNotNull(storedUser)
        Assertions.assertTrue(storedUser!!.isAccepted)
        Assertions.assertNull(storedUser.store)

        return storedUser
    }

    @Test
    @Transactional
    fun givenGoodUser_whenLoginRouteHit_returnValidJWT() {
        val body = getJsonFromObject(mockLoginBodyAsObject)
        val request = getRequestObjectWithJSONBody(body)

        val storedUser = getStoredAcceptedUserWithStore()

        mvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("message").value(
                    Matchers.matchesPattern(
                        "User \"${mockRegisterBodyAsObject.username}\" logged in."
                    )
                )
            )
            .andExpect(jsonPath("data[0].id").isNumber)
            .andExpect(jsonPath("data[0].role").value(storedUser.role.name))
            .andExpect(jsonPath("data[0].store.store_id").value(storedUser.store!!.id))
            .andExpect(jsonPath("data[0].store.store_name").value(storedUser.store!!.storeName))
            .andExpect(jsonPath("data[0].first_name").value(storedUser.firstName))
            .andExpect(jsonPath("data[0].last_name").value(storedUser.lastName))
            .andExpect(jsonPath("data[0].email").value(storedUser.email))
            .andExpect(jsonPath("data[0].username").value(storedUser.username))
            .andExpect(jsonPath("data[0].is_accepted").value(true))
    }

    private fun getStoredAcceptedUserWithStore(): User {
        val validUser = MockEntitiesHelper.generateUserEntityWithStore(mockLoginBodyAsObject.password)
        Assertions.assertNotNull(validUser.store)
        Assertions.assertNotNull(validUser.isAccepted)
        validUser.username = mockLoginBodyAsObject.username
        return userRepository.save(validUser)
    }

    private fun getJsonFromObject(obj: Any): String = ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .writeValueAsString(obj)
}
