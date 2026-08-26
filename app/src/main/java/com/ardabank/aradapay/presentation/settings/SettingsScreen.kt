package com.ardabank.aradapay.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.model.Currency
import com.ardabank.aradapay.presentation.common.M3FilterChipGroup
import com.ardabank.aradapay.presentation.components.SlideToConfirmButton
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.AccentRoseContainer
import com.ardabank.aradapay.presentation.theme.LightBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userCurrency: Currency = Currency.TRY,
    isPinEnabled: Boolean = false,
    onBackClick: () -> Unit = {},
    onSetPinClick: () -> Unit = {},
    onRemovePinClick: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    var selectedCurrency by remember { mutableStateOf(userCurrency) }
    var hidePhone by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var isBiometricActive by remember { mutableStateOf(true) }
    var showKvkkModal by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

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
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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

        // SECTION 1: GÜVENLİK & GİZLİLİK
        item {
            Text(
                text = "GÜVENLİK & GİZLİLİK",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // PIN Row
        item {
            SettingsRowItem(
                icon = Icons.Default.Security,
                iconBgColor = if (isPinEnabled) Color(0xFF00875A) else Color(0xFF64748B),
                title = "2FA Finansal PIN Kilidi",
                subtitle = if (isPinEnabled) "Aktif • Bakiyeleri koruyor" else "Pasif • Henüz belirlenmedi",
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (isPinEnabled) onRemovePinClick() else onSetPinClick()
                },
                trailingContent = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPinEnabled) Color(0xFFFFF1F2) else Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = if (isPinEnabled) "Kaldır" else "Ayarla",
                            color = if (isPinEnabled) AccentRose else PrimaryEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Biometric Switch Row
        item {
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
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Biyometrik Giriş", color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Parmak izi veya Yüz ile hızlı doğrulama", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                }

                Switch(
                    checked = isBiometricActive,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        isBiometricActive = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryEmerald
                    )
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Phone Privacy Switch
        item {
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
                            Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Rehberde Numarayı Gizle", color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Aramalarda sadece @tag kodun görünür", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                }

                Switch(
                    checked = hidePhone,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        hidePhone = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryEmerald
                    )
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Notifications Switch
        item {
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
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Anlık Bildirimler", color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Borç, harcama ve fitleşme duyuruları", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                }

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        notificationsEnabled = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryEmerald
                    )
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // SECTION 2: TERCİHLER & YASAL
        item {
            Text(
                text = "TERCİHLER & YASAL BİLGİLER",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // Currency Selection Row
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
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedCurrency = it
                    }
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // KVKK Row
        item {
            SettingsRowItem(
                icon = Icons.Default.PrivacyTip,
                iconBgColor = Color(0xFF3B82F6),
                title = "KVKK Aydınlatma Metni",
                subtitle = "6698 & 5070 Sayılı Kanun Kapsamında Haklarınız",
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    showKvkkModal = true
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

        // SECTION 3: HESAP YÖNETİMİ
        item {
            Text(
                text = "HESAP YÖNETİMİ",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        item {
            SettingsRowItem(
                icon = Icons.Default.Delete,
                iconBgColor = Color(0xFFEF4444),
                title = "Hesabımı ve Verilerimi Sil",
                subtitle = "KVKK m.11 unutulma hakkı kapsamında tüm veriler silinir",
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showDeleteAccountDialog = true
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

        // Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                com.ardabank.aradapay.presentation.components.AradaPayLogo(
                    logoSize = com.ardabank.aradapay.presentation.components.AradaPayLogoSize.SM
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "AradaPay",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Sürüm 2.6.0 • Apple iOS HIG Standardı",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }

    // KVKK Modal Dialog
    if (showKvkkModal) {
        AlertDialog(
            onDismissRequest = { showKvkkModal = false },
            confirmButton = {
                TextButton(onClick = { showKvkkModal = false }) { Text("Kapat", color = PrimaryEmerald, fontWeight = FontWeight.Bold) }
            },
            title = { Text("KVKK Aydınlatma Metni", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "6698 Sayılı KVKK Uyarınca: AradaPay (ArdaBank), finansal bölüşüm verilerinizi 256-bit şifreleme ile işler. Silme hakkınızı dilediğiniz an kullanabilirsiniz.",
                    color = Color(0xFF475569),
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // DELETE ACCOUNT FULL-PAGE INTRINSIC SCREEN
    if (showDeleteAccountDialog) {
        BackHandler { showDeleteAccountDialog = false }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = LightBackground
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { showDeleteAccountDialog = false },
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Hesabı ve Verileri Sil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = CircleShape,
                    color = AccentRoseContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = AccentRose,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    text = "Hesabınızı Kalıcı Olarak Silmek Üzeresiniz",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(0.5.dp, Color(0xFFE5E5EA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "⚠️ Bu işlem geri alınamaz!",
                            fontWeight = FontWeight.Bold,
                            color = AccentRose,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "KVKK m.11 unutulma hakkı kapsamında tüm harcama kayıtlarınız, grup üyelikleriniz, borç-alacak bakiyeleriniz ve profiliniz kalıcı olarak silinecektir.",
                            color = Color(0xFF475569),
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                SlideToConfirmButton(
                    text = "Hesabı Kalıcı Olarak Sil",
                    completedText = "Hesap Silindi",
                    sliderColor = AccentRose,
                    thumbColor = AccentRose,
                    backgroundColor = Color(0xFFFFF1F2),
                    onConfirm = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    }
                )

                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Vazgeç ve İptal Et",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .bounceClick { showDeleteAccountDialog = false }
                            .padding(8.dp)
                    )
                }
            }
        }
        return
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    iconBgColor: Color,
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
                color = iconBgColor,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
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

