package viewmodel

import data.local.UserData
import data.local.UserDataForCreate
import data.network.User
import data.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import service.AuthenticationService

class AuthenticationViewModel(private val authenticationService: AuthenticationService) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _userFlow = MutableStateFlow<Resource<User>>(Resource.Loading())
    val userFlow = _userFlow.asStateFlow()

    private val _userLoginFlow = MutableStateFlow("asda1234434")
    val userLoginFlow = _userLoginFlow.asStateFlow()

    private val _userPasswordFlow = MutableStateFlow("12342342")
    val userPasswordFlow = _userPasswordFlow.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible = _passwordVisible.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError = _passwordError.asStateFlow()

    private val _firstNameFlow = MutableStateFlow("")
    val firstNameFlow = _firstNameFlow.asStateFlow()

    private val _secondNameFlow = MutableStateFlow("")
    val secondNameFlow = _secondNameFlow.asStateFlow()

    private val _firstNameError = MutableStateFlow<String?>(null)
    val firstNameError = _firstNameError.asStateFlow()

    private val _secondNameError = MutableStateFlow<String?>(null)
    val secondNameError = _secondNameError.asStateFlow()

    fun updatePasswordVisible(isVisible: Boolean) {
        _passwordVisible.value = isVisible
    }

    fun setFirstName(firstName: String) {
        _firstNameFlow.value = firstName
        _firstNameError.value = null
    }

    fun setSecondName(secondName: String) {
        _secondNameFlow.value = secondName
        _secondNameError.value = null
    }

    fun setLogin(login: String) {
        _userLoginFlow.value = login
        clearLoginError()
    }

    fun setPassword(password: String) {
        _userPasswordFlow.value = password
        clearPasswordError()
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    fun clearPasswordError() {
        _passwordError.value = null
    }

    fun authUser(): Boolean {
        if (_userLoginFlow.value.trim().isEmpty())
            _loginError.value = "Укажите ваш логин"

        if (_userPasswordFlow.value.trim().isEmpty())
            _passwordError.value = "Укажите ваш пароль"

        if (_userLoginFlow.value.trim().isEmpty() || _userPasswordFlow.value.trim().isEmpty())
            return false

        coroutineScope.launch {
            _userFlow.emit(
                authenticationService.authUser(UserData(_userLoginFlow.value, _userPasswordFlow.value)).also {
                    if (it is Resource.Error) {
                        _loginError.value = "Проверьте правильность введенных данных"
                        _passwordError.value = "Проверьте правильность введенных данных"
                    }
                }
            )
        }

        return true
    }

    fun registrationUser(): Boolean {
        if (_userLoginFlow.value.trim().isEmpty())
            _loginError.value = "Укажите ваш логин"

        if (_userPasswordFlow.value.trim().isEmpty())
            _passwordError.value = "Укажите ваш пароль"

        if (_firstNameFlow.value.trim().isEmpty())
            _firstNameError.value = "Укажите ваше имя"

        if (_secondNameFlow.value.trim().isEmpty())
            _secondNameError.value = "Укажите вашу фамилию"

        if (_userLoginFlow.value.trim().isEmpty() || _userPasswordFlow.value.trim().isEmpty()
            || _firstNameFlow.value.trim().isEmpty() || _secondNameFlow.value.trim().isEmpty()
        )
            return false

        coroutineScope.launch {
            _userFlow.emit(
                authenticationService.createUser(UserDataForCreate(_userLoginFlow.value, _userPasswordFlow.value, _firstNameFlow.value, secondNameFlow.value)).also {
                    if (it is Resource.Error) {
                        _loginError.value = "Проверьте правильность введенных данных"
                        _passwordError.value = "Проверьте правильность введенных данных"
                    }
                }
            )
        }

        return true
    }
}