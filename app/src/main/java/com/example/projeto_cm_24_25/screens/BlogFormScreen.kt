package com.example.projeto_cm_24_25.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.projeto_cm_24_25.R
import com.example.projeto_cm_24_25.data.BlogViewModel
import com.example.projeto_cm_24_25.data.model.Blog
import com.example.projeto_cm_24_25.data.repository.DataStoreRepository
import com.example.projeto_cm_24_25.ui.theme.primaryColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BlogFormScreen(viewModel: BlogViewModel, navController: NavHostController) {
    val context = LocalContext.current
    // Titulo do blog
    val titleTextState = remember { mutableStateOf("") }
    // Conteudo do blog
    val contentTextState = remember { mutableStateOf("") }

    val dataStore = DataStoreRepository(context)
    // Recebe o nome de utilizador
    val userName = dataStore.getUserName.collectAsState(initial = "")

    // Variaveis para radio buttons
    val radioOptions : Array<String> = BlogType.entries.map { it.type }.toTypedArray()
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        CenterAlignedTopAppBar(
            title = { Text("Post content \uD83D\uDCDD") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = primaryColor,
                titleContentColor = Color.Black,
            ),
            navigationIcon = {
                IconButton(onClick = {
                    // Voltar para o ecra da lista dos blogs
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "go back to map screen"
                    )
                }
            }
        )

        TextField(
            value = titleTextState.value,
            onValueChange = {titleTextState.value = it},
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedPlaceholderColor = Color.White,
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedTextColor = Color.White,
            ),
            label = {Text("Type some title")},
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        TextField(
            value = contentTextState.value,
            onValueChange = {contentTextState.value = it},
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f).padding(horizontal = 14.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedPlaceholderColor = Color.White,
                focusedContainerColor = Color.Black,
                unfocusedContainerColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedTextColor = Color.White,
            ),
            label = {Text("What do you want to say ?")},
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
        )

        // Escolha das opcoes do tipo de conteudo
        Column(
            Modifier.selectableGroup()
        ) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.Black
            ),
            onClick = {
                // Validar campos
                if(titleTextState.value.isEmpty()) {
                    Toast.makeText(context, "❌ Please provide some title", Toast.LENGTH_SHORT).show()
                    return@Button
                } else if(contentTextState.value.isEmpty()) {
                    Toast.makeText(context, "❌ Please write something", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                // Criar um objeto kotlin blog
                val blog = Blog(
                    title = titleTextState.value,
                    content = contentTextState.value,
                    author = userName.value.toString(),
                    publishedDate = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    ),
                    imageUri = when(selectedOption) {
                        BlogType.COMBAT.type -> {
                            R.drawable.blog_combact
                        }
                        BlogType.MEDICINE.type -> {
                            R.drawable.blog_medicine
                        }
                        BlogType.SURVIVAL.type -> {
                            R.drawable.blog_survival
                        }
                        BlogType.LIFESTYLE.type -> {
                            R.drawable.blog_lifestyle
                        }
                        else -> {0}
                    }
                )

                // Guardar no Firebase
                viewModel.addBlogData(blog)
                navController.popBackStack()
                Toast.makeText(context, "\uD83D\uDCDD Posted successfully !", Toast.LENGTH_LONG).show()
            }
        ) {
            Text(
                text = "Submit Post"
            )
        }
    }
}

enum class BlogType(val type: String) {
    COMBAT("Combat Strategies and Tactics"),
    MEDICINE("Medicine and Healthcare"),
    SURVIVAL("Survival Stories & Experiences"),
    LIFESTYLE("Post-Apocalyptic Lifestyle")
}