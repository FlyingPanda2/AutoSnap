package com.pandoscorp.autosnap.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val databaseUrl =
        "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    private val userRef: DatabaseReference = database.getReference("users")

    suspend fun registerUser(user: User, password: String): String {
        return try {
            // Регистрация в Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")

            // Сохранение данных пользователя
            user.id = userId
            userRef.child(userId).setValue(user).await()

            "Пользователь успешно зарегистрирован"
        } catch (e: Exception) {
            Log.e("UserRepository", "Ошибка регистрации: ${e.message}", e)
            "Ошибка регистрации: ${e.message}"
        }
    }

    suspend fun isUserClient(userId: String): Boolean {
        return try {
            val clientRef = FirebaseDatabase.getInstance()
                .getReference("clients/$userId")
            val snapshot = clientRef.get().await()
            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    fun generateNewServiceId(): String {
        return database.getReference("services").push().key ?: throw Exception("Failed to generate ID")
    }

    //Вход пользователя
    suspend fun loginUser(email: String, password: String): String {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            return "Вход выполнен успешно"
        } catch (e: Exception) {
            Log.e("UserRepository", "Ошибка входа: ${e.message}", e)
            return "Ошибка входа: ${e.message}"
        }
    }

    //Получение пользователя по ID
    suspend fun getUserById(userId: String): User? = withContext(Dispatchers.IO) {
        try {
            Log.d("UserDebug", "Fetching user $userId")
            val snapshot = userRef.child(userId).get().await()

            if (!snapshot.exists()) return@withContext null

            // Получаем данные как Map<*, *>
            val data = snapshot.value as? Map<*, *> ?: return@withContext null

            val username = data["username"] as? String ?: ""
            val email = data["email"] as? String ?: ""
            val phone = data["phone"] as? String ?: ""
            val address = data["address"] as? String ?: ""

            User(
                id = userId,
                username = username,
                email = email,
                phone = phone,
                address = address
            )
        } catch (e: Exception) {
            Log.e("UserDebug", "Error loading user", e)
            null
        }
    }

    suspend fun getUsername(userId: String): String = withContext(Dispatchers.IO) {
        try {
            val snapshot = userRef.child(userId).child("username").get().await()
            snapshot.getValue(String::class.java) ?: "Гость"
        } catch (e: Exception) {
            Log.e("UserRepo", "Error loading username", e)
            "Гость"
        }
    }

    // Функция для удаления пользователя
    suspend fun deleteUser(userId: String): String {
        try {
            userRef.child(userId).removeValue().await()
            return "Пользователь успешно удален"
        } catch (e: Exception) {
            Log.e("UserRepository", "Ошибка удаления пользователя: ${e.message}", e)
            return "Ошибка удаления пользователя: ${e.message}"
        }
    }

    // Функция для обновления пользователя
    suspend fun updateUser(user: User): String {
        try {
            userRef.child(user.id).setValue(user).await()
            return "Данные успешно обновлены"
        } catch (e: Exception) {
            Log.e("UserRepository", "Ошибка обновления данных пользователя: ${e.message}", e)
            return "Ошибка обновления данных пользователя: ${e.message}"
        }
    }

    suspend fun getUserServices(userId: String): Map<String, Service> {
        return try {
            val snapshot = userRef.child(userId).child("services").get().await()
            snapshot.getValue(object : GenericTypeIndicator<Map<String, Service>>() {})
                ?: emptyMap()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting services", e)
            emptyMap()
        }
    }

    suspend fun addUserService(userId: String, service: Service): Boolean {
        return try {
            val serviceKey = userRef.child(userId).child("services").push().key
                ?: return false

            userRef.child(userId)
                .child("services")
                .child(serviceKey)
                .setValue(service)
                .await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding service", e)
            false
        }
    }
}
