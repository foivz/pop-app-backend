package hr.foi.pop.backend.exceptions

import kotlin.reflect.KClass

class ResponseSenderCustomizerNotFound(private val exceptionType: KClass<out Throwable>) : RuntimeException() {
    override val message: String
        get() = "ResponseSenderErrorCustomizerFactory could not handle exception of type ${exceptionType.simpleName}"
}
