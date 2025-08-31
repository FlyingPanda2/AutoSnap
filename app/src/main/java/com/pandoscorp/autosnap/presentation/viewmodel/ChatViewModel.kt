package com.pandoscorp.autosnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandoscorp.autosnap.domain.model.ChatMessage
import com.pandoscorp.autosnap.repository.ChatRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: MutableStateFlow<List<ChatMessage>> = _messages

    private val _state = MutableStateFlow(ChatState())
    val state: MutableStateFlow<ChatState> = _state

    fun loadMessages(userId: String, shopId: String) {
        viewModelScope.launch {
            repository.getMessages(userId, shopId)
                .onStart { _state.value = _state.value.copy(isLoading = true) }
                .catch { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Unknown error",
                        isLoading = false
                    )
                }
                .collect { messages ->
                    _messages.value = messages
                    _state.value = _state.value.copy(isLoading = false)
                }
        }
    }

    fun sendMessage(userId: String, shopId: String, text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true)

            val message = ChatMessage(
                senderId = userId,
                receiverId = shopId,
                text = text.trim()
            )

            repository.sendMessage(message)
                .onSuccess {
                    // Успешная отправка
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Send failed",
                        isSending = false
                    )
                }
            _state.value = _state.value.copy(isSending = false)
        }
    }
}

data class ChatState(
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null
)