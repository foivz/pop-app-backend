package hr.foi.pop.backend.utils

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest
class PasswordHasherTest {
    @Test
    fun givenEncoderReady_whenStringEncoded_ThenExpectBCryptFormat() {
        val encodedValue1 = encoder.encode("Secret string 1")
        assertValueInBCryptFormat(encodedValue1)

        val encodedValue2 = encoder.encode("Secret string 2")
        assertValueInBCryptFormat(encodedValue2)
    }

    private fun assertValueInBCryptFormat(encoded: String) {
        bcryptRegex.matches(encoded)
    }


    @Test
    fun givenEncodedValue_whenDecoded_SameAsOriginal() {
        val secretString1 = "Secret string 1"
        val encodedValue1 = encoder.encode(secretString1)
        assert(encoder.matches(secretString1, encodedValue1))

        val secretString2 = "Secret string 2"
        val encodedValue2 = encoder.encode(secretString2)
        assert(encoder.matches(secretString2, encodedValue2))
    }

    companion object {
        lateinit var encoder: BCryptPasswordEncoder
        val bcryptRegex = Regex("\\$2a\\$10\$[./A-Za-z0-9]{22}\\\$[./A-Za-z0-9]{31}\$")

        @JvmStatic
        @BeforeAll
        fun initializeBCryptPassword() {
            assertDoesNotThrow("You're no longer using Spring Boot's BCryptPasswordEncoder. Check and fix!") {
                encoder = encoder() as BCryptPasswordEncoder
            }
        }
    }
}
