package ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import timer
import ui.authentication.AuthenticationScreen

class SplashScreen: Screen {
    override val key: ScreenKey
        get() = uniqueScreenKey


    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        MaterialTheme {
            val navigator = LocalNavigator.currentOrThrow
            CoroutineScope(Dispatchers.Default).timer(1000) {
                navigator.replace(AuthenticationScreen())
            }

            Column(Modifier.fillMaxSize()) {
                Image(
                    painterResource("image_splash.jpg"),
                    null
                )
            }
        }
    }
}