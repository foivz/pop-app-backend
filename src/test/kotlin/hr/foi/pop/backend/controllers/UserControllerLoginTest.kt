package hr.foi.pop.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import hr.foi.pop.backend.controllers.UserControllerRegistrationTest.Companion.mockRegisterBodyAsObject
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.request_bodies.LoginRequestBody
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
import org.springframework.web.context.WebApplicationContext


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerLoginTest {
    companion object {
        const val loginRoute = "/api/v2/auth/login"
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

    private val mockLoginBodyAsObject = LoginRequestBody(
        "ihorvat",
        "test123"
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
    fun givenNonAcceptedCorrectUser_whenLoginRouteHit_returnValidJWTAndWarningMessage() {
        val body = getJsonFromObject(mockLoginBodyAsObject)
        val request = getRequestObjectWithJSONBody(body)

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
            .andExpect(jsonPath("data[0].role").value("buyer"))
            .andExpect(jsonPath("data[0].store").isEmpty)
            .andExpect(jsonPath("data[0].first_name").value("Ivan"))
            .andExpect(jsonPath("data[0].last_name").value("Horvat"))
            .andExpect(jsonPath("data[0].email").value("ihorvat@foi.hr"))
            .andExpect(jsonPath("data[0].username").value("ihorvat"))
            .andExpect(jsonPath("data[0].is_accepted").value(true))
    }

    private fun getJsonFromObject(obj: Any): String = ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .writeValueAsString(obj)
}
