package hr.foi.pop.backend.security.jwt

import hr.foi.pop.backend.models.refresh_token.RefreshToken
import hr.foi.pop.backend.models.refresh_token.RefreshTokenMapper

data class TokenPair(
    val accessToken: String,
    private val refreshTokenEntity: RefreshToken
) {
    val refreshToken = RefreshTokenMapper().mapDto(refreshTokenEntity)
}
