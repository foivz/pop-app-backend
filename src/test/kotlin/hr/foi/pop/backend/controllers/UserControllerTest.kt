package hr.foi.pop.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.event.Event
import hr.foi.pop.backend.models.role.Role
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.services.UserService
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime


@WithMockUser("tester")
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    companion object {
        const val route = "/api/v2/users"
    }

    @Autowired
    lateinit var context: WebApplicationContext

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    @MockBean
    lateinit var userService: UserService

    private val mockBodyAsObject = RegisterRequestBody(
        "Ivan",
        "Horvat",
        "ihorvat",
        "ihorvat@foi.hr",
        "test123",
        "buyer"
    )

    @Test
    @WithAnonymousUser
    fun onAnonymousUser_triesToSendRequest_Status401() {
        mvc.perform(
            MockMvcRequestBuilders.post(route).with(csrf())
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun onRegisterRequest_WhenNoRequestSent_Status400() {
        mvc.perform(
            MockMvcRequestBuilders.post(route).with(csrf())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun onRegisterRequest_WhenBadRequestSent_Status400() {
        val body = "{\"random\": \"Object\"}"

        val request = getRequestObjectWithJSONBody(body)

        mvc.perform(request).andExpect(status().isBadRequest)
    }

    @Test
    fun onInvalidUserData_WhenRequestSent_Status400WithMessage() {
        val mockedUserWithNoLastName = mockBodyAsObject.copy(lastName = "")

        val body = ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .writeValueAsString(mockedUserWithNoLastName)

        val request = getRequestObjectWithJSONBody(body)

        Mockito
            .`when`(userService.registerUser(any()))
            .thenThrow(UserCheckException(ApplicationErrorType.ERR_LASTNAME_INVALID))

        mvc.perform(request)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_LASTNAME_INVALID.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_LASTNAME_INVALID.name))
    }

    @Test
    fun onRegisterRequest_WhenRequestIsComplete_ThenAppropriateSuccessMessage() {
        val body = ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .writeValueAsString(mockBodyAsObject)

        val request = getRequestObjectWithJSONBody(body)

        val userMockId = 1
        val userMockEventId = 1
        val userMockDateOfRegister = LocalDateTime.now()

        Mockito
            .`when`(userService.registerUser(any()))
            .thenReturn(User().apply {
                id = userMockId
                firstName = mockBodyAsObject.firstName
                lastName = mockBodyAsObject.lastName
                username = mockBodyAsObject.username
                email = mockBodyAsObject.email
                event = Event().apply { id = userMockEventId }
                role = Role().apply { name = mockBodyAsObject.roleValue!!.name.lowercase() }
                dateOfRegister = userMockDateOfRegister
            })

        mvc.perform(request)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("success").value(true))
            .andExpect(jsonPath("message").value(Matchers.matchesPattern("User \"ihorvat\" registered with ID $userMockId.")))
            .andExpect(jsonPath("data[0].id").isNumber)
            .andExpect(jsonPath("data[0].role").value("buyer"))
            .andExpect(jsonPath("data[0].store").value(null))
            .andExpect(jsonPath("data[0].event").value(userMockEventId))
            .andExpect(jsonPath("data[0].first_name").value("Ivan"))
            .andExpect(jsonPath("data[0].last_name").value("Horvat"))
            .andExpect(jsonPath("data[0].email").value("ihorvat@foi.hr"))
            .andExpect(jsonPath("data[0].username").value("ihorvat"))
            .andExpect(jsonPath("data[0].date_of_register").value(userMockDateOfRegister.toString().removeSuffix("00")))
            .andExpect(jsonPath("data[0].balance").value(0))
            .andExpect(jsonPath("data[0].is_accepted").value(false))
    }

    private fun getRequestObjectWithJSONBody(jsonBody: String) = MockMvcRequestBuilders
        .post(route)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
}
