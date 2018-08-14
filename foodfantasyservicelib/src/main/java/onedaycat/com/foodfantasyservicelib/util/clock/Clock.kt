package onedaycat.com.foodfantasyservicelib.util.clock

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class Clock {
    companion object {
        var freezeTime: Date = Date()

        fun setFreezeTimes(time:String) {
            val date = SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(time)
            freezeTime = date
        }

        fun resetFreeze() {
            freezeTime = Date()
        }

        fun getFreezeTimes(): Date {
            return freezeTime
        }

        fun NowUTC(): String {
            if (freezeTime.time.toInt() != 0) {
                return createTimeStamp(convertDate(Calendar.getInstance().time))
            }

            return createTimeStamp(convertDate(freezeTime))
        }

        fun convertDate(date: Date): LocalDateTime {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        }

        fun createTimeStamp(dateTime: LocalDateTime):String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")
            return formatter.format(dateTime)
        }
    }
}