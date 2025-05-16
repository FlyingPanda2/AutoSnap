package com.pandoscorp.autosnap.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceForm(
    navController: NavHostController
) {

    var serviceName by remember {mutableStateOf("")}
    var serviceLong by remember {mutableStateOf("")}
    var servicePrice by remember {mutableStateOf("")}
    var serviceDescription by remember {mutableStateOf("")}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая услуга") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Done"
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                OutlinedTextField(
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    label = { Text("Название") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                )
                OutlinedTextField(
                    value = serviceLong,
                    onValueChange = { serviceName = it },
                    label = { Text("Длительность") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    value = servicePrice,
                    onValueChange = { serviceName = it },
                    label = { Text("Стоимость") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    value = serviceDescription,
                    onValueChange = { serviceName = it },
                    label = { Text("Описание") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
            }
        }
    )

}