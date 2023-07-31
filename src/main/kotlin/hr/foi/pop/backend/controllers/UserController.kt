package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.responses.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/users")
class UserController {

    @PostMapping
    fun registerUser(): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    false,
                    "User not in correct format!",
                    ApplicationErrorType.ERR_BAD_REQUEST
                )
            )
    }

}
