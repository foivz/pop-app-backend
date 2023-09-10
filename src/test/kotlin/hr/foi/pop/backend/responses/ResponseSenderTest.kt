package hr.foi.pop.backend.responses

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import hr.foi.pop.backend.definitions.ApplicationErrorType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse

@SpringBootTest
class ResponseSenderTest {
    val mockErrorObject = ErrorResponse("Authorization bearer token is expired!", ApplicationErrorType.ERR_JWT_EXPIRED)

    @Test
    fun givenMockResponse_whenMockedJwtExpiredError_updateResponseSenderObject() {
        val mockHttpResponse = MockHttpServletResponse()
        val responseSender = ResponseSender(mockHttpResponse)

        responseSender.apply {
            setBody(mockErrorObject)
            setHttpStatus(HttpStatus.FORBIDDEN)
            send()
        }

        assertResponseWriterContainsCorrectInformation(mockHttpResponse)
    }

    private fun assertResponseWriterContainsCorrectInformation(response: MockHttpServletResponse) {
        val responseContent = response.contentAsString
        val responseContentTypeAndEncoding = response.contentType!!
        val responseContentType = responseContentTypeAndEncoding.split(';')[0]

        val objectMapper = ObjectMapper()
        val json: JsonNode = objectMapper.readTree(responseContent)

        Assertions.assertEquals("UTF-8", response.characterEncoding, "Encoding not correct!")
        Assertions.assertEquals("application/json", responseContentType, "Content-type not correct!")
        Assertions.assertEquals(mockErrorObject.message, json["message"].textValue(), "Message not correct!")
        Assertions.assertEquals(mockErrorObject.errorMessage, json["error_message"].textValue(), "Error not correct!")
        Assertions.assertEquals(mockErrorObject.errorCode, json["error_code"].intValue(), "Error code not correct!")
    }
}
