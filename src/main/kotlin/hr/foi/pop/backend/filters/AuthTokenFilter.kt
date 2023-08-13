package hr.foi.pop.backend.filters

import hr.foi.pop.backend.exceptions.BadJwtFormatException
import hr.foi.pop.backend.security.jwt.JwtUtils
import hr.foi.pop.backend.services.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class AuthTokenFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    @Autowired
    private lateinit var userDetailsService: UserService

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
            logger.error(ex.message)
        } catch (ex: Exception) {
            logger.error("Cannot set user authentication: $ex")
        }

        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): String {
        val authHeader = request.getHeader("Authorization")
        val authPrefix = "Bearer "

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(authPrefix)) {
            val startIndexOfJwt = authHeader.indexOf(authPrefix)
            return authHeader.substring(startIndexOfJwt)
        } else {
            throw BadJwtFormatException()
        }
    }
}
