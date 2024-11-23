package com.example.projeto_cm_24_25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.projeto_cm_24_25.navigation.NavGraph
import com.example.projeto_cm_24_25.ui.theme.Projetocm2425Theme

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Projetocm2425Theme {
                navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}