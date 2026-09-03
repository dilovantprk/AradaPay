package com.ardabank.aradapay.presentation.receipt

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.components.AradaPayBrandWordmark
import com.ardabank.aradapay.presentation.components.AradaPayLogoSize
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.LightBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * AradaPay Dijital İşlem Dekontu - Apple Wallet Pass Estetiği.
 * Tırtıklı bilet kesim ayracı, SHA-256 & Merkle Tree kriptografik güvenlik rozeti ve iOS aksiyon butonları.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AradaPayReceiptModalSheet(
    receipt: AradaPayReceipt?,
    onDismiss: () -> Unit
) {
    if (receipt == null) return

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val sealCode = remember(receipt) {
        ReceiptCryptographicVerifier.generateSealCode(receipt)
    }

    // QR Code Bitmap Generation for verification
    val qrBitmap = remember(receipt.referenceNo) {
        try {
            val qrText = "https://aradapay.com/receipt/${receipt.referenceNo}?seal=$sealCode"
            val bitMatrix = QRCodeWriter().encode(qrText, BarcodeFormat.QR_CODE, 200, 200)
            val bmp = android.graphics.Bitmap.createBitmap(200, 200, android.graphics.Bitmap.Config.RGB_565)
            for (x in 0 until 200) {
                for (y in 0 until 200) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.parseColor("#0F172A") else android.graphics.Color.WHITE)
                }
            }
            bmp
        } catch (e: Exception) {
            null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LightBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. TOP BAR (BRAND & CLEAN CLOSE BUTTON)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AradaPayBrandWordmark(
                    logoSize = AradaPayLogoSize.SM,
                    textSize = 20
                )

                FilledTonalIconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDismiss()
                    },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                }
            }

            // 2. APPLE WALLET PASS CARD CONTAINER
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(0.5.dp, Color(0xFFE5E5EA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // TOP HALF: HERO AMOUNT & STATUS
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryEmeraldContainer,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "İşlem Başarılı",
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f", receipt.totalAmount)} ₺",
                            color = Color(0xFF0F172A),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryEmeraldContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${receipt.type.title} • Doğrulandı",
                                    color = PrimaryEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "${receipt.date} • ${receipt.time}",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp
                        )
                    }

                    // PERFORATED / SERRATED TICKET CUTOUT DIVIDER
                    TicketPerforatedDivider(
                        modifier = Modifier.fillMaxWidth()
                    )

                    // BOTTOM HALF: KEY-VALUE DETAILS
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReceiptDetailRow(label = "Harcama / İşlem", value = receipt.title)
                        ReceiptDetailRow(label = "İşlem Referans No", value = receipt.referenceNo, isMonospace = true)
                        ReceiptDetailRow(label = "Ödeyen / Gönderen", value = receipt.senderName)

                        receipt.senderIban?.let {
                            ReceiptDetailRow(label = "Gönderen IBAN", value = it, isMonospace = true)
                        }

                        ReceiptDetailRow(label = "Alıcı / Muhatap", value = receipt.receiverName)

                        receipt.receiverIban?.let {
                            ReceiptDetailRow(label = "Alıcı IBAN", value = it, isMonospace = true)
                        }

                        ReceiptDetailRow(label = "Kategori", value = receipt.category)
                        ReceiptDetailRow(
                            label = "İşlem Türü",
                            value = when (receipt.type) {
                                ReceiptType.FAST_TRANSFER -> "FAST Transfer"
                                ReceiptType.CROSS_SETTLEMENT -> "Otomatik Mahsuplaşma"
                                ReceiptType.GROUP_EXPENSE -> "Ortak Harcama Bölüşümü"
                                ReceiptType.PARTIAL_SETTLEMENT -> "Kısmi Fitleşme"
                            }
                        )

                        receipt.note?.let {
                            ReceiptDetailRow(label = "Açıklama", value = it)
                        }
                    }
                }
            }

            // 3. PARTICIPANTS BREAKDOWN (IF PRESENT)
            if (receipt.participants.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(0.5.dp, Color(0xFFE5E5EA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bölüşüm & Katılımcı Payları",
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        receipt.participants.forEachIndexed { index, participant ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${participant.name} (${participant.tag})",
                                    color = Color(0xFF334155),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%.2f", participant.shareAmount)} ₺",
                                        color = Color(0xFF0F172A),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (participant.isPaid) PrimaryEmeraldContainer else Color(0xFFF1F5F9)
                                    ) {
                                        Text(
                                            text = if (participant.isPaid) "Ödendi" else "Bekliyor",
                                            color = if (participant.isPaid) PrimaryEmerald else Color(0xFF64748B),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            if (index < receipt.participants.size - 1) {
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            // 4. SHA-256 / MERKLE TREE CRYPTOGRAPHIC INTEGRITY BADGE
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(0.5.dp, Color(0xFFE5E5EA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimaryEmeraldContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Dijital Güvenlik Onayı",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Doğrulanmış İşlem Kaydı",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryEmeraldContainer
                        ) {
                            Text(
                                text = "Doğrulandı",
                                color = PrimaryEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Mühür Kodu Strip with Copy
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("SealCode", sealCode))
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(context, "Güvenlik Kodu Kopyalandı", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = sealCode,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            Icon(Icons.Default.ContentCopy, contentDescription = "Kopyala", tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                        }
                    }

                    // QR Verification Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bu işlem AradaPay dağıtık kayıt defterinde dijital mühürle kaydedilmiştir.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        qrBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Doğrulama QR Kodu",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            }

            // 5. ACTION BUTTONS (Apple iOS Tactile Spring Action Bar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary Share Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .bounceClick {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            PdfReceiptGenerator.sharePdfReceipt(context, receipt)
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Paylaş",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                // Primary PDF Dekont Button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        PdfReceiptGenerator.openPdfReceipt(context, receipt)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F172A),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(52.dp)
                        .bounceClick {
                            PdfReceiptGenerator.openPdfReceipt(context, receipt)
                        },
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PDF Dekont",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Apple Wallet style perforated ticket divider with semicircular notch cutouts on edges.
 */
@Composable
private fun TicketPerforatedDivider(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(20.dp)) {
            val notchRadius = 10.dp.toPx()
            val canvasWidth = size.width
            val centerY = size.height / 2

            // Left Cutout Semicircle (matching background color)
            drawCircle(
                color = Color(0xFFF2F2F7),
                radius = notchRadius,
                center = Offset(0f, centerY)
            )

            // Right Cutout Semicircle
            drawCircle(
                color = Color(0xFFF2F2F7),
                radius = notchRadius,
                center = Offset(canvasWidth, centerY)
            )

            // Middle Dashed Perforated Line
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(notchRadius + 8.dp.toPx(), centerY),
                end = Offset(canvasWidth - notchRadius - 8.dp.toPx(), centerY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = pathEffect
            )
        }
    }
}

@Composable
private fun ReceiptDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF0F172A),
    isMonospace: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF64748B),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.SemiBold,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 13.sp
        )
    }
}
