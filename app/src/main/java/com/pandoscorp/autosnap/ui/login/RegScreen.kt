package com.pandoscorp.autosnap.ui.login

import android.util.Log
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.ScreenObject

@Composable
fun RegistrationForm(NavController: NavHostController) {

    val auth = remember { Firebase.auth }
    var errorState = remember { mutableStateOf("") }
    val regMap = remember {
        mutableStateMapOf(
            "name" to "",
            "email" to "",
            "phone" to "",
            "password" to "",
            "confirmPassword" to ""
        )
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
            value = regMap["name"] ?: "",
            onValueChange = { regMap["name"] = it },
            label = { Text("Имя") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            singleLine = true
        )

        OutlinedTextField(
            value = regMap["email"] ?: "",
            onValueChange = { regMap["email"] = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        OutlinedTextField(
            value = regMap["phone"] ?: "",
            onValueChange = { regMap["phone"] = it },
            label = { Text("Номер телефона") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )

        OutlinedTextField(
            value = regMap["password"] ?: "",
            onValueChange = { regMap["password"] = it },
            label = { Text("Пароль") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        OutlinedTextField(
            value = regMap["confirmPassword"] ?: "",
            onValueChange = { regMap["confirmPassword"] = it },
            label = { Text("Повторите пароль") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            shape = CircleShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        if (errorState.value.isNotEmpty()) {
            Text(
                text = errorState.value,
                textAlign = TextAlign.Center,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = {
                signUp(
                    auth,
                    regMap,
                    onSignUpSucsess = {},
                    onSignUpFailure = { error ->
                        errorState.value = error
                    }
                )
            },
            modifier = Modifier
                .width(260.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,       // цвет текста
                containerColor = Color.Blue
            )

        ) {
            Text("Зарегистрироваться")
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Text(
            "У меня уже есть аккаунт",
            Modifier.clickable { NavController.navigate(ScreenObject.LoginScreen.route) }
        )
    }
}

private fun signUp(
    auth: FirebaseAuth,
    regMap: Map<String, String>,
    onSignUpSucsess: () -> Unit,
    onSignUpFailure: (String) -> Unit
) {
    for ((key, value) in regMap)
        if (value.isEmpty()) {
            onSignUpFailure("Поле $key быть заполнено")
            return
        }
    if (regMap["password"] != regMap["confirmPassword"]) {
        onSignUpFailure("Пароли не совпадают")
        return
    }


    auth.createUserWithEmailAndPassword(regMap["email"] ?: "", regMap["password"] ?: "")
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val userId = auth.currentUser?.uid

                if (userId != null) {
                    val databaseUrl =
                        "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
                    val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
                    val userRef: DatabaseReference =
                        database.getReference("users").child(userId)

                    val userData = mapOf(
                        "Name" to regMap["name"],
                        "Email" to regMap["email"],
                        "Phone" to regMap["phone"]
                    )

                    userRef.setValue(userData).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            onSignUpSucsess()
                            Log.d("MyLog", "Данные успешно сохранены")
                        } else {
                            onSignUpFailure("Ошибка сохранения данных пользователя")
                        }
                    }.addOnFailureListener {
                        onSignUpFailure(it.message ?: "Ошибка при записи данных")
                    }
                } else {
                    onSignUpFailure("Не удалось получить UID пользователя")
                }
            } else {
                onSignUpFailure(task.exception?.message ?: "Ошибка регистрации")
            }
        }.addOnFailureListener {
            onSignUpFailure(it.message ?: "Sign up error")
        }
}




