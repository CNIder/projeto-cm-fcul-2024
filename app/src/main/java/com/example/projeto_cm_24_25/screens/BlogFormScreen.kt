package com.example.projeto_cm_24_25.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projeto_cm_24_25.data.BlogViewModel
import com.example.projeto_cm_24_25.data.model.Blog

@Composable
fun BlogFormScreen(viewModel: BlogViewModel) {
    // remember the state of blog title
    val titleTextState = remember { mutableStateOf("") }
    // remember the state of blog content
    val contentTextState = remember { mutableStateOf("") }

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
                // create blog object
                val blog = Blog(
                    title = titleTextState.value,
                    content = contentTextState.value,
                    author = "claudio"
                )
                // add blog data to firebase
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