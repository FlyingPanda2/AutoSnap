package com.pandoscorp.autosnap.ui.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.material3.Card
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.navigation.ScreenObject
import com.pandoscorp.autosnap.repository.UserRepository
import com.pandoscorp.autosnap.ui.viewmodel.ServiceViewModel
import com.pandoscorp.autosnap.ui.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceChooseForm(
    navController: NavHostController,
    serviceViewModel: ServiceViewModel
) {
    var isChecked by remember { mutableStateOf(false) }

    val services by serviceViewModel.services.collectAsState()
    val currentUser = Firebase.auth.currentUser

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
                    IconButton(onClick = { }) {
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
                    text = "ООО:",
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
                    modifier = Modifier.padding(paddingValues)
                ) {
                    items(services) { service ->
                        ServiceItem(
                            service = service,
                            onClick = {}
                        )
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
fun ServiceItem(
    service: Service,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Цена: ${service.price} ₽")
            Text("Длительность: ${service.duration} мин")
            Text("Описание: ${service.description}")
        }
    }
}