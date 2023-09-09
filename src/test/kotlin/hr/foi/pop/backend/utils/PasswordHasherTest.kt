package hr.foi.pop.backend.utils

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class PasswordHasherTest {
    val bcryptRegex = Regex("\\$2a\\$10\$[./A-Za-z0-9]{22}\\\$[./A-Za-z0-9]{31}\$")

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun givenEncoderReady_whenStringEncoded_thenExpectBCryptFormat() {
        val encodedValue1 = passwordEncoder.encode("Secret string 1")
        assertValueInBCryptFormat(encodedValue1)

        val encodedValue2 = passwordEncoder.encode("Secret string 2")
        assertValueInBCryptFormat(encodedValue2)
    }

    private fun assertValueInBCryptFormat(encoded: String) {
        bcryptRegex.matches(encoded)
    }


    @Test
    fun givenEncodedValue_whenDecoded_sameAsOriginal() {
        val secretString1 = "Secret string 1"
        val encodedValue1 = passwordEncoder.encode(secretString1)
        assert(passwordEncoder.matches(secretString1, encodedValue1))

        val secretString2 = "Secret string 2"
        val encodedValue2 = passwordEncoder.encode(secretString2)
        assert(passwordEncoder.matches(secretString2, encodedValue2))
    }
}
