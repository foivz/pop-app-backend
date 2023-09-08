package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.ChangeUserStatusException
import hr.foi.pop.backend.exceptions.UserNotFoundException
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
        val newStatus = request.activated!!
        val parsedUserId = Integer.parseInt(userId)

        return if (newStatus) {
            val user: User = userService.activateUser(parsedUserId)
            getOkResponse("User '${user.username}' activated.", user)
        } else {
            val user: User = userService.deactivateUser(parsedUserId)
            getOkResponse("User '${user.username}' deactivated.", user)
        }
    }

    private fun getOkResponse(responseMessage: String, user: User): ResponseEntity<SuccessResponse> {
        val userDTO: UserDTO = UserMapper().mapDto(user)
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse(responseMessage, userDTO))
    }

    @ExceptionHandler(ChangeUserStatusException::class)
    fun handleChangeUserException(ex: ChangeUserStatusException) =
        getBadRequestResponse(ex.message, ex.error)


    @ExceptionHandler(NumberFormatException::class)
    fun handleNumberFormatException(ex: NumberFormatException) =
        getBadRequestResponse(
            "User with provided user ID not found.",
            ApplicationErrorType.ERR_USER_INVALID
        )

    private fun getBadRequestResponse(message: String, error: ApplicationErrorType) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(message, error))

    @ExceptionHandler(UserNotFoundException::class)
    fun handleJpaObjectRetrievalFailureException(ex: UserNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(ex.message ?: "User not found", ApplicationErrorType.ERR_USER_INVALID)
        )
    }
}
