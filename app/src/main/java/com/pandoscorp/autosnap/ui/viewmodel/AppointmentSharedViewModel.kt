package com.pandoscorp.autosnap.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Client
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.model.SimpleDate
import com.pandoscorp.autosnap.utilis.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class AppointmentSharedViewModel {
    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    private val _selectedCar = MutableStateFlow<Car?>(null)
    val selectedCar: StateFlow<Car?> = _selectedCar

    private val _selectedServices = mutableStateListOf<Service>()
    val selectedServices: List<Service> get() = _selectedServices

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate

    fun selectDate(date: Date) {
        _selectedDate.value = date
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
}

data class ScheduleState(
    val selectedDate: SimpleDate,
    val currentMonth: Int,
    val currentYear: Int,
    val markedDates: Set<String>
)