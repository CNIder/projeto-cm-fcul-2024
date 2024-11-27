package com.example.projeto_cm_24_25.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.data.UserViewModel
import com.example.projeto_cm_24_25.navigation.Screen


@Composable
fun LoginScreen(navController: NavHostController, viewModel: UserViewModel) {
    val name by viewModel.userName.observeAsState(initial = "")
    // state to hold the text of textfield
    val nameState = remember { mutableStateOf(TextFieldValue("")) }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        // textfield for user name input
        TextField(
            value = name,
            onValueChange = { newValue -> viewModel.onNameUpdate(newValue) },
            label = { Text("Type your name") },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Yellow,
                focusedPlaceholderColor = Color.Yellow
            )
        )
        Text(
            text = "Hello $name !!",
            color = Color.Green,
            fontSize = 20.sp
        )
        Button(
            onClick = {navController.navigate(Screen.Home.route)}
        ) {
         Text(text = "Register")
        }
    }
}