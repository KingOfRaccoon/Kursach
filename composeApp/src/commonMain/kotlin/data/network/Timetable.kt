package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Timetable(
    var events: List<Event>,
    var lessons: List<Lesson>
)