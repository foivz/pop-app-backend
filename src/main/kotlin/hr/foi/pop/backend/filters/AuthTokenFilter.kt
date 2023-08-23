package hr.foi.pop.backend.filters

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.BadJwtFormatException
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.ResponseSender
import hr.foi.pop.backend.security.jwt.JwtUtils
import hr.foi.pop.backend.services.UserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter


open class AuthTokenFilter(
    private val excludedRoutes: List<AntPathRequestMatcher>
) : OncePerRequestFilter() {

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    @Autowired
    private lateinit var userDetailsService: UserService

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return excludedRoutes.stream().anyMatch { matcher ->
            matcher.matches(request)
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = parseJwt(request)
            val isJwtValid = jwtUtils.validateJwtToken(jwt)

            if (isJwtValid) {
                val username = jwtUtils.getUsernameFromJwtToken(jwt)
                val userDetails = userDetailsService.loadUserByUsername(username)

                UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).let { token ->
                    token.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = token
                }
            }
        } catch (ex: BadJwtFormatException) {
            handleBadJwtToken(ex, response)
            return
        } catch (ex: MalformedJwtException) {
            handleBadJwtToken(ex, response)
            return
        } catch (ex: ExpiredJwtException) {
            logger.error(ex.message)
            notifyUserOfExpiredJwtToken(response)
            return
        } catch (ex: Exception) {
            logger.error("Cannot set user authentication: $ex")
        }

        filterChain.doFilter(request, response)
    }

    private fun handleBadJwtToken(ex: RuntimeException, response: HttpServletResponse) {
        logger.error(ex.message)
        notifyUserOfBadJwtToken(response)
    }

    private fun notifyUserOfBadJwtToken(response: HttpServletResponse) {
        val error = ErrorResponse("Your token is invalid.", ApplicationErrorType.ERR_JWT_INVALID)
        sendError(response, error)
    }

    private fun notifyUserOfExpiredJwtToken(response: HttpServletResponse) {
        val error = ErrorResponse("Authorization bearer token is expired!", ApplicationErrorType.ERR_JWT_EXPIRED)
        sendError(response, error)
    }

    private fun sendError(
        response: HttpServletResponse,
        error: ErrorResponse
    ) {
        response.setHeader("WWW-Authenticate", "Bearer realm=\"Secure and personalized API access\"")
        ResponseSender(response).apply {
            setHttpStatus(HttpStatus.UNAUTHORIZED)
            setBody(error)
            send()
        }
    }

    protected fun parseJwt(request: HttpServletRequest): String {
        val authHeader = request.getHeader("Authorization")
        val authPrefix = "Bearer "

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(authPrefix)) {
            return authHeader.substring(authPrefix.length)
        } else {
            throw BadJwtFormatException()
        }
    }
}
