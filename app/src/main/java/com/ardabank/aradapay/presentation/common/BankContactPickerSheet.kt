package com.ardabank.aradapay.presentation.common

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.expense.ExpenseParticipant
import com.ardabank.aradapay.presentation.friends.PhoneBookContact
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.util.ContactsHelper
import com.ardabank.aradapay.util.NotificationHelper

import androidx.activity.compose.BackHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankContactPickerScreen(
    title: String = "Katılımcı Seçimi",
    allFriends: List<ExpenseParticipant>,
    selectedIds: Set<String>,
    onDismiss: () -> Unit,
    onConfirmSelection: (selectedIds: Set<String>, updatedAllFriends: List<ExpenseParticipant>) -> Unit
) {
    BackHandler { onDismiss() }

    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedNonMemberForInvite by remember { mutableStateOf<PhoneBookContact?>(null) }

    val currentFriendsList = remember { mutableStateListOf<ExpenseParticipant>().apply { addAll(allFriends) } }
    val currentSelected = remember { mutableStateListOf<String>().apply { addAll(selectedIds) } }

    val rawPhoneContacts = remember {
        mutableStateListOf<com.ardabank.aradapay.presentation.friends.PhoneBookContact>().apply {
            addAll(ContactsHelper.readDeviceContacts(context))
        }
    }

    // 1. REGISTERED ARADAPAY MEMBERS (ALL FRIENDS + MEMBER PHONE CONTACTS)
    val combinedMembers = remember(currentFriendsList.size, rawPhoneContacts.size) {
        val memberFromPhone = rawPhoneContacts.filter { it.isAradaPayMember }.mapIndexed { index, contact ->
            ExpenseParticipant(
                id = "phone_member_${index + 100}",
                name = contact.name,
                tag = contact.memberTag ?: "${contact.name.split(" ").first()}#${1000 + index * 37}",
                avatar = contact.name.take(2).uppercase()
            )
        }
        (currentFriendsList + memberFromPhone).distinctBy { it.tag.lowercase() }
    }

    // 2. NON-MEMBER PHONE CONTACTS (ONLY INVITE AVAILABLE)
    val nonMemberPhoneContacts = remember(rawPhoneContacts.size) {
        rawPhoneContacts.filter { !it.isAradaPayMember }
    }

    val filteredMembers = remember(combinedMembers, searchQuery) {
        combinedMembers.filter {
            searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.tag.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredNonMembers = remember(nonMemberPhoneContacts, searchQuery) {
        nonMemberPhoneContacts.filter {
            searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery, ignoreCase = true)
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Top App Bar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    FilledTonalIconButton(
                        onClick = onDismiss,
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
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                // Green Confirm Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryEmerald,
                    modifier = Modifier
                        .size(38.dp)
                        .bounceClick {
                            onConfirmSelection(currentSelected.toSet(), combinedMembers)
                            onDismiss()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Onayla",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

                // 2. Universal Inline Participant Selection & Search Row
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp)
                            .padding(vertical = 10.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Seninle ve: ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )

                        // Selected Participant Pills (Horizontal Capsule Design)
                        if (currentSelected.isNotEmpty()) {
                            val selectedParticipants = combinedMembers.filter { currentSelected.contains(it.id) }.distinctBy { it.id }
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                items(selectedParticipants, key = { "pill_chip_${it.id}" }) { p ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = PrimaryEmeraldContainer,
                                        border = BorderStroke(1.dp, PrimaryEmerald),
                                        modifier = Modifier.bounceClick { currentSelected.remove(p.id) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = PrimaryEmerald,
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = if (p.avatar.length <= 2) p.avatar else p.name.take(2).uppercase(),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = p.name.split(" ").first(),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryEmerald
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Seamless Inline Text Input
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 2.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = if (currentSelected.isEmpty()) "İsim, telefon veya #tag gir..." else "Kişi ekle...",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 14.sp
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = Color(0xFF0F172A),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(PrimaryEmerald),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }

                // 5. UNBOXED Contact List (100% Flat Rows with Inset Dividers)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = if (currentSelected.isNotEmpty()) 80.dp else 16.dp)
                ) {
                    // Section 1: Members
                    if (filteredMembers.isNotEmpty()) {
                        item {
                            Text(
                                text = "ARADAPAY ÜYELERİ (${filteredMembers.size})",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(start = 2.dp, top = 8.dp, bottom = 4.dp)
                            )
                        }

                        itemsIndexed(filteredMembers, key = { _, contact -> "member_${contact.id}" }) { index, contact ->
                            val isChecked = currentSelected.contains(contact.id)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .bounceClick {
                                        if (isChecked) {
                                            currentSelected.remove(contact.id)
                                        } else {
                                            currentSelected.add(contact.id)
                                            NotificationHelper.showSystemNotification(
                                                context = context,
                                                title = "Arkadaşlık İsteği İletildi",
                                                message = "${contact.name} (${contact.tag}) kişisine arkadaşlık isteği başarıyla gönderildi."
                                            )
                                            Toast.makeText(context, "${contact.name} kişisine arkadaşlık isteği iletildi", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isChecked) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = contact.avatar,
                                                color = if (isChecked) PrimaryEmerald else Color(0xFF0F172A),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = contact.name,
                                            color = Color(0xFF0F172A),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Text(
                                            text = contact.tag,
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                // Checkmark
                                Icon(
                                    imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = if (isChecked) PrimaryEmerald else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            if (index < filteredMembers.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 54.dp),
                                    color = Color(0xFFF1F5F9),
                                    thickness = 0.8.dp
                                )
                            }
                        }
                    }

                    // Section 2: Non-members from phone
                    if (filteredNonMembers.isNotEmpty()) {
                        item {
                            Text(
                                text = "REHBERDEKİ DİĞER KİŞİLER (${filteredNonMembers.size})",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(start = 2.dp, top = 14.dp, bottom = 4.dp)
                            )
                        }

                        itemsIndexed(filteredNonMembers, key = { _, nonMember -> "non_member_${nonMember.phone}" }) { index, nonMember ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .bounceClick {
                                        selectedNonMemberForInvite = nonMember
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = nonMember.name.take(2).uppercase(),
                                                color = Color(0xFF64748B),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = nonMember.name,
                                            color = Color(0xFF334155),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${nonMember.phone} • Üye Değil",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                FilledTonalButton(
                                    onClick = {
                                        selectedNonMemberForInvite = nonMember
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color(0xFFE0F2FE),
                                        contentColor = Color(0xFF0284C7)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Davet Et", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            if (index < filteredNonMembers.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 54.dp),
                                    color = Color(0xFFF1F5F9),
                                    thickness = 0.8.dp
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Bottom Sticky Confirmation Button
                Button(
                    onClick = {
                        onConfirmSelection(currentSelected.toSet(), combinedMembers)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryEmerald,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 4.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentSelected.isNotEmpty()) "Seçimi Tamamla (${currentSelected.size} Kişi)" else "Listeyi Kapat",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        selectedNonMemberForInvite?.let { contact ->
            SmartInviteChannelSheet(
                contactName = contact.name,
                contactPhone = contact.phone,
                onDismiss = { selectedNonMemberForInvite = null }
            )
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankContactPickerSheet(
    title: String = "Katılımcı Seçimi",
    allFriends: List<ExpenseParticipant>,
    selectedIds: Set<String>,
    onDismiss: () -> Unit,
    onConfirmSelection: (selectedIds: Set<String>, updatedAllFriends: List<ExpenseParticipant>) -> Unit
) {
    BankContactPickerScreen(
        title = title,
        allFriends = allFriends,
        selectedIds = selectedIds,
        onDismiss = onDismiss,
        onConfirmSelection = onConfirmSelection
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartInviteChannelSheet(
    contactName: String,
    contactPhone: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val cleanFirstName = contactName.split(" ").first()
    val smartInviteMessage = "Selam $cleanFirstName! Ortak harcamalarımızı ve hesaplarımızı kolayca bölüşüp fitleşmek için AradaPay'e katıl: https://aradapay.com/join/arda1453"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
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
                .padding(horizontal = 22.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Davet Kanalı Seçin",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$contactName kişisine akıllı davet bağlantısı gönder",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                FilledTonalIconButton(
                    onClick = onDismiss,
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Target Contact Summary Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0F2FE),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = contactName.take(2).uppercase(),
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = contactName,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = contactPhone.ifBlank { "Kayıtlı telefon numarası" },
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Text(
                text = "İLETİŞİM SEÇENEKLERİ",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            // 1. WhatsApp Option
            InviteOptionCard(
                icon = Icons.Default.Chat,
                iconColor = Color(0xFF25D366),
                containerColor = Color(0xFFF0FDF4),
                title = "WhatsApp ile Gönder",
                subtitle = "Doğrudan sohbet penceresi ve hazır link açılır",
                onClick = {
                    try {
                        val cleanPhone = contactPhone.replace("+", "").replace(" ", "").trim()
                        val uri = if (cleanPhone.isNotBlank()) {
                            Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(smartInviteMessage)}")
                        } else {
                            Uri.parse("whatsapp://send?text=${Uri.encode(smartInviteMessage)}")
                        }
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                        Toast.makeText(context, "$contactName için WhatsApp açılıyor...", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, smartInviteMessage)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Davet Gönder"))
                    }
                    onDismiss()
                }
            )

            // 2. SMS Option
            InviteOptionCard(
                icon = Icons.Default.Message,
                iconColor = Color(0xFF0284C7),
                containerColor = Color(0xFFF0F9FF),
                title = "SMS ile Gönder",
                subtitle = "${contactPhone.ifBlank { "SMS" }} numarasına mesaj taslağı açılır",
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("sms:${contactPhone.trim()}")
                            putExtra("sms_body", smartInviteMessage)
                        }
                        context.startActivity(intent)
                        Toast.makeText(context, "$contactName için SMS taslağı açılıyor...", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, smartInviteMessage)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Davet Gönder"))
                    }
                    onDismiss()
                }
            )

            // 3. Telegram Option
            InviteOptionCard(
                icon = Icons.Default.Send,
                iconColor = Color(0xFF0088CC),
                containerColor = Color(0xFFF0F9FF),
                title = "Telegram ile Paylaş",
                subtitle = "Telegram mesajı olarak ilet",
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://msg?text=${Uri.encode(smartInviteMessage)}"))
                        context.startActivity(intent)
                        Toast.makeText(context, "Telegram açılıyor...", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, smartInviteMessage)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Davet Gönder"))
                    }
                    onDismiss()
                }
            )

            // 4. Copy Link Option
            InviteOptionCard(
                icon = Icons.Default.ContentCopy,
                iconColor = PrimaryEmerald,
                containerColor = PrimaryEmeraldContainer,
                title = "Davet Bağlantısını Kopyala",
                subtitle = "Özel davet linki panoya kopyalanır",
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("AradaPay Davet", smartInviteMessage))
                    Toast.makeText(context, "Davet bağlantısı kopyalandı", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            )

            // 5. General Share Sheet
            InviteOptionCard(
                icon = Icons.Default.Share,
                iconColor = Color(0xFF334155),
                containerColor = Color(0xFFF1F5F9),
                title = "Diğer Paylaşım Seçenekleri",
                subtitle = "Sistem paylaşım menüsünü aç",
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, smartInviteMessage)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "$contactName kişisine Davet Gönder"))
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun InviteOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    containerColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = containerColor,
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                color = Color(0xFF64748B),
                fontSize = 11.sp
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(14.dp)
        )
    }
    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.8.dp)
}
