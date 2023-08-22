package hr.foi.pop.backend.security.response_sender_error_customization

import hr.foi.pop.backend.responses.ResponseSender

abstract class ResponseSenderCustomizer(protected val responseSender: ResponseSender) {
    abstract fun getCustomizedResponseSender(): ResponseSender
}
