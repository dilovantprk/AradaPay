package com.ardabank.aradapay.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.model.Currency
import com.ardabank.aradapay.presentation.common.M3FilterChipGroup
import com.ardabank.aradapay.presentation.common.PinPadDialog
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userCurrency: Currency = Currency.TRY,
    isPinInitiallyEnabled: Boolean = false,
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var selectedCurrency by remember { mutableStateOf(userCurrency) }
    var isPinSet by remember { mutableStateOf(isPinInitiallyEnabled) }
    var showPinDialog by remember { mutableStateOf(false) }
    var isBiometricActive by remember { mutableStateOf(true) }
    var autoHideBalances by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }
    var showResetDataDialog by remember { mutableStateOf(false) }

    fun triggerHaptic(type: HapticFeedbackType = HapticFeedbackType.TextHandleMove) {
        if (hapticFeedbackEnabled) {
            haptic.performHapticFeedback(type)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // 1. TOP BAR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = {
                        triggerHaptic()
                        onBackClick()
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color(0xFFF1F5F9)
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Ayarlar",
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.size(40.dp))
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // =========================================================================
        // SECTION 1: GÜVENLİK
        // =========================================================================
        item {
            Text(
                text = "GÜVENLİK",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 4 Haneli PIN Kilidi
        item {
            SettingsActionRow(
                icon = Icons.Default.Lock,
                title = "4 Haneli PIN Kilidi",
                subtitle = if (isPinSet) "Aktif • Açılışta ve transferlerde istenir" else "Pasif • Uygulama şifresi belirle",
                onClick = {
                    triggerHaptic()
                    showPinDialog = true
                },
                trailingContent = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPinSet) Color(0xFFFFF1F2) else PrimaryEmeraldContainer
                    ) {
                        Text(
                            text = if (isPinSet) "Kaldır" else "Ayarla",
                            color = if (isPinSet) AccentRose else PrimaryEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Biyometrik Giriş (Parmak İzi / Yüz)
        item {
            SettingsToggleRow(
                icon = Icons.Default.Fingerprint,
                title = "Biyometrik Giriş",
                subtitle = "Parmak izi veya Yüz tanıma ile giriş yap",
                checked = isBiometricActive,
                onCheckedChange = {
                    triggerHaptic()
                    isBiometricActive = it
                }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Bakiye Maskeleme
        item {
            SettingsToggleRow(
                icon = Icons.Default.VisibilityOff,
                title = "Bakiyeleri Otomatik Gizle",
                subtitle = "Uygulama açılışında tutarları •••• ₺ olarak maskele",
                checked = autoHideBalances,
                onCheckedChange = {
                    triggerHaptic()
                    autoHideBalances = it
                }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // =========================================================================
        // SECTION 2: TERCİHLER
        // =========================================================================
        item {
            Text(
                text = "TERCİHLER",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Para Birimi Seçimi
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Varsayılan Para Birimi",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                M3FilterChipGroup(
                    items = listOf(Currency.TRY to "₺ TRY", Currency.USD to "$ USD", Currency.EUR to "€ EUR"),
                    selectedItem = selectedCurrency,
                    onItemSelected = {
                        triggerHaptic()
                        selectedCurrency = it
                        Toast.makeText(context, "Para birimi ${it.name} olarak ayarlandı", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Anlık Bildirimler
        item {
            SettingsToggleRow(
                icon = Icons.Default.Notifications,
                title = "Anlık Bildirimler",
                subtitle = "Harcama ekleme, borç ve fitleşme bildirimleri",
                checked = notificationsEnabled,
                onCheckedChange = {
                    triggerHaptic()
                    notificationsEnabled = it
                }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Dokunsal Geri Bildirim (Titreşim)
        item {
            SettingsToggleRow(
                icon = Icons.Default.Vibration,
                title = "Dokunsal Geri Bildirim",
                subtitle = "İşlem ve buton dokunuşlarında hafif titreşim",
                checked = hapticFeedbackEnabled,
                onCheckedChange = {
                    hapticFeedbackEnabled = it
                    if (it) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // =========================================================================
        // SECTION 3: HESAP & VERİ
        // =========================================================================
        item {
            Text(
                text = "HESAP & VERİ",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Profil Bilgilerini Düzenle
        item {
            SettingsActionRow(
                icon = Icons.Default.Edit,
                title = "Profil Bilgilerini Düzenle",
                subtitle = "İsim, FAST IBAN ve kullanıcı etiketi",
                onClick = {
                    triggerHaptic()
                    onEditProfileClick()
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(13.dp)
                    )
                }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Verileri Sıfırla
        item {
            SettingsActionRow(
                icon = Icons.Default.Delete,
                title = "Tüm Verileri Sıfırla",
                subtitle = "Yerel harcama geçmişini ve kayıtları temizle",
                onClick = {
                    triggerHaptic(HapticFeedbackType.LongPress)
                    showResetDataDialog = true
                },
                titleColor = AccentRose,
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = AccentRose,
                        modifier = Modifier.size(13.dp)
                    )
                }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Minimal Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AradaPay",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "v1.0.0 • Güvenli Finansal Bölüşüm",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }

    // PIN Pad Modal Dialog
    if (showPinDialog) {
        PinPadDialog(
            onDismiss = { showPinDialog = false },
            onPinEntered = { _ ->
                showPinDialog = false
                isPinSet = !isPinSet
                val msg = if (isPinSet) "4 haneli PIN kilidi başarıyla aktifleştirildi" else "PIN kilidi kaldırıldı"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            },
            onBiometricClick = {
                showPinDialog = false
                isBiometricActive = true
                Toast.makeText(context, "Biyometrik doğrulama aktif", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Reset Data Confirmation Dialog
    if (showResetDataDialog) {
        AlertDialog(
            onDismissRequest = { showResetDataDialog = false },
            title = {
                Text("Verileri Sıfırla", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Text(
                    "Tüm yerel harcama geçmişiniz, grup kayıtlarınız ve bakiye verileriniz sıfırlanacaktır. Bu işlem geri alınamaz.",
                    color = Color(0xFF475569),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDataDialog = false
                        onDeleteAccount()
                        Toast.makeText(context, "Tüm veriler sıfırlandı", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Sıfırla", color = AccentRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDataDialog = false }) {
                    Text("Vazgeç", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: Color = Color(0xFF0F172A),
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, color = titleColor, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = Color(0xFF64748B), fontSize = 12.sp)
            }
        }

        trailingContent()
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = Color(0xFF64748B), fontSize = 12.sp)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryEmerald,
                checkedBorderColor = PrimaryEmerald,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1),
                uncheckedBorderColor = Color(0xFF94A3B8)
            )
        )
    }
}

