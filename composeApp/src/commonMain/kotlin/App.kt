import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
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
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App()  {
    startKoin{
        modules(modules)
    }
    BottomSheetNavigator(sheetShape = RoundedCornerShape(20.dp, 20.dp)) {
        Navigator(
            screen = SplashScreen(),
            onBackPressed = { currentScreen ->
                println("Navigator: Pop screen #${(currentScreen).key}")
                true
            }
        )
    }
}