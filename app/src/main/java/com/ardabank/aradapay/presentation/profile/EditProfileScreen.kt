package com.ardabank.aradapay.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.LightBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

data class AvatarOption(val id: String, val emoji: String, val bgColor: Color)

@Composable
fun EditProfileScreen(
    currentName: String = "Mehmet Dilovan",
    currentIban: String = "TR64 0006 2000 0000 1122 3344 55",
    currentAvatarEmoji: String = "MD",
    onBackClick: () -> Unit = {},
    onSaveProfile: (newName: String, newIban: String, newAvatar: String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var nameInput by remember { mutableStateOf(currentName) }
    var ibanInput by remember { mutableStateOf(currentIban) }
    var selectedAvatar by remember { mutableStateOf(currentAvatarEmoji) }

    val avatarPresets = listOf(
        AvatarOption("1", "MD", Color(0xFF00875A)),
        AvatarOption("2", "AP", Color(0xFF0284C7)),
        AvatarOption("3", "AR", Color(0xFF6366F1)),
        AvatarOption("4", "TR", Color(0xFFF97316)),
        AvatarOption("5", "ME", Color(0xFF8B5CF6)),
        AvatarOption("6", "AB", Color(0xFFEF4444)),
        AvatarOption("7", "BK", Color(0xFF059669)),
        AvatarOption("8", "KP", Color(0xFFEC4899)),
        AvatarOption("9", "ST", Color(0xFF0D9488)),
        AvatarOption("10", "PL", Color(0xFF475569))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 1. TOP BAR
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
                    text = "Profili Düzenle",
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.size(40.dp))
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 2. HERO: MONOGRAM PREVIEW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (selectedAvatar.isNotEmpty()) selectedAvatar else nameInput.take(2).uppercase(),
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = if (nameInput.isNotBlank()) nameInput else "İsimsiz Kullanıcı",
                        color = Color(0xFF0F172A),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Aktif Profil Görünümü",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 3. SECTION 1: MONOGRAM AVATAR SEÇİMİ
            Text(
                text = "MONOGRAM AVATAR SEÇİMİ",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                items(avatarPresets) { option ->
                    val isSelected = selectedAvatar == option.emoji
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) option.bgColor else Color(0xFFF1F5F9),
                        border = if (isSelected) BorderStroke(2.dp, PrimaryEmerald) else null,
                        modifier = Modifier
                            .size(46.dp)
                            .bounceClick {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedAvatar = option.emoji
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = option.emoji,
                                color = if (isSelected) Color.White else Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 4. SECTION 2: KİŞİSEL VE BANKA BİLGİLERİ
            Text(
                text = "KİŞİSEL & BANKA BİLGİLERİ",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 4.1. Name Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AD SOYAD",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    BasicTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        ),
                        cursorBrush = SolidColor(PrimaryEmerald),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        decorationBox = { innerTextField ->
                            if (nameInput.isEmpty()) {
                                Text(
                                    text = "Adınızı ve soyadınızı girin",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 4.2. FAST IBAN Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FAST IBAN ADRESİ",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    BasicTextField(
                        value = ibanInput,
                        onValueChange = { ibanInput = it },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        ),
                        cursorBrush = SolidColor(PrimaryEmerald),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (ibanInput.isEmpty()) {
                                Text(
                                    text = "TR64 0006 ...",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Fixed Bottom Save Action
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            val isProfileValid = nameInput.isNotBlank()
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (isProfileValid) {
                        onSaveProfile(nameInput.trim(), ibanInput.trim(), selectedAvatar)
                        Toast.makeText(context, "Profil Güncellendi", Toast.LENGTH_SHORT).show()
                        onBackClick()
                    } else {
                        Toast.makeText(context, "Lütfen adınızı giriniz", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isProfileValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryEmerald,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE2E8F0),
                    disabledContentColor = Color(0xFF94A3B8)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .bounceClick {
                        if (isProfileValid) {
                            onSaveProfile(nameInput.trim(), ibanInput.trim(), selectedAvatar)
                            Toast.makeText(context, "Profil Güncellendi", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        }
                    },
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Değişiklikleri Kaydet",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

