package com.pandoscorp.autosnap.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.model.User
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.repository.UserRepository
import com.pandoscorp.autosnap.ui.viewmodel.ServiceViewModel
import com.pandoscorp.autosnap.ui.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ServiceChooseForm(
    navController: NavHostController,
    serviceViewModel: ServiceViewModel,
    sharedViewModel: SharedViewModel,
    currentUser: User?
) {

    val services by serviceViewModel.services.collectAsState()

    val username = currentUser?.username ?: "Гость"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Услуги") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(ScreenObject.NewAppointmentScreen.route) },
                        enabled = sharedViewModel.selectedServices.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Done"
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White)
                        .clickable { navController.navigate(ScreenObject.AddServiceScreen.route) },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "+ Добавить услугу вручную",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )

                        Icon(
                            Icons.Sharp.KeyboardArrowRight,
                            contentDescription = "Done",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ООО: ${username}",
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 3.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Мои услуги",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(services) { service ->

                        SelectableServiceItem(
                            service = service.copy(
                                isSelected = sharedViewModel.selectedServices.any { it.id == service.id }
                            ),
                            onToggle = {
                                if (sharedViewModel.selectedServices.any { it.id == service.id }) {
                                    sharedViewModel.removeSelectedService(service.id)
                                } else {
                                    sharedViewModel.addSelectedService(service)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun AnimatedCircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 24.dp
) {
    val transition = updateTransition(checked, label = "checkboxTransition")
    val borderColor by transition.animateColor(label = "borderColor") { isChecked ->
        if (isChecked) color else Color.Gray
    }
    val checkAlpha by transition.animateFloat(label = "checkAlpha") { isChecked ->
        if (isChecked) 1f else 0f
    }

    Box(
        modifier = modifier
            .size(size)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Checked",
            tint = color,
            modifier = Modifier
                .size(size * 0.7f)
                .alpha(checkAlpha)
        )
    }
}

@Composable
fun SelectableServiceItem(
    service: Service,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (service.isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onToggle() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardColors(containerColor = Color.White, contentColor = Color.Black, disabledContentColor = Color.White, disabledContainerColor = Color.White)
        ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Основное содержимое карточки
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = service.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Цена: ${service.price} ₽")
                Text(text = "Длительность: ${service.duration} мин")
                Text(text = "Описание: ${service.description}")
            }

            // Галочка с ручной анимацией
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .alpha(animatedAlpha)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Выбрано",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}