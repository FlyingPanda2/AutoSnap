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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun SheduleForm(
    navController: NavHostController
) {
    Scaffold(
        topBar = { TopAppBar() },
        bottomBar = { BottomBar() }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CalendarGrid()
            CurrentDayInfo()
            CreateEntryCard()
        }
    }
}

@Composable
fun DropdownMenuButton() {
    Box(modifier = Modifier.clickable { /* Открыть меню */ }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "ДЕНЬ", color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun CalendarGrid() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            repeat(7) { index ->
                Text(
                    text = when (index) {
                        0 -> "П"
                        1 -> "В"
                        2 -> "С"
                        3 -> "Ч"
                        4 -> "П"
                        5 -> "С"
                        else -> "В"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentSize(Alignment.Center)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(31) { day ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(8.dp)
                ) {
                    DayCell(day = day, isSelected = day == 1)
                }
            }
        }
    }
}

@Composable
fun DayCell(day: Int, isSelected: Boolean = false) {
    val backgroundColor = if (isSelected) Color(0xFF0E8A9B) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = Modifier
            .size(40.dp)
            .aspectRatio(1f)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(percent = 50)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.White else Color.LightGray,
                shape = RoundedCornerShape(percent = 50)
            )
            .clickable { /* Обработка клика */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CurrentDayInfo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Сегодня пн, 7 апреля",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "09:00 - 21:00",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Next",
            tint = Color.Gray,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun CreateEntryCard() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "14:40",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "создать запись / перерыв",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Magenta
                )
            }
            Text(
                text = "свободно: 6 часов 20 мин",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun BottomBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Поиск */ }) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Выручка за день",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "0 ₽",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { /* Календарь */ }) {
            Icon(Icons.Default.DateRange, contentDescription = "Calendar")
        }
    }
}

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Назад */ }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Text(
            text = "Апрель",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )

        DropdownMenuButton()
    }
}