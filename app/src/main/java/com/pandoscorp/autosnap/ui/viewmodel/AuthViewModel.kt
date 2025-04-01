package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandoscorp.autosnap.model.User
import com.pandoscorp.autosnap.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationState = MutableStateFlow("")
    val registrationState: StateFlow<String> = _registrationState

    private val _loginState = MutableStateFlow("")
    val loginState: StateFlow<String> = _loginState

    private val _errorState = MutableStateFlow("")
    val errorState: StateFlow<String> = _errorState

    fun setError(message: String) {
        _errorState.value = message
    }

    // Функция для регистрации пользователя
    fun registerUser(user: User, password: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.registerUser(user, password)
                _registrationState.value = result
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