package ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import ui.Colors
import ui.Colors.mainBackground
import ui.elements.Texts.textSubTitle
import viewmodel.TimetableViewModel

class HomeScreen : Screen {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val viewModel = koinInject<TimetableViewModel>()

        val datesState = viewModel.datesFlow.collectAsState()
        val currentSelectedItemState = viewModel.currentSelectedItem.collectAsState()

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
                        Column {
                            Row(Modifier.fillMaxWidth().padding(18.dp, 8.dp, 18.dp), Arrangement.SpaceBetween) {
                                textSubTitle("Расписание", Modifier.align(Alignment.CenterVertically))
                                IconButton({}) {
                                    Icon(
                                        painterResource("icons/icon_calendar.xml"),
                                        "",
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
                                    ) { viewModel.setCurrentSelectedItem(-1) }
                                LazyRow(
                                    Modifier.fillMaxWidth().padding(start = 3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    itemsIndexed(datesState.value.subList(1, datesState.value.lastIndex)) { index, it ->
                                        DateItem(
                                            it.getShortcutDayOfWeek(),
                                            it.dayOfMonth.toString(),
                                            currentSelectedItemState.value == index
                                        ) { viewModel.setCurrentSelectedItem(index) }
                                    }
                                }
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
                    fontSize = 13.sp,
                    lineHeight = 15.sp
                )
                textSubTitle(numberDay, Modifier.align(Alignment.CenterHorizontally), 15.sp, 22.5.sp, Color.White)
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
                textSubTitle(numberDay, Modifier.align(Alignment.CenterHorizontally), 15.sp, 22.5.sp)
            }
        }
    }

    @Composable
    fun DayItem() {

    }
}