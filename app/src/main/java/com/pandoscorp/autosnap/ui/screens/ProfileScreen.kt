package com.pandoscorp.autosnap.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pandoscorp.autosnap.R
import com.pandoscorp.autosnap.model.User
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.repository.UserRepository
import com.pandoscorp.autosnap.ui.theme.BackgroundGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileForm(
    navController: NavHostController,
    userId: String,
    userRepository: UserRepository
) {
    var ProfileImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        ProfileImageUri = uri
    }

    var user by remember { mutableStateOf<User?>(null) }
    var updatedName by remember { mutableStateOf("") }
    var updatedEmail by remember { mutableStateOf("") }
    var updatedPhone by remember { mutableStateOf("") }

    // Получение данных пользователя при загрузке экрана
    LaunchedEffect(userId) {
        try {
            val fetchedUser = userRepository.getUserById(userId)
            if (fetchedUser != null) {
                user = fetchedUser
                updatedName = fetchedUser.username
                updatedEmail = fetchedUser.email
                updatedPhone = fetchedUser.phone
            }
        } catch (e: Exception) {
            Log.e("ProfileForm", "Ошибка получения пользователя: ${e.message}", e)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Профиль",
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(Color.White),
            modifier = Modifier.shadow(elevation = 5.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(170.dp)
                .clipToBounds()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") }
                    .border(2.dp, Color.Gray, CircleShape)
            ) {
                if (ProfileImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(ProfileImageUri)
                                .build()
                        ),
                        contentDescription = "Фото профиля",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.logo_without_text),
                        contentDescription = "Стандартное фото",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-15).dp, y = (-15).dp)
                    .clip(CircleShape)
                    .background(BackgroundGray)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Добавить фото",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(top = 40.dp, start = 30.dp, end = 30.dp)
        ) {
            OutlinedTextField(
                value = updatedName,
                onValueChange = { updatedName = it },
                label = { Text("Имя пользователя") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                shape = CircleShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )

            OutlinedTextField(
                value = updatedEmail,
                onValueChange = { updatedEmail = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                shape = CircleShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            OutlinedTextField(
                value = updatedPhone,
                onValueChange = { updatedPhone = it },
                label = { Text("Номер телефона") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                shape = CircleShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val updatedUser = User(
                            id = userId,
                            username = updatedName,
                            email = updatedEmail,
                            phone = updatedPhone
                        )
                        userRepository.updateUser(updatedUser)
                        Log.d("ProfileForm", "Данные успешно обновлены")
                    } catch (e: Exception) {
                        Log.e("ProfileForm", "Ошибка обновления данных: ${e.message}", e)
                    }
                }
            }
        ) {
            Text("Сохранить")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        userRepository.deleteUser(userId)
                        Log.d("ProfileForm", "Аккаунт успешно удален")
                        navController.navigate(ScreenObject.RegScreen.route){
                            popUpTo(0) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileForm", "Ошибка удаления аккаунта: ${e.message}", e)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(Color.Red)
        ) {
            Text("Удалить аккаунт")
        }
    }
}