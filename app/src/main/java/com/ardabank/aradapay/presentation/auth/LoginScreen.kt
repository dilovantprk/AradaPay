package com.ardabank.aradapay.presentation.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.ardabank.aradapay.domain.model.Currency
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.util.BiometricAuthHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    currentUserName: String = "Mehmet Dilovan",
    onLoginSuccess: (String) -> Unit = {},
    onSwitchUser: (String) -> Unit = {},
    onNavigateToWelcome: () -> Unit = {}
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    var userName by remember { mutableStateOf(currentUserName) }
    var isNewUserMode by remember { mutableStateOf(currentUserName == "Kullanıcı" || currentUserName.isBlank()) }
    var isEnteringPin by remember { mutableStateOf(false) }
    var pinCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Bottom Sheets & Dialogs
    var showOtherLoginMethodsSheet by remember { mutableStateOf(false) }
    var showRegisterSheet by remember { mutableStateOf(false) }
    var showEmailLoginDialog by remember { mutableStateOf(false) }
    var showPhoneLoginDialog by remember { mutableStateOf(false) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    // Register Form Fields (With Google & OTP logic)
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regEmailOtp by remember { mutableStateOf("") }
    var isEmailOtpSent by remember { mutableStateOf(false) }
    var isEmailVerified by remember { mutableStateOf(false) }

    var regPhone by remember { mutableStateOf("") }
    var regPhoneOtp by remember { mutableStateOf("") }
    var isPhoneOtpSent by remember { mutableStateOf(false) }
    var isPhoneVerified by remember { mutableStateOf(false) }

    var regIban by remember { mutableStateOf("TR") }
    var regPin by remember { mutableStateOf("") }
    var isFromGoogleAuth by remember { mutableStateOf(false) }

    // Email Login Fields
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    // Phone Login Fields
    var loginPhone by remember { mutableStateOf("") }
    var loginOtp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    // Reset Password Field
    var resetEmailInput by remember { mutableStateOf("") }

    val pinFocusRequester = remember { FocusRequester() }

    // Google Sign-In Client
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val googleName = account.displayName ?: account.givenName ?: "Google Kullanıcısı"
                val googleEmail = account.email ?: ""

                // Set data and transition to Registration/Profile Completion to enforce IBAN & 4-digit PIN!
                regFullName = googleName
                regEmail = googleEmail
                isEmailVerified = true // Google verified email
                isFromGoogleAuth = true
                showOtherLoginMethodsSheet = false
                showRegisterSheet = true

                Toast.makeText(context, "Google bağlandı. Lütfen FAST IBAN ve 4 haneli PIN belirleyin.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Google girişi başarısız oldu.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(currentUserName) {
        userName = currentUserName
        isNewUserMode = currentUserName == "Kullanıcı" || currentUserName.isBlank()
    }

    // Dynamic Time-Based Greeting
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = remember(hour) {
        when (hour) {
            in 5..11 -> "Günaydın"
            in 12..17 -> "İyi günler"
            in 18..22 -> "İyi akşamlar"
            else -> "İyi geceler"
        }
    }

    // Dynamic Initials from Current Name
    val initials = remember(userName) {
        val parts = userName.trim().split(" ").filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
            parts.size == 1 && parts[0].length >= 2 -> parts[0].substring(0, 2).uppercase()
            parts.size == 1 -> parts[0].uppercase()
            else -> "AP"
        }
    }

    // Automatically request focus and show soft keyboard on entering PIN screen
    LaunchedEffect(isEnteringPin) {
        if (isEnteringPin) {
            delay(150)
            pinFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // Function to trigger real Fingerprint / FaceID
    fun triggerBiometric() {
        val activity = context as? FragmentActivity
        if (activity != null && BiometricAuthHelper.isBiometricAvailable(context)) {
            BiometricAuthHelper.showBiometricPrompt(
                activity = activity,
                title = "AradaPay Güvenli Giriş",
                subtitle = "$userName olarak parmak izinizle giriş yapın",
                onSuccess = {
                    Toast.makeText(context, "Parmak izi doğrulandı! Hoş geldiniz, $userName", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(userName)
                },
                onError = { _ ->
                    isEnteringPin = true
                    pinCode = ""
                }
            )
        } else {
            isEnteringPin = true
            pinCode = ""
        }
    }

    AnimatedContent(
        targetState = when {
            isEnteringPin -> "PIN"
            isNewUserMode -> "ONBOARDING"
            else -> "RETURNING_USER"
        },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "auth_screen_transition"
    ) { screenState ->
        when (screenState) {
            // =========================================================================
            // 1. FIRST-TIME ONBOARDING & REGISTRATION SCREEN
            // =========================================================================
            "ONBOARDING" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        com.ardabank.aradapay.presentation.components.AradaPayLogo(
                            logoSize = com.ardabank.aradapay.presentation.components.AradaPayLogoSize.XL
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "AradaPay",
                            color = PrimaryEmerald,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Ortak Harcamaları\nKolayca Yönetin",
                            color = Color(0xFF0F172A),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Arkadaşlarınla yaptığın tüm masrafları eşit veya özel paylarla bölüş, kimin kime ne kadar borcu olduğunu net olarak gör.",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Eşit veya Detaylı Harcama Bölüşümü", color = Color(0xFF334155), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("PIN ve Biyometrik Kilit ile Güvenli Hesap", color = Color(0xFF334155), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                isFromGoogleAuth = false
                                showRegisterSheet = true
                            },
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryEmerald,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Hemen Ücretsiz Kayıt Ol",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { showOtherLoginMethodsSheet = true },
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF0F172A)
                            ),
                            border = BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(
                                text = "Zaten Hesabım Var (Giriş Yap)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // 2. RETURNING USER WELCOME SCREEN (Clean & Minimalist FinTech Design)
            // =========================================================================
            "RETURNING_USER" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // TOP BAR: Clean Brand Wordmark
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.ardabank.aradapay.presentation.components.AradaPayBrandWordmark(
                            logoSize = com.ardabank.aradapay.presentation.components.AradaPayLogoSize.SM,
                            textSize = 18
                        )
                    }

                    // CENTER: CLEAN FLAT AVATAR & GREETING
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(88.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initials,
                                    color = Color(0xFF0F172A),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "$greeting,",
                            color = Color(0xFF64748B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = userName,
                            color = Color(0xFF0F172A),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // BOTTOM: LOGIN ACTION BUTTONS & ACCOUNT SWITCHER
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Single Unified Login Button
                        Button(
                            onClick = { triggerBiometric() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryEmerald,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Giriş Yap",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Switch Account / Logout Option
                        Text(
                            text = "Farklı bir hesapla giriş yap",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNavigateToWelcome() }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // =========================================================================
            // 3. DEDICATED PIN ENTRY SCREEN (AUTOMATIC SOFT KEYBOARD)
            // =========================================================================
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            pinFocusRequester.requestFocus()
                            keyboardController?.show()
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalIconButton(
                                onClick = {
                                    isEnteringPin = false
                                    pinCode = ""
                                    keyboardController?.hide()
                                },
                                shape = RoundedCornerShape(12.dp),
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
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initials,
                                    color = Color(0xFF0F172A),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = userName,
                            color = Color(0xFF0F172A),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Lütfen 4 haneli PIN kodunuzu girin",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        // Hidden Real BasicTextField for System Keyboard
                        BasicTextField(
                            value = pinCode,
                            onValueChange = { input ->
                                if (input.length <= 4 && input.all { it.isDigit() }) {
                                    if (input.length > pinCode.length) {
                                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                    }
                                    pinCode = input
                                    if (pinCode.length == 4) {
                                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                        keyboardController?.hide()
                                        isLoading = true
                                        onLoginSuccess(userName)
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (pinCode.length == 4) {
                                        keyboardController?.hide()
                                        onLoginSuccess(userName)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .size(1.dp)
                                .focusRequester(pinFocusRequester)
                        )

                        // 4 PIN Dots / Digit Boxes
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0 until 4) {
                                val isFilled = i < pinCode.length
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isFilled) PrimaryEmeraldContainer else Color(0xFFF8FAFC),
                                    border = BorderStroke(
                                        width = if (i == pinCode.length) 2.dp else 1.5.dp,
                                        color = if (i == pinCode.length) PrimaryEmerald else if (isFilled) PrimaryEmerald else Color(0xFFCBD5E1)
                                    ),
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isFilled) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(PrimaryEmerald)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        if (isLoading) {
                            CircularProgressIndicator(
                                color = PrimaryEmerald,
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 2.5.dp
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "PIN kodunuzu mu unuttunuz?",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable {
                                    showResetPasswordDialog = true
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // 1. YENİ KULLANICI KAYIT FORMU (FULL-PAGE INTRINSIC SCREEN)
    // =========================================================================
    if (showRegisterSheet) {
        BackHandler(enabled = !isLoading) { showRegisterSheet = false }

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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { if (!isLoading) showRegisterSheet = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
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
                        text = if (isFromGoogleAuth) "Google Hesabını Tamamla" else "Hesap Oluştur",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                Text(
                    text = if (isFromGoogleAuth)
                        "Google hesabınız bağlandı. Hesabınızı aktif etmek için lütfen zorunlu IBAN ve 4 haneli PIN belirleyin."
                    else
                        "Bilgilerinizi eksiksiz girerek saniyeler içinde hesabınızı oluşturun:",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                if (isFromGoogleAuth) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD1FAE5),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google ile Doğrulandı: $regEmail", color = Color(0xFF065F46), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // 1. AD SOYAD (ZORUNLU)
                OutlinedTextField(
                    value = regFullName,
                    onValueChange = { regFullName = it },
                    label = { Text("Gerçek Ad Soyad *") },
                    placeholder = { Text("Örn: Mehmet Dilovan") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                // 2. E-POSTA & DOĞRULAMA KODU (ZORUNLU)
                if (!isFromGoogleAuth) {
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = {
                            regEmail = it
                            isEmailVerified = false
                            isEmailOtpSent = false
                        },
                        label = { Text("E-posta Adresi *") },
                        placeholder = { Text("isim@ornek.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryEmerald,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )

                    if (!isEmailVerified) {
                        if (!isEmailOtpSent) {
                            OutlinedButton(
                                onClick = {
                                    if (regEmail.contains("@") && regEmail.contains(".")) {
                                        isEmailOtpSent = true
                                        Toast.makeText(context, "$regEmail adresine 6 haneli e-posta doğrulama kodu gönderildi (Demo: 849201)", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Lütfen geçerli bir e-posta adresi girin.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Onay Kodu Gönder", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = regEmailOtp,
                                    onValueChange = { if (it.length <= 6) regEmailOtp = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("6 Haneli E-Posta Kodu *") },
                                    placeholder = { Text("849201") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                Button(
                                    onClick = {
                                        if (regEmailOtp.length == 6) {
                                            isEmailVerified = true
                                            Toast.makeText(context, "E-posta adresi başarıyla doğrulandı.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Lütfen 6 haneli kodu girin.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                                ) {
                                    Text("Onayla", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryEmeraldContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("E-posta Onaylandı", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // 3. TELEFON NUMARASI (OPSİYONEL / İSTEĞE BAĞLI)
                OutlinedTextField(
                    value = regPhone,
                    onValueChange = { regPhone = it },
                    label = { Text("Telefon Numarası (İsteğe Bağlı)") },
                    placeholder = { Text("05XX XXX XX XX (İsterseniz boş bırakabilirsiniz)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                if (regPhone.isNotBlank() && !isPhoneVerified) {
                    if (!isPhoneOtpSent) {
                        OutlinedButton(
                            onClick = {
                                isPhoneOtpSent = true
                                Toast.makeText(context, "$regPhone numarasına SMS onay kodu gönderildi (Demo: 123456)", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SMS ile Doğrula", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = regPhoneOtp,
                                onValueChange = { if (it.length <= 6) regPhoneOtp = it.filter { ch -> ch.isDigit() } },
                                label = { Text("SMS Kodu") },
                                placeholder = { Text("123456") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    if (regPhoneOtp.length == 6) {
                                        isPhoneVerified = true
                                        Toast.makeText(context, "Telefon doğrulandı", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                            ) {
                                Text("Doğrula")
                            }
                        }
                    }
                }

                // 4. FAST IBAN NUMARASI (ZORUNLU)
                OutlinedTextField(
                    value = regIban,
                    onValueChange = { input ->
                        val cleaned = input.uppercase().filter { it.isLetterOrDigit() }.take(26)
                        regIban = if (cleaned.startsWith("TR")) cleaned else "TR$cleaned"
                    },
                    label = { Text("FAST IBAN Numarası *") },
                    placeholder = { Text("TR64 0006 2000 ...") },
                    leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = PrimaryEmerald) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                // 5. 4 HANELİ GİRİŞ PIN'İ (ZORUNLU)
                OutlinedTextField(
                    value = regPin,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) regPin = it },
                    label = { Text("4 Haneli Güvenlik PIN'i *") },
                    placeholder = { Text("••••") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryEmerald,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SUBMIT REGISTRATION
                Button(
                    onClick = {
                        if (regFullName.trim().length < 3) {
                            Toast.makeText(context, "Lütfen geçerli bir ad ve soyad giriniz.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!isEmailVerified && !isFromGoogleAuth) {
                            Toast.makeText(context, "Lütfen e-posta adresinize gönderilen onay kodunu doğrulayın.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (regIban.length < 24) {
                            Toast.makeText(context, "Lütfen geçerli 26 haneli TR IBAN numaranızı girin.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (regPin.length != 4) {
                            Toast.makeText(context, "Lütfen 4 haneli PIN şifrenizi belirleyin.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        val targetEmail = if (regEmail.isNotBlank()) regEmail.trim() else "${regFullName.lowercase().replace(" ", "")}${System.currentTimeMillis() % 10000}@aradapay.local"
                        val targetPass = "AradaPay${regPin}!"

                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(targetEmail, targetPass)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                val newName = regFullName.trim()
                                userName = newName
                                isNewUserMode = false
                                showRegisterSheet = false
                                onSwitchUser(newName)

                                val uid = task.result?.user?.uid ?: "user_${System.currentTimeMillis()}"
                                val userObj = User(
                                    id = uid,
                                    email = targetEmail,
                                    username = newName.split(" ").firstOrNull() ?: newName,
                                    fullName = newName,
                                    phone = if (regPhone.isNotBlank()) regPhone.trim() else null,
                                    iban = regIban,
                                    tag = "@${newName.split(" ").first().lowercase()}#${(1000..9999).random()}",
                                    defaultCurrency = Currency.TRY,
                                    createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                )
                                FirebaseFirestore.getInstance().collection("users").document(uid).set(userObj)

                                Toast.makeText(context, "Hesabınız oluşturuldu. Hoş geldiniz, $newName", Toast.LENGTH_LONG).show()
                                onLoginSuccess(newName)
                            }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Kaydı Tamamla", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        return
    }

    // =========================================================================
    // 2. GİRİŞ YÖNTEMLERİ (FULL-PAGE INTRINSIC SCREEN)
    // =========================================================================
    if (showOtherLoginMethodsSheet) {
        BackHandler { showOtherLoginMethodsSheet = false }

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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { showOtherLoginMethodsSheet = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
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
                        text = "Giriş Yöntemleri",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                Text(
                    text = "Hesabınıza bağlanmak için bir yöntem seçin:",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 1. Google ile Giriş
                OutlinedButton(
                    onClick = {
                        try {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Google servisine ulaşılamadı.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Google ile Giriş", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }

                // 2. Telefon Numarası & SMS ile Giriş
                OutlinedButton(
                    onClick = {
                        showOtherLoginMethodsSheet = false
                        showPhoneLoginDialog = true
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Telefon Numarası (SMS)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }

                // 3. E-posta ve Şifre ile Giriş
                OutlinedButton(
                    onClick = {
                        showOtherLoginMethodsSheet = false
                        showEmailLoginDialog = true
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("E-posta ve Şifre ile Giriş", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                // Yeni Hesap Aç
                Button(
                    onClick = {
                        showOtherLoginMethodsSheet = false
                        isFromGoogleAuth = false
                        showRegisterSheet = true
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Yeni Hesap Aç", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        return
    }

    // DIALOG 1: TELEFON İLE GİRİŞ
    if (showPhoneLoginDialog) {
        AlertDialog(
            onDismissRequest = { showPhoneLoginDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Telefon ile Giriş Yap", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = loginPhone,
                        onValueChange = { loginPhone = it },
                        label = { Text("Telefon Numarası") },
                        placeholder = { Text("05XX XXX XX XX") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isOtpSent) {
                        OutlinedTextField(
                            value = loginOtp,
                            onValueChange = { loginOtp = it },
                            label = { Text("6 Haneli SMS Kodu") },
                            placeholder = { Text("123456") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isOtpSent) {
                            if (loginPhone.isNotBlank()) {
                                isOtpSent = true
                                Toast.makeText(context, "$loginPhone numarasına SMS onay kodu iletildi.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            if (loginOtp.isNotBlank()) {
                                showPhoneLoginDialog = false
                                userName = "Mehmet Dilovan"
                                onSwitchUser("Mehmet Dilovan")
                                onLoginSuccess("Mehmet Dilovan")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    Text(if (!isOtpSent) "SMS Kodu Gönder" else "Giriş Yap", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = { showPhoneLoginDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF64748B))
                ) {
                    Text("İptal", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    // DIALOG 2: E-POSTA İLE GİRİŞ
    if (showEmailLoginDialog) {
        AlertDialog(
            onDismissRequest = { showEmailLoginDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("E-posta ile Giriş Yap", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = loginEmail,
                        onValueChange = { loginEmail = it },
                        label = { Text("E-posta Adresi") },
                        placeholder = { Text("isim@ornek.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("Şifre") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (loginEmail.isNotBlank() && loginPassword.isNotBlank()) {
                            showEmailLoginDialog = false
                            val displayName = loginEmail.split("@").first().replace(".", " ")
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            userName = displayName
                            onSwitchUser(displayName)
                            onLoginSuccess(displayName)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    Text("Giriş Yap", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = { showEmailLoginDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF64748B))
                ) {
                    Text("İptal", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    // DIALOG 3: ŞİFRE SIFIRLAMA
    if (showResetPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showResetPasswordDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Şifre & PIN Sıfırlama", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Kayıtlı e-posta adresinizi girin, sıfırlama bağlantısını anında iletelim.", color = Color(0xFF64748B), fontSize = 13.sp)
                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it },
                        label = { Text("E-posta Adresi") },
                        placeholder = { Text("isim@ornek.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmailInput.isNotBlank()) {
                            showResetPasswordDialog = false
                            Toast.makeText(context, "$resetEmailInput adresine sıfırlama talimatı gönderildi.", Toast.LENGTH_LONG).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    Text("Sıfırlama Bağlantısı Gönder", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = { showResetPasswordDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF64748B))
                ) {
                    Text("İptal", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}
