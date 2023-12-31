package hr.foi.pop.backend.handlers

import hr.foi.pop.backend.controllers.AuthenticationController
import hr.foi.pop.backend.controllers.AuthenticationControllerRegistrationTest
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.services.UserService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [AuthenticationController::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BadRequestHandlerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var userService: UserService

    @MockBean
    lateinit var authenticationService: AuthenticationService

    @Test
    @WithMockUser("tester")
    fun givenNonParseableJsonObjectForUserRegistrationRequest_whenCatched_properMessageReturned() {
        val request = buildPostRequest()

        mockMvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("error_code").value(ApplicationErrorType.ERR_BAD_BODY.code))
            .andExpect(MockMvcResultMatchers.jsonPath("error_message").value(ApplicationErrorType.ERR_BAD_BODY.name))
    }

    private fun buildPostRequest(): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.post(AuthenticationControllerRegistrationTest.registerRoute).with(csrf())
    }
}
