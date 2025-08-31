// AddClientScreen.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientForm(
    navController: NavHostController,
    viewModel: AddClientViewModel
) {
    val userState by viewModel.uiState.collectAsState()
    val isCarFormVisible by viewModel.isCarFormVisible.collectAsState()
    val currentCarData by viewModel.currentCarData.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Новый клиент",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (userState.name.isBlank() || userState.surname.isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Заполните обязательные поля",
                                        withDismissAction = true
                                    )
                                }
                                return@IconButton
                            }
                            viewModel.saveClientToFirebase()
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Клиент добавлен",
                                    duration = SnackbarDuration.Short
                                )
                                delay(500)
                                navController.popBackStack()
                            }
                            viewModel.clearState()
                        },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Сохранить",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Основная информация о клиенте
                item {
                    Text(
                        "Основная информация",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    FormTextField(
                        value = userState.name,
                        onValueChange = { viewModel.updateField("name", it) },
                        label = "Имя*",
                        placeholder = "Введите имя",
                    )
                }

                item {
                    FormTextField(
                        value = userState.surname,
                        onValueChange = { viewModel.updateField("surname", it) },
                        label = "Фамилия*",
                        placeholder = "Введите фамилию",
                    )
                }

                item {
                    FormTextField(
                        value = userState.birthdate,
                        onValueChange = { viewModel.updateField("birthdate", it) },
                        label = "Дата рождения",
                        placeholder = "ДД.ММ.ГГГГ",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }

                item {
                    FormTextField(
                        value = userState.phone,
                        onValueChange = { viewModel.updateField("phone", it) },
                        label = "Телефон",
                        placeholder = "+7 (XXX) XXX-XX-XX",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                }

                item {
                    FormTextField(
                        value = userState.note,
                        onValueChange = { viewModel.updateField("note", it) },
                        label = "Заметка",
                        placeholder = "Дополнительная информация",
                    )
                }

                // Секция автомобилей
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Автомобили",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        FilledTonalButton(
                            onClick = { viewModel.toggleCarFormVisibility() },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(if (isCarFormVisible) "Скрыть" else "Добавить")
                        }
                    }
                }

                if (isCarFormVisible) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                FormTextField(
                                    value = currentCarData.brand,
                                    onValueChange = { viewModel.updateField("carBrand", it) },
                                    label = "Марка",
                                    placeholder = "Например: Toyota",
                                )

                                FormTextField(
                                    value = currentCarData.model,
                                    onValueChange = { viewModel.updateField("carModel", it) },
                                    label = "Модель",
                                    placeholder = "Например: Camry",
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    FormTextField(
                                        value = currentCarData.year,
                                        onValueChange = { viewModel.updateField("carYear", it) },
                                        label = "Год",
                                        placeholder = "2020",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )

                                    FormTextField(
                                        value = currentCarData.engineVolume.toString(),
                                        onValueChange = { viewModel.updateField("carEngineVolume", it) },
                                        label = "Объем (л)",
                                        placeholder = "2.5",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                FormTextField(
                                    value = currentCarData.horsePower,
                                    onValueChange = { viewModel.updateField("carHorsePower", it) },
                                    label = "Мощность (л.с.)",
                                    placeholder = "180",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                )

                                Button(
                                    onClick = { viewModel.saveCar() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 2.dp,
                                        pressedElevation = 4.dp
                                    )
                                ) {
                                    Text("Сохранить автомобиль")
                                }
                            }
                        }
                    }
                }

                // Список сохраненных автомобилей
                if (userState.cars!!.isNotEmpty()) {
                    item {
                        Text(
                            "Сохраненные автомобили",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(userState.cars!!) { car ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "${car.brand} ${car.model}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "${car.year} год • ${car.engineVolume} л • ${car.horsePower} л.с.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                IconButton(
                                    onClick = { /* TODO: Редактирование автомобиля */ },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Редактировать",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next
    ),
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        leadingIcon = leadingIcon,
        // Add these default parameters to match the function signature
        supportingText = null,
        isError = false,
        visualTransformation = VisualTransformation.None,
        keyboardActions = KeyboardActions.Default,
        interactionSource = remember { MutableInteractionSource() }
    )
}