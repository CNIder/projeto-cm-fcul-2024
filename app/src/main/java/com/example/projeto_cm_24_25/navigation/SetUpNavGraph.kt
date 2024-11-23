package com.example.projeto_cm_24_25.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projeto_cm_24_25.data.UserViewModel
import com.example.projeto_cm_24_25.screens.HomeScreen
import com.example.projeto_cm_24_25.screens.LoginScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val viewModel : UserViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController, viewModel)
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController)
        }
    }
}
