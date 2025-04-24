package com.pandoscorp.autosnap.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.R
import com.pandoscorp.autosnap.navigation.ScreenObject


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun TopAppBar(NavController: NavHostController, menuOpenClose: () -> Unit) {

    val auth = remember { Firebase.auth }
    val userId = auth.currentUser?.uid
    var userName by remember { mutableStateOf("Имя пользователя") }

    if (userId != null) {
        val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
        val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
        val userRef: DatabaseReference = database.getReference("users").child(userId)

        LaunchedEffect(userId) {
            Log.d("LOG", userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("username").value as? String ?: "Имя не найдено"
                        userName = name
                    } else {
                        userName = "Пользователь не найден"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    userName = "Ошибка загрузки данных"
                }
            })
        }

        TopAppBar(
            title = {
                Text(
                    text = userName,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(onClick = menuOpenClose) {
                    Icon(
                        Icons.Filled.Menu,
                        contentDescription = "Меню",
                    )
                }
            },
            actions = {
                IconButton({ NavController.navigate(ScreenObject.ProfileScreen.route + "/$userId") }) {
                    Image(
                        painter = painterResource(R.drawable.logo_without_text),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(Color.White),
            modifier = Modifier
                .shadow(elevation = 5.dp)
                .statusBarsPadding()


        )
    }
}