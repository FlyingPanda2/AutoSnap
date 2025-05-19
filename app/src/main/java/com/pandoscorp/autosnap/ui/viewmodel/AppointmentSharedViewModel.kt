package com.pandoscorp.autosnap.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.times
import androidx.compose.ui.unit.times
import androidx.lifecycle.ViewModel
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Client
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.model.SimpleDate
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.utilis.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime
import java.util.Date
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlin.time.times

class AppointmentSharedViewModel: ViewModel() {
    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    private val _selectedCar = MutableStateFlow<Car?>(null)
    val selectedCar: StateFlow<Car?> = _selectedCar

    private val _selectedServices = mutableStateListOf<Service>()
    val selectedServices: List<Service> get() = _selectedServices

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate

    // Для хранения состояния выбора (из меню или из формы)
    private val _isDateSelectionMode = MutableStateFlow(false)
    val isDateSelectionMode: StateFlow<Boolean> = _isDateSelectionMode

    private val _durationHours = MutableStateFlow(0)
    val durationHours: StateFlow<Int> = _durationHours

    private val _durationMinutes = MutableStateFlow(0)
    val durationMinutes: StateFlow<Int> = _durationMinutes

    private val _startTime = MutableStateFlow<LocalTime?>(null)
    val startTime: StateFlow<LocalTime?> = _startTime

    fun updateDuration(hours: Int, minutes: Int) {
        _durationHours.value = hours
        _durationMinutes.value = minutes
    }

    fun setDateSelectionMode(enabled: Boolean) {
        _isDateSelectionMode.value = enabled
    }

    fun selectDate(date: Date) {
        _selectedDate.value = date
    }

    fun clearDate() {
        _selectedDate.value = null
    }

    fun addSelectedService(service: Service) {
        if (!_selectedServices.any { it.id == service.id }) {
            _selectedServices.add(service)
        }
    }

    fun removeSelectedService(serviceId: String) {
        _selectedServices.removeAll { it.id == serviceId }
    }

    fun clearSelectedServices() {
        _selectedServices.clear()
    }

    fun selectClient(client: Client) {
        _selectedClient.value = client
        _selectedCar.value = null
    }

    fun selectCar(car: Car) {
        _selectedCar.value = car
    }

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

    val totalDuration: StateFlow<String>
        get() = MutableStateFlow(
            _selectedServices.sumOf { it.duration }.let { totalMinutes ->
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                when {
                    hours > 0 && minutes > 0 -> "${hours}ч ${minutes}мин"
                    hours > 0 -> "${hours}ч"
                    else -> "${minutes}мин"
                }
            }
        ).asStateFlow()

    fun setStartTime(time: LocalTime) {
        _startTime.value = time
    }

    private val _discountPercent = MutableStateFlow(0) // 0-100%
    val discountPercent: StateFlow<Int> = _discountPercent

    val totalPrice: StateFlow<Int> = snapshotFlow { _selectedServices.toList() }
        .map { services -> services.sumOf { it.price } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Сумма скидки в рублях
    val discountAmount: StateFlow<Int> = combine(
        totalPrice,
        discountPercent
    ) { price, percent ->
        (price * percent / 100).coerceAtMost(price) // Не больше чем общая стоимость
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Итоговая стоимость (с учетом скидки)
    val finalPrice: StateFlow<Int> = combine(
        totalPrice,
        discountAmount
    ) { price, discount ->
        price - discount
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun setDiscountPercent(percent: Int) {
        _discountPercent.value = percent.coerceIn(0, 100)
    }
}

data class ScheduleState(
    val selectedDate: SimpleDate,
    val currentMonth: Int,
    val currentYear: Int,
    val markedDates: Set<String>
)