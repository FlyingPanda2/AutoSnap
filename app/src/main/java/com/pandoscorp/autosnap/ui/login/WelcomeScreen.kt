package com.pandoscorp.autosnap.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.R
import com.pandoscorp.autosnap.ScreenObject
import com.pandoscorp.autosnap.ui.theme.Blue
import com.pandoscorp.autosnap.ui.theme.LightBlue

@Composable
fun WelcomeForm(NavController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Spacer(modifier = Modifier.padding(30.dp))

        Image(
            painter = painterResource(R.drawable.car_service),
            contentDescription = "Main top WelcomeScreen picture",
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(25.dp)),
            contentScale = ContentScale.Crop

        )

        Spacer(modifier = Modifier.padding(15.dp))

        Text(
            "Добро пожаловать в AutoSnap!",
            fontSize = 25.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Соединим водителей и мастеров в 1 клик",
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.padding(100.dp))

        Button(
            onClick = { NavController.navigate(ScreenObject.MainScreen.route) },
            modifier = Modifier
                .width(280.dp)
                .height(60.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightBlue,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(text = "Регистрация", fontSize = 16.sp)
        }

        // Кнопка Войти
        Button(
            onClick = { NavController.navigate(ScreenObject.LoginScreen.route) },
            modifier = Modifier
                .width(280.dp)
                .height(60.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue
            )

        ) {
            Text(text = "Войти", fontSize = 16.sp)
        }


    }
}
