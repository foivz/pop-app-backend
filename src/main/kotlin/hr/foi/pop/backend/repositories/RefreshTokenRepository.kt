package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.refresh_token.RefreshToken
import hr.foi.pop.backend.models.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Int> {
    fun getRefreshTokenByOwner(owner: User): RefreshToken
}
