package data.network

import data.time.DataTime
import kotlinx.serialization.Serializable

@Serializable
abstract class TimetableItem(
    var _id: Int = 0,
    var _name: String = "",
    var _dateTimeStart: String = ""
) {
    abstract fun getDataTimeStart(): DataTime
    abstract fun getDataTimeEnd(): DataTime
}