package hr.foi.pop.backend.models.refresh_token

import hr.foi.pop.backend.utils.GenericMapper
import java.time.Duration
import java.time.LocalDateTime

class RefreshTokenMapper : GenericMapper<RefreshTokenDto, RefreshToken> {
    override fun mapDto(e: RefreshToken): RefreshTokenDto {
        val remainingTime = Duration.between(LocalDateTime.now(), e.expirationDate)
        return RefreshTokenDto(
            e.token,
            TokenValidityInformation(remainingTime.toMinutes(), "minutes")
        )
    }

    override fun map(d: RefreshTokenDto): RefreshToken {
        throw NotImplementedError("Cannot convert refresh token DTO to refresh token entity!")
    }
}
