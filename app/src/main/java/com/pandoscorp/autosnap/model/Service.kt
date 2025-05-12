package com.pandoscorp.autosnap.model

data class Service(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val duration: Int = 0,
    val shopId: String = ""
)
