package onedaycat.com.food_fantasy.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    @JvmStatic
    fun toSimpleString(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date)
    }
}