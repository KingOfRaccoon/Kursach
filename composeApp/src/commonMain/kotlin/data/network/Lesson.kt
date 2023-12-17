package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val dateTimeStart: String,
    val duration: Int,
    val filter: String,
    val groupsIds: List<Int>,
    val id: Int,
    val nameSubject: String,
    val number: Int,
    val rooms: List<Room>,
    val subjectId: Int,
    val teachersIds: List<Int>,
    val typeId: Int
)