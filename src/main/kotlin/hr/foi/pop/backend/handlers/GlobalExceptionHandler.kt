package hr.foi.pop.backend.handlers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.responses.ErrorResponse
import org.springframework.boot.json.JsonParseException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(JsonParseException::class)
    fun handleBadRequestBody(): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Request body JSON could not be parsed!", ApplicationErrorType.ERR_BAD_BODY))
    }
}
