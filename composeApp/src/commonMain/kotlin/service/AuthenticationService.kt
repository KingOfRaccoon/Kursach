package service

import data.local.UserData
import data.local.UserDataForCreate
import data.network.ResponseSusses
import data.network.User
import data.util.Postman
import data.util.Resource
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.koin.core.parameter.parametersOf

class AuthenticationService(private val postman: Postman) {
    private val baseUrl = "https://hot-up-skylark.ngrok-free.app/"
    private val authRoute = "auth/"
    private val createUserRoute = "createUser/"
    suspend fun authUser(userData: UserData): Resource<User> {
        return postman.submitForm(baseUrl, authRoute, parameters {
            append("login", userData.login)
            append("password", userData.password)
        })
    }

    suspend fun createUser(userDataForCreate: UserDataForCreate): Resource<User> {
        return postman.submitForm(baseUrl, createUserRoute, parameters {
            append("login", userDataForCreate.login)
            append("password", userDataForCreate.password)
            append("firstName", userDataForCreate.firstName)
            append("secondName", userDataForCreate.secondName)
        })
    }
}