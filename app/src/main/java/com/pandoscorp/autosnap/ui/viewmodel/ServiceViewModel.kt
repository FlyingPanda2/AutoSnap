package com.pandoscorp.autosnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServiceViewModel(
    private val repository: ServiceRepository = ServiceRepository()
) : ViewModel() {
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    fun loadUserServices(userId: String) {
        viewModelScope.launch {
            repository.getUserServices(userId).collect { services ->
                _services.value = services
            }
        }
    }
}