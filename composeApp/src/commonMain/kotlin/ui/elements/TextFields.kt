package ui.elements

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.Colors

object TextFields {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun OutlinedTextFieldValidation(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier.fillMaxWidth(0.8f),
        enabled: Boolean = true,
        readOnly: Boolean = false,
        textStyle: TextStyle = LocalTextStyle.current,
        label: @Composable (() -> Unit)? = null,
        placeholder: @Composable (() -> Unit)? = null,
        leadingIcon: @Composable (() -> Unit)? = null,
        error: String = "",
        isError: Boolean = error.isNotEmpty(),
        trueTrailingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = {
            if (error.isNotEmpty())
                Icon(painterResource("icons/icon_error.xml"), "error", tint = MaterialTheme.colors.error)
            else
                if (trueTrailingIcon != null) {
                    trueTrailingIcon()
                }
        },
        visualTransformation: VisualTransformation = VisualTransformation.None,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        keyboardActions: KeyboardActions = KeyboardActions.Default,
        singleLine: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        shape: Shape = MaterialTheme.shapes.small,
        colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = Color.Black
        )
    ) {
        Column(modifier = modifier) {
            OutlinedTextField(
                enabled = enabled,
                readOnly = readOnly,
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = singleLine,
                textStyle = textStyle,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                isError = isError,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                maxLines = maxLines,
                interactionSource = interactionSource,
                shape = shape,
                colors = colors
            )
            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 16.dp, top = 0.dp)
                )
            }
        }
    }

    @Composable
    fun outlinedTextField(
        label: String,
        valueState: State<String>,
        actionValueChange: (String) -> Unit,
        valueErrorState: State<String?>,
        modifier: Modifier = Modifier.fillMaxWidth().padding(16.dp, 24.dp, 16.dp),
        icon: @Composable (() -> Unit)? = null,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Colors.textViewBorder,
            focusedBorderColor = Colors.textViewBorder,
            unfocusedLabelColor = Colors.textViewBorder,
            focusedLabelColor = Colors.textViewBorder
        )
    ) {
        OutlinedTextFieldValidation(
            value = valueState.value,
            onValueChange = actionValueChange,
            label = { Text(text = label) },
            modifier = modifier,
            shape = RoundedCornerShape(30.dp),
            isError = valueErrorState.value != null,
            error = valueErrorState.value.orEmpty(),
            visualTransformation = visualTransformation,
            singleLine = true,
            trueTrailingIcon = icon,
            colors = colors
        )
    }
}