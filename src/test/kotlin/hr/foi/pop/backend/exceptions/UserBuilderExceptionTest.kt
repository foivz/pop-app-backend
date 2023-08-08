package hr.foi.pop.backend.exceptions

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserBuilderExceptionTest {
    @Test
    fun givenExceptionCreatedWithOnePropertyName_WhenExceptionMessageChecked_MakesSense() {
        val mockException = UserBuilderException("username")
        Assertions.assertEquals("User has no username set!", mockException.message)
    }

    @Test
    fun givenExceptionCreatedWithTwoPropertyNames_WhenExceptionMessageChecked_MakesSense() {
        val mockException = UserBuilderException("username", "password")
        Assertions.assertEquals("User has no username, password set!", mockException.message)
    }
}
