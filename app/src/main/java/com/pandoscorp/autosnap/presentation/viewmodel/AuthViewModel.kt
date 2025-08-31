package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.domain.model.Client
import com.pandoscorp.autosnap.domain.model.User
import com.pandoscorp.autosnap.repository.ClientRepository
import com.pandoscorp.autosnap.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val userRepository: UserRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _registrationState = MutableStateFlow("")
    val registrationState: StateFlow<String> = _registrationState

    private val _loginState = MutableStateFlow("")
    val loginState: StateFlow<String> = _loginState

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> = _errorState

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app")

    fun setError(message: String) {
        _errorState.value = message
    }

    fun registerUser(user: User, password: String, clientData: Client? = null) {
        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
                val userId = authResult.user?.uid ?: throw Exception("User ID is null")

                if (clientData != null) {
                    clientData.id = userId
                    clientRepository.addRealClient(clientData)
                } else {
                    val serviceUser = user.copy(
                        id = userId,
                        address = user.address ?: "",
                        services = emptyMap()
                    )
                    database.getReference("users/$userId").setValue(serviceUser).await()
                }

                _registrationState.value = "Пользователь успешно зарегистрирован"
            } catch (e: Exception) {
                _errorState.value = "Ошибка регистрации: ${e.message}"
            }
        }
    }


    // Функция для входа пользователя
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _errorState.value = ""
                _loginState.value = "Выполняется вход..."

                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid ?: throw Exception("User ID is null")

                val isClient = checkIfUserIsClient(userId)

                _loginState.value = if (isClient) {
                    "CLIENT_LOGIN_SUCCESS"
                } else {
                    "SERVICE_LOGIN_SUCCESS"
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Ошибка входа", e)
                _errorState.value = "Ошибка входа: ${e.message}"
                _loginState.value = ""
            }
        }
    }

    private suspend fun checkIfUserIsClient(userId: String): Boolean {
        return try {
            val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
            // Проверяем наличие записи в clients/{userId}
            val clientSnapshot = FirebaseDatabase.getInstance(databaseUrl)
                .getReference("clients/$userId")
                .get()
                .await()

            clientSnapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    // Функция для очистки состояний
    fun clearStates() {
        _registrationState.value = ""
        _loginState.value = ""
        _errorState.value = ""
    }
}