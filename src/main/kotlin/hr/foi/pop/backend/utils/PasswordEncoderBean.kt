package hr.foi.pop.backend.utils

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Bean
fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder()
}
