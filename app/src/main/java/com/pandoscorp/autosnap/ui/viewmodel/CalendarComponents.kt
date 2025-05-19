package com.pandoscorp.autosnap.ui.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pandoscorp.autosnap.model.SimpleDate
import com.pandoscorp.autosnap.utilis.DateUtils
import java.util.Calendar

@Composable
fun CalendarView(
    state: ScheduleState,
    onDateSelected: (SimpleDate) -> Unit,
    onMonthChanged: (Int) -> Unit,
    onYearChanged: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MonthYearSelector(
            month = state.currentMonth,
            year = state.currentYear,
            onMonthChange = onMonthChanged,
            onYearChange = onYearChanged
        )

        DaysOfWeekHeader()

        CalendarGrid(
            month = state.currentMonth,
            year = state.currentYear,
            selectedDate = state.selectedDate,
            markedDates = state.markedDates,
            onDateClick = onDateSelected
        )
    }
}

@Composable
private fun MonthYearSelector(
    month: Int,
    year: Int,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = {
            if (month > 1) onMonthChange(month - 1)
            else {
                onMonthChange(12)
                onYearChange(year - 1)
            }
        }) {
            Icon(Icons.Default.ArrowBack, "Previous month")
        }

        Text(
            text = "${getMonthName(month)} $year",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = {
            if (month < 12) onMonthChange(month + 1)
            else {
                onMonthChange(1)
                onYearChange(year + 1)
            }
        }) {
            Icon(Icons.Default.ArrowForward, "Next month")
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    month: Int,
    year: Int,
    selectedDate: SimpleDate,
    markedDates: Set<String>,
    onDateClick: (SimpleDate) -> Unit
) {
    val daysInMonth = DateUtils.getDaysInMonth(month, year)
    val firstDayOfWeek = getFirstDayOfWeek(month, year)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(6 * 7) { index ->
            val dayNumber = index - firstDayOfWeek + 2
            if (dayNumber in 1..daysInMonth) {
                val date = SimpleDate(dayNumber, month, year)
                DayCell(
                    day = dayNumber,
                    isSelected = date == selectedDate,
                    isMarked = markedDates.contains(date.toKeyString()),
                    onClick = { onDateClick(date) }
                )
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isMarked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .background(
                color = when {
                    isSelected -> Color.DarkGray
                    isMarked -> MaterialTheme.colorScheme.secondaryContainer
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Center
    ) {
        Text(
            text = day.toString(),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

private fun getFirstDayOfWeek(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance().apply {
        set(year, month - 1, 1)
    }
    // Convert to Monday-based week (1-7)
    return (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
}

fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Января"
        2 -> "Февраля"
        3 -> "Марта"
        4 -> "Апреля"
        5 -> "Мая"
        6 -> "Июня"
        7 -> "Июля"
        8 -> "Августа"
        9 -> "Сентября"
        10 -> "Октября"
        11 -> "Ноября"
        12 -> "Декабря"
        else -> ""
    }
}