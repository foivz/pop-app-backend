package hr.foi.pop.backend.security.jwt

data class JwtPair(
    val accessToken: String,
    val refreshToken: String
)
