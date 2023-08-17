package hr.foi.pop.backend.filters

import hr.foi.pop.backend.exceptions.BadJwtFormatException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletRequest

@SpringBootTest
class AuthTokenFilterTest : AuthTokenFilter() {
    @Test
    fun givenCorrectJWT_WhenReceived_ParseNormally() {
        val mockJwt = "eyJhbGciOiJIUzI1NiJ9." +
                "eyJzdWIiOiJjdmVsYXNxdWV6IiwiaWF0IjoxNjkyMzAyMjU2LCJleHAiOjE2OTIzMDM0NTZ9." +
                "vKDqFIAWK1lmfK-pWo1lUQPePCT-pihqKGQFn7rdO_k"

        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer $mockJwt")
        }

        var parsedJwt: String? = null
        Assertions.assertDoesNotThrow { parsedJwt = parseJwt(request) }

        parsedJwt?.let { actualParsedJwt ->
            Assertions.assertEquals(mockJwt, actualParsedJwt)
        }
    }

    @Test
    fun givenInvalidJWT_WhenReceived_ThrowException() {
        val request = MockHttpServletRequest()
        request.addHeader(
            "Authorization", "I am JWT without a 'Bearer' prefix!"
        )

        Assertions.assertThrows(BadJwtFormatException::class.java) { parseJwt(request) }
    }
}
