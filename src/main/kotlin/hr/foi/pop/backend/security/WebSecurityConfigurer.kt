package hr.foi.pop.backend.security

import hr.foi.pop.backend.filters.AuthTokenFilter
import hr.foi.pop.backend.services.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableMethodSecurity
class WebSecurityConfigurer {

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    @Autowired
    private lateinit var unauthorizedHandler: AuthenticationExceptionHandler

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    private val excludedRoutesForAuthTokenFiltering = listOf(
        AntPathRequestMatcher("/api/v2/auth/**"),
        AntPathRequestMatcher("/h2-console/**")
    )

    @Bean
    fun authenticationJwtTokenFilter(): AuthTokenFilter {
        return AuthTokenFilter(excludedRoutesForAuthTokenFiltering)
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(authenticationService)
        authProvider.setPasswordEncoder(passwordEncoder)
        return authProvider
    }

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer? {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring().requestMatchers(AntPathRequestMatcher("/h2-console/**"))
        }
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        enableH2Console(http)

        http.csrf { csrf -> csrf.disable() }
            .exceptionHandling { exception -> exception.authenticationEntryPoint(unauthorizedHandler) }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.POST, "/api/v2/auth/**").permitAll()
                    .anyRequest().authenticated()
            }

        http.authenticationProvider(authenticationProvider())
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    private fun enableH2Console(http: HttpSecurity) {
        http.headers { httpHeaders -> httpHeaders.frameOptions { frameOptions -> frameOptions.disable() } }
        http.csrf { csrf ->
            csrf.ignoringRequestMatchers(toH2Console())
        }.authorizeHttpRequests { auth ->
            auth.requestMatchers(toH2Console()).permitAll()
        }
    }
}
