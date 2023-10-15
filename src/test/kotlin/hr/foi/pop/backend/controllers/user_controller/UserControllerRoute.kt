package hr.foi.pop.backend.controllers.user_controller

fun getRouteForUser(userId: Int): String {
    return "/api/v2/users/${userId}"
}
