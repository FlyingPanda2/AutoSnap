﻿package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.repository.ServiceRepository
import com.pandoscorp.autosnap.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ServiceViewModel() : ViewModel() {
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedServices = mutableStateListOf<Service>()
    val selectedServices: List<Service> get() = _selectedServices

    fun toggleServiceSelect(service: Service) {
        if (_selectedServices.any { it.id == service.id }) {
            _selectedServices.removeAll { it.id == service.id }
        } else {
            _selectedServices.add(service)
        }
    }

    fun clearSelection() {
        _selectedServices.clear()
    }

    fun toggleServiceSelection(serviceId: String) {
        _services.value = _services.value.map { service ->
            if (service.id == serviceId) {
                service.copy(isSelected = !service.isSelected)
            } else {
                service
            }
        }
    }

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            ServiceRepository().getAllServices()
                .onStart { _isLoading.value = true }
                .catch { e ->
                    Log.e("ViewModel", "Error: ${e.message}")
                    _isLoading.value = false
                }
                .collect {
                    _services.value = it
                    _isLoading.value = false
                    Log.d("ViewModel", "Loaded ${it.size} services") // Лог количества
                }
        }
    }
}