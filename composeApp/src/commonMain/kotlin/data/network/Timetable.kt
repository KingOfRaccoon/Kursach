package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Timetable(
    var events: List<Event> = listOf(),
    var lessons: List<Lesson> = listOf()
) {
    fun getTimetableItems() = (events + lessons).sortedBy { it._dateTimeStart }
}