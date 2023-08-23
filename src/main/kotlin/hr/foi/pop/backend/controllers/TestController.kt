package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.responses.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/test")
class TestController {
    @GetMapping("auth")
    fun testAuthentication(): ResponseEntity<SuccessResponse> {
        return ResponseEntity.ok(SuccessResponse("It seems you are correctly logged in with a valid JWT!"))
    }
}
