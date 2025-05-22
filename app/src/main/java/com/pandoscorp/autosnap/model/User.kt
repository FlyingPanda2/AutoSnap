package com.pandoscorp.autosnap.model

data class User(
    var id: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val clients: List<Client> = emptyList(),
    val services: Map<String, Service> = emptyMap()
)
