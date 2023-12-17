package data.network

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firstName: String,
    val id: Int,
    val secondName: String
)