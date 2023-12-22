package data.network

import data.time.DataTime
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val name: String,
    val dateTimeStart: String,
    val dateTimeEnd: String,
    val place: String,
    val typeIds: List<Int>,
    val types: List<TypeLesson> = listOf(),
    val id: Int = -1
) : TimetableItem(id, name, dateTimeStart) {
    fun getMap() = hashMapOf(
        "typeId" to typeIds.joinToString(",") { it.toString() },
        "name" to name,
        "place" to place,
        "dateTimeStart" to dateTimeStart,
        "dateTimeEnd" to dateTimeEnd
    )

    override fun getDataTimeStart() = DataTime.parse(dateTimeStart.split("Z").first())
    override fun getDataTimeEnd() = DataTime.parse(dateTimeEnd.split("Z").first())
}