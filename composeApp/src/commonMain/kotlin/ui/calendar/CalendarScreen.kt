package ui.calendar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.local.EmptyTime
import data.network.Event
import data.network.Lesson
import data.time.DataTime
import data.util.Resource
import epicarchitect.calendar.compose.basis.*
import epicarchitect.calendar.compose.basis.config.LocalBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.config.rememberBasisEpicCalendarConfig
import epicarchitect.calendar.compose.basis.state.LocalBasisEpicCalendarState
import epicarchitect.calendar.compose.pager.EpicCalendarPager
import epicarchitect.calendar.compose.pager.config.rememberEpicCalendarPagerConfig
import epicarchitect.calendar.compose.pager.state.rememberEpicCalendarPagerState
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import ui.Colors
import ui.addevent.AddEventScreen
import ui.detailEvent.DetailEventScreen
import ui.detailLesson.DetailLessonScreen
import ui.elements.Texts.scaledSp
import ui.elements.Texts.textSubTitle
import ui.home.desktopSnapFling
import viewmodel.AuthenticationViewModel
import viewmodel.TimetableViewModel

class CalendarScreen : Screen {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val authenticationViewModel = koinInject<AuthenticationViewModel>()
        val timetableViewModel = koinInject<TimetableViewModel>()

        val coroutineScope = rememberCoroutineScope()
        val state = rememberEpicCalendarPagerState(
            config = rememberEpicCalendarPagerConfig(
                basisConfig = rememberBasisEpicCalendarConfig(
                    contentColor = Colors.textMain
                )
            )
        )
        val selectedDay = remember { mutableStateOf(DataTime.now()) }
        val userDataState =
            timetableViewModel.combineUserTimetable(authenticationViewModel.userFlow).collectAsState(Resource.Loading())
        val timetable = timetableViewModel.getTimetable(
            userDataState.value.data?.groupId,
            userDataState.value.data?.teacherId
        ).collectAsState(mapOf())

