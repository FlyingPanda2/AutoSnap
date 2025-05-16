package com.pandoscorp.autosnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.pandoscorp.autosnap.model.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    fun selectClient(client: Client) {
        _selectedClient.value = client
    }
}