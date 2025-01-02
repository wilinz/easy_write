@file:OptIn(ExperimentalMaterial3Api::class)

package com.eazywrite.app.ui.wiget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * @author wilinz
 * @date 2023/3/20 20:09
 */
@Composable
fun OutlinedFormField(
    state: FormFieldState,
    onValueChange: (String) -> String? = { it },
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    errorPaddingValues: PaddingValues = PaddingValues(vertical = 4.dp),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(8.dp),
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    Column {
        OutlinedTextField(
            value = state.text,
            onValueChange = {
                onValueChange(it)?.let {
                    state.text = it
                }
            },
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            isError = !state.validState.isValid,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors
        )
        if (!state.validState.isValid && state.validState.error.isNotEmpty()) {
            Text(
                text = state.validState.error,
                modifier = Modifier.padding(errorPaddingValues),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

}


data class ValidState(
    val isValid: Boolean,
    val error: String = ""
)

fun Boolean.toValidState(error: String = "") = ValidState(this, error)

class FormFieldState(
    text: String,
    var validator: FormFieldState.() -> ValidState = { ValidState(true) }
) {
    private var _text = mutableStateOf(text)
    var text: String
        set(value) {
            _text.value = value
            reVerify()
        }
        get() = _text.value

    var validState: ValidState by mutableStateOf(validator())

    fun reVerify() {
        validState = validator()
    }
}