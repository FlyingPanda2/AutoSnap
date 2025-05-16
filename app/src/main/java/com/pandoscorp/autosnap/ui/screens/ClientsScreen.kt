package com.pandoscorp.autosnap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.ui.viewmodel.ClientsViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsForm(
    navController: NavHostController,
    viewModel: ClientsViewModel,
    forSelection: Boolean = true
) {
    val clients by viewModel.clients.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Клиенты",
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Действие поиска */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.White),
                modifier = Modifier.shadow(elevation = 5.dp)
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Основной контент (список клиентов)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(clients) { client ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    if (forSelection) {
                                        navController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("selected_client", client)
                                        navController.popBackStack()
                                    }
                                }
                        ) {
                            Text(
                                text = "${client.name} ${client.surname}",
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            if (client.phone.isNotEmpty()) {
                                Text(
                                    text = client.phone,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                // Кнопка "+" в правом нижнем углу
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Закрепляем кнопку в правом нижнем углу
                        .padding(16.dp) // Отступы от краев экрана
                        .size(60.dp) // Размер кнопки
                        .background(Color.DarkGray, CircleShape) // Фон кнопки
                        .clickable { navController.navigate(ScreenObject.AddClientScreen.route) }, // Действие при нажатии
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 25.sp
                    )
                }
            }
        }
    )
}