        MaterialTheme {
            Scaffold(
                containerColor = Colors.mainBackground,
                topBar = {
                    TopAppBar(
                        backgroundColor = Color.White,
                        title = {
                            textSubTitle("Календарь")
                        },
                        elevation = 0.dp,
                        navigationIcon = if (navigator.canPop) {
                            {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(
                                        painterResource("icons/icon_arrow_back.xml"),
                                        "Back"
                                    )
                                }
                            }
                        } else {
                            null
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { bottomSheetNavigator.show(AddEventScreen(selectedDay.value)) },
                        shape = CircleShape,
                        backgroundColor = Colors.textViewBorder
                    ) {
                        Icon(painterResource("icons/icon_add.xml"), "", tint = Color.White)
                    }
                },
                floatingActionButtonPosition = FabPosition.End

            ) {
                Column(Modifier.fillMaxWidth().padding(top = it.calculateTopPadding())) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp, 0.dp, 26.dp, 26.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(2.dp)
                    ) {
                        Column(
                            Modifier.fillMaxWidth().padding(18.dp, 16.dp, 18.dp, 24.dp)
                                .clip(RoundedCornerShape(CornerSize(8.dp))).background(Colors.mainBackground),
                            Arrangement.spacedBy(16.dp)
                        ) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                IconButton({ coroutineScope.launch { state.scrollMonths(-1) } }) {
                                    Icon(painterResource("icons/icon_navigate.xml"), "", tint = Colors.textViewBorder)
                                }

                                textSubTitle(
                                    DataTime.getMouth(state.currentMonth.month.number),
                                    Modifier.align(Alignment.CenterVertically)
                                )

                                IconButton({ coroutineScope.launch { state.scrollMonths(1) } }) {
                                    Icon(
                                        painterResource("icons/icon_navigate.xml"),
                                        "",
                                        Modifier.rotate(180f),
                                        Colors.textViewBorder
                                    )
                                }
                            }

                            EpicCalendarPager(
                                modifier = Modifier.desktopSnapFling(state.pagerState, coroutineScope),
                                state = state,
                                onDayOfMonthClick = {
                                    timetableViewModel.loadDayTimetable(
                                        DataTime(it.atTime(0, 0)).getIsoFormat(),
                                        userDataState.value.data?.groupId,
                                        userDataState.value.data?.teacherId,
                                        ownerId = userDataState.value.data?.id ?: -1
                                    )
                                    selectedDay.value = DataTime(it.atTime(0, 0))
                                },
                                dayOfWeekContent = dayOfWeek(),
                                dayOfMonthContent = dayOfMouth(selectedDay.value.getIsoFormat())
                            )
                        }
                    }

                    LazyColumn(
                        Modifier.fillMaxWidth().padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            timetableViewModel.createItems(
                                timetable.value[selectedDay.value.getIsoFormat()]?.data?.getTimetableItems().orEmpty()
                            )
                        ) {
                            when (it) {
                                is Lesson -> LessonItem(it) { date, lessonId ->
                                    bottomSheetNavigator.show(DetailLessonScreen(date, lessonId))
                                }

                                is Event -> EventItem(it) { date, eventId ->
                                    bottomSheetNavigator.show(DetailEventScreen(date, eventId))
                                }

                                is EmptyTime -> EmptyTimeItem(it)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EmptyTimeItem(emptyTime: EmptyTime) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
                .padding(bottom = (30 * emptyTime.paddingCoefficient).dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            textSubTitle(
                "${if (emptyTime.startTime > 9) emptyTime.startTime else "0${emptyTime.startTime}"}:00",
                textSize = 14.scaledSp(),
                lineHeight = 18.scaledSp(),
                modifier = Modifier.align(Alignment.CenterVertically),
                textColor = Colors.textMain
            )
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                color = Colors.chipsNonActive,
                thickness = 1.dp
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
    @Composable
    fun LessonItem(lesson: Lesson, navigateToDetailLessonScreen: (String, Int) -> Unit) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp).clickable {
            navigateToDetailLessonScreen(lesson.getDataTimeStart().getIsoFormat(), lesson.id)
        }) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                textSubTitle(lesson.getDataTimeStart().getTime(), textSize = 14.scaledSp(), lineHeight = 18.scaledSp())
                textSubTitle(
                    lesson.getDataTimeEnd().getTime(),
                    textSize = 14.scaledSp(),
                    lineHeight = 18.scaledSp(),
                    textColor = Colors.textButtonNonActive
                )
            }

            Column(Modifier.fillMaxWidth().padding(start = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                            .align(Alignment.CenterVertically),
                        color = Colors.chipsNonActive,
                        thickness = 1.dp
                    )

                    textSubTitle(
                        lesson.type.name,
                        textSize = 14.scaledSp(),
                        lineHeight = 18.scaledSp(),
                        modifier = Modifier.align(Alignment.CenterVertically),
                        textColor = Colors.textLight
                    )

                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                            .align(Alignment.CenterVertically),
                        color = Colors.chipsNonActive,
                        thickness = 1.dp
                    )
                }

                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8FFDB)),
                    shape = RoundedCornerShape(CornerSize(9.dp))
                ) {
                    Column(Modifier.padding(12.dp, 9.dp)) {
                        textSubTitle(
                            lesson.nameSubject,
                            textAlign = TextAlign.Start
                        )

                        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Chip(
                                {}, colors = ChipDefaults.chipColors(Color(0x66FFFFFF)), shape = RoundedCornerShape(
                                    CornerSize(6.dp)
                                )
                            ) {
                                textSubTitle(
                                    lesson.rooms.joinToString { it.buildingName + ", " + it.name },
                                    textSize = 14.scaledSp(),
                                    lineHeight = 18.scaledSp(),
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                )
                            }

                            Chip(
                                {}, colors = ChipDefaults.chipColors(Color(0x66FFFFFF)), shape = RoundedCornerShape(
                                    CornerSize(6.dp)
                                )
                            ) {
                                textSubTitle(
                                    if (lesson.teachers.isNotEmpty()) {
                                        if (lesson.teachers.size < 2) lesson.teachers.first().fullName()
                                        else if (lesson.teachers.size % 10 < 5 && lesson.teachers.size !in 11..19)
                                            "${lesson.teachers.size} преподавателя"
                                        else "${lesson.teachers.size} преподавателей"
                                    } else {
                                        if (lesson.teachersIds.isNotEmpty()) "Загружаются..." else "Нет преподавателей"
                                    },
                                    textSize = 14.scaledSp(),
                                    lineHeight = 18.scaledSp(),
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                )
                            }

                            Chip(
                                {}, colors = ChipDefaults.chipColors(Color(0x66FFFFFF)), shape = RoundedCornerShape(
                                    CornerSize(6.dp)
                                )
                            ) {
                                textSubTitle(
                                    if (lesson.groups.isNotEmpty()) {
                                        if (lesson.groups.size < 2) lesson.groups.first().groupName
                                        else if (lesson.groups.size % 10 < 5 && lesson.groups.size !in 11..19)
                                            "${lesson.groups.size} группы"
                                        else "${lesson.groups.size} групп"
                                    } else {
                                        if (lesson.groupsIds.isNotEmpty()) "Загружаются..." else "Нет групп"
                                    },
                                    textSize = 14.scaledSp(),
                                    lineHeight = 18.scaledSp(),
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
    @Composable
    fun EventItem(event: Event, navigateToDetailEventScreen: (String, Int) -> Unit) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp).clickable {
            navigateToDetailEventScreen(event.getDataTimeStart().getIsoFormat(), event.id)
        }) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                textSubTitle(event.getDataTimeStart().getTime(), textSize = 14.scaledSp(), lineHeight = 18.scaledSp())
                textSubTitle(
                    event.getDataTimeEnd().getTime(),
                    textSize = 14.scaledSp(),
                    lineHeight = 18.scaledSp(),
                    textColor = Colors.textButtonNonActive
                )
            }

            Column(Modifier.fillMaxWidth().padding(start = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                            .align(Alignment.CenterVertically),
                        color = Colors.chipsNonActive,
                        thickness = 1.dp
                    )
                    textSubTitle(
                        if (event.types.isNotEmpty()) event.types.first().name else {
                            if (event.typeIds.isNotEmpty()) "Загружается..." else "Мероприятие"
                        },
                        textSize = 14.scaledSp(),
                        lineHeight = 18.scaledSp(),
                        modifier = Modifier.align(Alignment.CenterVertically),
                        textColor = Colors.textLight
                    )
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                            .align(Alignment.CenterVertically),
                        color = Colors.chipsNonActive,
                        thickness = 1.dp
                    )
                }

                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8FFDB)),
                    shape = RoundedCornerShape(CornerSize(9.dp))
                ) {
                    Column(Modifier.padding(12.dp, 9.dp)) {
                        textSubTitle(
                            event.name,
                            textAlign = TextAlign.Start
                        )

                        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Chip(
                                {}, colors = ChipDefaults.chipColors(Color(0x66FFFFFF)), shape = RoundedCornerShape(
                                    CornerSize(6.dp)
                                )
                            ) {
                                textSubTitle(
                                    event.place,
                                    textSize = 14.scaledSp(),
                                    lineHeight = 18.scaledSp(),
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun dayOfWeek(): BasisDayOfWeekContent = {
        Text(
            text = it.localized(),
            textAlign = TextAlign.Center,
            color = Colors.textViewBorder
        )
    }

    @Composable
    fun dayOfMouth(selectedDay: String): BasisDayOfMonthContent = {
        val gradientColors =
            listOf(
                Color(0xFF1DAAFD),
                Color(0xFF3668FB),
                Color(0xFF644EFF)
            )
        val state = LocalBasisEpicCalendarState.current!!
        val config = LocalBasisEpicCalendarConfig.current
        Box(
            if (DataTime(it.atTime(0, 0)).getIsoFormat() == selectedDay)
                Modifier.fillMaxSize().border(
                    BorderStroke(
                        2.dp,
                        Brush.linearGradient(gradientColors)
                    ), RoundedCornerShape(16.dp)
                )
            else
                Modifier.fillMaxSize(),
            Alignment.Center
        ) {
            Text(
                modifier = Modifier.alpha(
                    alpha = remember(it, state.currentMonth) {
                        if (it in state.currentMonth) 1.0f else 0.5f
                    }
                ),
                text = it.dayOfMonth.toString(),
                textAlign = TextAlign.Center,
                color = config.contentColor
            )
        }
    }
}