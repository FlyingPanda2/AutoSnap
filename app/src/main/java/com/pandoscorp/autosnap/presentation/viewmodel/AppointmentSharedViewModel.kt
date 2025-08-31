package com.pandoscorp.autosnap.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import com.pandoscorp.autosnap.domain.model.Car
import com.pandoscorp.autosnap.domain.model.Client
import com.pandoscorp.autosnap.domain.model.Service
import com.pandoscorp.autosnap.domain.model.SimpleDate
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pandoscorp.autosnap.domain.model.Appointment
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

    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
    val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    // Функция для проверки, является ли запись сервисной
    fun isServiceCenterAppointment(appointment: Appointment): Boolean {
        return appointment.serviceCenterId.isNotEmpty()
    }

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private var personalAppointmentsListener: ValueEventListener? = null

    init {
        // Добавляем слушатель состояния аутентификации
        auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user ->
                setupPersonalAppointmentsListener(user.uid)
            } ?: run {
                _appointments.value = emptyList()
            }
        }
    }

    private fun setupPersonalAppointmentsListener(userId: String) {
        // Удаляем старый слушатель, если он есть
        personalAppointmentsListener?.let {
            database.getReference("users/$userId/appointments").removeEventListener(it)
        }

        // Создаем новый слушатель
        personalAppointmentsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val personalApps = snapshot.children.mapNotNull {
                    it.getValue(Appointment::class.java)?.copy(id = it.key ?: "")
                }
                _appointments.value = personalApps.sortedBy { it.time }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Personal appointments listener cancelled", error.toException())
            }
        }.also { listener ->
            database.getReference("users/$userId/appointments").addValueEventListener(listener)
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.currentUser?.uid?.let { userId ->
            personalAppointmentsListener?.let {
                database.getReference("users/$userId/appointments").removeEventListener(it)
            }
        }
    }

    fun acceptAppointment(
        appointment: Appointment,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: run {
                    onError("Пользователь не авторизован")
                    return@launch
                }

                // 1. Получаем текущие данные записи
                val appointmentRef = database.getReference("appointments/${appointment.id}")
                val appointmentSnapshot = appointmentRef.get().await()

                if (!appointmentSnapshot.exists()) {
                    onError("Запись не найдена")
                    return@launch
                }

                // 2. Преобразуем данные записи
                val appointmentData = mutableMapOf<String, Any?>()
                appointmentSnapshot.children.forEach { child ->
                    appointmentData[child.key!!] = child.value
                }

                // 3. Модифицируем данные для личной записи
                appointmentData["serviceCenterId"] = "" // Убираем привязку к сервису

                // 4. Преобразуем дату в стандартный формат
                val originalDate = appointmentData["date"] as? String ?: ""
                appointmentData["date"] = when {
                    originalDate.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")) -> { // формат DD.MM.YYYY
                        val (day, month, year) = originalDate.split(".")
                        "$year-$month-$day"
                    }
                    originalDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> { // уже правильный формат
                        originalDate
                    }
                    else -> { // неизвестный формат - используем текущую дату
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    }
                }

                val newAppointmentRef = database.reference.child("users")
                    .child(userId)
                    .child("appointments")
                    .push()

                val newAppointmentId = newAppointmentRef.key ?: throw Exception("Не удалось создать ключ записи")

                // 5. Создаем обновления
                val newAppointmentKey = database.reference.child("users").child(userId)
                    .child("appointments").push().key!!

                appointmentData["id"] = newAppointmentId

                // 7. Сохраняем новую запись
                newAppointmentRef.setValue(appointmentData).await()

                // 8. Удаляем оригинальную запись из сервисных
                appointmentRef.removeValue().await()

                // 7. Обновляем UI
                withContext(Dispatchers.Main) {
                    onSuccess()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Ошибка: ${e.message ?: "Неизвестная ошибка"}")
                }
            }
        }
    }

    private fun convertDateToStandardFormat(dateStr: String?): String {
        return if (dateStr.isNullOrEmpty()) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        } else {
            try {
                // Преобразуем из формата "24.05.2025" в "2025-05-24"
                if (dateStr.contains(".")) {
                    val parts = dateStr.split(".")
                    "${parts[2]}-${parts[1]}-${parts[0]}"
                } else {
                    dateStr // Если уже в правильном формате, оставляем как есть
                }
            } catch (e: Exception) {
                Log.e("DateConvert", "Error converting date: $dateStr", e)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            }
        }
    }

    fun loadAppointmentsForDate(date: SimpleDate) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: run {
                    _appointments.value = emptyList()
                    return@launch
                }

                // Форматируем дату во всех возможных форматах
                val dayStr = date.day.toString().padStart(2, '0')
                val monthStr = date.month.toString().padStart(2, '0')
                val targetDate1 = "$dayStr.$monthStr.${date.year}"  // 24.05.2025
                val targetDate2 = "${date.year}-${monthStr}-${dayStr}" // 2025-05-24

                Log.d("Appointments", "Ищем записи для даты: $targetDate1 или $targetDate2")

                // 1. Загрузка сервисных записей (из appointments/)
                val serviceApps = database.getReference("appointments")
                    .get()
                    .await()
                    .children
                    .mapNotNull { it.getValue(Appointment::class.java) }
                    .filter { app ->
                        (app.date == targetDate1 || app.date == targetDate2) &&
                                app.serviceCenterId == userId
                    }.also {
                        Log.d("Appointments", "Сервисные записи: ${it.size}")
                    }

                // 2. Загрузка личных записей (из users/{userId}/appointments)
                val personalApps = database.getReference("users/$userId/appointments")
                    .get()
                    .await()
                    .children
                    .mapNotNull { it.getValue(Appointment::class.java) }
                    .filter { app ->
                        app.date == targetDate1 || app.date == targetDate2
                    }.also {
                        Log.d("Appointments", "Личные записи: ${it.size}")
                    }

                // Объединяем и сортируем по времени
                val allApps = (serviceApps + personalApps).sortedBy { it.time }

                withContext(Dispatchers.Main) {
                    _appointments.value = allApps
                    Log.d("Appointments", "Всего записей: ${allApps.size}")
                    allApps.forEach { app ->
                        Log.d("Appointments", "Запись: ${app.id}, дата: ${app.date}, время: ${app.time}, тип: ${if (app.serviceCenterId == userId) "сервис" else "личная"}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Appointments", "Ошибка загрузки", e)
                _appointments.value = emptyList()
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
            createdAt = createdAt,
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
            // Пробуем загрузить из users/{userId}/clients
            val userId = auth.currentUser?.uid ?: return@withContext null
            val userClientRef = database.getReference("users/$userId/clients/$clientId")
            val userClientSnapshot = userClientRef.get().await()

            if (userClientSnapshot.exists()) {
                return@withContext parseClientSnapshot(userClientSnapshot, clientId)
            }

            // Если не нашли, пробуем загрузить из clients/
            val globalClientRef = database.getReference("clients/$clientId")
            val globalClientSnapshot = globalClientRef.get().await()

            if (globalClientSnapshot.exists()) {
                return@withContext parseClientSnapshot(globalClientSnapshot, clientId)
            }

            null
        } catch (e: Exception) {
            Log.e("ClientLoad", "Ошибка при загрузке клиента", e)
            null
        }
    }

    private fun parseClientSnapshot(snapshot: DataSnapshot, clientId: String): Client {
        return Client(
            id = clientId,
            name = snapshot.child("name").getValue(String::class.java) ?: "",
            surname = snapshot.child("surname").getValue(String::class.java) ?: "",
            phone = snapshot.child("phone").getValue(String::class.java) ?: "",
            email = snapshot.child("email").getValue(String::class.java) ?: "",
            birthdate = snapshot.child("birthdate").getValue(String::class.java) ?: "",
            note = snapshot.child("note").getValue(String::class.java) ?: "",
            cars = snapshot.child("cars").children.mapNotNull { carSnapshot ->
                carSnapshot.getValue(Car::class.java)?.copy(id = carSnapshot.key ?: "")
            }
        )
    }

    suspend fun loadAppointmentClient(appointmentId: String): Client? = withContext(Dispatchers.IO) {
        try {
            val snapshot = database.getReference("appointments/$appointmentId").get().await()
            val clientId = snapshot.child("clientId").value as? String ?: return@withContext null

            // Загружаем клиента из правильного места
            val clientSnapshot = database.getReference("clients/$clientId").get().await()

            // Вручную парсим клиента, чтобы правильно обработать Map -> List
            val data = clientSnapshot.value as? Map<*, *> ?: return@withContext null

            val carsMap = (data["cars"] as? Map<String, Map<String, Any>>) ?: emptyMap()

            val carList = carsMap.map { (key, value) ->
                Car(
                    id = key,
                    brand = value["brand"] as? String ?: "",
                    model = value["model"] as? String ?: "",
                    year = value["year"] as? String ?: "",
                    engineVolume = (value["engineVolume"] as? Double) ?: 0.0,
                    horsePower = value["horsePower"] as? String ?: ""
                )
            }

            Client(
                id = clientId,
                name = data["name"] as? String ?: "",
                surname = data["surname"] as? String ?: "",
                birthdate = data["birthdate"] as? String ?: "",
                email = data["email"] as? String ?: "",
                phone = data["phone"] as? String ?: "",
                note = data["note"] as? String ?: "",
                cars = carList
            )

        } catch (e: Exception) {
            Log.e("Appointments", "Error loading client", e)
            null
        }
    }

    suspend fun loadAppointmentCar(appointmentId: String): Car? = withContext(Dispatchers.IO) {
        try {
            val snapshot = database.getReference("appointments/$appointmentId").get().await()
            val clientId = snapshot.child("clientId").value as? String ?: return@withContext null
            val carId = snapshot.child("carId").value as? String ?: return@withContext null

            // Загружаем авто из правильного места
            val carSnapshot = database.getReference("clients/$clientId/cars/$carId").get().await()
            carSnapshot.getValue(Car::class.java)?.copy(id = carId)
        } catch (e: Exception) {
            Log.e("Appointments", "Error loading car", e)
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
            // Пробуем загрузить из users/{userId}/clients/{clientId}/cars
            val userId = auth.currentUser?.uid ?: return@withContext null
            val userCarRef = database.getReference("users/$userId/clients/$clientId/cars/$carId")
            val userCarSnapshot = userCarRef.get().await()

            if (userCarSnapshot.exists()) {
                return@withContext userCarSnapshot.getValue(Car::class.java)?.copy(id = carId)
            }

            // Если не нашли, пробуем загрузить из clients/{clientId}/cars
            val globalCarRef = database.getReference("clients/$clientId/cars/$carId")
            val globalCarSnapshot = globalCarRef.get().await()

            if (globalCarSnapshot.exists()) {
                return@withContext globalCarSnapshot.getValue(Car::class.java)?.copy(id = carId)
            }

            null
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

//    private val _startTime = mutableStateOf<LocalTime?>(null)
//    val startTime: Thread.State<LocalTime> = _startTime

//    fun updateStartTime(time: LocalTime) {
//        _startTime.value = time
//    }
//
//    fun clearStartTime() {
//        _startTime.value = null
//    }

}

data class ScheduleState(
    val selectedDate: SimpleDate,
    val currentMonth: Int,
    val currentYear: Int,
    val markedDates: Set<String>
)