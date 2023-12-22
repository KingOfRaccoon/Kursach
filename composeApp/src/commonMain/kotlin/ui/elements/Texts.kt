package ui.elements

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ui.Colors

object Texts {
    @Composable
    fun textTitle(
        text: String,
        modifier: Modifier = Modifier,
        textColor: Color = Colors.textMain,
        textAlign: TextAlign = TextAlign.Center
    ) {
        Text(
            text,
            modifier,
            textAlign = textAlign,
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif
            ),
            fontWeight = FontWeight(600),
            color = textColor
        )
    }

    @Composable
    fun textSubTitle(
        text: String,
        modifier: Modifier = Modifier,
        textSize: TextUnit = 20.scaledSp(),
        lineHeight: TextUnit = 24.scaledSp(),
        textColor: Color = Colors.textMain,
        textAlign: TextAlign = TextAlign.Center,
        isSingleLine: Boolean = false
    ) {
        Text(
            text,
            modifier,
            textAlign = textAlign,
            style = TextStyle(
                fontSize = textSize,
                lineHeight = lineHeight,
                fontFamily = FontFamily.SansSerif
            ),
            fontWeight = FontWeight(500),
            color = textColor,
            maxLines = if (isSingleLine) 1 else Int.MAX_VALUE,
        )
    }

    sealed class AutoSizeConstraint(open val min: TextUnit = TextUnit.Unspecified) {
        data class Width(override val min: TextUnit = TextUnit.Unspecified) : AutoSizeConstraint(min)
        data class Height(override val min: TextUnit = TextUnit.Unspecified) : AutoSizeConstraint(min)
    }

    @Composable
    fun AutoSizeText(
        text: AnnotatedString,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontSize: TextUnit = TextUnit.Unspecified,
        fontStyle: FontStyle? = null,
        fontWeight: FontWeight? = null,
        fontFamily: FontFamily? = null,
        letterSpacing: TextUnit = TextUnit.Unspecified,
        textDecoration: TextDecoration? = null,
        textAlign: TextAlign? = null,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Clip,
        softWrap: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        style: TextStyle = LocalTextStyle.current,
        constraint: AutoSizeConstraint = AutoSizeConstraint.Width(),
    ) {
        var textStyle by remember { mutableStateOf(style) }
        var readyToDraw by remember { mutableStateOf(false) }

        Text(
            modifier = modifier.drawWithContent {
                if (readyToDraw) drawContent()
            },
            text = text,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            style = style,
            onTextLayout = { result ->
                fun constrain() {
                    val reducedSize = textStyle.fontSize * 0.9f
                    if (constraint.min != TextUnit.Unspecified && reducedSize <= constraint.min) {
                        textStyle = textStyle.copy(fontSize = constraint.min)
                        readyToDraw = true
                    } else {
                        textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9f)
                    }
                }

                when (constraint) {
                    is AutoSizeConstraint.Height -> {
                        if (result.didOverflowHeight) {
                            constrain()
                        } else {
                            readyToDraw = true
                        }
                    }

                    is AutoSizeConstraint.Width -> {
                        if (result.didOverflowWidth) {
                            constrain()
                        } else {
                            readyToDraw = true
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun AutoSizeText(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontSize: TextUnit = TextUnit.Unspecified,
        fontStyle: FontStyle? = null,
        fontWeight: FontWeight? = null,
        fontFamily: FontFamily? = null,
        letterSpacing: TextUnit = TextUnit.Unspecified,
        textDecoration: TextDecoration? = null,
        textAlign: TextAlign? = null,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Clip,
        softWrap: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        style: TextStyle = LocalTextStyle.current,
        constraint: AutoSizeConstraint = AutoSizeConstraint.Width(),
    ) {
        AutoSizeText(
            modifier = modifier,
            text = AnnotatedString(text),
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            style = style,
            constraint = constraint
        )
    }

    @Composable
    fun Int.scaledSp(): TextUnit {
        val value: Int = this
        return with(LocalDensity.current) {
            val fontScale = this.fontScale
            val textSize = value / fontScale
            textSize.sp
        }
    }
}