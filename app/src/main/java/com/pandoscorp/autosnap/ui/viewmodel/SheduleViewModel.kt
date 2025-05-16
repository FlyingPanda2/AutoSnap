package com.pandoscorp.autosnap.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pandoscorp.autosnap.model.SimpleDate
import com.pandoscorp.autosnap.utilis.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SheduleViewModel : ViewModel() {
    private val currentDate = DateUtils.getCurrentDate()

    private val _state = MutableStateFlow(
        ScheduleState(
            selectedDate = currentDate,
            currentMonth = currentDate.month,
            currentYear = currentDate.year,
            markedDates = setOf(currentDate.toKeyString())
        )
    )
    val state: StateFlow<ScheduleState> = _state

    fun selectDate(date: SimpleDate) {
        _state.value = _state.value.copy(selectedDate = date)
    }

    fun changeMonth(month: Int) {
        _state.value = _state.value.copy(currentMonth = month)
    }

    fun changeYear(year: Int) {
        _state.value = _state.value.copy(currentYear = year)
    }
}

data class ScheduleState(
    val selectedDate: SimpleDate,
    val currentMonth: Int,
    val currentYear: Int,
    val markedDates: Set<String>
)