package com.pandoscorp.autosnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pandoscorp.autosnap.domain.model.Appointment
import com.pandoscorp.autosnap.domain.model.Car
import com.pandoscorp.autosnap.domain.model.Service
import com.pandoscorp.autosnap.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Locale

class ClientsMainViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val databaseUrl = "https://autosnap-c15c0-default-rtdb.europe-west1.firebasedatabase.app"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)

    private val _upcomingAppointments = MutableStateFlow<List<Pair<Appointment, Boolean>>>(emptyList())
    val upcomingAppointments = _upcomingAppointments.asStateFlow()

    private val _history = MutableStateFlow<List<Appointment>>(emptyList())
    val history = _history.asStateFlow()

    private val _selectedService = MutableStateFlow<User?>(null)
    val selectedService = _selectedService.asStateFlow()

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars = _cars.asStateFlow()

    private val _carsMap = MutableStateFlow<Map<String, Car>>(emptyMap())
    private val _servicesMap = MutableStateFlow<Map<String, Service>>(emptyMap())

    // Добавляем новые StateFlow для доступа в UI
    val carsMap = _carsMap.asStateFlow()
    val servicesMap = _servicesMap.asStateFlow()

    private val _serviceCentersMap = MutableStateFlow<Map<String, User>>(emptyMap())
    val serviceCentersMap = _serviceCentersMap.asStateFlow()

    private fun loadServiceCenters() {
        database.getReference("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val centers = mutableMapOf<String, User>()
                    snapshot.children.forEach { userSnapshot ->
                        try {
                            val userData = userSnapshot.value as? Map<String, Any>
                            userData?.let {
                                centers[userSnapshot.key ?: ""] = User(
                                    id = userSnapshot.key ?: "",
                                    username = it["username"] as? String ?: "",
                                    email = it["email"] as? String ?: "",
                                    phone = it["phone"] as? String ?: "",
                                    address = it["address"] as? String ?: "",
                                    // Остальные поля оставляем по умолчанию
                                    clients = emptyList(),
                                    services = emptyMap()
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("ServiceCenterError", "Error parsing user data for ${userSnapshot.key}", e)
                        }
                    }
                    _serviceCentersMap.value = centers
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("ServiceCenterError", error.message)
                }
            })
    }

    init {
        if (auth.currentUser != null) {
            loadCars()
            setupAppointmentsListener()
            loadHistory()
            loadAdditionalData()
            loadServiceCenters() // Добавляем загрузку автосервисов
        }
    }

    private fun setupAppointmentsListener() {
        val currentUserId = auth.currentUser?.uid ?: return

        // Слушаем записи клиента
        database.getReference("clients/$currentUserId/appointments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(clientSnapshot: DataSnapshot) {
                    val clientAppointments = clientSnapshot.children.mapNotNull { snapshot ->
                        snapshot.getValue(Appointment::class.java)?.copy(id = snapshot.key ?: "")
                    }

                    // Проверяем, какие записи есть в общем списке (appointments)
                    database.getReference("appointments")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(globalSnapshot: DataSnapshot) {
                                val globalAppointments = globalSnapshot.children.mapNotNull {
                                    it.getValue(Appointment::class.java)
                                }

                                val appointmentsWithStatus = clientAppointments.map { clientAppointment ->
                                    // true = на подтверждении (есть в обоих местах), false = подтверждена (только у клиента)
                                    val isPending = globalAppointments.any { it.id == clientAppointment.id }
                                    Pair(clientAppointment, isPending)
                                }

                                _upcomingAppointments.value = appointmentsWithStatus.sortedBy {
                                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                        .parse("${it.first.date} ${it.first.time}")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("AppointmentError", "Failed to load global appointments", error.toException())
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AppointmentError", "Failed to load client appointments", error.toException())
                }
            })
    }

    private fun loadAdditionalData() {
        val userId = auth.currentUser?.uid ?: return

        // Загрузка автомобилей клиента
        database.getReference("clients/$userId/cars").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val carsMap = snapshot.children.associate {
                        it.key!! to it.getValue(Car::class.java)!!.copy(id = it.key!!)
                    }
                    _carsMap.value = carsMap
                    _cars.value = carsMap.values.toList() // <-- Вот это добавляем
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CarError", error.message)
                }
            }
        )

        // Загрузка всех услуг из всех сервисных центров
        database.getReference("users").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val services = mutableMapOf<String, Service>()
                    snapshot.children.forEach { userSnapshot ->
                        userSnapshot.child("services").children.forEach { serviceSnapshot ->
                            serviceSnapshot.getValue(Service::class.java)?.let {
                                services[serviceSnapshot.key!!] = it.copy(id = serviceSnapshot.key!!)
                            }
                        }
                    }
                    _servicesMap.value = services
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("ServiceError", error.message)
                }
            }
        )
    }

    private fun loadUpcomingAppointments() {
        val currentUserId = auth.currentUser?.uid ?: ""

        // Загружаем записи из раздела клиента
        database.getReference("clients/$currentUserId/appointments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(clientAppointmentsSnapshot: DataSnapshot) {
                    val clientAppointments = mutableListOf<Appointment>()
                    clientAppointmentsSnapshot.children.forEach { appointmentSnapshot ->
                        appointmentSnapshot.getValue(Appointment::class.java)?.let { appointment ->
                            clientAppointments.add(appointment.copy(id = appointmentSnapshot.key ?: ""))
                        }
                    }

                    // Загружаем подтвержденные записи из общего раздела
                    database.getReference("appointments")
                        .orderByChild("clientId")
                        .equalTo(currentUserId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(globalAppointmentsSnapshot: DataSnapshot) {
                                val confirmedIds = globalAppointmentsSnapshot.children.map { it.key }

                                // Создаем список всех записей с пометкой о подтверждении
                                val allAppointments = clientAppointments.map { app ->
                                    Pair(app, confirmedIds.contains(app.id)) // Pair<Appointment, Boolean>
                                }

                                // Сортируем по дате и времени
                                val sorted = allAppointments.sortedWith(
                                    compareBy(
                                        { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(it.first.date) },
                                        { SimpleDateFormat("HH:mm", Locale.getDefault()).parse(it.first.time) }
                                    )
                                )

                                _upcomingAppointments.value = sorted
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("AppointmentError", "Failed to load confirmed appointments", error.toException())
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AppointmentError", "Failed to load client appointments", error.toException())
                }
            })
    }

    private fun loadCars() {
        auth.currentUser?.uid?.let { userId ->
            database.getReference("clients/$userId/cars")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val carsList = mutableListOf<Car>()
                        snapshot.children.forEach { carSnapshot ->
                            try {
                                val car = carSnapshot.getValue(Car::class.java)
                                car?.let {
                                    carsList.add(it.copy(id = carSnapshot.key ?: ""))
                                    Log.d("CarDebug", "Loaded car: ${it.brand} ${it.model}")
                                }
                            } catch (e: Exception) {
                                Log.e("CarError", "Error parsing car data", e)
                            }
                        }
                        _cars.value = carsList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("CarError", "Failed to load cars: ${error.message}")
                    }
                })
        }
    }

    fun setSelectedService(service: User) {
        _selectedService.value = service
    }

    fun clearSelectedService() {
        _selectedService.value = null
    }

    private fun loadHistory() {
        database.getReference("appointments")
            .orderByChild("clientId")
            .equalTo(auth.currentUser?.uid ?: "")
            .limitToLast(5)
            .get()
            .addOnSuccessListener { snapshot ->
                val appointments = snapshot.children.mapNotNull { child ->
                    child.getValue(Appointment::class.java)?.copy(id = child.key ?: "")
                }
                _history.value = appointments
            }
    }
}