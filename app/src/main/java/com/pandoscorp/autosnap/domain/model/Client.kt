package com.pandoscorp.autosnap.domain.model

data class Client(
    var id: String = "",
    val name: String = "",
    val surname: String = "",
    val birthdate: String = "",
    val email: String = "",
    val phone: String = "",
    val note: String = "",
    val cars: List<Car> = emptyList()
)
