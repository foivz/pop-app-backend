package hr.foi.pop.backend.services

import hr.foi.pop.backend.models.user.UserDTO
import hr.foi.pop.backend.repositories.UserRepository
import hr.foi.pop.backend.utils.UserChecker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {
    @Autowired
    lateinit var userRepository: UserRepository

    protected fun validateUser(user: UserDTO) {
        UserChecker(user, userRepository).validateUserProperties()
    }


}
