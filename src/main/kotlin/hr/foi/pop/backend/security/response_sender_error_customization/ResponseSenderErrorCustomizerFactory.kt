package hr.foi.pop.backend.security.response_sender_error_customization

import hr.foi.pop.backend.exceptions.ResponseSenderCustomizerNotFound
import hr.foi.pop.backend.responses.ResponseSender
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.InsufficientAuthenticationException
import kotlin.reflect.KClass

class ResponseSenderErrorCustomizerFactory(private val responseSender: ResponseSender) {
    private val responseSenderCustomizers = mapOf<KClass<out Throwable>, KClass<out ResponseSenderCustomizer>>(
        ExpiredJwtException::class to ExpiredJwtResponseSenderCustomizer::class,
        InsufficientAuthenticationException::class to RouteNotAccessibleResponseSenderCustomizer::class
    )

    fun getCustomizerBasedOnException(exception: Throwable): ResponseSenderCustomizer {
        val appropriateCustomizerClass = responseSenderCustomizers[exception::class]
            ?: throw ResponseSenderCustomizerNotFound(exception::class)

        val foundClassConstructor = appropriateCustomizerClass.constructors.first()
        return foundClassConstructor.call(responseSender)
    }
}
