package hr.foi.pop.backend.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

class JsonMockRequestGenerator(
    private val route: String,
    private val method: HttpMethod = HttpMethod.POST
) {
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

    private fun getRequestObjectWithJSONBody(jsonBody: String) =
        getBuilderForMethod()
            .with(csrf())
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)

    private fun getBuilderForMethod(): MockHttpServletRequestBuilder {
        return when (method) {
            HttpMethod.GET -> MockMvcRequestBuilders.get(route)
            HttpMethod.PATCH -> MockMvcRequestBuilders.patch(route)
            HttpMethod.PUT -> MockMvcRequestBuilders.put(route)
            HttpMethod.DELETE -> MockMvcRequestBuilders.delete(route)
            else -> MockMvcRequestBuilders.post(route)
        }
    }
}
