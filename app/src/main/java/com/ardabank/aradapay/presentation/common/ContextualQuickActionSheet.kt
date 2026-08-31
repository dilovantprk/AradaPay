package com.ardabank.aradapay.presentation.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.theme.AccentRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextualQuickActionSheet(
    title: String,
    subtitle: String? = null,
    avatarText: String? = null,
    iban: String? = null,
    isPinned: Boolean = false,
    isMuted: Boolean = false,
    deleteText: String = "Sil",
    onDismiss: () -> Unit,
    onPinToggle: (() -> Unit)? = null,
    onAddExpense: (() -> Unit)? = null,
    onSettleUp: (() -> Unit)? = null,
    onViewDetail: (() -> Unit)? = null,
    onSendNudge: (() -> Unit)? = null,
    onMuteToggle: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var pinned by remember { mutableStateOf(isPinned) }
    var muted by remember { mutableStateOf(isMuted) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Başlık: Kişi Adı (Referans görseldeki gibi temiz ve net)
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 1. Sabitle
            ContextualMenuRow(
                text = if (pinned) "Sabitlemeyi Kaldır" else "Sabitle",
                onClick = {
                    pinned = !pinned
                    onPinToggle?.invoke()
                    Toast.makeText(context, if (pinned) "$title başa sabitlendi" else "$title sabitlemesi kaldırıldı", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 0.8.dp)

            // 2. Harcama Ekle
            if (onAddExpense != null) {
                ContextualMenuRow(
                    text = "Harcama Ekle",
                    onClick = {
                        onDismiss()
                        onAddExpense()
                    }
                )
                HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 0.8.dp)
            }

            // 3. Fitleş & Öde
            if (onSettleUp != null) {
                ContextualMenuRow(
                    text = "Fitleş & Öde",
                    onClick = {
                        onDismiss()
                        onSettleUp()
                    }
                )
                HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 0.8.dp)
            }

            // 4. FAST IBAN Kopyala
            if (!iban.isNullOrBlank()) {
                ContextualMenuRow(
                    text = "FAST IBAN Kopyala",
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("IBAN", iban))
                        Toast.makeText(context, "FAST IBAN kopyalandı", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )
                HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 0.8.dp)
            }

            // 5. Ödeme Hatırlatması Gönder
            if (onSendNudge != null) {
                ContextualMenuRow(
                    text = "Ödeme Hatırlatması Gönder",
                    onClick = {
                        onDismiss()
                        onSendNudge()
                    }
                )
                HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 0.8.dp)
            }

            // 6. Mesajları / Bildirimleri Sessize Al
            ContextualMenuRow(
                text = if (muted) "Bildirimlerin Sesini Aç" else "Bildirimleri Sessize Al",
                onClick = {
                    muted = !muted
                    onMuteToggle?.invoke()
                    Toast.makeText(context, if (muted) "$title sessize alındı" else "$title bildirimleri açıldı", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 0.8.dp)

            // 7. Sil / Ayrıl (Kırmızı Vurgulu)
            if (onDelete != null) {
                ContextualMenuRow(
                    text = deleteText,
                    textColor = AccentRose,
                    onClick = {
                        onDismiss()
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
private fun ContextualMenuRow(
    text: String,
    textColor: Color = Color(0xFF0F172A),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

