package hr.foi.pop.backend.request_bodies

data class BraintreeAmountRequestBody(val amount: String, val nonceFromTheClient: String)
