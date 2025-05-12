package com.pandoscorp.autosnap.repository

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pandoscorp.autosnap.model.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository(private val firestore: FirebaseFirestore) {

    fun getMessages(userId: String, shopId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("chat_messages")
            .where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("senderId", userId),
                        Filter.equalTo("receiverId", shopId)
                    ),
                    Filter.and(
                        Filter.equalTo("senderId", shopId),
                        Filter.equalTo("receiverId", userId)
                    )
                )
            )
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(ChatMessage::class.java) ?: emptyList()
                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(message: ChatMessage): Result<Unit> {
        return try {
            firestore.collection("chat_messages")
                .document()
                .set(message)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}