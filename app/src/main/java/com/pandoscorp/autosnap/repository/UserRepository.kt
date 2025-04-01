package com.pandoscorp.autosnap.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.model.User
import kotlinx.coroutines.tasks.await
import kotlin.runCatching

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val databaseUrl =
        "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    private val userRef: DatabaseReference = database.getReference("users")

    //Регистрация пользователя
    suspend fun registerUser(user: User, password: String): String {
        try{
            auth.createUserWithEmailAndPassword(user.email, password)

            val userId = auth.currentUser?.uid
            if(userId != null){
                user.id = userId
                userRef.child(userId).setValue(user).await()
            }
            return "Пользователь успешно зарегистрирован"
        }catch (e: Exception){
            Log.e("UserRepository", "Ошибка регистрации: ${e.message}", e)
            return "Ошибка регистрации: ${e.message}"
        }
    }

    //Вход пользователя
    suspend fun loginUser(email: String, password: String): String {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            return "Вход выполнен успешно"
        }catch (e: Exception){
            Log.e("UserRepository", "Ошибка входа: ${e.message}", e)
            return "Ошибка входа: ${e.message}"
        }
    }

    //Получение пользователя по ID
    suspend fun getUserById(userId: String): User? {
        try {
            val snapshot = userRef.child(userId).get().await()
            if (snapshot.exists()) {
                return snapshot.getValue(User::class.java)
            } else {
                return null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Ошибка получения пользователя: ${e.message}", e)
            return null
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
}
