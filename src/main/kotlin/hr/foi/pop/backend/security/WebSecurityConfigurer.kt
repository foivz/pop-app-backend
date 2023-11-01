package hr.foi.pop.backend.security

import hr.foi.pop.backend.filters.AuthTokenFilter
import hr.foi.pop.backend.services.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
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

    @Autowired
    private lateinit var environment: Environment

    private fun isProductionProfile() = environment.activeProfiles[0] == "prod"

    private val excludedRoutesFromSpringSecurity = arrayOf(
        AntPathRequestMatcher("/favicon.ico"),
        AntPathRequestMatcher("/error")
    )

    private val excludedRoutesForAuthTokenFiltering = arrayOf(
        *excludedRoutesFromSpringSecurity,
        AntPathRequestMatcher("/api/v2/auth/**"),
    )

    @Bean
    fun authenticationJwtTokenFilter(): AuthTokenFilter {
        return when (isProductionProfile()) {
            true -> AuthTokenFilter(excludedRoutesForAuthTokenFiltering.toList())
            false -> AuthTokenFilter(
                listOf(
                    *excludedRoutesForAuthTokenFiltering,
                    AntPathRequestMatcher("/h2-console/**")
                )
            )
        }
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
            when (isProductionProfile()) {
                true -> web.ignoring().requestMatchers(
                    *excludedRoutesFromSpringSecurity
                )

                false -> web.ignoring().requestMatchers(
                    *excludedRoutesFromSpringSecurity,
                    AntPathRequestMatcher("/h2-console/**"),
                )
            }
        }
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        if (!isProductionProfile()) enableH2Console(http)

        http.csrf { csrf -> csrf.disable() }
            .exceptionHandling { exception -> exception.authenticationEntryPoint(unauthorizedHandler) }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.POST, "/api/v2/auth/**").permitAll()
                    .requestMatchers(HttpMethod.PATCH, "/api/v2/users/{userId:[\\d]+}/activate").hasAuthority("admin")
                    .requestMatchers(HttpMethod.PATCH, "/api/v2/users/{userId:[\\d]+}/balance").hasAuthority("admin")
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
