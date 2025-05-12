package com.pandoscorp.autosnap.model

data class Appointment(
    val id: String = "",
    val userId: String = "",
    val serviceId: String = "",
    val shopId: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "pending"
)
