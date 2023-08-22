package hr.foi.pop.backend.security

import hr.foi.pop.backend.responses.ResponseSender
import hr.foi.pop.backend.security.response_sender_error_customization.ResponseSenderErrorCustomizerFactory
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthenticationExceptionHandler : AuthenticationEntryPoint {
    private val logger = LoggerFactory.getLogger(AuthenticationExceptionHandler::class.java)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger.error("Unauthorized error: ${authException.message}")

        var responseSender = ResponseSender(response)
        val customizerFactory = ResponseSenderErrorCustomizerFactory(responseSender)
        val customizer = customizerFactory.getCustomizerBasedOnException(authException)
        responseSender = customizer.getCustomizedResponseSender()

        responseSender.send()
    }
}
