package ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import data.network.ItemFilter
import data.network.ItemGroupFilter
import data.network.ItemTeacherFilter
import data.util.Resource
import ui.elements.TextFields
import ui.elements.Texts.textSubTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersDialog(
    filters: State<Pair<Resource<List<ItemTeacherFilter>>, Resource<List<ItemGroupFilter>>>>,
    filterState: MutableState<String>,
    needDialogSearch: MutableState<Boolean>,
    itemFilterState: MutableState<ItemFilter?>,
    function: (ItemFilter) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { },
        Modifier,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            modifier = Modifier.wrapContentSize()
                .fillMaxWidth(0.7f)
                .fillMaxHeight(0.8f)
                .background(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Выберите группу или преподавателя",
                )

                TextFields.outlinedTextField(
                    "Группа или преподаватель",
                    filterState,
                    { filterState.value = it },
                    mutableStateOf<String?>(null),
                    Modifier.fillMaxWidth()
                )

                LazyColumn(Modifier.fillMaxWidth()) {
                    items(
                        filters.value.second.data.orEmpty()
                            .filter { it.filter.contains(filterState.value) }
                                + filters.value.first.data.orEmpty()
                            .filter { it.filter.contains(filterState.value) }) {
                        when (it) {
                            is ItemGroupFilter -> GroupItem(it, itemFilterState, needDialogSearch, function)
                            is ItemTeacherFilter -> TeacherItem(it, itemFilterState, needDialogSearch, function)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupItem(
    itemGroupFilter: ItemGroupFilter,
    itemFilterState: MutableState<ItemFilter?>,
    needDialogSearch: MutableState<Boolean>,
    function: (ItemFilter) -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable {
        itemFilterState.value = itemGroupFilter
        needDialogSearch.value = false
        function(itemGroupFilter)
    }) {
        textSubTitle(itemGroupFilter.name)
    }
}

@Composable
fun TeacherItem(
    itemTeacherFilter: ItemTeacherFilter,
    itemFilterState: MutableState<ItemFilter?>,
    needDialogSearch: MutableState<Boolean>,
    function: (ItemFilter) -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable {
        itemFilterState.value = itemTeacherFilter
        needDialogSearch.value = false
        function(itemTeacherFilter)
    }) {
        textSubTitle(itemTeacherFilter.name)
    }
}