package com.pandoscorp.autosnap.ui.login

import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.ScreenObject

@Composable
fun LoginForm(NavController: NavHostController) {

    val auth = remember { Firebase.auth }
    var errorState = remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.padding(20.dp))

        Text(
            text = "Вход",
            fontSize = 24.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        Spacer(modifier = Modifier.padding(100.dp))


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = CircleShape,
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = CircleShape,
            singleLine = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Left
        ) {

            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )

            Text(
                "Запомнить меня"
            )
        }
        if (errorState.value.isNotEmpty()) {
            Text(
                text = errorState.value,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(180.dp))

        Button(
            onClick = {
                SignIn(
                    auth,
                    email,
                    password,
                    NavController = NavController,
                    onSignInSucsess = {
                        Log.d("MyLog", "Sucsess")
                    },
                    onSignInFailure = { error ->
                        errorState.value = error
                    }
                )
            },
            modifier = Modifier
                .width(210.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color.Blue
            )

        ) {
            Text(
                "Войти",
                style = TextStyle(
                    fontSize = 18.sp, // Изменяем размер шрифта
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Text(
            "У меня еще нет аккаунта",
            modifier = Modifier.clickable { NavController.navigate(ScreenObject.RegScreen.route) }
        )
    }
}

private fun SignIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    NavController: NavHostController,
    onSignInSucsess: () -> Unit,
    onSignInFailure: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onSignInFailure("Email или пароль не должны быть пустыми!")
        return
    }
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                onSignInSucsess()
                NavController.navigate(ScreenObject.MainScreen.route)
            }
        }.addOnFailureListener {
            onSignInFailure(it.message ?: "Sign in error")
        }
}
