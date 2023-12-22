package ui.addtype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import ui.Colors
import ui.elements.Buttons
import ui.elements.Buttons.materialButton
import ui.elements.Texts
import viewmodel.AuthenticationViewModel
import viewmodel.TimetableViewModel

class AddTypeScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalBottomSheetNavigator.current
        val timetableViewModel = koinInject<TimetableViewModel>()
        val authenticationViewModel = koinInject<AuthenticationViewModel>()

        val nameType = timetableViewModel.nameNewType
        val shortNameType = timetableViewModel.shortNameNewType
        val userData = authenticationViewModel.userFlow.collectAsState()

        val scrollState = rememberScrollState()

        MaterialTheme {
            Column(
                Modifier.fillMaxWidth().padding(18.dp, 16.dp).verticalScroll(scrollState),
                Arrangement.spacedBy(16.dp)
            ) {
                Divider(Modifier.fillMaxWidth(0.2f).align(Alignment.CenterHorizontally), Colors.textLight, 4.dp)

                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
                    IconButton({ navigator.pop() }) {
                        Icon(painterResource("icons/icon_arrow_back.xml"), "")
                    }
                    Texts.textTitle("Новый тип мероприятий", textAlign = TextAlign.Start)
                }

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp))) {
                    TextField(
                        nameType.value,
                        timetableViewModel::setNameNewType,
                        Modifier.fillMaxWidth().padding(0.dp),
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight(500)
                        ),
                        singleLine = true,
                        placeholder = {
                            Texts.textSubTitle(
                                "Название",
                                textSize = 18.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Start,
                                textColor = Colors.textButtonNonActive
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent
                        )
                    )
                }

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp))) {
                    TextField(
                        shortNameType.value,
                        timetableViewModel::setShortNameType,
                        Modifier.fillMaxWidth().padding(0.dp),
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight(500)
                        ),
                        singleLine = true,
                        placeholder = {
                            Texts.textSubTitle(
                                "Короткое название (максимум 2 элемента)",
                                textSize = 18.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Start,
                                textColor = Colors.textButtonNonActive
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent
                        )
                    )
                }

                materialButton(
                    {
                        timetableViewModel.createType(userData.value.data?.id ?: 0)
                        navigator.pop()
                    },
                    Modifier.fillMaxWidth(),
                    "Добавить"
                )
            }
        }
    }
}