package hr.foi.pop.backend.exceptions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UserAuthenticationException(reason: String) : RuntimeException("Please check your credentials!") {
    private val logger: Logger = LoggerFactory.getLogger(UserAuthenticationException::class.java)

    init {
        logger.error(reason)
    }
}
