package com.pandoscorp.autosnap.domain.model

data class Service(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Int = 0,
    val duration: Int = 0,
    var isSelected: Boolean = false
)
