package com.pandoscorp.autosnap.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.pandoscorp.autosnap.model.Client
import com.pandoscorp.autosnap.model.User
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun RegistrationForm(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // Состояние для выбора типа пользователя
    var userType by remember { mutableStateOf("client") } // "client" или "service"

    // Общие поля
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Поля для клиента
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    // Поля для автосервиса
    var serviceName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val registrationState by authViewModel.registrationState.collectAsState()
    val errorState by authViewModel.errorState.collectAsState()

    LaunchedEffect(registrationState) {
        if (registrationState == "Пользователь успешно зарегистрирован") {
            // Даем время на обработку всех событий
            delay(1000)

            when (userType) {
                "client" -> {
                    navController.navigate(ScreenObject.ClientMainScreen.route) {
                        popUpTo(ScreenObject.RegScreen.route) { inclusive = true }
                    }
                }
                "service" -> {
                    navController.navigate(ScreenObject.MainScreen.route) {
                        popUpTo(ScreenObject.RegScreen.route) { inclusive = true }
                    }
                }
            }
            authViewModel.clearStates()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Создать аккаунт",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        // Переключатель типа пользователя
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { userType = "client" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (userType == "client") Color.Blue else Color.LightGray
                )
            ) {
                Text("Клиент")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { userType = "service" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (userType == "service") Color.Blue else Color.LightGray
                )
            ) {
                Text("Автосервис")
            }
        }

        // Общие поля для всех типов пользователей
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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
                .padding(bottom = 8.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )

        // Поля в зависимости от типа пользователя
        if (userType == "client") {
            // Поля для клиента
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Имя") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = CircleShape,
                singleLine = true
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Фамилия") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = CircleShape,
                singleLine = true
            )

            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Дата рождения (ДД.ММ.ГГГГ)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = CircleShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        } else {
            // Поля для автосервиса
            OutlinedTextField(
                value = serviceName,
                onValueChange = { serviceName = it },
                label = { Text("Название автосервиса") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = CircleShape,
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Адрес") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = CircleShape,
                singleLine = true
            )
        }

        // Поля паролей
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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
                .padding(bottom = 8.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        if (errorState.isNotEmpty()) {
            Text(
                text = errorState,
                textAlign = TextAlign.Center,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (password != confirmPassword) {
                    authViewModel.setError("Пароли не совпадают")
                    return@Button
                }

                val user = User(
                    id = "", // Будет заполнено при регистрации
                    username = if (userType == "client") "$firstName $lastName" else serviceName,
                    email = email,
                    phone = phone,
                )

                val clientData = if (userType == "client") {
                    Client(
                        name = firstName,
                        surname = lastName,
                        birthdate = birthDate,
                        email = email,
                        phone = phone,
                    )
                } else null

                authViewModel.registerUser(user, password, clientData)
            },
            modifier = Modifier
                .fillMaxWidth()
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




