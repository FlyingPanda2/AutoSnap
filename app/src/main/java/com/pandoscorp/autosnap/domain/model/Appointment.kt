package com.pandoscorp.autosnap.domain.model

import java.time.LocalTime
import java.util.Date

data class Appointment(
    var id: String = "",
    val clientId: String = "",
    val carId: String = "",
    val serviceIds: List<String> = emptyList(),
    var date: String = "",
    val time: String = "",
    val totalPrice: Int = 0,
    val discountPercent: Int = 0,
    val createdAt: String = "",
    val serviceCenterId: String = ""
)
