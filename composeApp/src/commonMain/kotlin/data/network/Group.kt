package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val groupId: Int,
    val groupName: String,
    val id: Int
)