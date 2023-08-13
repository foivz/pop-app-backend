package hr.foi.pop.backend.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.responses.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthEntryPointJwt : AuthenticationEntryPoint {
    private val logger = LoggerFactory.getLogger(AuthEntryPointJwt::class.java)
    
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger.error("Unauthorized error: ${authException.message}")

        response.apply {
            setHeader("Content-type", "application/json")
            status = HttpStatus.FORBIDDEN.value()
            characterEncoding = "UTF-8"

            val jsonBody = getErrorBodyJson()
            writer.print(jsonBody)
            writer.flush()
        }
    }

    private fun getErrorBodyJson(): String {
        val error = ErrorResponse("Authorization bearer token is invalid!", ApplicationErrorType.ERR_JWT_INVALID)
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(error)
    }
}
