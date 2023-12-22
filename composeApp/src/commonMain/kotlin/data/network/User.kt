package data.network

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firstName: String,
    val id: Int,
    val secondName: String,
    val filter: String,
    var groupId: Int? = null,
    var groupName: String = "",
    var teacherId: Int? = null,
    var teacherName: String = ""
) {
    fun getName() = groupName.ifEmpty { teacherName }
}