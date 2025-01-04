package com.example.projeto_cm_24_25.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.ui.theme.primaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogReaderScreen(navController: NavHostController, blog: Blog) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ){
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "\uD83D\uDCD6 ${blog.title.uppercase()} \uD83D\uDCD6",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 19.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = primaryColor,
                navigationIconContentColor = Color.White
            ),
            navigationIcon = {
                IconButton(onClick = {
                    // Voltar para o ecra da lista dos blogs
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "go back to blog list screen"
                    )
                }
            }
        )
        Column(
            modifier = Modifier.padding(16.dp)
        ){
            // Autor do blog
            Text(
                text = "\uD83D\uDE05 ${blog.author} posted ",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(5.dp))

            // Conteudo do blog
            Text(
                text = blog.content,
                modifier = Modifier.fillMaxWidth().border(2.dp, Color.Red).padding(9.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 20.sp
            )
        }
    }
}