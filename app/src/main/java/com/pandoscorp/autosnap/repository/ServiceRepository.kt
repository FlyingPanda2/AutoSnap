package com.pandoscorp.autosnap.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.model.Service
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ServiceRepository {
    // Явно указываем URL вашей базы (как в консоли Firebase)
    private val db = Firebase.database("https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app")
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    fun getAllServices(): Flow<List<Service>> = callbackFlow {
        val ref =
            userId?.let { db.getReference("users").child(it).child("services") }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.e("FirebaseData", "Path 'services' doesn't exist!")
                    trySend(emptyList())
                    return
                }

                val services = snapshot.children.mapNotNull {
                    try {
                        it.getValue(Service::class.java)?.copy(id = it.key ?: "")
                    } catch (e: Exception) {
                        Log.e("FirebaseData", "Parse error at ${it.key}: ${e.message}")
                        null
                    }
                }

                Log.d("FirebaseData", "Loaded ${services.size} services")
                trySend(services)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error ${error.code}: ${error.message}")
                close(Exception(error.message))
            }
        }

        ref?.addValueEventListener(listener)
        awaitClose { ref?.removeEventListener(listener) }
    }
}