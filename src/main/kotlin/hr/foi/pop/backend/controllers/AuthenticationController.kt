package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.UserAuthenticationException
import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.exceptions.UserNotAcceptedException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserLoginResponseDTO
import hr.foi.pop.backend.models.user.UserMapper
import hr.foi.pop.backend.request_bodies.LoginRequestBody
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.SuccessResponse
import hr.foi.pop.backend.responses.WarningResponse
import hr.foi.pop.backend.services.AuthenticationService
import hr.foi.pop.backend.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2/auth")
class AuthenticationController {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterRequestBody): ResponseEntity<SuccessResponse> {
        val savedUser = userService.registerUser(request)
        val userDTO = UserMapper().mapDto(savedUser)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            SuccessResponse("User \"${savedUser.username}\" registered with ID ${savedUser.id}.", userDTO)
        )
    }

    @PostMapping("login")
    fun loginUser(@RequestBody request: LoginRequestBody): ResponseEntity<out SuccessResponse> {
        val jwt = authenticationService.authenticateAndGenerateJWT(request.username, request.password)
        val userEntity = authenticationService.loadUserByUsername(request.username) as User
        val userDto = UserMapper().mapDto(userEntity)
        val loginResponse = UserLoginResponseDTO(userDto)

        val responseBuilder = ResponseEntity.status(HttpStatus.OK)
        val baseMessage = "User \"${loginResponse.username}\" logged in"

        val response = if (loginResponse.store != null) {
            responseBuilder.body(
                SuccessResponse("$baseMessage.", loginResponse, jwt)
            )
        } else {
            responseBuilder.body(
                WarningResponse(
                    ApplicationErrorType.WARN_STORE_NOT_SET,
                    "$baseMessage with warnings.",
                    loginResponse,
                    jwt
                )
            )
        }

        return response
    }

    @ExceptionHandler(UserCheckException::class)
    fun handleBadRegistrationRequestBody(ex: UserCheckException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Could not register user!", ex.error))
    }

    @ExceptionHandler(UserAuthenticationException::class)
    fun handleInvalidUserLoginAttempt(ex: UserAuthenticationException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(ex.message ?: "Authentication error.", ApplicationErrorType.ERR_USER_INVALID))
    }

    @ExceptionHandler(UserNotAcceptedException::class)
    fun handleNonAcceptedUserLoginAttempt(ex: UserNotAcceptedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponse(
                    ex.message ?: "Deactivated user login attempt.",
                    ApplicationErrorType.ERR_NOT_ACTIVATED
                )
            )
    }
}
