package com.pandoscorp.autosnap.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GarageMasterChat() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Шапка чата
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color(0xFF4A6FA5))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF006D3E))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GM",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Гаражный Мастер",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Онлайн",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Список сообщений
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages.reversed()) { message ->
                MessageItem(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Поле ввода (статичное, без логики)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("Напишите сообщение...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF4A6FA5))
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Отправить",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    val bubbleColor = if (message.isFromClient) {
        Color(0xFF4A6FA5)
    } else {
        Color.White
    }

    val textColor = if (message.isFromClient) {
        Color.White
    } else {
        Color.Black
    }

    val bubbleShape = if (message.isFromClient) {
        RoundedCornerShape(16.dp).copy(
            topEnd = CornerSize(4.dp)
        )
    } else {
        RoundedCornerShape(16.dp).copy(
            topStart = CornerSize(4.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.isFromClient) 48.dp else 0.dp,
                end = if (!message.isFromClient) 48.dp else 0.dp
            ),
        contentAlignment = if (message.isFromClient) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bubbleColor)
                    .padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 16.sp
                )
            }

            Text(
                text = message.sender,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

data class ChatMessage(
    val text: String,
    val sender: String,
    val isFromClient: Boolean
)

val messages = listOf(
    ChatMessage(
        text = "Здравствуйте! Подскажите, пожалуйста, сколько будет стоить замена ремня ГРМ на Volkswagen Polo?",
        sender = "Анастасия",
        isFromClient = true
    ),
    ChatMessage(
        text = "Добрый день, Анастасия! Для Volkswagen Polo замена ремня ГРМ с роликами и помпой стоит 8500 рублей с учетом запчастей.",
        sender = "Гаражный Мастер",
        isFromClient = false
    ),
    ChatMessage(
        text = "А сколько времени займет работа?",
        sender = "Анастасия",
        isFromClient = true
    ),
    ChatMessage(
        text = "Обычно это занимает около 3-4 часов. Можем выполнить работу в тот же день, если запишетесь с утра.",
        sender = "Гаражный Мастер",
        isFromClient = false
    ),
    ChatMessage(
        text = "Спасибо! А какие у вас график работы?",
        sender = "Анастасия",
        isFromClient = true
    ),
    ChatMessage(
        text = "Мы работаем с 8:00 до 20:00 без выходных. Можем записать вас на удобное время.",
        sender = "Гаражный Мастер",
        isFromClient = false
    )
)