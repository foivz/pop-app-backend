package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.models.products.Product
import hr.foi.pop.backend.models.products.ProductDTO
import hr.foi.pop.backend.models.products.ProductMapper
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
        val mappedProducts = getProductsDTOs(receivedProducts)

        val responseMessage: String

        if (mappedProducts.size > 0) {
            val storeName = mappedProducts[0].store.storeName
            responseMessage = "Fetched products from store \"$storeName\"."
        } else {
            responseMessage = "Your store has no products!"
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body(
                SuccessResponse(
                    responseMessage,
                    mappedProducts
                )
            )
    }

    private fun getProductsDTOs(receivedProducts: List<Product>): List<ProductDTO> {
        val productMapper = ProductMapper()
        val mappedProducts = receivedProducts.map {
            productMapper.mapDto(it)
        }
        return mappedProducts
    }
}
