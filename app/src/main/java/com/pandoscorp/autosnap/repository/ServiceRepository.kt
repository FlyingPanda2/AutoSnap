package com.pandoscorp.autosnap.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pandoscorp.autosnap.model.Service
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// ServiceRepository.kt
class ServiceRepository(
    private val db: FirebaseFirestore
) {
    fun getServicesByShop(shopId: String): Flow<List<Service>> = callbackFlow {
        val listener = db.collection("services")
            .whereEqualTo("shopId", shopId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val services = snapshot?.toObjects(Service::class.java) ?: emptyList()
                trySend(services)
            }

        awaitClose { listener.remove() }
    }
}