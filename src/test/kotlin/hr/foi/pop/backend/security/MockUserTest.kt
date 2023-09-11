package hr.foi.pop.backend.security

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest
class MockUserTest {
    @Test
    @WithMockUser(username = "StoreServiceTester", authorities = ["seller"])
    fun givenWithMockUserAnnotation_whenTestRan_principalShouldCheckOutToActualUserObject() {
        val principal = SecurityContextHolder.getContext().authentication.principal as User

        Assertions.assertEquals("StoreServiceTester", principal.username)
        Assertions.assertTrue(principal.authorities.contains(GrantedAuthority {
            "seller"
        }))
    }
}
