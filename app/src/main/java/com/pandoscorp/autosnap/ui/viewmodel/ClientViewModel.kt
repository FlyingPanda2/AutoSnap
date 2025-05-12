package com.pandoscorp.autosnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.pandoscorp.autosnap.model.Client
import com.pandoscorp.autosnap.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ClientsViewModel : ViewModel() {
    private val repository = ClientRepository()

    private val _clients = MutableStateFlow<List<Client>>(emptyList())
    val clients: StateFlow<List<Client>> = _clients

    fun loadClients() {
        repository.getClients(
            onSuccess = { clients ->
                _clients.value = clients
            },
            onError = { errorMessage ->
                println(errorMessage)
            }
        )
    }
}