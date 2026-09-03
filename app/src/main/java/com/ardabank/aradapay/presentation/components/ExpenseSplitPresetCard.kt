package com.ardabank.aradapay.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald

enum class QuickSplitPreset {
    YOU_PAID_SPLIT_EQUALLY,
    YOU_ARE_OWED_FULL,
    FRIEND_PAID_SPLIT_EQUALLY,
    FRIEND_IS_OWED_FULL
}

/**
 * Splitwise'ın ünlü 4'lü harcama bölüşüm önayarı ve ikili avatar göstergesi ("How was this expense split?").
 * Tekli ve çoklu kişi seçimlerinde kişi başı payları ve alacak/borçları dinamik olarak hesaplar.
 */
@Composable
fun ExpenseSplitPresetSelector(
    friendNames: List<String>,
    totalAmount: Double,
    selectedPreset: QuickSplitPreset,
    onPresetSelected: (QuickSplitPreset) -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val count = friendNames.size.coerceAtLeast(1)
    val isMulti = count > 1
    val totalPeople = count + 1 // + sen

    val firstFriend = friendNames.firstOrNull() ?: "Arkadaş"
    val friendsSummary = if (count == 1) {
        firstFriend
    } else if (count == 2) {
        "${friendNames[0]} ve ${friendNames[1]}"
    } else {
        "${friendNames[0]}, ${friendNames[1]} ve ${count - 2} kişi daha"
    }

    val equalShareWithMe = if (totalPeople > 0) totalAmount / totalPeople else 0.0
    val totalReceivable = equalShareWithMe * count
    val equalShareWithoutMe = if (count > 0) totalAmount / count else 0.0
    val hasAmount = totalAmount > 0.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isMulti) "Bu harcama nasıl bölüşüldü? (Toplam $totalPeople kişi)" else "Bu harcama nasıl bölüşüldü?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // 1. Masayı sen üstlendin, eşit bölüşüldü
        PresetRowItem(
            preset = QuickSplitPreset.YOU_PAID_SPLIT_EQUALLY,
            title = if (isMulti) "Masayı sen üstlendin, eşit bölüşüldü ($totalPeople kişi)." else "Masayı sen üstlendin, eşit bölüşüldü.",
            subtitle = if (isMulti) {
                if (hasAmount) {
                    "Kişi başı ${String.format(java.util.Locale.US, "%.2f", equalShareWithMe)} ₺ • $friendsSummary masadan payını kapatacak (Toplam ${String.format(java.util.Locale.US, "%.2f", totalReceivable)} ₺ pay)"
                } else {
                    "Kişi başı eşit bölüşülür • $friendsSummary payını masaya bırakır"
                }
            } else {
                if (hasAmount) {
                    "$firstFriend masadan payına düşeni (${String.format(java.util.Locale.US, "%.2f", totalAmount / 2.0)} ₺) verecek"
                } else {
                    "$firstFriend ile yarı yarıya eşit bölüşülür"
                }
            },
            isReceivable = true,
            isSelected = selectedPreset == QuickSplitPreset.YOU_PAID_SPLIT_EQUALLY,
            onClick = { onPresetSelected(QuickSplitPreset.YOU_PAID_SPLIT_EQUALLY) }
        )

        // 2. Tüm masayı sen üstlendin
        PresetRowItem(
            preset = QuickSplitPreset.YOU_ARE_OWED_FULL,
            title = if (isMulti) "Masayı sen üstlendin, tüm tutar onların payı ($count kişi)." else "Tüm masayı sen üstlendin.",
            subtitle = if (isMulti) {
                if (hasAmount) {
                    "Kişi başı ${String.format(java.util.Locale.US, "%.2f", equalShareWithoutMe)} ₺ • Tüm masa (${String.format(java.util.Locale.US, "%.2f", totalAmount)} ₺) onların payı"
                } else {
                    "Sen hariç $count kişi arasında bölüşülür • Masayı sen üstlendin"
                }
            } else {
                if (hasAmount) {
                    "$firstFriend masadan payına düşen: ${String.format(java.util.Locale.US, "%.2f", totalAmount)} ₺"
                } else {
                    "Tüm masayı sen üstlendin, pay arkadaşa ait"
                }
            },
            isReceivable = true,
            isSelected = selectedPreset == QuickSplitPreset.YOU_ARE_OWED_FULL,
            onClick = { onPresetSelected(QuickSplitPreset.YOU_ARE_OWED_FULL) }
        )

        // 3. Masayı arkadaş üstlendi, eşit bölüşüldü
        PresetRowItem(
            preset = QuickSplitPreset.FRIEND_PAID_SPLIT_EQUALLY,
            title = if (isMulti) "$firstFriend masayı üstlendi, eşit bölüşüldü ($totalPeople kişi)." else "$firstFriend masayı üstlendi, eşit bölüşüldü.",
            subtitle = if (isMulti) {
                if (hasAmount) {
                    "Kişi başı ${String.format(java.util.Locale.US, "%.2f", equalShareWithMe)} ₺ • $firstFriend için senin payına düşen: ${String.format(java.util.Locale.US, "%.2f", equalShareWithMe)} ₺"
                } else {
                    "Kişi başı eşit bölüşülür • Masayı $firstFriend üstlendi"
                }
            } else {
                if (hasAmount) {
                    "$firstFriend masayı üstlendi, senin payın: ${String.format(java.util.Locale.US, "%.2f", totalAmount / 2.0)} ₺"
                } else {
                    "$firstFriend masayı üstlendi, yarı payın ayrıldı"
                }
            },
            isReceivable = false,
            isSelected = selectedPreset == QuickSplitPreset.FRIEND_PAID_SPLIT_EQUALLY,
            onClick = { onPresetSelected(QuickSplitPreset.FRIEND_PAID_SPLIT_EQUALLY) }
        )

        // 4. Tüm masayı arkadaş üstlendi
        PresetRowItem(
            preset = QuickSplitPreset.FRIEND_IS_OWED_FULL,
            title = if (isMulti) "Tüm masayı $firstFriend üstlendi." else "Tüm masayı $firstFriend üstlendi.",
            subtitle = if (isMulti) {
                if (hasAmount) {
                    "$firstFriend masayı üstlendi, senin payın: ${String.format(java.util.Locale.US, "%.2f", equalShareWithMe)} ₺"
                } else {
                    "$firstFriend masayı üstlendi, kendi payın ayrıldı"
                }
            } else {
                if (hasAmount) {
                    "$firstFriend masayı üstlendi, senin payın: ${String.format(java.util.Locale.US, "%.2f", totalAmount)} ₺"
                } else {
                    "Tüm masayı $firstFriend üstlendi, payın ayrıldı"
                }
            },
            isReceivable = false,
            isSelected = selectedPreset == QuickSplitPreset.FRIEND_IS_OWED_FULL,
            onClick = { onPresetSelected(QuickSplitPreset.FRIEND_IS_OWED_FULL) }
        )

        Spacer(modifier = Modifier.height(6.dp))

        // "More options" / "Gelişmiş Seçenekler" butonu
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            OutlinedButton(
                onClick = onMoreOptionsClick,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF334155)
                ),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .height(42.dp)
            ) {
                Text(
                    text = "Gelişmiş Seçenekler",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun ExpenseSplitPresetSelector(
    friendName: String,
    totalAmount: Double,
    selectedPreset: QuickSplitPreset,
    onPresetSelected: (QuickSplitPreset) -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExpenseSplitPresetSelector(
        friendNames = listOf(friendName),
        totalAmount = totalAmount,
        selectedPreset = selectedPreset,
        onPresetSelected = onPresetSelected,
        onMoreOptionsClick = onMoreOptionsClick,
        modifier = modifier
    )
}

@Composable
private fun PresetRowItem(
    preset: QuickSplitPreset,
    title: String,
    subtitle: String,
    isReceivable: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // İkili Avatar İkonu
        DualAvatarIndicator(preset = preset)

        Spacer(modifier = Modifier.width(16.dp))

        // Metin Bilgisi
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isReceivable) PrimaryEmerald else Color(0xFFEA580C)
            )
        }

        // Seçim İkonu (Tiki)
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Seçili",
                tint = Color(0xFF334155),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DualAvatarIndicator(
    preset: QuickSplitPreset,
    modifier: Modifier = Modifier
) {
    val ringColor = when (preset) {
        QuickSplitPreset.YOU_PAID_SPLIT_EQUALLY, QuickSplitPreset.YOU_ARE_OWED_FULL -> PrimaryEmerald
        QuickSplitPreset.FRIEND_PAID_SPLIT_EQUALLY, QuickSplitPreset.FRIEND_IS_OWED_FULL -> Color(0xFFEA580C)
    }

    Box(
        modifier = modifier
            .width(52.dp)
            .height(38.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Sol Avatar (Sen / Kullanıcı)
        Box(
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.CenterStart)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF0F172A),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Halka göstergesi
            if (preset == QuickSplitPreset.YOU_PAID_SPLIT_EQUALLY || preset == QuickSplitPreset.YOU_ARE_OWED_FULL) {
                Canvas(modifier = Modifier.size(34.dp)) {
                    val stroke = 3.dp.toPx()
                    drawArc(
                        color = ringColor,
                        startAngle = if (preset == QuickSplitPreset.YOU_PAID_SPLIT_EQUALLY) 90f else 0f,
                        sweepAngle = if (preset == QuickSplitPreset.YOU_PAID_SPLIT_EQUALLY) 180f else 360f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Sağ Avatar (Arkadaş)
        Box(
            modifier = Modifier
                .size(26.dp)
                .align(Alignment.CenterEnd)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.5.dp, Color.White),
                modifier = Modifier.size(26.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Mail,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Halka göstergesi (Arkadaş ödediyse)
            if (preset == QuickSplitPreset.FRIEND_PAID_SPLIT_EQUALLY || preset == QuickSplitPreset.FRIEND_IS_OWED_FULL) {
                Canvas(modifier = Modifier.size(26.dp)) {
                    val stroke = 2.5.dp.toPx()
                    drawArc(
                        color = ringColor,
                        startAngle = if (preset == QuickSplitPreset.FRIEND_PAID_SPLIT_EQUALLY) 90f else 0f,
                        sweepAngle = if (preset == QuickSplitPreset.FRIEND_PAID_SPLIT_EQUALLY) 180f else 360f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
