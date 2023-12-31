package ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.network.*
import data.time.DataTime
import data.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import onScrollCancel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import ui.Colors
import ui.Colors.mainBackground
import ui.Colors.textButtonNonActive
import ui.calendar.CalendarScreen
import ui.detailEvent.DetailEventScreen
import ui.detailLesson.DetailLessonScreen
import ui.dialogs.FiltersDialog
import ui.elements.Texts.scaledSp
import ui.elements.Texts.textSubTitle
import ui.elements.Texts.textTitle
import ui.search.SearchScreen
import viewmodel.AuthenticationViewModel
import viewmodel.TimetableViewModel

class HomeScreen : Screen {
    @OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        val viewModel = koinInject<TimetableViewModel>()
        val authenticationViewModel = koinInject<AuthenticationViewModel>()

        val datesState = viewModel.datesFlow.collectAsState()
        val currentSelectedItemState = viewModel.currentSelectedItem.collectAsState()
        val datesListState = rememberLazyListState()
        val userData =
            viewModel.combineUserTimetable(authenticationViewModel.userFlow).collectAsState(Resource.Loading())
        val timetable =
            viewModel.getTimetable(userData.value.data?.groupId, userData.value.data?.teacherId).collectAsState(mapOf())
        val pagerState = remember { mutableIntStateOf(0) }

        MaterialTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        backgroundColor = mainBackground,
                        title = {
                            textSubTitle("GuApp")
                        },
                        elevation = 0.dp
                    )
                }
            ) {
                Column(
                    Modifier.fillMaxSize().background(mainBackground).padding(top = it.calculateTopPadding())
                ) {
                    Card(
                        modifier = Modifier
                            .padding(18.dp, 16.dp, 18.dp)
                            .shadow(
                                shape = RoundedCornerShape(18.dp),
                                spotColor = Color(0xCC4F5A85),
                                elevation = 5.dp,
                            ),
                        elevation = 0.dp,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(Modifier.animateContentSize(tween(500))) {
                            Row(
                                Modifier.fillMaxWidth().padding(18.dp, 8.dp, 18.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                textSubTitle(
                                    "Расписание", Modifier.weight(1f)
                                        .padding(end = 12.dp)
                                        .align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Start
                                )

                                Chip(
                                    { bottomSheetNavigator.show(SearchScreen()) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    colors = ChipDefaults.chipColors(mainBackground)
                                ) {
                                    Text(userData.value.data?.getName().orEmpty(), color = Colors.textLight)
                                }

                                Box(
                                    Modifier.weight(1f)
                                        .padding(end = 12.dp)
                                        .align(Alignment.CenterVertically),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        painterResource("icons/icon_calendar.xml"),
                                        "",
                                        Modifier.clickable {
                                            navigator.push(CalendarScreen())
                                        },
                                        tint = Colors.textViewBorder
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp, 9.dp).clip(
                                    RoundedCornerShape(8.dp)
                                ).background(mainBackground)
                            ) {
                                if (datesState.value.isNotEmpty())
                                    HeaderDateItem(
                                        datesState.value.first().getShortcutDayOfWeek(),
                                        datesState.value.first().dayOfMonth.toString()
                                    ) {
                                        viewModel.setCurrentSelectedItem(-1)
                                        pagerState.value = 0
                                    }
                                LazyRow(
                                    Modifier.fillMaxWidth().padding(start = 3.dp),
                                    datesListState,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    itemsIndexed(datesState.value.subList(1, datesState.value.size)) { index, it ->
                                        DateItem(
                                            it.getShortcutDayOfWeek(),
                                            it.dayOfMonth.toString(),
                                            currentSelectedItemState.value == index
                                        ) {
                                            viewModel.setCurrentSelectedItem(index)
                                            pagerState.value = index + 1
                                            viewModel.loadDayTimetable(
                                                it.getIsoFormat(),
                                                userData.value.data?.groupId,
                                                userData.value.data?.teacherId,
                                                ownerId = userData.value.data?.id
                                            )
                                        }
                                    }
                                }
                            }

                            val today = DataTime.getStartThisWeek().goToNNextDay(-27)
                            Box(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                                DayItem(
                                    timetable.value[today.goToNNextDay(currentSelectedItemState.value + 4)
                                        .getIsoFormat()]
                                        ?: Resource.Loading(), bottomSheetNavigator
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun HeaderDateItem(nameDay: String, numberDay: String, action: () -> Unit) {
        val gradientColors =
            listOf(
                Color(0xFF1DAAFD),
                Color(0xFF3668FB),
                Color(0xFF644EFF)
            )

        Box(Modifier.clip(RoundedCornerShape(8.dp))) {
            Column(
                Modifier.clip(
                    RoundedCornerShape(CornerSize(8.dp))
                ).background(Brush.linearGradient(gradientColors)).clickable { action() }
                    .padding(11.dp, 6.dp, 11.dp, 6.dp)
                    .padding(horizontal = 1.5.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
            ) {
                Text(
                    nameDay,
                    Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White,
                    fontSize = 13.scaledSp(),
                    lineHeight = 15.scaledSp()
                )
                textSubTitle(
                    numberDay,
                    Modifier.align(Alignment.CenterHorizontally),
                    15.scaledSp(),
                    22.scaledSp(),
                    Color.White
                )
            }
        }
    }

    @Composable
    private fun DateItem(nameDay: String, numberDay: String, isSelected: Boolean, action: () -> Unit) {
        val gradientColors =
            listOf(
                Color(0xFF1DAAFD),
                Color(0xFF3668FB),
                Color(0xFF644EFF)
            )
        Box(Modifier.clip(RoundedCornerShape(8.dp))) {
            Column(
                if (isSelected)
                    Modifier.border(
                        2.dp,
                        Brush.linearGradient(gradientColors),
                        RoundedCornerShape(CornerSize(8.dp))
                    ).clickable { action() }.padding(11.dp, 6.dp, 11.dp, 6.dp)
                else
                    Modifier.border(2.dp, Color.Transparent).clickable { action() }.padding(11.dp, 6.dp, 11.dp, 6.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
            ) {
                Text(
                    nameDay,
                    Modifier.align(Alignment.CenterHorizontally),
                    color = Colors.textLight,
                    fontSize = 13.sp,
                    lineHeight = 15.sp
                )
                textSubTitle(numberDay, Modifier.align(Alignment.CenterHorizontally), 15.scaledSp(), 22.scaledSp())
            }
        }
    }

    @Composable
    fun DayItem(resourceTimetable: Resource<Timetable>, bottomSheetNavigator: BottomSheetNavigator) {
        when (resourceTimetable) {
            is Resource.Error -> {

            }

            is Resource.Loading -> {

            }

            is Resource.Success -> ListEvents(resourceTimetable.data, bottomSheetNavigator)
        }
    }

    @Composable
    fun ListEvents(timetable: Timetable, bottomSheetNavigator: BottomSheetNavigator) {
        println("ListEvents")
        if (timetable.getTimetableItems().isEmpty())
            return ItemNoEvents()

        println("ListEvents 2")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            items(timetable.getTimetableItems()) {
                when (it) {
                    is Lesson -> ItemLesson(it) { date, lessonId ->
                        bottomSheetNavigator.show(DetailLessonScreen(date, lessonId))
                    }

                    is Event -> ItemEvent(it) { date, eventId ->
                        bottomSheetNavigator.show(DetailEventScreen(date, eventId))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun ItemLesson(lesson: Lesson, navigateToDetailLessonScreen: (String, Int) -> Unit) {
        Row(Modifier.clickable {
            navigateToDetailLessonScreen(lesson.getDataTimeStart().getIsoFormat(), lesson.id)
        }, Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f).align(Alignment.CenterVertically)) {
                textSubTitle(lesson.getDataTimeStart().getTime(), textSize = 14.scaledSp(), lineHeight = 18.scaledSp())
                textSubTitle(
                    lesson.getDataTimeEnd().getTime(),
                    textSize = 14.scaledSp(),
                    lineHeight = 18.scaledSp(),
                    textColor = textButtonNonActive
                )
            }

            Card(Modifier.weight(10f), backgroundColor = mainBackground, elevation = 0.dp) {
                Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceBetween) {
                    Column(Modifier.weight(10f)) {
                        textSubTitle(
                            lesson.nameSubject,
                            textAlign = TextAlign.Start
                        )

                        Row {
                            Icon(painterResource("icons/icon_location.xml"), "")
                            textSubTitle(
                                lesson.rooms.joinToString { it.buildingName + ", " + it.name },
                                textSize = 14.scaledSp(),
                                lineHeight = 18.scaledSp(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    textSubTitle(
                        lesson.type.shortName,
                        Modifier.weight(1f).padding(start = 10.dp),
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun ItemEvent(event: Event, navigateToDetailEventScreen: (String, Int) -> Unit) {
        Row(Modifier.fillMaxWidth().clickable {
            navigateToDetailEventScreen(event.getDataTimeStart().getIsoFormat(), event.id)
        }, Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f).fillMaxWidth().align(Alignment.CenterVertically)) {
                textSubTitle(event.getDataTimeStart().getTime(), textSize = 14.scaledSp(), lineHeight = 18.scaledSp())
                textSubTitle(
                    event.getDataTimeEnd().getTime(),
                    textSize = 14.scaledSp(),
                    lineHeight = 18.scaledSp(),
                    textColor = textButtonNonActive
                )
            }

            Card(Modifier.weight(10f), backgroundColor = mainBackground, elevation = 0.dp) {
                Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.spacedBy(10.dp)) {
                    Column {
                        textSubTitle(
                            event.name,
                            textAlign = TextAlign.Start
                        )
                        Row {
                            Icon(painterResource("icons/icon_location.xml"), "")
                            textSubTitle(
                                event.place,
                                textSize = 14.scaledSp(),
                                lineHeight = 18.scaledSp(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    if (event.types.isNotEmpty())
                        textSubTitle(
                            event.types.first().shortName, Modifier.weight(1f).padding(start = 10.dp),
                            textAlign = TextAlign.End
                        )
                }
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun ItemNoEvents() {
        Column(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Image(painterResource("images/image_no_lessons.xml"), "", Modifier.align(Alignment.Center))
            }
            textTitle("Пар и дел нет!", Modifier.fillMaxWidth().padding(bottom = 8.dp))
        }
    }
}

private var sJob: Job? = null

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.desktopSnapFling(pagerState: PagerState, scrollScope: CoroutineScope) = onScrollCancel {
    val offset = pagerState.currentPageOffsetFraction
    if (!pagerState.isScrollInProgress) {
        sJob?.cancel()
        if (offset > 0.2f) {
            sJob = scrollScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1, 0f)
            }
        } else if (offset < -0.2f) {
            sJob = scrollScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1, 0f)
            }
        } else if (offset != 0f) {
            sJob = scrollScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage, 0f)
            }
        }
    }
}