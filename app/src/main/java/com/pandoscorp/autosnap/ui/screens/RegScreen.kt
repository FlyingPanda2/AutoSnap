package com.pandoscorp.autosnap.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.model.User
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.ui.viewmodel.AuthViewModel

@Composable
fun RegistrationForm(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val registrationState by authViewModel.registrationState.collectAsState()
    val errorState by authViewModel.errorState.collectAsState()

    LaunchedEffect(registrationState) {
        if (registrationState.isNotEmpty()) {
            if (registrationState == "Пользователь успешно зарегистрирован") {
                navController.navigate(ScreenObject.MainScreen.route) {
                    popUpTo(ScreenObject.RegScreen.route) { inclusive = true }
                }
                authViewModel.clearStates()
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.padding(20.dp))

        Text(
            text = "Создать аккаунт",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 50.dp)
        )

        Spacer(modifier = Modifier.padding(40.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Номер телефона") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Повторите пароль") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        if (errorState.isNotEmpty()) {
            Text(
                text = errorState,
                textAlign = TextAlign.Center,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    authViewModel.setError("Пароли не совпадают")
                    return@Button
                }
                val user = User(username = username, email = email, phone = phone)

                authViewModel.registerUser(user, password)
            },
            modifier = Modifier
                .width(260.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color.Blue
            )

        ) {
            Text("Зарегистрироваться")
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Text(
            "У меня уже есть аккаунт",
            Modifier.clickable {
                navController.navigate(ScreenObject.LoginScreen.route) {
                    popUpTo(ScreenObject.RegScreen.route) { inclusive = true }
                }
            }
        )
    }
}




