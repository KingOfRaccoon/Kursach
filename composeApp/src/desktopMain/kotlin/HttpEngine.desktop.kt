import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

actual val httpClientEngine: HttpClientEngineFactory<*>
    get() = CIO