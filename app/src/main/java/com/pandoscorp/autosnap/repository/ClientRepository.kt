package com.pandoscorp.autosnap.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.model.Client

class ClientRepository {
    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    private val auth = FirebaseAuth.getInstance()

    fun getClients(onSuccess: (List<Client>) -> Unit, onError: (String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onError("Пользователь не авторизован")
            return
        }

        val clientsRef = database.getReference("users").child(userId).child("clients")
        clientsRef.get().addOnSuccessListener { snapshot ->
            val clients = mutableListOf<Client>()
            for (child in snapshot.children) {
                val client = child.getValue(Client::class.java)
                if (client != null) {
                    clients.add(client)
                }
            }
            onSuccess(clients)
        }.addOnFailureListener {
            onError("Ошибка загрузки клиентов: ${it.message}")
        }
    }
}