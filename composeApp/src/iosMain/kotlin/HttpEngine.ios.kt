import androidx.compose.ui.Modifier
import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual val httpClientEngine: HttpClientEngineFactory<*>
    get() = Darwin

actual fun Modifier.onScrollCancel(action: () -> Unit): Modifier {
    return this
}