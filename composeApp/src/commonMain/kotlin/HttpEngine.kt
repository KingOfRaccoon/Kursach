import androidx.compose.ui.Modifier
import io.ktor.client.engine.*

expect val httpClientEngine: HttpClientEngineFactory<*>

expect fun Modifier.onScrollCancel(action: () -> Unit): Modifier