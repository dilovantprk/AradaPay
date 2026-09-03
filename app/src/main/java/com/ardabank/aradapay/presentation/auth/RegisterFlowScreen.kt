package com.ardabank.aradapay.presentation.auth

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.model.Currency
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.presentation.common.UserAvatar
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.util.ImageStorageHelper

@Composable
fun RegisterFlowScreen(
    initialFullName: String = "",
    initialEmail: String = "",
    isGoogleVerified: Boolean = false,
    viewModel: AuthViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onRegisterSuccess: (User) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentStep by remember { mutableIntStateOf(1) }

    // Step 1: Kişisel Bilgiler & Hesap Şifresi
    var fullName by remember { mutableStateOf(initialFullName) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var accountPassword by remember { mutableStateOf("") }
    var confirmAccountPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Step 2: 4 Haneli Hızlı Giriş PIN Kodu
    var pinCode by remember { mutableStateOf("") }
    var confirmPinCode by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var isConfirmPinVisible by remember { mutableStateOf(false) }

    // Handle Success & Error States
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                val user = (uiState as AuthUiState.Success).user
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                Toast.makeText(context, "Hesabınız başarıyla oluşturuldu! Hoş geldiniz, ${user.fullName}.", Toast.LENGTH_LONG).show()
                onRegisterSuccess(user)
            }
            is AuthUiState.Error -> {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                Toast.makeText(context, (uiState as AuthUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.clearErrors()
            }
            else -> {}
        }
    }

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = ImageStorageHelper.saveProfileAvatar(context, uri)
            if (savedPath != null) {
                avatarUrl = savedPath
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                Toast.makeText(context, "Profil fotoğrafı eklendi", Toast.LENGTH_SHORT).show()
            } else {
                avatarUrl = uri.toString()
            }
        }
    }

    // Step-by-step BackHandler
    BackHandler(enabled = uiState !is AuthUiState.Loading) {
        if (currentStep > 1) {
            currentStep--
        } else {
            onBackClick()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // =========================================================================
            // TOP APP BAR & 2-STEP PROGRESS BAR
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            onBackClick()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
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
                    text = if (currentStep == 1) "Adım 1 / 2 • Hesap & Şifre" else "Adım 2 / 2 • 4 Haneli PIN",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            // 2-Segment Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..2).forEach { step ->
                    val isCompletedOrActive = currentStep >= step
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp),
                        shape = RoundedCornerShape(2.dp),
                        color = if (isCompletedOrActive) PrimaryEmerald else Color(0xFFE2E8F0)
                    ) {}
                }
            }

            HorizontalDivider(
                color = Color(0xFFF1F5F9),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 10.dp)
            )

            // =========================================================================
            // STEP CONTENT
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally(animationSpec = tween(280)) { it } + fadeIn(animationSpec = tween(280)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(280)) { -it } + fadeOut(animationSpec = tween(200)))
                        } else {
                            (slideInHorizontally(animationSpec = tween(280)) { -it } + fadeIn(animationSpec = tween(280)))
                                .togetherWith(slideOutHorizontally(animationSpec = tween(280)) { it } + fadeOut(animationSpec = tween(200)))
                        }
                    },
                    label = "RegisterStepAnimation"
                ) { step ->
                    when (step) {
                        1 -> {
                            // =========================================================================
                            // ADIM 1: HESAP BİLGİLERİ & HESAP ŞİFRESİ
                            // =========================================================================
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // Header
                                    Column {
                                        Text(
                                            text = "Hesabını Oluştur 👋",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Temel bilgilerinizi ve hesabınıza giriş için ana şifrenizi belirleyin.",
                                            color = Color(0xFF64748B),
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }

                                    // Avatar Picker (Centered)
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF8FAFC),
                                            border = BorderStroke(2.dp, if (avatarUrl.isNotBlank()) PrimaryEmerald else Color(0xFFCBD5E1)),
                                            modifier = Modifier
                                                .size(76.dp)
                                                .bounceClick {
                                                    photoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                if (avatarUrl.isNotBlank()) {
                                                    UserAvatar(
                                                        userName = fullName.ifBlank { "User" },
                                                        avatarUrl = avatarUrl,
                                                        size = 76.dp
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.AddAPhoto,
                                                        contentDescription = "Fotoğraf Ekle",
                                                        tint = PrimaryEmerald,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (avatarUrl.isNotBlank()) "Fotoğrafı Değiştir" else "Fotoğraf Ekle (Opsiyonel)",
                                            color = PrimaryEmerald,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.clickable {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            }
                                        )
                                    }

                                    // 1. Ad Soyad
                                    OutlinedTextField(
                                        value = fullName,
                                        onValueChange = { fullName = it },
                                        label = { Text("Ad Soyad", maxLines = 1) },
                                        placeholder = { Text("Örn: Mehmet Dilovan", maxLines = 1) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryEmerald)
                                        },
                                        singleLine = true,
                                        maxLines = 1,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // 2. E-posta
                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        label = { Text("E-posta Adresi", maxLines = 1) },
                                        placeholder = { Text("ornek@mail.com", maxLines = 1) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryEmerald)
                                        },
                                        singleLine = true,
                                        maxLines = 1,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Email,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // 3. Telefon
                                    OutlinedTextField(
                                        value = phone,
                                        onValueChange = { if (it.length <= 11) phone = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Telefon Numarası", maxLines = 1) },
                                        placeholder = { Text("05XX XXX XX XX", maxLines = 1) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryEmerald)
                                        },
                                        singleLine = true,
                                        maxLines = 1,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // 4. Hesap Şifresi
                                    OutlinedTextField(
                                        value = accountPassword,
                                        onValueChange = { accountPassword = it },
                                        label = { Text("Hesap Şifresi (Giriş için)", maxLines = 1) },
                                        placeholder = { Text("En az 6 karakter", maxLines = 1) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryEmerald)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = if (isPasswordVisible) "Gizle" else "Göster",
                                                    tint = Color(0xFF94A3B8)
                                                )
                                            }
                                        },
                                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        maxLines = 1,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // 5. Hesap Şifresi Tekrarı
                                    OutlinedTextField(
                                        value = confirmAccountPassword,
                                        onValueChange = { confirmAccountPassword = it },
                                        label = { Text("Hesap Şifresi Tekrar", maxLines = 1) },
                                        placeholder = { Text("Şifrenizi tekrar girin", maxLines = 1) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryEmerald)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = if (isConfirmPasswordVisible) "Gizle" else "Göster",
                                                    tint = Color(0xFF94A3B8)
                                                )
                                            }
                                        },
                                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        maxLines = 1,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = { focusManager.clearFocus() }
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Devam Et Button
                                Button(
                                    onClick = {
                                        if (fullName.isBlank()) {
                                            Toast.makeText(context, "Lütfen adınızı ve soyadınızı girin.", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (!email.contains("@") || !email.contains(".")) {
                                            Toast.makeText(context, "Lütfen geçerli bir e-posta adresi girin.", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (accountPassword.length < 6) {
                                            Toast.makeText(context, "Hesap şifreniz en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (accountPassword != confirmAccountPassword) {
                                            Toast.makeText(context, "Hesap şifreleri eşleşmiyor.", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }

                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        currentStep = 2
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryEmerald,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .bounceClick { },
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text("Devam Et (PIN Belirle)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        2 -> {
                            // =========================================================================
                            // ADIM 2: 4 HANELİ HIZLI GİRİŞ PIN KODU
                            // =========================================================================
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                    // Header
                                    Column {
                                        Text(
                                            text = "4 Haneli Hızlı Giriş PIN'i 🔢",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Uygulamayı her açtığınızda şifre girmek yerine bu 4 haneli PIN kodunuz veya parmak izinizle hızlıca giriş yapacaksınız.",
                                            color = Color(0xFF64748B),
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp
                                        )
                                    }

                                    // Info Card
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = PrimaryEmeraldContainer.copy(alpha = 0.3f),
                                        border = BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = PrimaryEmerald,
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Pin,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "Bu PIN yalnızca uygulamaya hızlı giriş ve para transferi onaylarında kullanılır.",
                                                fontSize = 13.sp,
                                                color = Color(0xFF0F172A),
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }

                                    // 1. 4 Haneli PIN Kodu
                                    OutlinedTextField(
                                        value = pinCode,
                                        onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) pinCode = it },
                                        label = { Text("4 Haneli PIN Kodu", maxLines = 1) },
                                        placeholder = { Text("••••", maxLines = 1) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryEmerald)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                                Icon(
                                                    imageVector = if (isPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = if (isPinVisible) "Gizle" else "Göster",
                                                    tint = Color(0xFF94A3B8)
                                                )
                                            }
                                        },
                                        visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        maxLines = 1,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.NumberPassword,
                                            imeAction = ImeAction.Next
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // 2. 4 Haneli PIN Kodu Tekrarı
                                    OutlinedTextField(
                                        value = confirmPinCode,
                                        onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) confirmPinCode = it },
                                        label = { Text("PIN Kodunu Tekrar Girin", maxLines = 1) },
                                        placeholder = { Text("••••", maxLines = 1) },
                                        leadingIcon = {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryEmerald)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { isConfirmPinVisible = !isConfirmPinVisible }) {
                                                Icon(
                                                    imageVector = if (isConfirmPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = if (isConfirmPinVisible) "Gizle" else "Göster",
                                                    tint = Color(0xFF94A3B8)
                                                )
                                            }
                                        },
                                        visualTransformation = if (isConfirmPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        maxLines = 1,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.NumberPassword,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = { focusManager.clearFocus() }
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Text(
                                        text = "Kaydolarak AradaPay Kullanım Koşulları ve Gizlilik Politikası'nı kabul etmiş olursunuz.",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Kaydı Tamamla Button
                                Button(
                                    onClick = {
                                        if (pinCode.length != 4) {
                                            Toast.makeText(context, "Lütfen tam 4 haneli bir PIN kodu belirleyin.", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (pinCode != confirmPinCode) {
                                            Toast.makeText(context, "PIN kodları birbiriyle eşleşmiyor.", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }

                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.register(
                                            fullName = fullName,
                                            email = email,
                                            pass = accountPassword,
                                            confirmPass = confirmAccountPassword,
                                            phone = phone.ifBlank { null },
                                            avatarUrl = avatarUrl,
                                            pin = pinCode
                                        )
                                    },
                                    enabled = uiState !is AuthUiState.Loading,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryEmerald,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .bounceClick { },
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                ) {
                                    if (uiState is AuthUiState.Loading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(22.dp),
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Kaydı Tamamla & Başla",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


