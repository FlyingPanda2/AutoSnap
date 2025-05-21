package com.pandoscorp.autosnap.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.model.Client
import kotlinx.coroutines.tasks.await

class ClientRepository {
    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    private val auth = FirebaseAuth.getInstance()

    suspend fun addRealClient(client: Client) {
        try {
            database.getReference("clients/${client.id}")
                .setValue(client)
                .await()
        } catch (e: Exception) {
            throw Exception("Не удалось добавить клиента: ${e.message}")
        }
    }

    fun getClients(onSuccess: (List<Client>) -> Unit, onError: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: run {
            onError("Пользователь не авторизован")
            return
        }

        database.getReference("users/$userId/clients")
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    val clients = snapshot.children.mapNotNull { child ->
                        try {
                            // Получаем данные клиента как HashMap
                            val clientData = child.value as? Map<String, Any>
                                ?: throw Exception("Invalid client data format")

                            // Преобразуем в объект Client
                            Client(
                                id = child.key ?: "",
                                name = clientData["name"] as? String ?: "",
                                surname = clientData["surname"] as? String ?: "",
                                phone = clientData["phone"] as? String ?: "",
                                note = clientData["note"] as? String ?: "",
                                // Добавьте другие поля по аналогии
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    onSuccess(clients)
                } catch (e: Exception) {
                    onError("Ошибка обработки данных: ${e.message}")
                }
            }
            .addOnFailureListener {
                onError("Ошибка загрузки клиентов: ${it.message ?: "Неизвестная ошибка"}")
            }
    }
}