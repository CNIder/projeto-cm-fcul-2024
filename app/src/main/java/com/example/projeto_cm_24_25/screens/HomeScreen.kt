package com.example.projeto_cm_24_25.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.BlogViewModel
import com.example.projeto_cm_24_25.navigation.NavigationItem

@Composable
fun HomeScreen(navController: NavHostController) {
    // list of all screens in bottom bar
    val navItemList = listOf(
        NavigationItem("Map", Icons.Default.Place),
        //NavigationItem("Users", Icons.Default.Person),
        NavigationItem("Blog", Icons.Default.Menu),
    )
    // selected index variable
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // bottom bar
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed {index, navItem ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index},
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = null)
                        },
                        label = {
                            Text(navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding), selectedTabIndex, navController)
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int, navController: NavHostController) {
    val blogViewModel : BlogViewModel = viewModel()
    val mapViewModel : MapViewModel = viewModel()
    when(selectedIndex) {
        0 -> MapScreen(modifier, mapViewModel)
        1 -> BlogScreen(modifier, blogViewModel, navController)
    }
}