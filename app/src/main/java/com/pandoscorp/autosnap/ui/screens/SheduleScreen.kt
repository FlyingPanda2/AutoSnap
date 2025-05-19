package com.pandoscorp.autosnap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.ui.viewmodel.AppointmentSharedViewModel
import com.pandoscorp.autosnap.ui.viewmodel.CalendarView
import com.pandoscorp.autosnap.ui.viewmodel.getMonthName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheduleForm(
    navController: NavHostController,
    viewModel: AppointmentSharedViewModel,
    forDateSelection: Boolean = false
) {
    val state by viewModel.state.collectAsState()

    val selectedDate by viewModel.selectedDate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Записи",
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
        },
        content = { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                CalendarView(
                    state = state,
                    onDateSelected = { date ->
                        viewModel.selectDate(date)
                    },
                    onMonthChanged = { month ->
                        viewModel.changeMonth(month)
                    },
                    onYearChanged = { year ->
                        viewModel.changeYear(year)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Выбрано: ${state.selectedDate.day} ${getMonthName(state.selectedDate.month)} ${state.selectedDate.year}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .shadow(elevation = 5.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if(!forDateSelection){
                    LazyColumn(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .shadow(elevation = 2.dp)
                                    .background(Color.Gray)
                                    .clickable { navController.navigate(ScreenObject.NewAppointmentScreen.route) },
                                contentAlignment = Alignment.Center,
                            ){
                                Text(
                                    text = "Создать запись",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    fontSize = 16.sp

                                )
                            }
                        }
                    }
                }
            }
        }
    )
}