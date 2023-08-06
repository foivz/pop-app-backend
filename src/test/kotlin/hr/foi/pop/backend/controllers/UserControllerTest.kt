package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.models.event.Event
import hr.foi.pop.backend.models.role.Role
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.services.UserService
import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest
class UserControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var userService: UserService

    private val route = "/api/v2/users"

    @Test
    fun onRegisterRequest_WhenNoRequestSent_Status400() {
        mockMvc.perform(MockMvcRequestBuilders.post(route))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun onRegisterRequest_WhenBadRequestSent_Status400() {
        val body = "{\"random\": \"Object\"}"

        val request = getRequestObjectWithJSONBody(body)

        mockMvc.perform(request).andExpect(status().isBadRequest)
    }

    @Test
    fun onRegisterRequest_WhenRequestIsComplete_ThenAppropriateSuccessMessageAndPersisted() {
        val body = "{" +
                "    \"first_name\": \"Ivan\"," +
                "    \"last_name\": \"Horvat\"," +
                "    \"username\": \"ihorvat\"," +
                "    \"email\": \"ihorvat@foi.hr\"," +
                "    \"password\": \"test123\"," +
                "    \"role\": \"buyer\"" +
                "}"

        val request = getRequestObjectWithJSONBody(body)

        val mockBodyAsObject = RegisterRequestBody(
            "Ivan",
            "Horvat",
            "ihorvat",
            "ihorvat@foi.hr",
            "test123",
            "buyer"
        )
        val userMockId = 1
        val userMockEventId = 1
        val userMockDateOfRegister = LocalDateTime.now()

        Mockito
            .`when`(userService.registerUser(userInfo = mockBodyAsObject))
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

        mockMvc.perform(request)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("success").value(true))
            .andExpect(jsonPath("message").value(matchesPattern("User \\\"ihorvat\\\" registered with ID $userMockId.")))
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
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
}
