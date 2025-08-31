// AutoServiceChooseViewModel.kt
package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pandoscorp.autosnap.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AutoServiceChooseViewModel : ViewModel() {
    private val _services = MutableStateFlow<List<User>>(emptyList())
    val services: StateFlow<List<User>> = _services

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    val userRef = database.getReference("users")
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    init {
        loadServices()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    init {
        loadServices()
    }

    fun loadServices() {
        if (FirebaseAuth.getInstance().currentUser?.uid == null) {
            Log.e("Auth", "User not authenticated")
            return
        }

        _loadingState.value = true

        database.getReference("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val centers = mutableListOf<User>()

                    snapshot.children.forEach { userSnapshot ->
                        try {
                            // Явно читаем только разрешенные поля
                            val username = userSnapshot.child("username").getValue(String::class.java)
                            if (!username.isNullOrEmpty()) {
                                centers.add(User(
                                    id = userSnapshot.key ?: "",
                                    username = username,
                                    email = userSnapshot.child("email").getValue(String::class.java) ?: "",
                                    phone = userSnapshot.child("phone").getValue(String::class.java) ?: "",
                                    address = userSnapshot.child("address").getValue(String::class.java) ?: ""
                                ))
                            }
                        } catch (e: Exception) {
                            Log.e("Firebase", "Error parsing user data", e)
                        }
                    }

                    _services.value = centers
                    _loadingState.value = false
                    Log.d("Firebase", "Successfully loaded ${centers.size} service centers")
                }

                override fun onCancelled(error: DatabaseError) {
                    _loadingState.value = false
                    Log.e("Firebase", "Load cancelled: ${error.message}")
                }
            })
    }
}