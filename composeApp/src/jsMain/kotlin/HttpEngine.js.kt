import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import io.ktor.client.engine.*
import io.ktor.client.engine.js.*
import kotlinx.coroutines.delay

actual val httpClientEngine: HttpClientEngineFactory<*>
    get() = Js

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.onScrollCancel(action: () -> Unit): Modifier = composed {
    var currentEventCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(currentEventCount) {
        if (currentEventCount != 0) {
            delay(50L)
            action()
        }
    }
    return@composed onPointerEvent(PointerEventType.Scroll) {
        currentEventCount += 1
    }
}