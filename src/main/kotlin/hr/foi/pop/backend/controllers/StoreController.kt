package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.exceptions.InvalidStoreNameException
import hr.foi.pop.backend.exceptions.UserHasStoreException
import hr.foi.pop.backend.models.store.StoreMapper
import hr.foi.pop.backend.request_bodies.CreateStoreRequestBody
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.SuccessResponse
import hr.foi.pop.backend.services.StoreService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2/stores")
class StoreController {
    @Autowired
    lateinit var storeService: StoreService

    @PostMapping
    fun createStore(@RequestBody request: CreateStoreRequestBody): ResponseEntity<SuccessResponse> {
        val newStoreName = request.storeName!!

        val newStore = storeService.createStore(newStoreName)
        val newStoreDto = StoreMapper().mapDto(newStore)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SuccessResponse("Store \"${newStore.storeName}\" created with ID ${newStore.id}.", newStoreDto))
    }

    @ExceptionHandler(InvalidStoreNameException::class)
    fun handleInvalidStoreNameException(ex: InvalidStoreNameException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse("Could not create a store with provided name!", ApplicationErrorType.ERR_STORE_NAME_INVALID)
        )
    }

    @ExceptionHandler(UserHasStoreException::class)
    fun handleUserHasStoreException(ex: UserHasStoreException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse("User already has a store!", ApplicationErrorType.ERR_SELLER_ALREADY_HAS_STORE)
        )
    }
}
