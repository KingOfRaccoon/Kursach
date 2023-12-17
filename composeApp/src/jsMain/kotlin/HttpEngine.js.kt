import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

actual val httpClientEngine: HttpClientEngineFactory<*>
    get() = Js