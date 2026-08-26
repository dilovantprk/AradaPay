package com.ardabank.aradapay.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

@Composable
fun FinancialHeroAmountCard(
    title: String,
    amountText: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String = "₺",
    quickChips: (@Composable RowScope.() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF64748B),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = currencySymbol,
                color = PrimaryEmerald,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("""^\d*([.,]\d{0,2})?$"""))) {
                        onAmountChange(input.replace(',', '.'))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                cursorBrush = SolidColor(PrimaryEmerald),
                textStyle = TextStyle(
                    color = Color(0xFF0F172A),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                ),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (amountText.isEmpty()) {
                            Text(
                                text = "0,00",
                                color = Color(0xFFCBD5E1),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        if (quickChips != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = quickChips
            )
        }
    }
}

/**
 * Standard Quick Increment Chip (+50 ₺, +100 ₺, etc.)
 */
@Composable
fun QuickIncrementChip(
    amount: Int,
    currencySymbol: String = "₺",
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.bounceClick(onClick = onClick)
    ) {
        Text(
            text = "+$amount $currencySymbol",
            color = Color(0xFF475569),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/**
 * Standard Contextual Action Chip (e.g. "Tümünü Kapat", "Yarısını Öde", "Kalanın Tamamı")
 */
@Composable
fun QuickActionChip(
    text: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isPrimary) PrimaryEmeraldContainer else Color(0xFFF8FAFC),
        border = if (isPrimary) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.bounceClick(onClick = onClick)
    ) {
        Text(
            text = text,
            color = if (isPrimary) PrimaryEmerald else Color(0xFF475569),
            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}
