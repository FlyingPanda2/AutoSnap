// AutoServiceChooseViewModel.kt
package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
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

    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    val userRef = database.getReference("users")

    init {
        loadServices()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    init {
        loadServices()
    }

    private fun loadServices() {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val validServices = mutableListOf<User>()

                snapshot.children.forEach { userSnapshot ->
                    // Проверяем, что это автосервис (есть username и нет clients/appointments)
                    if (userSnapshot.child("username").exists() &&
                        !userSnapshot.child("clients").exists()) {

                        val user = User(
                            id = userSnapshot.key ?: "",
                            username = userSnapshot.child("username").getValue(String::class.java) ?: "",
                            address = userSnapshot.child("address").getValue(String::class.java)?: "",
                            phone = userSnapshot.child("phone").getValue(String::class.java)?: "",
                            email = userSnapshot.child("email").getValue(String::class.java)?: ""
                        )

                        if (user.username.isNotEmpty()) {
                            validServices.add(user)
                            Log.d("Firebase", "Added service: ${user.username}")
                        }
                    }
                }

                _services.value = validServices
                Log.d("Firebase", "Total services loaded: ${validServices.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error loading services: ${error.message}")
            }
        })
    }
}