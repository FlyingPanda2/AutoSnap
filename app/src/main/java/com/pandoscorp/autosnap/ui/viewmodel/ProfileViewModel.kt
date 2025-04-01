package com.pandoscorp.autosnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandoscorp.autosnap.model.User
import com.pandoscorp.autosnap.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Состояние пользователя
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Состояние ошибок
    private val _errorState = MutableStateFlow<String>("")
    val errorState: StateFlow<String> = _errorState

    // Состояние загрузки
    private val _loadingState = MutableStateFlow<Boolean>(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    // Функция для получения пользователя по ID
    fun getUserById(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                val result = userRepository.getUserById(userId)
                if (result != null) {
                    _user.value = result
                } else {
                    _errorState.value = "Пользователь не найден"
                }
            } catch (e: Exception) {
                _errorState.value = e.message ?: "Ошибка получения данных"
            } finally {
                _loadingState.value = false
            }
        }
    }

    // Функция для обновления пользователя
    fun updateUser(user: User) {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                userRepository.updateUser(user)
                _errorState.value = "Данные успешно обновлены"
            } catch (e: Exception) {
                _errorState.value = e.message ?: "Ошибка обновления данных"
            } finally {
                _loadingState.value = false
            }
        }
    }

    // Функция для очистки состояний
    fun clearStates() {
        _errorState.value = ""
    }
}