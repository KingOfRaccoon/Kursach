package ui.elements

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

object Icons {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun passwordIcon(
        passwordVisible: State<Boolean>,
        updatePasswordVisible: (Boolean) -> Unit
    ) {
        val image = if (passwordVisible.value)
            painterResource("icons/icon_visibility_off.xml")
        else
            painterResource("icons/icon_visibility_on.xml")

        val description = if (passwordVisible.value) "Hide password" else "Show password"

        IconButton(onClick = { updatePasswordVisible(!passwordVisible.value) }) {
            Icon(image, description, tint = Color(0xFF273469))
        }
    }
}