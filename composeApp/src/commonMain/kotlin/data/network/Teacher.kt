package data.network

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val firstname: String,
    val id: Int,
    val image: String,
    val imageThumb: String,
    val lastname: String,
    val secondName: String,
    val tid: Int,
    val type: String
) {
    fun fullName() = if (lastname.isNotEmpty() && secondName.isNotEmpty()) lastname + " " +
            (if (firstname.isNotEmpty()) firstname.first() + "." else "") +
            (if (secondName.isNotEmpty()) secondName.first() + "." else "")
    else
        firstname
}