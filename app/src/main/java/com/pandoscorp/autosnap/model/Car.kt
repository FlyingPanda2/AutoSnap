package com.pandoscorp.autosnap.model

import kotlinx.coroutines.flow.MutableStateFlow

data class Car(
    val id: String = "",
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val engineVolume: Double = 0.0,
    val horsePower: String = ""
)
