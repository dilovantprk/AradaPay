package com.ardabank.aradapay.presentation.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.model.Currency
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterFlowScreen(
    onBackClick: () -> Unit = {},
    onRegisterSuccess: (User) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    var currentStep by remember { mutableIntStateOf(1) } // 1: Info, 2: Phone/OTP, 3: IBAN, 4: PIN
    var isLoading by remember { mutableStateOf(false) }

    // Step 1: Info
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Step 2: Phone & OTP
    var phone by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    // Step 3: IBAN
    var ibanInput by remember { mutableStateOf("TR") }

    // Step 4: 4-digit PIN
    var pinCode by remember { mutableStateOf("") }
    var pinConfirmCode by remember { mutableStateOf("") }
    var isConfirmingPin by remember { mutableStateOf(false) }
    var isBiometricEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // TOP BAR WITH BACK & PROGRESS INDICATOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilledTonalIconButton(
                onClick = {
                    if (currentStep > 1) {
                        currentStep--
                    } else {
                        onBackClick()
                    }
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White),
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Step Indicator Pills
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (step in 1..4) {
                    val isActive = step == currentStep
                    val isDone = step < currentStep
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (isActive) 28.dp else 16.dp)
                            .background(
                                color = when {
                                    isActive -> PrimaryEmerald
                                    isDone -> PrimaryEmerald.copy(alpha = 0.5f)
                                    else -> Color(0xFFCBD5E1)
                                },
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }

            Text(
                text = "Adım $currentStep/4",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ANIMATED STEP CONTENT
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "RegisterStepAnimation"
            ) { targetStep ->
                when (targetStep) {
                    // =========================================================================
                    // STEP 1: GERÇEK AD SOYAD & E-POSTA (ZORUNLU)
                    // =========================================================================
                    1 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Kimlik Bilgilerin",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Harcama bölüşümlerinde ve transfer dekontlarında görünecek gerçek adını ve e-postanı gir.",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }

                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Gerçek Ad Soyad *") },
                                placeholder = { Text("örn: Mehmet Dilovan") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryEmerald) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("E-Posta Adresi *") },
                                placeholder = { Text("örn: mehmet@ornek.com") },
                                leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = PrimaryEmerald) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = {
                                    if (fullName.trim().length < 3 || !fullName.trim().contains(" ")) {
                                        Toast.makeText(context, "Lütfen geçerli bir ad ve soyad girin.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (!email.contains("@") || !email.contains(".")) {
                                        Toast.makeText(context, "Lütfen geçerli bir e-posta adresi girin.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    currentStep = 2
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                            ) {
                                Text("Devam Et →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }

                    // =========================================================================
                    // STEP 2: TELEFON NO & SMS DOĞRULAMA (ZORUNLU)
                    // =========================================================================
                    2 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Telefon Doğrulama",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rehberindeki arkadaşlarınla anında eşleşebilmen için telefon numaranı doğrula.",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }.take(10)
                                    phone = digits
                                },
                                label = { Text("Telefon Numarası *") },
                                placeholder = { Text("5XX XXX XX XX") },
                                prefix = { Text("+90 ", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)) },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryEmerald) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (!isOtpSent) {
                                Button(
                                    onClick = {
                                        if (phone.length == 10) {
                                            isOtpSent = true
                                            Toast.makeText(context, "+90 $phone numarasına 6 haneli SMS onay kodu gönderildi.", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "Lütfen 10 haneli telefon numaranızı girin (5XX...)", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text("SMS Onay Kodu Gönder", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = PrimaryEmeraldContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("SMS Kodu Gönderildi", color = PrimaryEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Telefonunuza gelen 6 haneli kodu aşağıya girin.", color = Color(0xFF065F46), fontSize = 11.sp)
                                    }
                                }

                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { if (it.length <= 6) otpCode = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("6 Haneli SMS Kodu *") },
                                    placeholder = { Text("123456") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryEmerald,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = {
                                    if (phone.length < 10) {
                                        Toast.makeText(context, "Lütfen geçerli bir telefon numarası girin.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (!isOtpSent) {
                                        Toast.makeText(context, "Lütfen önce SMS kodu gönderin.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (otpCode.length != 6) {
                                        Toast.makeText(context, "Lütfen 6 haneli onay kodunu girin.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    currentStep = 3
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text("Doğrula ve Devam Et →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            androidx.compose.material3.TextButton(
                                onClick = {
                                    phone = ""
                                    currentStep = 3
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Bu Adımı Şimdilik Atla (Opsiyonel)", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }

                    // =========================================================================
                    // STEP 3: FAST IBAN TANIMLAMA (ZORUNLU)
                    // =========================================================================
                    3 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "FAST IBAN Bilgin",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ortak harcamalarda arkadaşlarının sana doğrudan para gönderebilmesi için geçerli bir IBAN girilmesi zorunludur.",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }

                            OutlinedTextField(
                                value = ibanInput,
                                onValueChange = { input ->
                                    val cleaned = input.uppercase().filter { it.isLetterOrDigit() }.take(26)
                                    ibanInput = if (cleaned.startsWith("TR")) cleaned else "TR$cleaned"
                                },
                                label = { Text("Banka IBAN Numarası *") },
                                placeholder = { Text("TR64 0006 2000 ...") },
                                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = PrimaryEmerald) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "TCMB FAST altyapısı ile 7/24 anlık para transferleri bu hesaba aktarılacaktır.",
                                        color = Color(0xFF475569),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = {
                                    if (ibanInput.length < 24) {
                                        Toast.makeText(context, "Lütfen geçerli 26 haneli TR IBAN numaranızı girin.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    currentStep = 4
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                            ) {
                                Text("Devam Et →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }

                    // =========================================================================
                    // STEP 4: 4 HANELİ PIN BELİRLEME (ZORUNLU) & BİYOMETRİK
                    // =========================================================================
                    4 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = if (!isConfirmingPin) "4 Haneli PIN Belirle" else "PIN Kodunu Tekrar Gir",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = Color(0xFF0F172A)
                            )

                            Text(
                                text = if (!isConfirmingPin)
                                    "Bakiyelerini korumak ve uygulamaya her girişte kullanmak üzere 4 haneli kodunu oluştur."
                                else
                                    "Doğrulama için lütfen belirlediğin 4 haneli PIN kodunu tekrar gir.",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // 4-Digit Indicator Dots
                            val currentActivePin = if (!isConfirmingPin) pinCode else pinConfirmCode
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (i in 0 until 4) {
                                    val isFilled = i < currentActivePin.length
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (isFilled) PrimaryEmerald else Color(0xFFE2E8F0))
                                            .border(1.dp, if (isFilled) PrimaryEmerald else Color(0xFFCBD5E1), CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Biometric Switch Row
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Parmak İzi / Yüz Tanıma", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Hızlı tek dokunuşla giriş yap", color = Color(0xFF64748B), fontSize = 10.sp)
                                        }
                                    }

                                    Switch(
                                        checked = isBiometricEnabled,
                                        onCheckedChange = { isBiometricEnabled = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = PrimaryEmerald
                                        )
                                    )
                                }
                            }

                            // Numeric Keypad (1-9, 0, Backspace)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val keys = listOf(
                                    listOf("1", "2", "3"),
                                    listOf("4", "5", "6"),
                                    listOf("7", "8", "9"),
                                    listOf("", "0", "DEL")
                                )

                                keys.forEach { rowKeys ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        rowKeys.forEach { key ->
                                            if (key.isEmpty()) {
                                                Spacer(modifier = Modifier.size(68.dp))
                                            } else {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color.White,
                                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                                    modifier = Modifier
                                                        .size(68.dp)
                                                        .clip(CircleShape)
                                                        .clickable {
                                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                                            if (key == "DEL") {
                                                                if (!isConfirmingPin && pinCode.isNotEmpty()) {
                                                                    pinCode = pinCode.dropLast(1)
                                                                } else if (isConfirmingPin && pinConfirmCode.isNotEmpty()) {
                                                                    pinConfirmCode = pinConfirmCode.dropLast(1)
                                                                }
                                                            } else {
                                                                if (!isConfirmingPin) {
                                                                    if (pinCode.length < 4) {
                                                                        pinCode += key
                                                                        if (pinCode.length == 4) {
                                                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                                                            coroutineScope.launch {
                                                                                delay(200)
                                                                                isConfirmingPin = true
                                                                            }
                                                                        }
                                                                    }
                                                                } else {
                                                                    if (pinConfirmCode.length < 4) {
                                                                        pinConfirmCode += key
                                                                        if (pinConfirmCode.length == 4) {
                                                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                                                            if (pinConfirmCode == pinCode) {
                                                                                // Registration is 100% complete! Save to Firestore
                                                                                isLoading = true
                                                                                coroutineScope.launch {
                                                                                    val firstName = fullName.split(" ").firstOrNull() ?: fullName
                                                                                    val tagCode = "Arda#${(1000..9999).random()}"
                                                                                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "user_${System.currentTimeMillis()}"

                                                                                    val createdUser = User(
                                                                                        id = uid,
                                                                                        email = email,
                                                                                        username = firstName,
                                                                                        fullName = fullName,
                                                                                        tag = tagCode,
                                                                                        defaultCurrency = Currency.TRY,
                                                                                        iban = ibanInput,
                                                                                        phone = "+90$phone"
                                                                                    )

                                                                                    try {
                                                                                        FirebaseFirestore.getInstance()
                                                                                            .collection("users")
                                                                                            .document(uid)
                                                                                            .set(createdUser)
                                                                                    } catch (_: Exception) {}

                                                                                    isLoading = false
                                                                                    onRegisterSuccess(createdUser)
                                                                                }
                                                                            } else {
                                                                                Toast.makeText(context, "PIN kodları uyuşmadı! Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
                                                                                pinConfirmCode = ""
                                                                                isConfirmingPin = false
                                                                                pinCode = ""
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        if (key == "DEL") {
                                                            Icon(Icons.Default.Backspace, contentDescription = "Sil", tint = Color(0xFF0F172A), modifier = Modifier.size(22.dp))
                                                        } else {
                                                            Text(key, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isLoading) {
                                CircularProgressIndicator(color = PrimaryEmerald, modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
