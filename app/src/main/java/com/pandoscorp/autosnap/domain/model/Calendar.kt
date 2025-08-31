package com.pandoscorp.autosnap.domain.model

data class SimpleDate(
    val day: Int,
    val month: Int, // 1-12
    val year: Int
) {
    fun toKeyString(): String = "$year-${month.twoDigits()}-${day.twoDigits()}"
}

private fun Int.twoDigits(): String = toString().padStart(2, '0')