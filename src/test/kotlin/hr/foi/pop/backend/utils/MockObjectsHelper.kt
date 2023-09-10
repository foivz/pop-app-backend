package hr.foi.pop.backend.utils

import hr.foi.pop.backend.request_bodies.RegisterRequestBody

class MockObjectsHelper {
    companion object {
        fun getMockRegisterRequestBody(
            username: String,
            email: String,
            password: String = "test123",
        ) = RegisterRequestBody(
            "Tester",
            "Testermann",
            username,
            email,
            password,
            "buyer"
        )
    }
}
