package ui.detailEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

class DetailEventScreen(private val date: String, private val eventId: Int) : Screen {

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
        val event = timetable.value[date]?.data?.events?.find { it.id == eventId }
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
                        textTitle(
                            if (event?.types?.isNotEmpty() == true) event.types.first().shortName else "",
                            Modifier.padding(14.dp, 7.dp),
                            Colors.textLight
                        )
                    }

                    textTitle(event?.name ?: "", textAlign = TextAlign.Start)
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
                                    event?.getDataTimeStart()?.getDayAndMouth().orEmpty(),
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
                                    event?.getDataTimeStart()?.getTime().orEmpty() + " - " + event?.getDataTimeEnd()
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
                    Row(Modifier.fillMaxWidth().padding(16.dp, 12.dp), Arrangement.spacedBy(12.dp)) {
                        Icon(painterResource("icons/icon_place.xml"), "", Modifier.align(Alignment.CenterVertically))

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            Chip(
                                {}, colors = ChipDefaults.chipColors(Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Texts.textSubTitle(
                                    event?.place.orEmpty(),
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