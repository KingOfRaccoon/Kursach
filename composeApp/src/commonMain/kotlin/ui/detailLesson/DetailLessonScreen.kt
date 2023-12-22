package ui.detailLesson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import data.util.Resource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import ui.Colors
import ui.elements.Texts
import ui.elements.Texts.scaledSp
import ui.elements.Texts.textTitle
import viewmodel.AuthenticationViewModel
import viewmodel.TimetableViewModel

class DetailLessonScreen(private val date: String, private val lessonId: Int) : Screen {

    @OptIn(ExperimentalResourceApi::class, ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val timetableViewModel = koinInject<TimetableViewModel>()
        val authenticationViewModel = koinInject<AuthenticationViewModel>()

        val userData = timetableViewModel.combineUserTimetable(authenticationViewModel.userFlow).collectAsState(Resource.Loading())
        val timetable = timetableViewModel.getTimetable(
            userData.value.data?.groupId,
            userData.value.data?.teacherId
        ).collectAsState(mapOf())

        val lesson = timetable.value[date]?.data?.lessons?.find { it.id == lessonId }
        println("lesson: $lesson")
        println("date: $date")
        println("lessonId: $lessonId")
        println("lessons: ${timetable.value[date]?.data?.lessons}")
        val scrollState = rememberScrollState()

        MaterialTheme {
            Column(
                Modifier.padding(18.dp, 16.dp).verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Divider(Modifier.fillMaxWidth(0.2f).align(Alignment.CenterHorizontally), Colors.textLight, 4.dp)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(12.dp)), Alignment.Center) {
                        textTitle(lesson?.number?.toString() ?: "", Modifier.padding(14.dp, 7.dp), Colors.textLight)
                    }

                    textTitle(
                        lesson?.nameSubject ?: "",
                        textAlign = TextAlign.Start,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp))) {
                    Row(Modifier.fillMaxWidth().padding(16.dp, 8.dp), Arrangement.spacedBy(12.dp)) {
                        Icon(painterResource("icons/icon_groups.xml"), "", Modifier.align(Alignment.CenterVertically))

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            lesson?.groups?.forEach {
                                Chip(
                                    {}, colors = ChipDefaults.chipColors(Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Texts.textSubTitle(
                                        it.groupName,
                                        textSize = 14.scaledSp(),
                                        lineHeight = 18.scaledSp(),
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        textColor = Colors.textLight
                                    )
                                }
                            }
                        }
                    }
                }

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp))) {
                    Row(Modifier.fillMaxWidth().padding(16.dp, 8.dp), Arrangement.spacedBy(12.dp)) {
                        Icon(painterResource("icons/icon_teachers.xml"), "", Modifier.align(Alignment.CenterVertically))

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            lesson?.teachers?.forEach {
                                Chip(
                                    {}, colors = ChipDefaults.chipColors(Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Texts.textSubTitle(
                                        it.fullName(),
                                        textSize = 14.scaledSp(),
                                        lineHeight = 18.scaledSp(),
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }
                }

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp))) {
                    Row(Modifier.fillMaxWidth().padding(16.dp, 8.dp), Arrangement.spacedBy(12.dp)) {
                        Icon(painterResource("icons/icon_clock.xml"), "", Modifier.align(Alignment.CenterVertically))

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Chip(
                                {}, colors = ChipDefaults.chipColors(Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Texts.textSubTitle(
                                    lesson?.getDataTimeStart()?.getDayAndMouth().orEmpty(),
                                    textSize = 14.scaledSp(),
                                    lineHeight = 18.scaledSp(),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }

                            Chip(
                                {}, colors = ChipDefaults.chipColors(Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Texts.textSubTitle(
                                    lesson?.getDataTimeStart()?.getTime().orEmpty() + " - " + lesson?.getDataTimeEnd()
                                        ?.getTime().orEmpty(),
                                    textSize = 14.scaledSp(),
                                    lineHeight = 18.scaledSp(),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }

                Box(Modifier.background(Colors.mainBackground, RoundedCornerShape(16.dp))) {
                    Row(Modifier.fillMaxWidth().padding(16.dp, 8.dp), Arrangement.spacedBy(12.dp)) {
                        Icon(painterResource("icons/icon_place.xml"), "", Modifier.align(Alignment.CenterVertically))

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Chip(
                                {}, colors = ChipDefaults.chipColors(Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Texts.textSubTitle(
                                    lesson?.rooms?.joinToString { it.buildingName + " " + it.name }.orEmpty(),
                                    textSize = 14.scaledSp(),
                                    lineHeight = 18.scaledSp(),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}