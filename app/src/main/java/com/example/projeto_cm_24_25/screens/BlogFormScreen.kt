package com.example.projeto_cm_24_25.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projeto_cm_24_25.data.BlogViewModel
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.data.repository.DataStoreRepository

@Composable
fun BlogFormScreen(viewModel: BlogViewModel) {
    val context = LocalContext.current
    // Titulo do blog
    val titleTextState = remember { mutableStateOf("") }
    // Conteudo do blog
    val contentTextState = remember { mutableStateOf("") }

    val dataStore = DataStoreRepository(context)
    // Recebe o nome de utilizador
    val userName = dataStore.getUserName.collectAsState(initial = "")

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
            .padding(15.dp)
    ){
        TextField(
            value = titleTextState.value,
            onValueChange = {titleTextState.value = it},
            modifier = Modifier.fillMaxWidth(),
            label = {Text("Blog Title")},
        )
        TextField(
            value = contentTextState.value,
            onValueChange = {contentTextState.value = it},
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f),
            label = {Text("Blog Content")},
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Validar campos
                if(titleTextState.value.isEmpty()) {
                    Toast.makeText(context, "Please provide the blog name", Toast.LENGTH_SHORT).show()
                    return@Button
                } else if(contentTextState.value.isEmpty()) {
                    Toast.makeText(context, "Please write something", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                // Criar um objeto kotlin blog
                val blog = Blog(
                    title = titleTextState.value,
                    content = contentTextState.value,
                    author = userName.value.toString()
                )
                viewModel.addBlogData(blog)
            }
        ) {
            Text(
                text = "Submit Post"
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun BlogFormScreenPreview() {
    BlogFormScreen(viewModel())
}