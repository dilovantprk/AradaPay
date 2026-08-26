package com.ardabank.aradapay.presentation.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

data class QuickActionItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val iconTint: Color = Color(0xFF0F172A),
    val iconBackground: Color = Color(0xFFF2F2F7),
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextualQuickActionSheet(
    title: String,
    subtitle: String,
    avatarText: String,
    iban: String? = null,
    onDismiss: () -> Unit,
    onSettleUp: (() -> Unit)? = null,
    onViewDetail: (() -> Unit)? = null,
    onSendNudge: (() -> Unit)? = null
) {
    val context = LocalContext.current

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
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Header Info Card
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PrimaryEmeraldContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = avatarText,
                            color = PrimaryEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color(0xFF0F172A),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Action Items (Unboxed Flat Rows)
            Column(modifier = Modifier.fillMaxWidth()) {
                if (iban != null) {
                    ContextualActionRow(
                        icon = Icons.Default.ContentCopy,
                        title = "FAST IBAN Kopyala",
                        subtitle = iban,
                        iconTint = PrimaryEmerald,
                        iconBackground = PrimaryEmeraldContainer,
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("IBAN", iban))
                            Toast.makeText(context, "IBAN Kopyalandı", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }

                if (onSettleUp != null) {
                    ContextualActionRow(
                        icon = Icons.Default.Payment,
                        title = "Fitleş & Öde",
                        subtitle = "Borç kapatma ve ödeme ekranını aç",
                        iconTint = PrimaryEmerald,
                        iconBackground = PrimaryEmeraldContainer,
                        onClick = {
                            onDismiss()
                            onSettleUp()
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }

                if (onViewDetail != null) {
                    ContextualActionRow(
                        icon = Icons.Default.History,
                        title = "İşlem Detayları & Geçmiş",
                        subtitle = "Ortak harcamalar ve hesap özeti",
                        onClick = {
                            onDismiss()
                            onViewDetail()
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }

                if (onSendNudge != null) {
                    ContextualActionRow(
                        icon = Icons.Default.NotificationsActive,
                        title = "Ödeme Hatırlatması Gönder",
                        subtitle = "Kişiye nazik ödeme bildirimi ilet",
                        onClick = {
                            onDismiss()
                            onSendNudge()
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun ContextualActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = Color(0xFF0F172A),
    iconBackground: Color = Color(0xFFF1F5F9),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = iconBackground,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color(0xFF0F172A),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}
