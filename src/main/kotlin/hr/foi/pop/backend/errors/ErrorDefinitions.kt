package hr.foi.pop.backend.errors

enum class ApplicationErrorType(val code: Int) {
    ERR_JWT_EXPIRED(1),
    ERR_JWT_INVALID(2),
    ERR_BAD_REQUEST(3),
    ERR_USERNAME_INVALID(101),
    ERR_USERNAME_USED(102),
    ERR_EMAIL_INVALID(103),
    ERR_EMAIL_USED(104),
    ERR_PASSWORD_INVALID(105),
    ERR_ROLE_INVALID(106),
}
