package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ActivateUserDefinitions
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.ActivateUserException
import hr.foi.pop.backend.exceptions.UserCheckException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserMapper
import hr.foi.pop.backend.request_bodies.ActivateUserRequestBody
import hr.foi.pop.backend.request_bodies.RegisterRequestBody
import hr.foi.pop.backend.response_bodies.ActivateUserResponseBody
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.SuccessResponse
import hr.foi.pop.backend.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
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

    @PatchMapping("/{userId}/activate")
    fun activateUser(
        @PathVariable userId: String,
        @RequestBody request: ActivateUserRequestBody
    ): ResponseEntity<*> {
        return try {
            request.activated!!

            val activateOrDeactivate: ActivateUserDefinitions = ActivateUserDefinitions.getEnumByCode(request.activated)

            val user: User = userService.activateOrDeactivateUser(userId, activateOrDeactivate)

            ResponseEntity.status(HttpStatus.OK).body(
                SuccessResponse(
                    "User '${user.username}' '${activateOrDeactivate.label}'",
                    ActivateUserResponseBody.mapResponse(user)
                )
            )

        } catch (exception: NullPointerException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse("Request body is not in correct format", ApplicationErrorType.ERR_BAD_BODY)
            )
        } catch (exception: ActivateUserException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse(exception.message, exception.getApplicationErrorType())
            )
        }
    }

    @ExceptionHandler(UserCheckException::class)
    fun handleBadRegistrationRequestBody(ex: UserCheckException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Could not register user!", ex.error))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJacksonConverterBadRequestBody(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Invalid request body. Please check request body.", ApplicationErrorType.ERR_BAD_BODY))
    }

}
