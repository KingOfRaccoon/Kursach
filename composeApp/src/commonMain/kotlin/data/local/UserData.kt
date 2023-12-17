package data.local

data class UserData(var login: String = "", var password: String = "") {

    fun getMap() = hashMapOf(
        "login" to login,
        "password" to password
    )
}