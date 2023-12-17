import kotlinx.coroutines.*

fun CoroutineScope.timer(
    tickInMillis: Long,
    action: () -> Unit
) {
    this.launch(Dispatchers.Default) {
//        while (true) {
            delay(tickInMillis)
            withContext(Dispatchers.Main) { action() }
//        }
    }
}