import kotlinx.coroutines.*

fun CoroutineScope.timer(
    tickInMillis: Long,
    action: () -> Unit
) {
    this.launch(Dispatchers.Default) {
        delay(tickInMillis); action()
    }
}