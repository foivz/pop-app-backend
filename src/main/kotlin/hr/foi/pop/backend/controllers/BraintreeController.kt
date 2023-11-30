package hr.foi.pop.backend.controllers

import com.braintreegateway.BraintreeGateway
import com.braintreegateway.Environment
import com.braintreegateway.TransactionRequest
import hr.foi.pop.backend.definitions.ApplicationErrorType
import hr.foi.pop.backend.request_bodies.BraintreeAmountRequestBody
import hr.foi.pop.backend.responses.ErrorResponse
import hr.foi.pop.backend.responses.Response
import hr.foi.pop.backend.responses.SuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.math.BigDecimal


@Controller
@RequestMapping("/api/v2/braintree")
class BraintreeController {
    val gateway: BraintreeGateway = BraintreeGateway(
        Environment.SANDBOX,
        "qvb7z2b79whqxrkr",
        "s7q2md5k4knysvjc",
        "079653c5579da04ed5231765279495d9"
    )

    @PostMapping("client-token")
    fun createBraintreeClientToken(@RequestBody body: BraintreeAmountRequestBody): ResponseEntity<Response> {
        val request = TransactionRequest()
            .amount(BigDecimal(body.amount))
            .paymentMethodNonce(body.nonceFromTheClient)
            .options()
            .submitForSettlement(true)
            .done()

        val result = gateway.transaction().sale(request)

        return if (result.isSuccess) {
            val transaction = result.target
            ResponseEntity.status(HttpStatus.CREATED).body(
                SuccessResponse("Transaction created.", transaction)
            )
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse("Transaction could not be created.", ApplicationErrorType.ERR_BAD_BODY)
            )
        }
    }
}
