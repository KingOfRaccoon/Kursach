package data.local

data class UserDataForCreate(
    var login: String = "",
    var password: String = "",
    val firstName: String = "",
    val secondName: String = ""
) {
    fun getMap() = hashMapOf(
        "login" to login,
        "password" to password
    )
}