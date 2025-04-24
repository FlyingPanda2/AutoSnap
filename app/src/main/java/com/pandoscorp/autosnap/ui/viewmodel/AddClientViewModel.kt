import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddClientViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val databaseUrl =
        "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    private val clientRef: DatabaseReference = database.getReference("client")

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
            "carBrand" -> _currentCarData.value = _currentCarData.value.copy(brand = value)
            "carModel" -> _currentCarData.value = _currentCarData.value.copy(model = value)
            "carYear" -> _currentCarData.value = _currentCarData.value.copy(year = value)
            "carEngineVolume" -> _currentCarData.value = currentCarData.value.copy(engineVolume = value.toDoubleOrNull() ?: 0.0)
            "carHorsePower" -> _currentCarData.value = currentCarData.value.copy(horsePower = value)
        }
    }

    fun saveCar() {
        if (_currentCarData.value.brand.isNotEmpty() && _currentCarData.value.model.isNotEmpty()) {
            val updatedCars = _uiState.value.cars + _currentCarData.value
            _uiState.value = _uiState.value.copy(cars = updatedCars)
            _currentCarData.value = Car() // Очистить данные текущего автомобиля
            toggleCarFormVisibility() // Скрыть форму
        }
    }

    fun saveClientToFirebase() {
        viewModelScope.launch {
            val client = _uiState.value
            val clientId = clientRef.push().key
            clientId?.let {
                val updatedUser = client.copy(id = it)
                clientRef.child(it).setValue(updatedUser)
                clientRef.child(it).child("cars").setValue(_currentCarData.value)
            }
        }
    }
}