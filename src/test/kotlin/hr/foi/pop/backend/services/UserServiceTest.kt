package hr.foi.pop.backend.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest : UserService() {
    @Autowired
    lateinit var userService: UserService

}
