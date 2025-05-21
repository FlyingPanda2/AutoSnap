package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pandoscorp.autosnap.model.Client
import com.pandoscorp.autosnap.model.User
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

    fun setError(message: String) {
        _errorState.value = message
    }

    fun registerUser(user: User, password: String, clientData: Client? = null) {
        viewModelScope.launch {
            try {
                // 1. Регистрация в FirebaseAuth
                val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
                val userId = authResult.user?.uid ?: throw Exception("User ID is null")

                if (clientData != null) {
                    // Клиент: сохраняем только в clients
                    clientData.id = userId
                    clientRepository.addRealClient(clientData)
                } else {
                    // Автосервис: сохраняем только в users
                    user.id = userId
                    userRepository.registerUser(user, password)
                }

                _registrationState.value = "Пользователь успешно зарегистрирован"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Ошибка регистрации: ${e.message}", e)
                _errorState.value = "Ошибка регистрации: ${e.message}"
            }
        }
    }

    // Функция для входа пользователя
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.loginUser(email, password)
                _loginState.value = result
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Ошибка входа: ${e.message}", e)
                _errorState.value = "Ошибка входа: ${e.message}"
            }
        }
    }

    // Функция для очистки состояний
    fun clearStates() {
        _registrationState.value = ""
        _loginState.value = ""
        _errorState.value = ""
    }
}