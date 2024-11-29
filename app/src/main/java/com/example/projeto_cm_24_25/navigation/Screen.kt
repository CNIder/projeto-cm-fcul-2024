package com.example.projeto_cm_24_25.navigation

sealed class Screen(val route: String) {
    object Login: Screen(route = "login_screen")
    object Home: Screen(route = "home_screen")
    object BlogForm: Screen(route = "blog_form_screen")
    object MapForm: Screen(route = "map_form_screen")
}