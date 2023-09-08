package hr.foi.pop.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import hr.foi.pop.backend.models.role.Role
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.request_bodies.ActivateUserRequestBody
import hr.foi.pop.backend.services.UserService
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserControllerTest {
    companion object {
        private const val route = "/api/v2/users"
        fun getActivateUserRoute(userId: Int): String {
            return "${route}/${userId}/activate"
        }
    }

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    private lateinit var mockMvc: MockMvc

    private val activateUserRequestBody: ActivateUserRequestBody = ActivateUserRequestBody(true)

    private val deactivateUserRequestBody: ActivateUserRequestBody = ActivateUserRequestBody(false)

    @BeforeAll
    fun setup() {
        mockMvc = MockMvcBuilderManager.getMockMvc(context, UserControllerTest::class)
    }

    @Test
    fun onActivateRequest_whenInvalidHttpMethod_status405() {
        MockMvcRequestBuilders
            .post(getActivateUserRoute(1))
            .content(ObjectMapper().writeValueAsString())


    }

    @Test
    fun onActivateRequest_whenInvalidRequestBody_status400() {
        val body = "{\"random\": \"Object\"}"

        val request = MockMvcRequestBuilders
            .patch(getActivateUserRoute(1))
            .content(body)
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request).andExpect(status().isBadRequest)
    }

    @Test
    fun onActivateRequest_withValidBody_status200() {
        val activated: ActivateUserRequestBody = ActivateUserRequestBody(false)

        val body = ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .writeValueAsString(activated)

        val request = MockMvcRequestBuilders
            .patch(getActivateUserRoute(1))
            .content(body)
            .contentType(MediaType.APPLICATION_JSON)

        Mockito
            .`when`(userService.activateOrDeactivateUser(any(), any()))
            .thenReturn(User().apply {
                id = 1
                firstName = "Catherine"
                lastName = "Velasquez"
                email = "cvelasquez@pop.app"
                username = "cvelasquez"
                role = Role().apply {
                    id = 1
                    name = "admin"
                }
                isAccepted = false
            })

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("success").value(true))
            .andExpect(jsonPath("message").value(Matchers.matchesPattern("User 'cvelasquez' deactivated")))
            .andExpect(jsonPath("data[0].id").value(1))
            .andExpect(jsonPath("data[0].role").value("admin"))
            .andExpect(jsonPath("data[0].first_name").value("Catherine"))
            .andExpect(jsonPath("data[0].last_name").value("Velasquez"))
            .andExpect(jsonPath("data[0].email").value("cvelasquez@pop.app"))
            .andExpect(jsonPath("data[0].username").value("cvelasquez"))
            .andExpect(jsonPath("data[0].activated").value(false))
    }
}
