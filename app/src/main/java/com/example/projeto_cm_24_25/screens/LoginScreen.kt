package com.example.projeto_cm_24_25.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.R
import com.example.projeto_cm_24_25.data.UserViewModel
import com.example.projeto_cm_24_25.navigation.Screen


@Composable
fun LoginScreen(navController: NavHostController, viewModel: UserViewModel) {
    val name by viewModel.userName.observeAsState(initial = "")
    Box(
        modifier = Modifier.fillMaxSize()
    )
    {
        // background image
        Image(
            painter = painterResource(R.drawable.login_background),
            contentDescription = "background image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize().align(Alignment.Center)
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(44.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            // textfield for user name input
            TextField(
                value = name,
                onValueChange = { newValue -> viewModel.onNameUpdate(newValue) },
                label = { Text("Type your name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Black,
                )
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { navController.navigate(Screen.Home.route) },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.07f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(text = "Register")
            }
        }
    }
}