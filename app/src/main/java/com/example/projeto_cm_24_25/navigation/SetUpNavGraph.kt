package com.example.projeto_cm_24_25.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projeto_cm_24_25.data.BlogViewModel
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.UserViewModel
import com.example.projeto_cm_24_25.screens.BlogFormScreen
import com.example.projeto_cm_24_25.screens.HomeScreen
import com.example.projeto_cm_24_25.screens.LoginScreen
import com.example.projeto_cm_24_25.screens.MapForm

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    val viewModel : UserViewModel = viewModel()
    val blogViewModel : BlogViewModel = viewModel()
    val mapViewModel : MapViewModel = viewModel()
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
        composable(route = Screen.BlogForm.route) {
            BlogFormScreen(blogViewModel, navController)
        }
        composable(route = Screen.MapForm.route) {
            MapForm(navController, mapViewModel)
        }
    }
}
