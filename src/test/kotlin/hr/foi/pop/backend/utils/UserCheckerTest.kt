package hr.foi.pop.backend.utils

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.repositories.UserRepository
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

private val templateRequestBodyForTesting =
    MockObjectsHelper.getMockRegisterRequestBody("userchecker-tester", "test@userchecker.com")

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserCheckerTest(@Autowired userRepository: UserRepository) :
    UserChecker(templateRequestBodyForTesting, userRepository) {

    val actualUserFromDatabase: User by lazy {
        super.userRepository.save(MockEntitiesHelper.generateUserEntityWithStore())
    }

    @BeforeAll
    fun ifMockUserAlreadyPersisted_deleteTheUser() {
        val user = userRepository.getUserByUsername(templateRequestBodyForTesting.username)
        if (user != null) {
            userRepository.delete(user)
        }
    }

    @BeforeEach
    fun ifUserOk_WhenChecked_NothingHappens() {
        super.user = templateRequestBodyForTesting
        assertDoesNotThrow { super.validateUserProperties() }
    }

    @Test
    fun ifUserHasBadFirstName_WhenChecked_Throws() {
        val firstNameOf1Char = "a"
        super.user = super.user.copy(firstName = firstNameOf1Char)

        val thrownException = assertThrows<UserCheckException> { super.validateFirstName() }
        assertExceptionIndicatesFirstNameInvalid(thrownException)
    }

    private fun assertExceptionIndicatesFirstNameInvalid(ex: UserCheckException) {
        assertExceptionErrorType(ex, ApplicationErrorType.ERR_FIRSTNAME_INVALID)
    }

    @Test
    fun ifUserHasBadLastName_WhenChecked_Throws() {
        val lastNameOf1Char = "b"
        super.user = super.user.copy(lastName = lastNameOf1Char)

        val thrownException = assertThrows<UserCheckException> { super.validateLastName() }
        assertExceptionIndicatesLastNameInvalid(thrownException)
    }

    private fun assertExceptionIndicatesLastNameInvalid(ex: UserCheckException) {
        assertExceptionErrorType(ex, ApplicationErrorType.ERR_LASTNAME_INVALID)
    }

    @Test
    fun ifUserHasBadUsername_WhenChecked_Throws() {
        val usernameSmallerThan4Chars = "bad"
        super.user = super.user.copy(username = usernameSmallerThan4Chars)

        val thrownException = assertThrows<UserCheckException> { super.validateUsername() }
        assertExceptionIndicatesUsernameInvalid(thrownException)
    }

    @Test
    fun ifUsernameMixedWithNumbers_WhenChecked_NothingHappens() {
        val correctUsernameStartsWithNumbers = "123_its_ok"
        super.user = super.user.copy(username = correctUsernameStartsWithNumbers)

        assertDoesNotThrow { super.validateUsername() }

        val correctUsernameEndsWithNumbers = "its_ok_123"
        super.user = super.user.copy(username = correctUsernameEndsWithNumbers)

        assertDoesNotThrow { super.validateUsername() }

        val correctUsernameContainsNumbers = "its_123_ok"
        super.user = super.user.copy(username = correctUsernameContainsNumbers)

        assertDoesNotThrow { super.validateUsername() }
    }

    @Test
    fun ifUsernameWithoutChars_WhenChecked_Throws() {
        val invalidUsernameOnlyNumbers = "134580"
        super.user = super.user.copy(username = invalidUsernameOnlyNumbers)

        val thrownExceptionOnlyNumbers = assertThrows<UserCheckException> { super.validateUsername() }
        assertExceptionIndicatesUsernameInvalid(thrownExceptionOnlyNumbers)

        val invalidUsernameNoChars = "?!$:___!123"
        super.user = super.user.copy(username = invalidUsernameNoChars)

        val thrownExceptionNoChars = assertThrows<UserCheckException> { super.validateUsername() }
        assertExceptionIndicatesUsernameInvalid(thrownExceptionNoChars)
    }

    private fun assertExceptionIndicatesUsernameInvalid(ex: UserCheckException) {
        assertExceptionErrorType(ex, ApplicationErrorType.ERR_USERNAME_INVALID)
    }

    @Test
    fun ifUserHasUsernameAlreadyInUse_WhenChecked_Throws() {
        val mockUsernameInUse = actualUserFromDatabase.username
        super.user = super.user.copy(username = mockUsernameInUse)

        val thrownException = assertThrows<UserCheckException> { super.validateUsername() }
        assertExceptionIndicatesUsernameInUse(thrownException)
    }

    private fun assertExceptionIndicatesUsernameInUse(ex: UserCheckException) {
        assertExceptionErrorType(ex, ApplicationErrorType.ERR_USERNAME_USED)
    }

    @Test
    fun ifUserPasswordTooShort_WhenChecked_Throws() {
        val badPassword = "bad"
        super.user = super.user.copy(password = badPassword)

        val thrownException = assertThrows<UserCheckException> { super.validatePassword() }
        assertExceptionIndicatesPasswordInvalid(thrownException)
    }

    @Test
    fun ifUserPasswordWithoutNumericDigits_WhenChecked_Throws() {
        val badPassword = "longpasswordwithoutnumbers"
        super.user = super.user.copy(password = badPassword)

        val thrownException = assertThrows<UserCheckException> { super.validatePassword() }
        assertExceptionIndicatesPasswordInvalid(thrownException)
    }

    private fun assertExceptionIndicatesPasswordInvalid(ex: UserCheckException) {
        assertExceptionErrorType(ex, ApplicationErrorType.ERR_PASSWORD_INVALID)
    }

    @Test
    fun ifEmailInvalid_WhenChecked_Throws() {
        val invalidEmail = "longpasswordwithoutnumbers"
        super.user = super.user.copy(email = invalidEmail)

        val thrownException = assertThrows<UserCheckException> { super.validateEmail() }
        assertExceptionIndicatesEmailInvalid(thrownException)
    }

    @Test
    fun ifEmailUsed_WhenChecked_Throws() {
        val usedEmail = actualUserFromDatabase.email
        super.user = super.user.copy(email = usedEmail)

        val thrownException = assertThrows<UserCheckException> { super.validateEmail() }
        assertExceptionIndicatesEmailInUse(thrownException)
    }

    private fun assertExceptionIndicatesEmailInvalid(ex: UserCheckException) {
        assertExceptionErrorType(ex, ApplicationErrorType.ERR_EMAIL_INVALID)
    }

    private fun assertExceptionIndicatesEmailInUse(ex: UserCheckException) {
        assertExceptionErrorType(ex, ApplicationErrorType.ERR_EMAIL_USED)
    }

    @Test
    fun ifRoleInvalid_WhenChecked_Throws() {
        val nonExistingRole = "bad_role"
        super.user = super.user.copy(role = nonExistingRole)

        val thrownException = assertThrows<UserCheckException> { super.validateRole() }
        assertExceptionIndicatesRoleInvalid(thrownException)
    }

    private fun assertExceptionIndicatesRoleInvalid(ex: UserCheckException) {
        assertExceptionErrorType(ex, ApplicationErrorType.ERR_ROLE_INVALID)
    }

    private fun assertExceptionErrorType(ex: UserCheckException, errType: ApplicationErrorType) {
        assert(ex.error == errType)
    }
}
