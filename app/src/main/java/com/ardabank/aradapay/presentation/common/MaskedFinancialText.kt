package com.ardabank.aradapay.presentation.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.util.SecurityUtils
import com.ardabank.aradapay.presentation.theme.TextPrimary

@Composable
fun MaskedFinancialText(
    amount: Double,
    currencySymbol: String = "₺",
    isLocked: Boolean,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    ),
    color: Color = TextPrimary
) {
    val textToShow = SecurityUtils.maskAmount(amount, currencySymbol, isLocked)
    Text(
        text = textToShow,
        modifier = modifier,
        style = style,
        color = color
    )
}
