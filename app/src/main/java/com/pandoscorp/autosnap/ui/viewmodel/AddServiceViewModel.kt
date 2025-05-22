package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddServiceViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _userServices = MutableStateFlow<Map<String, Service>>(emptyMap())
    val userServices: StateFlow<Map<String, Service>> = _userServices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val auth = FirebaseAuth.getInstance()

    fun addServiceToUser(service: Service, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUserId = auth.currentUser?.uid
                    ?: throw Exception("User not authenticated")

                // Генерируем новый ID перед добавлением
                val newService = service.copy(id = repository.generateNewServiceId())

                val success = repository.addUserService(currentUserId, newService)
                if (success) {
                    loadUserServices()
                }
                onResult(success)
            } catch (e: Exception) {
                Log.e("AddServiceViewModel", "Error adding service", e)
                _errorMessage.value = "Failed to add service: ${e.localizedMessage}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserServices() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val currentUserId = auth.currentUser?.uid
                    ?: throw Exception("User not authenticated")

                val services = repository.getUserServices(currentUserId)
                _userServices.value = services

                if (services.isEmpty()) {
                    Log.d("AddServiceViewModel", "No services found for user $currentUserId")
                }
            } catch (e: Exception) {
                Log.e("AddServiceViewModel", "Error loading services", e)
                _errorMessage.value = "Failed to load services: ${e.localizedMessage}"
                _userServices.value = emptyMap()
            } finally {
                _isLoading.value = false
            }


            fun clearErrorMessage() {
                _errorMessage.value = null
            }
        }
    }
}