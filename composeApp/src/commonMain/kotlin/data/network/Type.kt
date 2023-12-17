package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Type(
    val ownerId: Int,
    val name: String,
    val shortName: String,
    val lightColor: String,
    val darkColor: String,
    val id: Int = -1
) {
    fun getMap() = hashMapOf(
        "ownerId" to ownerId.toString(),
        "name" to name,
        "shortName" to shortName,
        "lightColor" to lightColor,
        "darkColor" to darkColor
    )
}