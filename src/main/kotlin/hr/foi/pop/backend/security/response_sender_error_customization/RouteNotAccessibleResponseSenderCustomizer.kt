package hr.foi.pop.backend.security.response_sender_error_customization

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.ResponseSender
import org.springframework.http.HttpStatus

class RouteNotAccessibleResponseSenderCustomizer(responseSender: ResponseSender) :
    ResponseSenderCustomizer(responseSender) {
    override fun getCustomizedResponseSender(): ResponseSender {
        responseSender.setBody(getAccessDeniedErrorBody())
        responseSender.setHttpStatus(HttpStatus.NOT_FOUND)
        return responseSender
    }

    private fun getAccessDeniedErrorBody(): ErrorResponse {
        return ErrorResponse("You can't access this route!", ApplicationErrorType.ERR_ROUTE_NOT_ACCESSIBLE)
    }
}
