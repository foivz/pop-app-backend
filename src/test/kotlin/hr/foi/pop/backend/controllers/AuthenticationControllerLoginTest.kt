package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.LoginRequestBody
import hr.foi.pop.backend.services.UserService
import hr.foi.pop.backend.utils.JsonMockRequestGenerator
import hr.foi.pop.backend.utils.MockEntitiesHelper
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import hr.foi.pop.backend.utils.MockObjectsHelper
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
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
        mvc = MockMvcBuilderManager.getMockMvc(context, AuthenticationControllerLoginTest::class)
    }

    private val mockLoginUser =
        MockObjectsHelper.getMockRegisterRequestBody("login-tester", "test@login.com")

    private val mockLoginBodyAsObject = LoginRequestBody(
        mockLoginUser.username,
        mockLoginUser.password
    )

    private val badJSON = "{\"random\": \"Object\"}"

    private val jsonRequester = JsonMockRequestGenerator(loginRoute)

    @Test
    fun givenInvalidBodyJSON_whenLoginRouteHit_returnError400() {
        val request = jsonRequester.getRequestWithJsonBody(badJSON)

        mvc.perform(request).andExpect(status().isBadRequest)
    }

    @Test
    fun givenNonExistentUser_whenLoginRouteHit_returnErrorMessage() {
        val body = LoginRequestBody("dont exist", "invalid")
        val request = jsonRequester.getRequestWithJsonBody(body)

        mvc.perform(request)
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_USER_INVALID.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_USER_INVALID.name))
    }

    @Test
    fun givenNonAcceptedCorrectUser_whenLoginRouteHit_returnError() {
        val body = mockLoginBodyAsObject
        val request = jsonRequester.getRequestWithJsonBody(body)

        Assertions.assertNotNull(userService.registerUser(mockLoginUser))

        mvc.perform(request)
            .andExpect(status().isForbidden)
            .andExpect(
                jsonPath("message").value(
                    Matchers.matchesPattern(
                        "User \"${mockLoginUser.username}\" is not accepted by the admin!"
                    )
                )
            )
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_NOT_ACTIVATED.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_NOT_ACTIVATED.name))
    }

    @Test
    fun givenAcceptedCorrectUserWithoutAStore_whenLoginRouteHit_returnValidJWTAndWarningMessage() {
        val body = mockLoginBodyAsObject
        val request = jsonRequester.getRequestWithJsonBody(body)

        val storedUser = getStoredAcceptedUserWithoutStore()

        val resultActions = mvc.perform(request)
        expectResultActionsToContainAcceptedUserInformation(resultActions, storedUser)

        resultActions
            .andExpect(jsonPath("message").value("User \"${mockLoginUser.username}\" logged in with warnings."))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.WARN_STORE_NOT_SET.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.WARN_STORE_NOT_SET.name))
            .andExpect(jsonPath("data[0].store").isEmpty)
    }

    private fun expectResultActionsToContainAcceptedUserInformation(resultActions: ResultActions, user: User) {
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("data[0].id").isNumber)
            .andExpect(jsonPath("data[0].role").value(user.role.name))
            .andExpect(jsonPath("data[0].first_name").value(user.firstName))
            .andExpect(jsonPath("data[0].last_name").value(user.lastName))
            .andExpect(jsonPath("data[0].email").value(user.email))
            .andExpect(jsonPath("data[0].username").value(user.username))
            .andExpect(jsonPath("data[0].is_accepted").value(true))
    }

    private fun getStoredAcceptedUserWithoutStore(): User {
        var storedUser: User? = userService.registerUser(mockLoginUser)
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
    fun givenGoodUser_whenLoginRouteHit_returnValidJWT() {
        val body = mockLoginBodyAsObject
        val request = jsonRequester.getRequestWithJsonBody(body)

        val storedUser = getStoredAcceptedUserWithStore()

        val resultActions = mvc.perform(request)
        expectResultActionsToContainAcceptedUserInformation(resultActions, storedUser)

        resultActions.andExpect(status().isOk)
            .andExpect(jsonPath("message").value("User \"${mockLoginUser.username}\" logged in."))
            .andExpect(jsonPath("data[0].store.store_id").value(storedUser.store!!.id))
            .andExpect(jsonPath("data[0].store.store_name").value(storedUser.store!!.storeName))
            .andExpect(jsonPath("data[0].token_pair.access_token").isString)
            .andExpect(jsonPath("data[0].token_pair.refresh_token.token").isString)
            .andExpect(jsonPath("data[0].token_pair.refresh_token.valid_for.time_unit").value("minutes"))
            .andExpect(jsonPath("data[0].token_pair.refresh_token.valid_for.time_amount").isNumber)
    }

    private fun getStoredAcceptedUserWithStore(): User {
        val validUser = MockEntitiesHelper.generateBuyerUserEntityWithStore(
            this::class,
            mockLoginBodyAsObject.password
        )

        Assertions.assertNotNull(validUser.store)
        Assertions.assertNotNull(validUser.isAccepted)

        validUser.username = mockLoginBodyAsObject.username
        return userRepository.save(validUser)
    }
}
