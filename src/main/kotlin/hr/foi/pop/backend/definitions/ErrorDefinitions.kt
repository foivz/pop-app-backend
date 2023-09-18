package hr.foi.pop.backend.definitions

enum class ApplicationErrorType(val code: Int) {
    ERR_JWT_EXPIRED(1),
    ERR_JWT_INVALID(2),
    ERR_BAD_BODY(3),
    ERR_ROUTE_NOT_ACCESSIBLE(4),
    ERR_REFRESH_TOKEN_EXPIRED(5),
    ERR_REFRESH_TOKEN_INVALID(6),
    ERR_USERNAME_INVALID(101),
    ERR_USERNAME_USED(102),
    ERR_EMAIL_INVALID(103),
    ERR_EMAIL_USED(104),
    ERR_PASSWORD_INVALID(105),
    ERR_ROLE_INVALID(106),
    ERR_FIRSTNAME_INVALID(107),
    ERR_LASTNAME_INVALID(108),
    ERR_USER_INVALID(201),
    ERR_ALREADY_ACTIVATED(202),
    ERR_ALREADY_DEACTIVATED(203),
    ERR_NOT_ACTIVATED(303),
    WARN_STORE_NOT_SET(304),
    ERR_STORE_NAME_INVALID(401),
    ERR_SELLER_ALREADY_HAS_STORE(402),
    ERR_ROLE_NOT_APPLICABLE(403),
    ERR_AMOUNT_TOO_LARGE(801),
    ERR_AMOUNT_NEGATIVE(802),
}
