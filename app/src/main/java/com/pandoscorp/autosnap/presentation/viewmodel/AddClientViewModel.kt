import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.pandoscorp.autosnap.domain.model.Car
import com.pandoscorp.autosnap.domain.model.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddClientViewModel() : ViewModel() {

    private val databaseUrl =
        "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    val auth = Firebase.auth


    private val _uiState = MutableStateFlow(Client())
    val uiState: StateFlow<Client> = _uiState

    private val _isCarFormVisible = MutableStateFlow(false)
    val isCarFormVisible: StateFlow<Boolean> = _isCarFormVisible

    private var _currentCarData = MutableStateFlow(Car())
    val currentCarData: StateFlow<Car> = _currentCarData

    fun toggleCarFormVisibility() {
        _isCarFormVisible.value = !_isCarFormVisible.value
    }

    fun updateField(field: String, value: String) {
        when (field) {
            "name" -> _uiState.value = _uiState.value.copy(name = value)
            "surname" -> _uiState.value = _uiState.value.copy(surname = value)
            "birthdate" -> _uiState.value = _uiState.value.copy(birthdate = value)
            "email" -> _uiState.value = _uiState.value.copy(email = value)
            "phone" -> _uiState.value = _uiState.value.copy(phone = value)
            "note" -> _uiState.value = _uiState.value.copy(note = value)
            "carBrand" -> _currentCarData.value = _currentCarData.value.copy(brand = value)
            "carModel" -> _currentCarData.value = _currentCarData.value.copy(model = value)
            "carYear" -> _currentCarData.value = _currentCarData.value.copy(year = value)
            "carEngineVolume" -> _currentCarData.value = currentCarData.value.copy(engineVolume = value.toDoubleOrNull() ?: 0.0)
            "carHorsePower" -> _currentCarData.value = currentCarData.value.copy(horsePower = value)
        }
    }

    fun saveCar() {
        if (_currentCarData.value.brand.isNotEmpty() && _currentCarData.value.model.isNotEmpty()) {
            // Генерируем уникальный ID для автомобиля
            val carWithId = _currentCarData.value.copy(id = UUID.randomUUID().toString())
            val updatedCars = _uiState.value.cars + carWithId
            _uiState.value = _uiState.value.copy(cars = updatedCars)
            _currentCarData.value = Car()
            toggleCarFormVisibility()
        }
    }

    private fun getClientRef(): DatabaseReference {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            throw IllegalStateException("Пользователь не авторизован")
        }
        return database.getReference("users").child(userId).child("clients")
    }

    fun saveClientToFirebase() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: run {
                println("Ошибка: Пользователь не авторизован")
                return@launch
            }

            val client = _uiState.value
            val clientRef = getClientRef()
            val clientId = clientRef.push().key ?: run {
                println("Не удалось создать уникальный ключ для клиента")
                return@launch
            }

            val clientWithoutCars = client.copy(cars = emptyList(), id = clientId)

            clientRef.child(clientId).setValue(clientWithoutCars)

            val carsRef = clientRef.child(clientId).child("cars")
            client.cars.forEach { car ->
                val carId = carsRef.push().key ?: return@forEach
                carsRef.child(carId).setValue(car.copy(id = carId))
            }
        }
    }

    fun clearState() {
        _uiState.value = Client()
        _isCarFormVisible.value = false
        _currentCarData.value = Car()
    }
}