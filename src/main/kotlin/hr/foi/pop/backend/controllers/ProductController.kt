package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.responses.SuccessResponse
import hr.foi.pop.backend.services.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/products")
class ProductController {

    @Autowired
    lateinit var productService: ProductService


    @GetMapping
    fun getProducts(): ResponseEntity<SuccessResponse> {
        val receivedProducts = productService.getProducts()

        val responseMessage: String

        if (receivedProducts.size > 0) {
            val storeName = receivedProducts[0].store.storeName
            responseMessage = "Fetched products from store \"$storeName\"."
        } else {
            responseMessage = "Your store has no products!"
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body(
                SuccessResponse(
                    responseMessage,
                    receivedProducts
                )
            )
    }
}
