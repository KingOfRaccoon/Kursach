import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import data.util.Postman
import org.koin.core.context.startKoin
import org.koin.dsl.module
import service.AuthenticationService
import service.TimetableService
import ui.splash.SplashScreen
import viewmodel.AuthenticationViewModel
import viewmodel.TimetableViewModel

val modules = module {
    single { Postman() }

    single { AuthenticationService(get()) }
    single { TimetableService(get()) }

    single { AuthenticationViewModel(get()) }
    single { TimetableViewModel(get()) }
}
@Composable
fun App()  {
    startKoin{
        modules(modules)
    }
    Navigator(
        screen = SplashScreen(),
        onBackPressed = { currentScreen ->
            println("Navigator: Pop screen #${(currentScreen).key}")
            true
        }
    )
}