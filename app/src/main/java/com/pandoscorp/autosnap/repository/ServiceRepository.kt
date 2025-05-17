package com.pandoscorp.autosnap.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.model.Service
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ServiceRepository {
    private val database = Firebase.database.reference
    private val servicesRef = database.child("users")

    fun getUserServices(userId: String): Flow<List<Service>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val services = mutableListOf<Service>()
                snapshot.child(userId).child("services").children.forEach { serviceSnapshot ->
                    serviceSnapshot.getValue(Service::class.java)?.let {
                        services.add(it.copy(id = serviceSnapshot.key ?: ""))
                    }
                }
                trySend(services)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        servicesRef.addValueEventListener(listener)
        awaitClose { servicesRef.removeEventListener(listener) }
    }
}