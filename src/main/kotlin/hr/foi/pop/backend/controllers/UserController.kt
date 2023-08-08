package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.user.UserMapper
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.SuccessResponse
import hr.foi.pop.backend.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2/users")
class UserController {
    @Autowired
    lateinit var userService: UserService

    @PostMapping
    fun registerUser(@RequestBody request: RegisterRequestBody?): ResponseEntity<*> {

        return try {

            val requestBody = request!!
            val savedUser = userService.registerUser(requestBody)
            val userDTO = UserMapper().mapDto(savedUser)

            ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse("User \"${savedUser.username}\" registered with ID ${savedUser.id}.", userDTO)
            )

        } catch (ex: NullPointerException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse("User not in correct format!", ApplicationErrorType.ERR_BAD_BODY))
        }

    }

    @ExceptionHandler(UserCheckException::class)
    fun handleBadRegistrationRequestBody(ex: UserCheckException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Could not register user!", ex.error))
    }

}
