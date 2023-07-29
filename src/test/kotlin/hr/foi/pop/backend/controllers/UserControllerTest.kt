package hr.foi.pop.backend.controllers

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest
class UserControllerTest(@Autowired var mockMvc: MockMvc) {

    @Test
    fun onRegisterRequest_WhenRequestIsCorrupted_ThenAppropriateErrorMessage() {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/users"))
            .andExpect(status().isBadRequest)
    }
}
