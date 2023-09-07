package hr.foi.pop.backend.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

class JsonMockRequestGenerator(private val route: String) {
    fun getRequestWithJsonBody(obj: Any): MockHttpServletRequestBuilder {
        val json = getJsonFromObject(obj)
        return getRequestWithJsonBody(json)
    }

    fun getRequestWithJsonBody(json: String): MockHttpServletRequestBuilder {
        return getRequestObjectWithJSONBody(json)
    }

    private fun getJsonFromObject(obj: Any): String = ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .writeValueAsString(obj)

    private fun getRequestObjectWithJSONBody(jsonBody: String) = MockMvcRequestBuilders
        .post(route)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
}
