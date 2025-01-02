package com.example.projeto_cm_24_25.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size
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

    LaunchedEffect(true) {
        viewModel.getBlogData()
    }

    Column(
        modifier = modifier
    ){
        TopAppBar(
            title = {
                Text(
                    text = "Howdy $userName \uD83D\uDE0B",
                    color = Color.White
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = primaryColor
            ),
        )
        Box()
        {
            // Lista dos blogs publicados
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black)
                    .padding(10.dp)
            ){
                items(blogData.value) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ){
                        BlogItem(it)
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
                Text(text = "Add Post \uD83D\uDCDD")
            }
        }
    }
}

@Composable
fun BlogItem(blog: Blog) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        shape = RoundedCornerShape(25.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = when(blog.imageUri){
                    R.drawable.blog_combact -> {
                        "\uD83E\uDD1C ${blog.title} \uD83E\uDDDF"
                    }
                    R.drawable.blog_medicine -> {
                        "\uD83C\uDF3F ${blog.title} \uD83E\uDDDF"
                    }
                    R.drawable.blog_survival -> {
                        "\uD83D\uDD25 ${blog.title} \uD83E\uDDDF"
                    }
                    R.drawable.blog_lifestyle -> {
                        "\uD83D\uDCF1 ${blog.title} \uD83E\uDDDF"
                    }
                    else -> {""}
                },
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(10.dp)
            )
            Spacer(Modifier.height(4.dp))
            Image(
                painter = painterResource(blog.imageUri),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${blog.author} posted on ${blog.publishedDate}",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(5.dp)
            )
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