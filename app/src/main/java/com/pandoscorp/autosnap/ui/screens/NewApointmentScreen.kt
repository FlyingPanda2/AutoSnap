package com.pandoscorp.autosnap.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.ui.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAppointmentForm(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val selectedClient by sharedViewModel.selectedClient.collectAsState()
    val selectedCar by sharedViewModel.selectedCar.collectAsState()

    val selectedServices by remember {
        derivedStateOf { sharedViewModel.selectedServices.toList() }
    }

    LaunchedEffect(selectedClient) {
        Log.d("NewAppointmentForm", "Обновлен выбранный клиент: ${selectedClient?.name ?: "null"}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Новая запись",
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
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Done"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.White),
                modifier = Modifier.shadow(elevation = 5.dp)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.LightGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "ООО",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White)
                        .clickable { navController.navigate("clients/selection") },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if(selectedClient == null){
                            Text(
                                text = "Выбрать клиента",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        } else{
                            Text(
                                text = selectedClient!!.name + " " + selectedClient!!.surname,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(2.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White)
                        .clickable { navController.navigate(ScreenObject.ClientCarsScreen.route) },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if(selectedCar == null){
                            Text(
                                text = "Выбрать автомобиль",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        }else{
                            Text(
                                text = "${selectedCar!!.brand} ${selectedCar!!.model} (${selectedCar!!.year})",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(2.dp))

                if(selectedServices.isEmpty()){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(elevation = 3.dp)
                            .background(Color.White)
                            .clickable { navController.navigate(ScreenObject.ServiceChooseScreen.route) },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Выбрать услуги",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        }
                    }
                }else{
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Выбранные услуги:",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        selectedServices.forEach { service ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { sharedViewModel.removeSelectedService(service.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Удалить услугу",
                                        tint = Color.Red
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = service.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${service.price} ₽ • ${service.duration} мин",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(elevation = 3.dp)
                            .background(Color.White)
                            .clickable { navController.navigate(ScreenObject.ServiceChooseScreen.route) },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Добавить еще услугу",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            )

                            Icon(
                                Icons.Sharp.KeyboardArrowRight,
                                contentDescription = "Done",

                                )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Дата",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Done",

                            )
                    }
                }

                Spacer(modifier = Modifier.padding(1.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Начало",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Done",

                            )
                    }
                }

                Spacer(modifier = Modifier.padding(1.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Длительность",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Done",

                            )
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Стоимость услуг",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Done",

                            )
                    }
                }

                Spacer(modifier = Modifier.padding(1.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Скидка на услуги",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Done",

                            )
                    }
                }

                Spacer(modifier = Modifier.padding(1.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Итоговая стоимость",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Done",

                            )
                    }
                }

            }

        }
    )
}