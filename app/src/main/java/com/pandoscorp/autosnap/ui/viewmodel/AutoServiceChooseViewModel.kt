// AutoServiceChooseViewModel.kt
package com.pandoscorp.autosnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutoServiceChooseViewModel : ViewModel() {
    private val _services = MutableStateFlow<List<User>>(emptyList())
    val services: StateFlow<List<User>> = _services

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadServices()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadServices() {
        viewModelScope.launch {
            val database = Firebase.database.reference.child("users")

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val servicesList = mutableListOf<User>()
                    snapshot.children.forEach { userSnapshot ->
                        servicesList.add(
                            User(
                                id = userSnapshot.key ?: "",
                                username = userSnapshot.child("username").getValue(String::class.java)?: "",
                                address = userSnapshot.child("address").getValue(String::class.java)?: "",
                                phone = userSnapshot.child("phone").getValue(String::class.java)?: "",
                                email = userSnapshot.child("email").getValue(String::class.java)?: ""
                            )
                        )
                    }
                    _services.value = servicesList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибок
                }
            })
        }
    }
}