package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Client
import com.pandoscorp.autosnap.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    private val _selectedCar = MutableStateFlow<Car?>(null)
    val selectedCar: StateFlow<Car?> = _selectedCar

    private val _selectedServices = MutableStateFlow<List<Service>>(emptyList())
    val selectedServices: StateFlow<List<Service>> = _selectedServices

    fun selectClient(client: Client) {
        _selectedClient.value = client
        _selectedCar.value = null
    }

    fun selectCar(car: Car) {
        _selectedCar.value = car
    }

    fun addService(service: Service) {
        _selectedServices.value = _selectedServices.value + service
    }

    fun removeService(service: Service) {
        _selectedServices.value = _selectedServices.value - service
    }
}