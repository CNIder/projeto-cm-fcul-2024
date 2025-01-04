package com.example.projeto_cm_24_25.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.projeto_cm_24_25.data.BlogViewModel
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.UserViewModel
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.data.repository.DataStoreRepository
import com.example.projeto_cm_24_25.screens.BlogFormScreen
import com.example.projeto_cm_24_25.screens.BlogReaderScreen
import com.example.projeto_cm_24_25.screens.HomeScreen
import com.example.projeto_cm_24_25.screens.LoginScreen
import com.example.projeto_cm_24_25.screens.MapForm

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    val viewModel : UserViewModel = viewModel()
    val blogViewModel : BlogViewModel = viewModel()
    val mapViewModel : MapViewModel = viewModel()

    val dataStore = DataStoreRepository(LocalContext.current)
    val userName = dataStore.getUserName.collectAsState(initial = "")
    var startDestination: String = Screen.Login.route

    if(userName.value != null) {
        if(userName.value!!.isNotEmpty()) {
            startDestination = Screen.Home.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
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
        composable(
            route = Screen.BlogReader.route,
            arguments = listOf(
                navArgument("author") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("content") { type = NavType.StringType },
            )
            ) {
            val author = it.arguments?.getString("author") ?: ""
            val title = it.arguments?.getString("title") ?: ""
            val content = it.arguments?.getString("content") ?: ""
            val blog = Blog(author, title, content)
            BlogReaderScreen(navController, blog)
        }
    }
}
