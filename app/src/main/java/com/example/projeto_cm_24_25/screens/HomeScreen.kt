package com.example.projeto_cm_24_25.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.data.MapViewModel
import com.example.projeto_cm_24_25.data.BlogViewModel
import com.example.projeto_cm_24_25.navigation.NavigationItem

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController) {
    // Lista do ecras no Bottom Bar
    val navItemList = listOf(
        NavigationItem("Map", Icons.Default.Place),
        NavigationItem("Blog", Icons.Default.Menu),
    )

    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Bottom bar
        bottomBar = {
            NavigationBar(
                containerColor =Color(38, 38, 38)
            ){
                navItemList.forEachIndexed {index, navItem ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index},
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = null,
                                    modifier = Modifier.size(35.dp))
                        },
                        colors = NavigationBarItemColors(

                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            selectedIndicatorColor = Color.Transparent,
                            disabledIconColor = Color.Unspecified,
                            disabledTextColor = Color.Unspecified,
                            unselectedTextColor = Color.White,
                            unselectedIconColor = Color.White,
                        )
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
        0 -> MapScreen(modifier, mapViewModel, navController)
        1 -> BlogScreen(modifier, blogViewModel, navController)
    }
}