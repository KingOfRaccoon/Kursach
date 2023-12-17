package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val dateTimeEnd: String,
    val dateTimeStart: String,
    val name: String,
    val place: String,
    val typeIds: List<Int>,
    val id: Int = -1,
) {
    fun getMap() = hashMapOf(
        "typeId" to typeIds.joinToString(",") { it.toString() },
        "name" to name,
        "place" to place,
        "dateTimeStart" to dateTimeStart,
        "dateTimeEnd" to dateTimeEnd
    )
}