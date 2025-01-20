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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.projeto_cm_24_25.data.BlogViewModel
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.data.repository.DataStoreRepository
import com.example.projeto_cm_24_25.navigation.Screen
import com.example.projeto_cm_24_25.ui.theme.primaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen(
    modifier: Modifier = Modifier,
    viewModel: BlogViewModel,
    navController: NavHostController
) {
    val blogData = viewModel.blogData.observeAsState(emptyList())

    val context = LocalContext.current


    val dataStore = DataStoreRepository(context)
    val userName = dataStore.getUserName.collectAsState(initial = "").value

    val isLoading = viewModel.isLoading.observeAsState()
    LaunchedEffect(true) {
        viewModel.getBlogData()
    }

    Column(
        modifier = modifier
    ){
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(25.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,


                ) {
                    Image(
                        painter = painterResource(R.drawable.bio_hazzard),
                        contentDescription = "Left image",
                        modifier = Modifier.size(40.dp)
                    )


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

            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(38,38,38)
            )
        )



        if(isLoading.value == true) {
            Loading()
        } else {
            Box()
            {
                // Lista dos blogs publicados
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .background(Color(64,64,64))
                        .padding(10.dp)
                ) {
                    items(blogData.value) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            BlogItem(it, navController)
                        }
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        // Navegar para o ecra do formulario do blog
                        navController.navigate(Screen.BlogForm.route)
                    },
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 12.dp),
                ) {
                    Icon(Icons.Filled.Add, "Floating button")
                    Text(text = "Add Post")
                }
            }
        }
    }
}

@Composable
fun Loading() {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black)
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CircularProgressIndicator(
            color = primaryColor
        )
    }
}

@Composable
fun BlogItem(blog: Blog, navController: NavHostController) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0,0,0),
        ),
        modifier = Modifier.padding(vertical = 8.dp),
        shape = RoundedCornerShape(15.dp),

        onClick = {
            navController.navigate("" +
                    "blog_reader_screen/${blog.author}/${blog.title}/${blog.content}")
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (blog.type) {
                    "COMBAT" -> {
                        " ${blog.title} \uD83E\uDD1C"
                    }

                    "MEDICINE" -> {
                        " ${blog.title} \uD83C\uDF3F"
                    }

                    "SURVIVAL" -> {
                        " ${blog.title} \uD83D\uDD25"
                    }

                    "LIFESTYLE" -> {
                        " ${blog.title} \uD83D\uDCF1"
                    }

                    else -> {
                        ""
                    }
                },
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(10.dp)
            )
            Spacer(Modifier.height(3.dp))
            when (blog.type) {
                "COMBAT" -> {
                    Image(
                        painter = painterResource(R.drawable.blog_combact),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                "MEDICINE" -> {
                    Image(
                        painter = painterResource(R.drawable.blog_medicine),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                "SURVIVAL" -> {
                    Image(
                        painter = painterResource(R.drawable.blog_survival),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                "LIFESTYLE" -> {
                    Image(
                        painter = painterResource(R.drawable.blog_lifestyle),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    Image(
                        painter = painterResource(R.drawable.blog_combact),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            /*Image(
                painter = painterResource(blog.imageUri),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )*/
            Spacer(Modifier.height(4.dp))
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
                        color = Color.White,
                        fontWeight = FontWeight.Normal
                    )
                )



            }
        }
    }
}

/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingWithStatePreview() {
    BlogScreen()
}
*/