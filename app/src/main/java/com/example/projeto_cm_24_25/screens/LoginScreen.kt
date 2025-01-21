package com.example.projeto_cm_24_25.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.R
import com.example.projeto_cm_24_25.data.UserViewModel
import com.example.projeto_cm_24_25.data.repository.DataStoreRepository
import com.example.projeto_cm_24_25.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, viewModel: UserViewModel) {
    val name by viewModel.userName.observeAsState(initial = "")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = DataStoreRepository(context)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.login_2),
            contentDescription = "background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 30.dp),
                text = "SURVIVOR APP",
                color = Color.White,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                modifier = Modifier,
                text = "Stay Alive. Stay Ahead",
                color = Color.Red,
                style = TextStyle(
                    fontSize = 18.sp,
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp)
                    .background(Color.Black.copy(alpha = 0.9f), shape = RoundedCornerShape(16.dp))
                    .padding(15.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = "Username",
                        color = Color.White
                    )
                    TextField(
                        value = name,
                        onValueChange = { newValue -> viewModel.onNameUpdate(newValue) },
                        label = {
                            Text(
                                "Type your name",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, Color.White, RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedPlaceholderColor = Color.White,
                            focusedContainerColor = Color.Black,
                            unfocusedContainerColor = Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            // Validar se o campo nao esta vazio
                            if(name.isEmpty()) {
                                Toast.makeText(context, "Enter your name!", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            // Guardar o nome no DataStore
                            scope.launch {
                                dataStore.saveUserName(name.lowercase())
                            }
                            navController.navigate(Screen.Home.route)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(238, 31, 39),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Register",
                            style = TextStyle( fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(R.drawable.bio_hazzard),
                contentDescription = "Biohazard",
                modifier = Modifier
                    .size(100.dp)
            )
        }
    }
}