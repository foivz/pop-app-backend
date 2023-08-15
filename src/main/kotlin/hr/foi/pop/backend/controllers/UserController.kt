package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.user.UserMapper
import hr.foi.pop.backend.request_bodies.LoginRequestBody
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.SuccessResponse
import hr.foi.pop.backend.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2/auth")
class UserController {
    @Autowired
    lateinit var userService: UserService

    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterRequestBody): ResponseEntity<SuccessResponse> {
        val savedUser = userService.registerUser(request)
        val userDTO = UserMapper().mapDto(savedUser)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            SuccessResponse("User \"${savedUser.username}\" registered with ID ${savedUser.id}.", userDTO)
        )
    }

    @PostMapping("login")
    fun loginUser(@RequestBody request: LoginRequestBody): ResponseEntity<SuccessResponse> {
        val jwt = userService.authenticateAndGenerateJWT(request.username, request.password)
        val userObject = userService.loadUserByUsername(request.username)

        return ResponseEntity.status(HttpStatus.OK).body(
            SuccessResponse("User \"${userObject.username}\" logged in.", userObject, jwt)
        )
    }

    @ExceptionHandler(UserCheckException::class)
    fun handleBadRegistrationRequestBody(ex: UserCheckException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Could not register user!", ex.error))
    }

}
