package hr.foi.pop.backend.repositories

import hr.foi.pop.backend.models.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun getUserByUsername(username: String): User?
}
