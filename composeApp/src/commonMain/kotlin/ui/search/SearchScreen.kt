package ui.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import ui.Colors
import ui.detailEvent.DetailEventScreen
import ui.detailLesson.DetailLessonScreen
import ui.dialogs.FiltersDialog
import ui.elements.Texts
import ui.elements.Texts.scaledSp
import viewmodel.AuthenticationViewModel
import viewmodel.TimetableViewModel

class SearchScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        val viewModel = koinInject<TimetableViewModel>()
        val authenticationViewModel = koinInject<AuthenticationViewModel>()

        val needDialogSearch = remember { mutableStateOf(false) }
        val filterState = remember { mutableStateOf("") }
        val itemFilterState = remember { mutableStateOf<ItemFilter?>(null) }

        val datesState = viewModel.datesFlow.collectAsState()
        val currentSelectedItemState = viewModel.currentSelectedSearchItem.collectAsState()
        val datesListState = rememberLazyListState()
        val userData =
            viewModel.combineUserTimetable(authenticationViewModel.userFlow).collectAsState(Resource.Loading())
        val timetable =
            viewModel.getTimetable(
                itemFilterState.value?.let { it as? ItemGroupFilter }?.id,
                itemFilterState.value?.let { it as? ItemTeacherFilter }?.id
            ).collectAsState(mapOf())
        val pagerState = remember { mutableIntStateOf(0) }

        val filters = viewModel.getFiltersTimetable()
            .collectAsState(Resource.Loading<List<ItemTeacherFilter>>() to Resource.Loading())



        MaterialTheme {
            Column(
                Modifier.padding(18.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Divider(Modifier.fillMaxWidth(0.2f).align(Alignment.CenterHorizontally), Colors.textLight, 4.dp)

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
                    FiltersDialog(filters, filterState, needDialogSearch, itemFilterState) { itemFilter: ItemFilter ->
                        val today = DataTime.now()
                        viewModel.loadWeekTimetable(
                            today.year.toString(),
                            (today.getWeek() - 4).toString(),
                            itemFilter.let { it as? ItemGroupFilter }?.id,
                            itemFilter.let { it as? ItemTeacherFilter }?.id,
                            ownerId = userData.value.data?.id ?: -1
                        )
                    }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp, 9.dp).clip(
                        RoundedCornerShape(8.dp)
                    ).background(Colors.mainBackground)
                ) {
                    if (datesState.value.isNotEmpty())
                        HeaderDateItem(
                            datesState.value.first().getShortcutDayOfWeek(),
                            datesState.value.first().dayOfMonth.toString()
                        ) {
                            viewModel.setCurrentSelectedSearchItem(-1)
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
                                viewModel.setCurrentSelectedSearchItem(index)
                                pagerState.value = index + 1
                                viewModel.loadDayTimetable(
                                    it.getIsoFormat(),
                                    itemFilterState.value?.let { it as? ItemGroupFilter }?.id,
                                    itemFilterState.value?.let { it as? ItemTeacherFilter }?.id,
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
                Texts.textSubTitle(
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
                Texts.textSubTitle(
                    numberDay,
                    Modifier.align(Alignment.CenterHorizontally),
                    15.scaledSp(),
                    22.scaledSp()
                )
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
//                        bottomSheetNavigator.replace(DetailLessonScreen(date, lessonId))
                    }

                    is Event -> ItemEvent(it) { date, eventId ->
//                        bottomSheetNavigator.replace(DetailEventScreen(date, eventId))
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
                Texts.textSubTitle(
                    lesson.getDataTimeStart().getTime(),
                    textSize = 14.scaledSp(),
                    lineHeight = 18.scaledSp()
                )
                Texts.textSubTitle(
                    lesson.getDataTimeEnd().getTime(),
                    textSize = 14.scaledSp(),
                    lineHeight = 18.scaledSp(),
                    textColor = Colors.textButtonNonActive
                )
            }

            Card(Modifier.weight(10f), backgroundColor = Colors.mainBackground, elevation = 0.dp) {
                Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceBetween) {
                    Column(Modifier.weight(10f)) {
                        Texts.textSubTitle(
                            lesson.nameSubject,
                            textAlign = TextAlign.Start
                        )

                        Row {
                            Icon(painterResource("icons/icon_location.xml"), "")
                            Texts.textSubTitle(
                                lesson.rooms.joinToString { it.buildingName + ", " + it.name },
                                textSize = 14.scaledSp(),
                                lineHeight = 18.scaledSp(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    Texts.textSubTitle(
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
                Texts.textSubTitle(
                    event.getDataTimeStart().getTime(),
                    textSize = 14.scaledSp(),
                    lineHeight = 18.scaledSp()
                )
                Texts.textSubTitle(
                    event.getDataTimeEnd().getTime(),
                    textSize = 14.scaledSp(),
                    lineHeight = 18.scaledSp(),
                    textColor = Colors.textButtonNonActive
                )
            }

            Card(Modifier.weight(10f), backgroundColor = Colors.mainBackground, elevation = 0.dp) {
                Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.spacedBy(10.dp)) {
                    Column {
                        Texts.textSubTitle(
                            event.name,
                            textAlign = TextAlign.Start
                        )
                        Row {
                            Icon(painterResource("icons/icon_location.xml"), "")
                            Texts.textSubTitle(
                                event.place,
                                textSize = 14.scaledSp(),
                                lineHeight = 18.scaledSp(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    if (event.types.isNotEmpty())
                        Texts.textSubTitle(
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
            Texts.textTitle("Пар и дел нет!", Modifier.fillMaxWidth().padding(bottom = 8.dp))
        }
    }
}