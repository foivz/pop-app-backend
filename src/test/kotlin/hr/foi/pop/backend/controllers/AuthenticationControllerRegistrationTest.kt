package hr.foi.pop.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.utils.DateMatcher
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime


@WithMockUser("tester")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerRegistrationTest {
    companion object {
        const val registerRoute = "/api/v2/auth/register"

        val mockRegisterBodyAsObject = RegisterRequestBody(
            "Ivan",
            "Horvat",
            "ihorvat",
            "ihorvat@foi.hr",
            "test123",
            "buyer"
        )
    }

    @Autowired
    lateinit var context: WebApplicationContext

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, AuthenticationControllerRegistrationTest::class)
    }

    private val badJSON = "{\"random\": \"Object\"}"

    @Test
    fun onRegisterRequest_WhenNoRequestSent_Status400() {
        mvc.perform(
            MockMvcRequestBuilders.post(registerRoute).with(csrf())
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun onRegisterRequest_WhenBadRequestSent_Status400() {
        val request = getRequestObjectWithJSONBody(badJSON)

        mvc.perform(request).andExpect(status().isBadRequest)
    }

    @Test
    fun onInvalidUserData_WhenRequestSent_Status400WithMessage() {
        val mockedUserWithNoLastName = mockRegisterBodyAsObject.copy(lastName = "")

        val body = getJsonFromObject(mockedUserWithNoLastName)

        val request = getRequestObjectWithJSONBody(body)

        mvc.perform(request)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("success").value(false))
            .andExpect(jsonPath("error_code").value(ApplicationErrorType.ERR_LASTNAME_INVALID.code))
            .andExpect(jsonPath("error_message").value(ApplicationErrorType.ERR_LASTNAME_INVALID.name))
    }

    private fun getJsonFromObject(obj: Any): String = ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .writeValueAsString(obj)

    @Test
    fun onRegisterRequest_WhenRequestIsComplete_ThenAppropriateSuccessMessage() {
        val body = getJsonFromObject(mockRegisterBodyAsObject)

        val request = getRequestObjectWithJSONBody(body)

        val userMockEventId = 1
        val mockRegDate = LocalDateTime.now()

        mvc.perform(request)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("success").value(true))
            .andExpect(jsonPath("message").value(Matchers.matchesPattern("User \"${mockRegisterBodyAsObject.username}\" registered with ID \\d+.")))
            .andExpect(jsonPath("data[0].id").isNumber)
            .andExpect(jsonPath("data[0].role").value("buyer"))
            .andExpect(jsonPath("data[0].store").value(null))
            .andExpect(jsonPath("data[0].event").value(userMockEventId))
            .andExpect(jsonPath("data[0].first_name").value("Ivan"))
            .andExpect(jsonPath("data[0].last_name").value("Horvat"))
            .andExpect(jsonPath("data[0].email").value("ihorvat@foi.hr"))
            .andExpect(jsonPath("data[0].username").value("ihorvat"))
            .andExpect(jsonPath("data[0].date_of_register", DateMatcher(mockRegDate, 500)))
            .andExpect(jsonPath("data[0].balance").value(0))
            .andExpect(jsonPath("data[0].is_accepted").value(false))
    }

    private fun getRequestObjectWithJSONBody(jsonBody: String) = MockMvcRequestBuilders
        .post(registerRoute)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
}
