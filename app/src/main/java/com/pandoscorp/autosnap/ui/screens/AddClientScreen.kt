// AddClientScreen.kt
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pandoscorp.autosnap.R
import com.pandoscorp.autosnap.navigation.ScreenObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientForm(
    navController: NavHostController,
    viewModel: AddClientViewModel
) {
    val userState by viewModel.uiState.collectAsState()
    val isCarFormVisible by viewModel.isCarFormVisible.collectAsState()
    val currentCarData by viewModel.currentCarData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Новый клиент")
                },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveClientToFirebase()
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Done"
                        )
                    }
                }
            )
        },
        content = {paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                item{
                    OutlinedTextField(
                        value = userState.name,
                        onValueChange = { viewModel.updateField("name", it) },
                        label = { Text("Имя") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }

                item{Spacer(modifier = Modifier.height(8.dp))}

                item{
                    OutlinedTextField(
                        value = userState.surname,
                        onValueChange = { viewModel.updateField("surname", it) },
                        label = { Text("Фамилия") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }

                item{Spacer(modifier = Modifier.height(8.dp))}

                item{
                    OutlinedTextField(
                        value = userState.birthdate,
                        onValueChange = { viewModel.updateField("birthdate", it) },
                        label = { Text("Дата рождения") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                item{Spacer(modifier = Modifier.height(8.dp))}

                item{
                    OutlinedTextField(
                        value = userState.phone,
                        onValueChange = { viewModel.updateField("phone", it) },
                        label = { Text("Телефон") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }

                item{Spacer(modifier = Modifier.height(16.dp))}

                item{
                    Text(
                        "Автомобили",
                        fontSize = 20.sp
                    )
                }

                item{Spacer(modifier = Modifier.height(8.dp))}

                item{
                    Button(
                        onClick = { viewModel.toggleCarFormVisibility() },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.DarkGray
                        )
                    ) {
                        Text(if (isCarFormVisible) "Скрыть" else "Добавить автомобиль")
                    }
                }

                if (isCarFormVisible) {
                    item{Spacer(modifier = Modifier.height(8.dp))}

                    item{
                        OutlinedTextField(
                            value = currentCarData.brand,
                            onValueChange = { viewModel.updateField("carBrand", it) },
                            label = { Text("Марка") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item{Spacer(modifier = Modifier.height(8.dp))}

                    item{
                        OutlinedTextField(
                            value = currentCarData.model,
                            onValueChange = { viewModel.updateField("carModel", it) },
                            label = { Text("Модель") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item{Spacer(modifier = Modifier.height(8.dp))}

                    item{
                        OutlinedTextField(
                            value = currentCarData.year,
                            onValueChange = { viewModel.updateField("carYear", it) },
                            label = { Text("Год выпуска") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    item{Spacer(modifier = Modifier.height(8.dp))}

                    item{
                        OutlinedTextField(
                            value = currentCarData.engineVolume.toString(),
                            onValueChange = { viewModel.updateField("carEngineVolume", it) },
                            label = { Text("Объем двигателя") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    item{Spacer(modifier = Modifier.height(8.dp))}

                    item{
                        OutlinedTextField(
                            value = currentCarData.horsePower,
                            onValueChange = { viewModel.updateField("carHorsePower", it) },
                            label = { Text("Лошадиные силы") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    item{Spacer(modifier = Modifier.height(8.dp))}

                    item{
                        Button(
                            onClick = { viewModel.saveCar() },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color.DarkGray
                            )
                        ) {
                            Text("Сохранить автомобиль")
                        }
                    }
                }
                if (userState.cars.isNotEmpty()) {
                    item{Spacer(modifier = Modifier.height(16.dp))}
                    item{
                        Text(
                            text = "Автомобили:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    userState.cars.forEachIndexed { index, car ->
                        item{
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 3.dp, // Толщина обводки
                                        color = Color.Black, // Цвет обводки
                                        shape = RectangleShape
                                    )
                                    .padding(15.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}. ${car.brand} ${car.model} (${car.year})",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    )
}