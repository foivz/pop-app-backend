package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.*
import hr.foi.pop.backend.models.user.User
import hr.foi.pop.backend.models.user.UserDTO
import hr.foi.pop.backend.models.user.UserMapper
import hr.foi.pop.backend.request_bodies.ActivateUserRequestBody
import hr.foi.pop.backend.request_bodies.ChangeRoleRequestBody
import hr.foi.pop.backend.request_bodies.AssignStoreRequestBody
import hr.foi.pop.backend.request_bodies.SetUserBalanceRequestBody
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
        val newStatus = request.isAccepted!!
        val parsedUserId = Integer.parseInt(userId)

        return if (newStatus) {
            val user: User = userService.activateUser(parsedUserId)
            getOkResponse("User '${user.username}' activated.", user)
        } else {
            val user: User = userService.deactivateUser(parsedUserId)
            getOkResponse("User '${user.username}' deactivated.", user)
        }
    }

    @PatchMapping("/{userId}/store")
    fun assignStore(
        @PathVariable userId: String,
        @RequestBody request: AssignStoreRequestBody
    ): ResponseEntity<SuccessResponse> {
        val receivedStoreName = request.storeName!!
        val parsedUserId = Integer.parseInt(userId)

        val user: User = userService.assignStore(parsedUserId, receivedStoreName)
        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse("User \"${user.username}\" assigned to store \"${user.store!!.storeName}\"."))
    }

    private fun getOkResponse(responseMessage: String, user: User): ResponseEntity<SuccessResponse> {
        val userDTO: UserDTO = UserMapper().mapDto(user)
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse(responseMessage, userDTO))
    }
    
    @PatchMapping("/{userId}/balance")
    fun activateUser(
        @PathVariable userId: String,
        @RequestBody request: SetUserBalanceRequestBody
    ): ResponseEntity<SuccessResponse> {
        val newBalance = request.amount!!
        val parsedUserId = Integer.parseInt(userId)

        val user: User = userService.setBalance(parsedUserId, newBalance)
        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse("New balance set for buyer \"${user.username}\": ${user.balance / 100f}"))
    }

    @PatchMapping("/{userId}/role")
    fun changeRole(
        @PathVariable userId: String,
        @RequestBody request: ChangeRoleRequestBody
    ): ResponseEntity<SuccessResponse> {
        val newRole = request.role!!
        val parsedUserId = Integer.parseInt(userId)

        val user = userService.changeRole(parsedUserId, newRole)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse("User \"${user.username}\" switched to the new role: \"${user.role.name}\"."))
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

    @ExceptionHandler(UserNotAcceptedException::class)
    fun handleUserNotAcceptedException(ex: UserNotAcceptedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(ex.message ?: "User not accepted!", ApplicationErrorType.ERR_NOT_ACTIVATED)
        )
    }

    @ExceptionHandler(NotAuthorizedException::class)
    fun handleNotAuthorizedException(ex: NotAuthorizedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(
                ex.message ?: "You lack permission for this action!",
                ApplicationErrorType.ERR_AUTHORIZATION_NOT_SUFFICIENT
            )
        )
    }

    @ExceptionHandler(BadRoleException::class)
    fun handleBadRoleException(ex: BadRoleException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(ex.message ?: "You lack permission for this action!", ex.error)
        )
    }

    @ExceptionHandler(StoreNotFoundException::class)
    fun handleStoreNotFoundException(ex: StoreNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(ex.message, ApplicationErrorType.ERR_STORE_NOT_AVAILABLE)
        )
    }

    @ExceptionHandler(UserHasStoreException::class)
    fun handleUserHasStoreException(ex: UserHasStoreException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(ex.message, ApplicationErrorType.ERR_BUYER_ALREADY_HAS_STORE)
        )
    }

    @ExceptionHandler(BadAmountException::class)
    fun handleBadAmountException(ex: BadAmountException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Balance could not be changed.", ex.error))
    }
}
