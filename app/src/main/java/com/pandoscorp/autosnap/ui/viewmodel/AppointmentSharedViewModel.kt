package com.pandoscorp.autosnap.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import com.pandoscorp.autosnap.model.Car
import com.pandoscorp.autosnap.model.Client
import com.pandoscorp.autosnap.model.Service
import com.pandoscorp.autosnap.model.SimpleDate
import com.pandoscorp.autosnap.utilis.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime
import java.util.Date
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pandoscorp.autosnap.model.Appointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class AppointmentSharedViewModel: ViewModel() {
    private val _selectedClient = MutableStateFlow<Client?>(null)
    val selectedClient: StateFlow<Client?> = _selectedClient

    private val _selectedCar = MutableStateFlow<Car?>(null)
    val selectedCar: StateFlow<Car?> = _selectedCar

    private val _selectedServices = mutableStateListOf<Service>()
    val selectedServices: List<Service> get() = _selectedServices

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate

    // Для хранения состояния выбора (из меню или из формы)
    private val _isDateSelectionMode = MutableStateFlow(false)
    val isDateSelectionMode: StateFlow<Boolean> = _isDateSelectionMode

    private val _durationHours = MutableStateFlow(0)
    val durationHours: StateFlow<Int> = _durationHours

    private val _durationMinutes = MutableStateFlow(0)
    val durationMinutes: StateFlow<Int> = _durationMinutes

    private val _startTime = MutableStateFlow<LocalTime?>(null)
    val startTime: StateFlow<LocalTime?> = _startTime

    private val _serviceCenterAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    private val _personalAppointments = MutableStateFlow<List<Appointment>>(emptyList())

    // Функция для проверки, является ли запись сервисной
    fun isServiceCenterAppointment(appointment: Appointment): Boolean {
        return appointment.serviceCenterId.isNotEmpty()
    }

    suspend fun loadAppointmentClient(appointmentId: String): Client? {
        return try {
            // Получаем clientId из записи
            val snapshot = database.getReference("appointments/$appointmentId").get().await()
            val clientId = snapshot.child("clientId").value as? String ?: return null

            // Загружаем данные клиента
            database.getReference("clients/$clientId").get().await()
                .getValue(Client::class.java)
                ?.copy(id = clientId)
        } catch (e: Exception) {
            Log.e("Appointments", "Error loading appointment client", e)
            null
        }
    }

    suspend fun loadAppointmentCar(appointmentId: String): Car? {
        return try {
            // Получаем clientId и carId из записи
            val snapshot = database.getReference("appointments/$appointmentId").get().await()
            val clientId = snapshot.child("clientId").value as? String ?: return null
            val carId = snapshot.child("carId").value as? String ?: return null

            // Загружаем данные автомобиля
            database.getReference("clients/$clientId/cars/$carId").get().await()
                .getValue(Car::class.java)
                ?.copy(id = carId)
        } catch (e: Exception) {
            Log.e("Appointments", "Error loading appointment car", e)
            null
        }
    }

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    // 2. Улучшенная функция загрузки
    fun loadAppointmentsForDate(date: SimpleDate) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: run {
                    _appointments.value = emptyList()
                    return@launch
                }

                val dbDateFormat = "${date.day.toString().padStart(2, '0')}.${date.month.toString().padStart(2, '0')}.${date.year}"

                // Загрузка сервисных записей
                database.getReference("appointments")
                    .orderByChild("date")
                    .equalTo(dbDateFormat)
                    .get()
                    .await()
                    .children
                    .mapNotNull { it.getValue(Appointment::class.java) }
                    .filter { it.serviceCenterId == userId }
                    .also { serviceApps ->
                        // Загрузка личных записей
                        val personalApps = database.getReference("users/$userId/appointments")
                            .orderByChild("date")
                            .equalTo(dbDateFormat)
                            .get()
                            .await()
                            .children
                            .mapNotNull { it.getValue(Appointment::class.java) }

                        // Обновление StateFlow в основном потоке
                        withContext(Dispatchers.Main) {
                            _appointments.value = serviceApps + personalApps
                            Log.d("Appointments", "UI should update with ${_appointments.value.size} items")
                        }
                    }
            } catch (e: Exception) {
                Log.e("Appointments", "Error: ${e.message}")
                _appointments.value = emptyList()
            }
        }
    }

    // Функции для принятия/отклонения записей
    fun acceptAppointment(appointment: Appointment, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                database.getReference("appointments/${appointment.id}/status")
                    .setValue("accepted")
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e.message ?: "Ошибка принятия записи") }
            } catch (e: Exception) {
                onError(e.message ?: "Ошибка принятия записи")
            }
        }
    }

    fun rejectAppointment(appointment: Appointment, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                database.getReference("appointments/${appointment.id}/status")
                    .setValue("rejected")
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e.message ?: "Ошибка отклонения записи") }
            } catch (e: Exception) {
                onError(e.message ?: "Ошибка отклонения записи")
            }
        }
    }

    fun updateDuration(hours: Int, minutes: Int) {
        _durationHours.value = hours
        _durationMinutes.value = minutes
    }

    fun setDateSelectionMode(enabled: Boolean) {
        _isDateSelectionMode.value = enabled
    }

    fun selectDate(date: Date) {
        _selectedDate.value = date
    }

    fun clearDate() {
        _selectedDate.value = null
    }

    fun addSelectedService(service: Service) {
        if (!_selectedServices.any { it.id == service.id }) {
            _selectedServices.add(service)
        }
    }

    fun removeSelectedService(serviceId: String) {
        _selectedServices.removeAll { it.id == serviceId }
    }

    fun clearSelectedServices() {
        _selectedServices.clear()
    }

    fun selectClient(client: Client) {
        _selectedClient.value = client
        _selectedCar.value = null
    }

    fun selectCar(car: Car) {
        _selectedCar.value = car
    }

    private val currentDate = DateUtils.getCurrentDate()

    private val _state = MutableStateFlow(
        ScheduleState(
            selectedDate = currentDate,
            currentMonth = currentDate.month,
            currentYear = currentDate.year,
            markedDates = setOf(currentDate.toKeyString())
        )
    )
    val state: StateFlow<ScheduleState> = _state

    fun selectDate(date: SimpleDate) {
        _state.value = _state.value.copy(selectedDate = date)
    }

    fun changeMonth(month: Int) {
        _state.value = _state.value.copy(currentMonth = month)
    }

    fun changeYear(year: Int) {
        _state.value = _state.value.copy(currentYear = year)
    }

    val totalDuration: StateFlow<String>
        get() = MutableStateFlow(
            _selectedServices.sumOf { it.duration }.let { totalMinutes ->
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                when {
                    hours > 0 && minutes > 0 -> "${hours}ч ${minutes}мин"
                    hours > 0 -> "${hours}ч"
                    else -> "${minutes}мин"
                }
            }
        ).asStateFlow()

    fun setStartTime(time: LocalTime) {
        _startTime.value = time
    }

    private val _discountPercent = MutableStateFlow(0) // 0-100%
    val discountPercent: StateFlow<Int> = _discountPercent

    val totalPrice: StateFlow<Int> = snapshotFlow { _selectedServices.toList() }
        .map { services -> services.sumOf { it.price } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Сумма скидки в рублях
    val discountAmount: StateFlow<Int> = combine(
        totalPrice,
        discountPercent
    ) { price, percent ->
        (price * percent / 100).coerceAtMost(price) // Не больше чем общая стоимость
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun setDiscountPercent(percent: Int) {
        _discountPercent.value = percent.coerceIn(0, 100)
    }

    fun loadClientCars(clientId: String, onSuccess: (List<Car>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: run {
                    onError("Пользователь не авторизован")
                    return@launch
                }

                database.getReference("users/$userId/clients/$clientId/cars")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val cars = snapshot.children.mapNotNull { child ->
                            try {
                                child.getValue(Car::class.java)?.copy(id = child.key ?: "")
                            } catch (e: Exception) {
                                null
                            }
                        }
                        onSuccess(cars)
                    }
                    .addOnFailureListener {
                        onError("Ошибка загрузки автомобилей: ${it.message ?: "Неизвестная ошибка"}")
                    }
            } catch (e: Exception) {
                onError("Ошибка: ${e.message}")
            }
        }
    }

    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveAppointment(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val client = _selectedClient.value
        val car = _selectedCar.value
        val services = _selectedServices.toList()
        val date = _state.value.selectedDate
        val time = _startTime.value

        if (client == null) {
            onError("Выберите клиента")
            return
        }
        if (car == null) {
            onError("Выберите автомобиль")
            return
        }
        if (services.isEmpty()) {
            onError("Выберите хотя бы одну услугу")
            return
        }
        if (time == null) {
            onError("Укажите время начала")
            return
        }

        val userId = auth.currentUser?.uid ?: run {
            onError("Пользователь не авторизован")
            return
        }

        // Форматирование даты и времени
        val dateStr = "${date.year}-${if (date.month < 10) "0${date.month}" else date.month}-${if (date.day < 10) "0${date.day}" else date.day}"
        val timeStr = time.format(DateTimeFormatter.ofPattern("HH:mm"))
        val createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Расчет итоговой цены
        val totalPrice = services.sumOf { it.price }
        val finalPrice = totalPrice - (totalPrice * _discountPercent.value / 100)

        val appointment = Appointment(
            clientId = client.id,
            carId = car.id,
            serviceIds = services.map { it.id },
            date = dateStr,
            time = timeStr,
            totalPrice = finalPrice,
            discountPercent = _discountPercent.value,
            createdAt = createdAt
        )

        viewModelScope.launch {
            try {
                val newAppointmentRef = database.getReference("users/$userId/appointments").push()
                appointment.id = newAppointmentRef.key ?: ""
                newAppointmentRef.setValue(appointment)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError("Ошибка сохранения: ${e.message}")
                    }
            } catch (e: Exception) {
                onError("Ошибка: ${e.message}")
            }
        }
    }

    fun convertDbDateToAppFormat(dbDate: String): String {
        return try {
            val parts = dbDate.split(".")
            // Преобразуем "23.05.2025" в "2025-05-23"
            "${parts[2]}-${parts[1]}-${parts[0]}"
        } catch (e: Exception) {
            Log.e("DateConvert", "Error converting date: $dbDate", e)
            dbDate // Возвращаем как есть в случае ошибки
        }
    }

    suspend fun getClientById(clientId: String): Client? = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: return@withContext null
            val ref = database.getReference("users/$userId/clients/$clientId")
            val snapshot = ref.get().await()

            if (!snapshot.exists()) return@withContext null

            // Читаем все поля вручную
            val id = clientId
            val name = snapshot.child("name").value as? String ?: ""
            val surname = snapshot.child("surname").value as? String ?: ""
            val phone = snapshot.child("phone").value as? String ?: ""
            val email = snapshot.child("email").value as? String ?: ""
            val birthdate = snapshot.child("birthdate").value as? String ?: ""
            val note = snapshot.child("note").value as? String ?: ""

            // Получаем Map<String, Car>
            val carsMap = snapshot.child("cars").children.mapNotNull { carSnapshot ->
                val carKey = carSnapshot.key ?: return@mapNotNull null
                val car = carSnapshot.getValue(Car::class.java)?.copy(id = carKey)
                car
            }

            // Создаём клиент с List<Car>
            Client(
                id = id,
                name = name,
                surname = surname,
                phone = phone,
                email = email,
                birthdate = birthdate,
                note = note,
                cars = carsMap
            )
        } catch (e: Exception) {
            Log.e("ClientLoad", "Ошибка при загрузке клиента", e)
            null
        }
    }

    suspend fun getServicesByIds(serviceIds: List<String>): List<Service> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: return@withContext emptyList()
            val servicesRef = database.getReference("users/$userId/services")
            val snapshot = servicesRef.get().await()

            val allServices = snapshot.children.mapNotNull { child ->
                val serviceId = child.key ?: return@mapNotNull null
                val service = child.getValue(Service::class.java)?.copy(id = serviceId)
                service
            }

            return@withContext allServices.filter { it.id in serviceIds }
        } catch (e: Exception) {
            Log.e("ServiceLoad", "Ошибка при загрузке услуг", e)
            emptyList()
        }
    }

    suspend fun getCarById(clientId: String, carId: String): Car? = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: return@withContext null
            val snapshot = database.getReference("users/$userId/clients/$clientId/cars/$carId").get().await()
            Log.d("CarLoad", "Загружаем автомобиль с ID: $carId для клиента: $clientId")
            snapshot.getValue(Car::class.java)?.copy(id = carId)
        } catch (e: Exception) {
            Log.e("CarLoad", "Ошибка при загрузке автомобиля", e)
            null
        }
    }

    fun deleteAppointment(appointmentId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: run {
                    onError("Пользователь не авторизован")
                    return@launch
                }

                database.getReference("users/$userId/appointments/$appointmentId")
                    .removeValue()
                    .addOnSuccessListener {
                        // Обновляем список после удаления
                        loadAppointmentsForDate(state.value.selectedDate)
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError("Ошибка удаления: ${e.message}")
                    }
            } catch (e: Exception) {
                onError("Ошибка: ${e.message}")
            }
        }
    }

}

data class ScheduleState(
    val selectedDate: SimpleDate,
    val currentMonth: Int,
    val currentYear: Int,
    val markedDates: Set<String>
)