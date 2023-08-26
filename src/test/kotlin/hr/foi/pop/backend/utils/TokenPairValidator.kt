package hr.foi.pop.backend.utils

import hr.foi.pop.backend.security.jwt.TokenPair
import org.junit.jupiter.api.Assertions
import java.util.*

class TokenPairValidator {
    companion object {
        fun assertTokenPairValid(pair: TokenPair) {
            assertJwtLooksFine(pair.accessToken)
            assertRefreshTokenLooksFine(pair.refreshToken.token)
        }

        private fun assertJwtLooksFine(jwt: String) {
            Assertions.assertTrue(jwt.length > 10)
            val jwtParts = jwt.split('.')
            Assertions.assertEquals(3, jwtParts.size, "JWT doesn't have 3 parts!")
            Assertions.assertEquals(20, jwtParts[0].length, "JWT's first part incorrect in size!")
            Assertions.assertFalse(jwtParts[1].length <= 10, "JWT's second part too short!")
            Assertions.assertFalse(jwtParts[2].length <= 20, "JWT's third part too short!")
        }

        private fun assertRefreshTokenLooksFine(refreshToken: String) {
            Assertions.assertEquals(64, refreshToken.length)

            val base64Decoder = Base64.getDecoder()
            Assertions.assertDoesNotThrow { base64Decoder.decode(refreshToken.toByteArray()) }
        }
    }
}
