package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.ChangeUserStatusException
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserDTO
import hr.foi.pop.backend.models.user.UserMapper
import hr.foi.pop.backend.request_bodies.ActivateUserRequestBody
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.SuccessResponse
import hr.foi.pop.backend.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2/users")
class UserController {
    @Autowired
    lateinit var userService: UserService

    @PatchMapping("/{userId}/activate")
    fun activateUser(
        @PathVariable userId: String,
        @RequestBody request: ActivateUserRequestBody
    ): ResponseEntity<*> {
        val newStatus = request.activated
        val parsedUserId = Integer.parseInt(userId)

        if (newStatus) {
            val user: User = userService.activateUser(parsedUserId)
            val userDTO: UserDTO = UserMapper().mapDto(user)

            return ResponseEntity.status(HttpStatus.OK).body(
                SuccessResponse(
                    "User '${user.username}' activated.",
                    userDTO
                )
            )
        } else {
            val user: User = userService.deactivateUser(parsedUserId)
            val userDTO: UserDTO = UserMapper().mapDto(user)

            return ResponseEntity.status(HttpStatus.OK).body(
                SuccessResponse(
                    "User '${user.username}' deactivated.",
                    userDTO
                )
            )
        }
    }

    @ExceptionHandler(ChangeUserStatusException::class)
    fun handleChangeUserException(ex: ChangeUserStatusException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    ex.message, ex.error
                )
            )
    }

    @ExceptionHandler(NumberFormatException::class)
    fun handleNumberFormatException(ex: NumberFormatException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                "User with provided user id does not exist in application.",
                ApplicationErrorType.ERR_USER_INVALID
            )
        )
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException::class)
    fun handleJpaObjectRetrievalFailureException(ex: JpaObjectRetrievalFailureException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                "User with provided user id does not exist in application.",
                ApplicationErrorType.ERR_USER_INVALID
            )
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJacksonConverterBadRequestBody(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Invalid request body. Please check request body.", ApplicationErrorType.ERR_BAD_BODY))
    }

}
