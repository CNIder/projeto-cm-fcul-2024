package com.example.projeto_cm_24_25.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projeto_cm_24_25.R
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.data.repository.DataStoreRepository



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogReaderScreen(navController: NavHostController, blog: Blog) {

    val context = LocalContext.current

    val dataStore = DataStoreRepository(context)
    val userName = dataStore.getUserName.collectAsState(initial = "").value


    Box(
        modifier = Modifier.fillMaxSize()
    )    {

        Image(
            painter = painterResource(R.drawable.login_3),
            contentDescription = "background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = 20.dp)
        ){
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(25.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,


                        ) {


                        Text(
                            text = "Blog",
                            color = Color.White,
                            modifier = Modifier.weight(1f), // Centraliza o texto
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp
                        )

                        // Botão no lado direito
                        ExtendedFloatingActionButton(
                            onClick = { },
                            modifier = Modifier.size(50.dp), // Tamanho ajustado
                            containerColor = Color(238, 31, 39)
                        ) {
                            Text(
                                text = userName?.firstOrNull()?.uppercase()?.toString() ?: "?",
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Voltar para o ecra da lista dos blogs
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back to map screen"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )


            Column(
                modifier = Modifier.padding(16.dp)
            ){
                // Autor do blog
                Text(
                    text = " ${blog.title} posted ",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    color = Color(80,80,80),
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
                Spacer(modifier = Modifier.height(15.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(70, 62, 62, 200), RoundedCornerShape(13.dp))
                        .padding(9.dp)
                ) {
                    Text(
                        text = blog.content,
                        color = Color.White,
                        fontSize = 17.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Posted by: ${blog.author}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = blog.publishedDate,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal
                        )
                    )

                }
            }
        }
    }
}