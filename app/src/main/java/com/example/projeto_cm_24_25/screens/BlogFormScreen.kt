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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import kotlin.text.firstOrNull

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
    val userName = dataStore.getUserName.collectAsState(initial = "").value

    val scrollState = rememberScrollState()

    // Variaveis para radio buttons
    val radioOptions : Array<String> = BlogType.entries.map { it.type }.toTypedArray()
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }


Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.login_2), // Substitua pelo seu arquivo de imagem
            contentDescription = "background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
                .verticalScroll(scrollState)
        ) {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(25.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                     {
                        Text(
                            text = "Report",
                            color = Color.White,
                            modifier = Modifier.weight(1f), // Centraliza o texto
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp
                        )

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
            modifier = Modifier
                .fillMaxWidth()
        ){
        TextField(
            value = titleTextState.value,
            onValueChange = { titleTextState.value = it },
            modifier = Modifier.fillMaxWidth().padding(14.dp).clip(RoundedCornerShape(15.dp))
                .border(0.5.dp, Color.White, RoundedCornerShape(15.dp)),
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
            label = { Text("Type some title") },
            textStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )

        TextField(
            value = contentTextState.value,
            onValueChange = { contentTextState.value = it },
            modifier = Modifier.fillMaxWidth().height(300.dp).padding(14.dp).clip(RoundedCornerShape(15.dp))
                .border(0.5.dp, Color.White, RoundedCornerShape(15.dp)),
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
            label = { Text("What do you want to say ?") },
            textStyle = TextStyle(
                fontSize = 15.sp
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
                        modifier = Modifier.padding(start = 16.dp),
                        color = Color.White
                    )
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth().height(80.dp).padding(10.dp).clip(RoundedCornerShape(8.dp)),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(238, 31, 39, 200),
                contentColor = Color.Black),
            onClick = {
                // Validar campos
                if (titleTextState.value.isEmpty()) {
                    Toast.makeText(context, "❌ Please provide some title", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                } else if (contentTextState.value.isEmpty()) {
                    Toast.makeText(context, "❌ Please write something", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                // Criar um objeto kotlin blog
                val blog = Blog(
                    title = titleTextState.value,
                    content = contentTextState.value,
                    author = userName.toString(),
                    publishedDate = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    ),
                    type = when (selectedOption) {
                        BlogType.COMBAT.type -> {
                            "COMBAT"
                        }

                        BlogType.MEDICINE.type -> {
                            "MEDICINE"
                        }

                        BlogType.SURVIVAL.type -> {
                            "SURVIVAL"
                        }

                        BlogType.LIFESTYLE.type -> {
                            "LIFESTYLE"
                        }

                        else -> {
                            ""
                        }
                    }
                )

                // Guardar no Firebase
                viewModel.addBlogData(blog)
                navController.popBackStack()
                Toast.makeText(context, "\uD83D\uDCDD Posted successfully !", Toast.LENGTH_LONG)
                    .show()
            }
        ) {
            Text(
                text = "Submit Post"
            )
        }}
    } }
}

enum class BlogType(val type: String) {
    COMBAT("Combat Strategies and Tactics"),
    MEDICINE("Medicine and Healthcare"),
    SURVIVAL("Survival Stories & Experiences"),
    LIFESTYLE("Post-Apocalyptic Lifestyle")
}