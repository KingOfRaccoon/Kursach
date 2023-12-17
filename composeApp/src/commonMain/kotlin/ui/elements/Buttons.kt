package ui.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.Colors

object Buttons {
    @Composable
    fun textButton(action: () -> Unit, modifier: Modifier, text: String, textColor: Color) {
        TextButton(action, modifier) {
            Text(text, color = textColor)
        }
    }

    @Composable
    fun materialButton(
        action: () -> Unit,
        modifier: Modifier,
        text: String,
        color: Color = Colors.textViewBorder,
        cornerSize: CornerSize = CornerSize(100.dp),
        textColor: Color = Color.White,
        textModifier: Modifier = Modifier.padding(0.dp, 6.dp)
    ) {
        Button(
            action,
            modifier,
            shape = MaterialTheme.shapes.small.copy(cornerSize),
            colors = ButtonDefaults.buttonColors(color)
        ) {
            Text(text, textModifier, textColor)
        }
    }
}