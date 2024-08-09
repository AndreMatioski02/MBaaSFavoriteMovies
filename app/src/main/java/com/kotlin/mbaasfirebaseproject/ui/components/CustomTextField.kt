package com.kotlin.mbaasfirebaseproject.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, style = TextStyle(fontSize = 24.sp)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        textStyle = TextStyle(fontSize = 24.sp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0XFFf0f0f0),
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        visualTransformation = if(isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}