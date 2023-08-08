package hr.foi.pop.backend.handlers

import hr.foi.pop.backend.controllers.UserControllerTest
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.services.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest
class BadRequestHandlerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var userService: UserService

    @Test
    fun givenNonParseableJsonObjectForUserRegistrationRequest_WhenCatched_ProperMessageReturned() {
        mockMvc.perform(MockMvcRequestBuilders.post(UserControllerTest.route))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("error_code").value(ApplicationErrorType.ERR_BAD_BODY.code))
            .andExpect(MockMvcResultMatchers.jsonPath("error_message").value(ApplicationErrorType.ERR_BAD_BODY.name))
    }
}
