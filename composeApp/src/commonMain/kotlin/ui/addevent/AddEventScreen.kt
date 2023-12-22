package ui.addevent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextButton
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import data.time.DataTime
import data.util.Resource
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import ui.Colors
import ui.addtype.AddTypeScreen
import ui.elements.Buttons.materialButton
import ui.elements.Texts.scaledSp
import ui.elements.Texts.textSubTitle
import ui.elements.Texts.textTitle
import viewmodel.AuthenticationViewModel
import viewmodel.TimetableViewModel

class AddEventScreen(private val startDate: DataTime) : Screen {

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalResourceApi::class, ExperimentalLayoutApi::class,
        ExperimentalMaterialApi::class
    )
    @Composable
    override fun Content() {
        val navigator = LocalBottomSheetNavigator.current

        val timetableViewModel = koinInject<TimetableViewModel>()
        val authenticationViewModel = koinInject<AuthenticationViewModel>()
        val scrollState = rememberScrollState()

        val typesUser = timetableViewModel.typesEventsStateFlow.collectAsState()

        val nameEvent = timetableViewModel.nameNewEvent
        val placeEvent = timetableViewModel.placeNewEvent
        val userData =
            timetableViewModel.combineUserTimetable(authenticationViewModel.userFlow).collectAsState(Resource.Loading())

        val datePickerState = rememberDatePickerState(
            if (startDate.getIsoFormat() == DataTime.now().getIsoFormat())
                startDate.getTimeInMilliSeconds()
            else
                startDate.goToNNextDay(1).getTimeInMilliSeconds()
        )
        val dateState = remember { mutableStateOf(startDate) }
        val needShowDatePickerState = remember { mutableStateOf(false) }

        val startTimeState = rememberTimePickerState(8)
        val startConfirmTimeState = remember { mutableStateOf(LocalTime(8, 0)) }
        val needShowStartTimePickerState = remember { mutableStateOf(false) }

        val endTimeState = rememberTimePickerState(9, 30)
        val endConfirmTimeState = remember { mutableStateOf(LocalTime(9, 30)) }
        val needShowEndTimePickerState = remember { mutableStateOf(false) }

        val selectedTypes = remember { mutableStateListOf<Int>() }

        MaterialTheme {
            Column(
                Modifier.fillMaxWidth().padding(18.dp, 16.dp).verticalScroll(scrollState),
                Arrangement.spacedBy(16.dp)
            ) {
                Divider(Modifier.fillMaxWidth(0.2f).align(Alignment.CenterHorizontally), Colors.textLight, 4.dp)

                textTitle("Новое мероприятие", Modifier.fillMaxWidth(), textAlign = TextAlign.Start)

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp))) {
                    TextField(
                        nameEvent.value,
                        timetableViewModel::setNameNewEvent,
                        Modifier.fillMaxWidth().padding(0.dp),
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight(500)
                        ),
                        singleLine = true,
                        placeholder = {
                            textSubTitle(
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
                        placeEvent.value,
                        timetableViewModel::setPlaceNewEvent,
                        Modifier.fillMaxWidth().padding(0.dp),
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight(500)
                        ),
                        singleLine = true,
                        placeholder = {
                            textSubTitle(
                                "Место проведения",
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

                if (needShowDatePickerState.value)
                    openDatePickerDialog(datePickerState, needShowDatePickerState, dateState)

                if (needShowStartTimePickerState.value)
                    openTimePickerDialog(startTimeState, needShowStartTimePickerState, startConfirmTimeState)

                if (needShowEndTimePickerState.value)
                    openTimePickerDialog(endTimeState, needShowEndTimePickerState, endConfirmTimeState)

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp)).clickable {
                    needShowDatePickerState.value = true
                }) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.spacedBy(9.dp)) {
                        Icon(painterResource("icons/icon_calendar.xml"), "", tint = Colors.textMain)

                        textSubTitle(
                            dateState.value.getDayAndMouth(),
                            textSize = 16.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp))) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(Modifier.clickable {
                            needShowStartTimePickerState.value = true
                        }, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Icon(painterResource("icons/icon_clock_start.xml"), "", tint = Colors.textMain)

                            textSubTitle(
                                startConfirmTimeState.value.toString(),
                                textSize = 16.sp,
                                lineHeight = 24.sp,
                                textAlign = TextAlign.Start
                            )
                        }

                        Icon(
                            painterResource("icons/icon_arrow_back.xml"),
                            "",
                            Modifier.rotate(180f),
                            tint = Colors.textMain
                        )

                        Row(Modifier.clickable {
                            needShowEndTimePickerState.value = true
                        }, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Icon(painterResource("icons/icon_clock_end.xml"), "", tint = Colors.textMain)

                            textSubTitle(
                                endConfirmTimeState.value.toString(),
                                textSize = 16.sp,
                                lineHeight = 24.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp))) {
                    FlowRow(
                        Modifier.fillMaxWidth().padding(16.dp, 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        typesUser.value.data?.results?.forEach {
                            Chip(
                                {
                                    if (selectedTypes.contains(it.id))
                                        selectedTypes.remove(it.id)
                                    else
                                        selectedTypes.add(it.id)
                                },
                                colors = ChipDefaults.chipColors(if (selectedTypes.contains(it.id)) Colors.textViewBorder else Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                textSubTitle(
                                    it.name,
                                    textSize = 14.scaledSp(),
                                    lineHeight = 18.scaledSp(),
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    textColor = if (selectedTypes.contains(it.id)) Color.White else Colors.textMain
                                )
                            }
                        }

                        Chip(
                            {
                                navigator.push(AddTypeScreen())
                            }, colors = ChipDefaults.chipColors(Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(painterResource("icons/icon_add.xml"), "", tint = Colors.textMain)
                        }
                    }
                }

                materialButton({
                    timetableViewModel.createEvent(
                        dateState.value.setTime(
                            startTimeState.hour,
                            startTimeState.minute
                        ).getTimeFormat(),
                        dateState.value.setTime(
                            endTimeState.hour,
                            endTimeState.minute
                        ).getTimeFormat(),
                        selectedTypes.toList(),
                        dateState.value.getIsoFormat(),
                        userData.value.data?.id ?: 0,
                        userData.value.data?.groupId,
                        userData.value.data?.teacherId
                    )
                    navigator.hide()
                }, Modifier.fillMaxWidth(), "Добавить")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun openDatePickerDialog(
        datePickerState: DatePickerState,
        showDialog: MutableState<Boolean>,
        confirmDateState: MutableState<DataTime>
    ) {
        DatePickerDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    confirmDateState.value = DataTime(
                        Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis ?: 0)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).also {
                                println(it)
                            }
                    )
                }) {
                    Text("Ок")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Отмена")
                }
            }, properties = DialogProperties()
        ) {
            DatePicker(datePickerState)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun openTimePickerDialog(
        timePickerState: TimePickerState,
        showDialog: MutableState<Boolean>,
        confirmTimeState: MutableState<LocalTime>
    ) {
        TimePickerDialog(
            "Выберите время",
            { showDialog.value = false },
            {
                showDialog.value = false
                confirmTimeState.value = LocalTime(timePickerState.hour, timePickerState.minute)
            },
        ) {
            TimePicker(timePickerState)
        }
    }

    @Composable
    fun TimePickerDialog(
        title: String = "Select Time",
        onCancel: () -> Unit,
        onConfirm: () -> Unit,
        toggle: @Composable () -> Unit = {},
        content: @Composable () -> Unit,
    ) {
        Dialog(
            onDismissRequest = onCancel,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
        ) {
            Surface(
                shape = shapes.large,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = shapes.large,
                        color = colors.surface
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = title,
                    )
                    content()
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        toggle()
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = onCancel
                        ) { Text("Отмена") }
                        TextButton(
                            onClick = onConfirm
                        ) { Text("Ок") }
                    }
                }
            }
        }
    }
}