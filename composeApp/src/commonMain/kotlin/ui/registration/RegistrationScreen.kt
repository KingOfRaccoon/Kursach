package ui.registration

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.network.ItemFilter
import data.network.ItemGroupFilter
import data.network.ItemTeacherFilter
import data.time.DataTime
import data.util.Resource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import ui.Colors
import ui.Colors.mainBackground
import ui.authentication.AuthenticationScreen
import ui.dialogs.FiltersDialog
import ui.elements.Buttons
import ui.elements.Icons.passwordIcon
import ui.elements.TextFields
import ui.elements.Texts
import ui.home.HomeScreen
import viewmodel.AuthenticationViewModel
import viewmodel.TimetableViewModel

class RegistrationScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinInject<AuthenticationViewModel>()
        val timetableViewModel = koinInject<TimetableViewModel>()

        val filters = timetableViewModel.getFiltersTimetable()
            .collectAsState(Resource.Loading<List<ItemTeacherFilter>>() to Resource.Loading())

        val needDialogSearch = remember { mutableStateOf(false) }
        val filterState = remember { mutableStateOf("") }
        val itemFilterState = remember { mutableStateOf<ItemFilter?>(null) }

        val firstNameState = viewModel.firstNameFlow.collectAsState()
        val secondNameState = viewModel.secondNameFlow.collectAsState()
        val loginState = viewModel.userLoginFlow.collectAsState()
        val passwordState = viewModel.userPasswordFlow.collectAsState()
        val userData = viewModel.userFlow.collectAsState()

        val loginErrorState = viewModel.loginError.collectAsState()
        val passwordErrorState = viewModel.passwordError.collectAsState()
        val firstNameErrorState = viewModel.firstNameError.collectAsState()
        val secondNameErrorState = viewModel.secondNameError.collectAsState()

        val passwordVisible = viewModel.passwordVisible.collectAsState()

        val scrollState = rememberScrollState()


        MaterialTheme {
            Column(Modifier.fillMaxSize().background(mainBackground).verticalScroll(scrollState)) {
                IconButton({
                    navigator.pop()
                }, Modifier.defaultMinSize(56.dp, 56.dp)) {
                    Image(
                        painterResource("icons/icon_arrow_back.xml"),
                        "back",
                        Modifier,
                        colorFilter = ColorFilter.tint(Colors.textMain)
                    )
                }
                Card(
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .shadow(
                            shape = RoundedCornerShape(18.dp),
                            spotColor = Color(0xCC4F5A85),
                            elevation = 5.dp,
                        ),
                    elevation = 0.dp,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column {
                        Texts.textTitle("Регистрация", Modifier.fillMaxWidth().padding(16.dp, 24.dp, 16.dp))

                        TextFields.outlinedTextField(
                            "Имя",
                            firstNameState,
                            viewModel::setFirstName,
                            firstNameErrorState
                        )

                        TextFields.outlinedTextField(
                            "Фамилия",
                            secondNameState,
                            viewModel::setSecondName,
                            secondNameErrorState
                        )

                        Text(
                            if (itemFilterState.value == null)
                                "Фильтр для расписания"
                            else
                                itemFilterState.value?.name.orEmpty(),
                            Modifier.fillMaxWidth().padding(16.dp, 24.dp, 16.dp)
                                .border(1.dp, Colors.textViewBorder, RoundedCornerShape(100.dp)).clickable {
                                    needDialogSearch.value = true
                                }.padding(18.dp),
                            color = if (itemFilterState.value == null) Colors.textViewBorder else Colors.textMain
                        )

                        if (needDialogSearch.value)
                            FiltersDialog(
                                filters,
                                filterState,
                                needDialogSearch,
                                itemFilterState
                            ) { itemFilter: ItemFilter ->

                            }

                        TextFields.outlinedTextField("Логин", loginState, viewModel::setLogin, loginErrorState)

                        TextFields.outlinedTextField(
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

                Buttons.materialButton(
                    {
                        viewModel.registrationUser(
                            timetableViewModel.generateTag(
                                itemFilterState.value?.let { it as? ItemGroupFilter }?.id,
                                itemFilterState.value?.let { it as? ItemTeacherFilter }?.id
                            )
                        )
                    },
                    Modifier.fillMaxWidth().align(Alignment.End).padding(18.dp, 32.dp, 18.dp),
                    "Создать аккаунт"
                )

                Row(Modifier.align(Alignment.CenterHorizontally).padding(18.dp, 0.dp)) {
                    Text("Вы уже зарегистрированы?", Modifier.align(Alignment.CenterVertically))

                    Buttons.textButton({
                        viewModel.setLogin("")
                        viewModel.setPassword("")
                        viewModel.clearLoginError()
                        viewModel.clearPasswordError()
                        navigator.replace(AuthenticationScreen())
                    }, Modifier.align(Alignment.CenterVertically), "Войдите", Colors.textViewBorder)
                }
            }
        }

        if (userData.value is Resource.Success) {
            navigator.replaceAll(HomeScreen())
            timetableViewModel.loadTypesEvents(userData.value.data?.id ?: -1)
            val today = DataTime.now()
            timetableViewModel.loadWeekTimetable(
                today.year.toString(),
                (today.getWeek() - 4).toString(),
                userData.value.data?.groupId,
                userData.value.data?.teacherId,
                ownerId = userData.value.data?.id ?: -1
            )
        }
    }
}