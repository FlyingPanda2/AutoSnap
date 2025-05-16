package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    private val _selectedCar = MutableStateFlow<Car?>(null)
    val selectedCar: StateFlow<Car?> = _selectedCar

    fun selectClient(client: Client) {
        _selectedClient.value = client
        _selectedCar.value = null
    }

    fun selectCar(car: Car) {
        _selectedCar.value = car
    }
}