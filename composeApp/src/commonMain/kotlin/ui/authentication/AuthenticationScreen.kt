package ui.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.util.Resource
import org.koin.compose.koinInject
import ui.Colors.mainBackground
import ui.Colors.textViewBorder
import ui.elements.Buttons.materialButton
import ui.elements.Buttons.textButton
import ui.elements.Icons.passwordIcon
import ui.elements.TextFields.outlinedTextField
import ui.elements.Texts.textTitle
import ui.home.HomeScreen
import ui.registration.RegistrationScreen
import viewmodel.AuthenticationViewModel
import viewmodel.TimetableViewModel

class AuthenticationScreen : Screen {
    override val key: ScreenKey
        get() = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val viewModel = koinInject<AuthenticationViewModel>()
        val timetableViewModel = koinInject<TimetableViewModel>()

        val passwordVisible = viewModel.passwordVisible.collectAsState()
        val loginState = viewModel.userLoginFlow.collectAsState()
        val passwordState = viewModel.userPasswordFlow.collectAsState()
        val loginErrorState = viewModel.loginError.collectAsState()
        val passwordErrorState = viewModel.passwordError.collectAsState()

        val scrollState = rememberScrollState()
        val userData = viewModel.userFlow.collectAsState()

        MaterialTheme {
            Column(Modifier.fillMaxSize().background(mainBackground).verticalScroll(scrollState)) {
                Card(
                    modifier = Modifier
                        .padding(18.dp, 56.dp, 18.dp)
                        .shadow(
                            shape = RoundedCornerShape(18.dp),
                            spotColor = Color(0xCC4F5A85),
                            elevation = 5.dp,
                        ),
                    elevation = 0.dp,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column {
                        textTitle("Вход", Modifier.fillMaxWidth().padding(16.dp, 24.dp, 16.dp))

                        outlinedTextField("Логин", loginState, viewModel::setLogin, loginErrorState)

                        outlinedTextField(
                            "Пароль", passwordState, viewModel::setPassword, passwordErrorState,
                            modifier = Modifier.fillMaxWidth().padding(16.dp, 24.dp),
                            { passwordIcon(passwordVisible, viewModel::updatePasswordVisible) },
                            visualTransformation = if (passwordVisible.value)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation()
                        )
                    }
                }

//                textButton(
//                    {},
//                    Modifier.align(Alignment.End).padding(end = 18.dp),
//                    "Забыли пароль?",
//                    textViewBorderColor
//                )

                materialButton(
                    viewModel::authUser,
                    Modifier.fillMaxWidth().align(Alignment.End).padding(18.dp, 32.dp, 18.dp),
                    "Войти"
                )

                Row(Modifier.align(Alignment.CenterHorizontally).padding(18.dp, 0.dp)) {
                    Text("Нет аккаунта?", Modifier.align(Alignment.CenterVertically))

                    textButton({
                        viewModel.setLogin("")
                        viewModel.setPassword("")
                        viewModel.clearLoginError()
                        viewModel.clearPasswordError()
                        navigator.push(RegistrationScreen())
                    }, Modifier.align(Alignment.CenterVertically), "Создайте аккаунт", textViewBorder)
                }
            }
        }

        if (userData.value is Resource.Success) {
            navigator.replaceAll(HomeScreen())
            timetableViewModel.loadTypesEvents(userData.value.data?.id ?: -1)
        }
    }
}