package data.network

import kotlinx.serialization.Serializable

@Serializable
data class TypeLesson(
    val id: Int = -1,
    val ownerId: Int = -1,
    val name: String = "",
    val shortName: String = "",
    val lightColor: String = "",
    val darkColor: String = ""
)