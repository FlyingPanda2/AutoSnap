package com.pandoscorp.autosnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.model.Car
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddClientCarViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database = FirebaseDatabase.getInstance(databaseUrl)

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
                val clientRef = database.getReference("clients/$userId")

                // Получаем текущего клиента
                val clientSnapshot = clientRef.get().await()

                // Создаем новый автомобиль
                val newCar = Car(
                    id = "${System.currentTimeMillis()}",
                    brand = brand.trim(),
                    model = model.trim(),
                    year = year.trim(),
                    engineVolume = engineVolume,
                    horsePower = horsePower.trim()
                )

                // Обновляем список автомобилей клиента
                val currentCars = if (clientSnapshot.hasChild("cars")) {
                    clientSnapshot.child("cars").getValue(MutableMap::class.java)?.toMutableMap() ?: mutableMapOf()
                } else {
                    mutableMapOf()
                }

                currentCars[newCar.id] = newCar
                clientRef.child("cars").setValue(currentCars).await()

                _success.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при добавлении: ${e.localizedMessage}"
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