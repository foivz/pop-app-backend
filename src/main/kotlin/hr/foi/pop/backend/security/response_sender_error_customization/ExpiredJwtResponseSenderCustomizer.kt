package hr.foi.pop.backend.security.response_sender_error_customization

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.ResponseSender
import org.springframework.http.HttpStatus

class ExpiredJwtResponseSenderCustomizer(responseSender: ResponseSender) : ResponseSenderCustomizer(responseSender) {
    override fun getCustomizedResponseSender(): ResponseSender {
        responseSender.setBody(getJwtExpiredErrorBody())
        responseSender.setHttpStatus(HttpStatus.FORBIDDEN)
        return responseSender
    }

    private fun getJwtExpiredErrorBody(): ErrorResponse {
        return ErrorResponse("Authorization bearer token is expired!", ApplicationErrorType.ERR_JWT_EXPIRED)
    }
}
