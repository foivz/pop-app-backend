package hr.foi.pop.backend.responses

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import hr.foi.pop.backend.exceptions.ResponseSenderException
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

class ResponseSender(private val response: HttpServletResponse) {
    private var jsonBody: String = ""
    private var statusValue: Int = 200

    fun setHttpStatus(status: HttpStatus) {
        statusValue = status.value()
    }

    fun setBody(response: Response) {
        val mapper = ObjectMapper()
        mapper.propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        jsonBody = mapper.writeValueAsString(response)
    }

    fun send() {
        if (jsonBody.isEmpty()) {
            throw ResponseSenderException("Cannot send response with empty JSON body!")
        }

        response.apply {
            characterEncoding = "UTF-8"
            contentType = "application/json"
            status = statusValue

            writer.print(jsonBody)
            writer.flush()
        }
    }


}
