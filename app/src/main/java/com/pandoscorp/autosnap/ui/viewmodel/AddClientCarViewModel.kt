package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.model.Car
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddClientCarViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database("https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun addCar(
        brand: String,
        model: String,
        year: String,
        engineVolume: Double,
        horsePower: String
    ) {
        if (brand.isBlank() || model.isBlank()) {
            _errorMessage.value = "Марка и модель обязательны"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("Пользователь не авторизован")
                val carsRef = database.getReference("clients/$userId/cars")

                // Создаем новый автомобиль с автоматическим ID
                val newCarRef = carsRef.push()
                val newCar = Car(
                    id = newCarRef.key ?: "",
                    brand = brand.trim(),
                    model = model.trim(),
                    year = year.trim(),
                    engineVolume = engineVolume,
                    horsePower = horsePower.trim()
                )

                // Сохраняем автомобиль напрямую по сгенерированному пути
                newCarRef.setValue(newCar).await()

                delay(500)

                _success.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при добавлении: ${e.localizedMessage}"
                Log.e("AddCar", "Error adding car", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _errorMessage.value = null
        _success.value = false
    }
}