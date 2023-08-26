package hr.foi.pop.backend.security.jwt

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
