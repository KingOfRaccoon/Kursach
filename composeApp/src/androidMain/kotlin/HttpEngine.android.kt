import androidx.compose.ui.Modifier
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

actual val httpClientEngine: HttpClientEngineFactory<*>
    get() = CIO

actual fun Modifier.onScrollCancel(action: () -> Unit): Modifier {
    return this
}