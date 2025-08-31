package com.pandoscorp.autosnap.utilis

import com.pandoscorp.autosnap.domain.model.SimpleDate
import java.util.Calendar

object DateUtils {
    fun getCurrentDate(): SimpleDate {
        val calendar = getCalendarInstance()
        return SimpleDate(
            day = calendar.get(Calendar.DAY_OF_MONTH),
            month = calendar.get(Calendar.MONTH) + 1,
            year = calendar.get(Calendar.YEAR)
        )
    }

    fun getDaysInMonth(month: Int, year: Int): Int {
        val calendar = getCalendarInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun getCalendarInstance(): Calendar {
        return Calendar.getInstance()
    }
}