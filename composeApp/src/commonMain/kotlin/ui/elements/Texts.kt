package ui.elements

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ui.Colors

object Texts {
    @Composable
    fun textTitle(text: String, modifier: Modifier = Modifier) {
        Text(
            text,
            modifier,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif
            ),
            fontWeight = FontWeight(600),
            color = Colors.textMain
        )
    }

    @Composable
    fun textSubTitle(
        text: String,
        modifier: Modifier = Modifier,
        textSize: TextUnit = 20.sp,
        lineHeight: TextUnit = 24.sp,
        textColor: Color = Colors.textMain
    ) {
        Text(
            text,
            modifier,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = textSize,
                lineHeight = lineHeight,
                fontFamily = FontFamily.SansSerif
            ),
            fontWeight = FontWeight(500),
            color = textColor
        )
    }
}