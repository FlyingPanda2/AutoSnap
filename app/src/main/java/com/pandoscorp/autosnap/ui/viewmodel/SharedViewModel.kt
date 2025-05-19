package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Client
import com.pandoscorp.autosnap.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class SharedViewModel : ViewModel() {
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
}