package hr.foi.pop.backend.security.jwt

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.ResponseSender
import io.jsonwebtoken.ExpiredJwtException
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

        val responseSender = prepareResponseSender(response, authException.cause ?: authException)
        responseSender.send()
    }

    private fun prepareResponseSender(response: HttpServletResponse, interceptedException: Throwable): ResponseSender {
        val responseSender = ResponseSender(response)

        if (interceptedException is ExpiredJwtException) {
            responseSender.setBody(getJwtExpiredErrorBody())
            responseSender.setHttpStatus(HttpStatus.FORBIDDEN)
        }

        return responseSender
    }

    private fun getJwtExpiredErrorBody(): ErrorResponse {
        return ErrorResponse("Authorization bearer token is expired!", ApplicationErrorType.ERR_JWT_EXPIRED)
    }
}
