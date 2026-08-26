package com.ardabank.aradapay.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.SurfaceBorder
import com.ardabank.aradapay.presentation.theme.SurfaceDark
import com.ardabank.aradapay.presentation.theme.SurfaceElevated
import com.ardabank.aradapay.presentation.theme.TextPrimary
import com.ardabank.aradapay.presentation.theme.TextSecondary

@Composable
fun <T> IOSSegmentedControl(
    items: List<Pair<T, String>>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFF1F5F9),
    selectedBackgroundColor: Color = Color.White,
    selectedTextColor: Color = Color(0xFF0F172A),
    unselectedTextColor: Color = Color(0xFF64748B),
    borderColor: Color = Color.Transparent
) {
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (item, label) ->
            val isSelected = item == selectedItem
            val targetBgColor = if (isSelected) selectedBackgroundColor else Color.Transparent
            val bgColor by animateColorAsState(targetValue = targetBgColor, label = "segBg")
            val targetTextColor = if (isSelected) selectedTextColor else unselectedTextColor
            val textColor by animateColorAsState(targetValue = targetTextColor, label = "segText")

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(bgColor)
                    .clickable { onItemSelected(item) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}
