package data.network

import data.time.DataTime
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val id: Int,
    val nameSubject: String,
    val dateTimeStart: String,
    val duration: Int,
    val filter: String,
    val groupsIds: List<Int>,
    val number: Int,
    val rooms: List<Room>,
    val subjectId: Int,
    val teachersIds: List<Int>,
    val typeId: Int,
    var groups: List<Group> = listOf(),
    var teachers: List<Teacher> = listOf(),
    var type: TypeLesson = TypeLesson()
): TimetableItem(id, nameSubject, dateTimeStart){
    override fun getDataTimeStart() = DataTime.parse(dateTimeStart.split("Z").first())
    override fun getDataTimeEnd() = DataTime.parse(dateTimeStart.split("Z").first()).endPair()
}