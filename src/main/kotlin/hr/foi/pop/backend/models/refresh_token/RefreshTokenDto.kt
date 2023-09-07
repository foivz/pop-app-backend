package hr.foi.pop.backend.models.refresh_token

data class RefreshTokenDto(
    val token: String,
    val validFor: TokenValidityInformation
)

data class TokenValidityInformation(
    val timeAmount: Long,
    val timeUnit: String,
)
