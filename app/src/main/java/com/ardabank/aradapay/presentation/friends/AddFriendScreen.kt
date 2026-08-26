package com.ardabank.aradapay.presentation.friends

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.util.ContactsHelper
import com.ardabank.aradapay.util.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(
    existingFriends: List<FriendProfile>,
    onBack: () -> Unit,
    onFriendAdded: (FriendProfile) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    // In-page manual entry fields
    var manualName by remember { mutableStateOf("") }
    var manualTagOrPhone by remember { mutableStateOf("") }
    var manualIban by remember { mutableStateOf("") }
    var showManualForm by remember { mutableStateOf(false) }

    // Contacts state
    val phoneBookContacts = remember {
        mutableStateListOf<PhoneBookContact>().apply {
            addAll(ContactsHelper.readDeviceContacts(context))
        }
    }
    var isContactsPermissionGranted by remember {
        mutableStateOf(ContactsHelper.hasContactsPermission(context))
    }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isContactsPermissionGranted = isGranted
        val contacts = ContactsHelper.readDeviceContacts(context)
        phoneBookContacts.clear()
        phoneBookContacts.addAll(contacts)
        if (isGranted) {
            Toast.makeText(context, "Rehber başarıyla senkronize edildi", Toast.LENGTH_SHORT).show()
        }
    }

    // Candidate registered AradaPay members
    val candidateMembers = remember {
        listOf(
            User(id = "cand_1", email = "caner@aradapay.com", username = "caner_e", fullName = "Caner Erkin", iban = "TR64 0006 2000 0000 7788 9900 11", tag = "@caner#1903"),
            User(id = "cand_2", email = "selin@aradapay.com", username = "selin_a", fullName = "Selin Aydın", iban = "TR64 0006 2000 0000 6677 8899 00", tag = "@selin#2839"),
            User(id = "cand_3", email = "deniz@aradapay.com", username = "deniz_c", fullName = "Deniz Çelik", iban = "TR64 0006 2000 0000 8899 0011 22", tag = "@deniz#5522"),
            User(id = "cand_4", email = "emre@aradapay.com", username = "emre_t", fullName = "Emre Tok", iban = "TR64 0006 2000 0000 9900 1122 33", tag = "@emre#6710"),
            User(id = "cand_5", email = "melis@aradapay.com", username = "melis_y", fullName = "Melis Yıldız", iban = "TR64 0006 2000 0000 1133 5577 99", tag = "@melis#8341")
        )
    }

    // Filter candidate members by search
    val filteredCandidates = remember(candidateMembers, searchQuery, existingFriends) {
        val existingIds = existingFriends.map { it.user.id }.toSet()
        val existingTags = existingFriends.mapNotNull { it.user.tag?.lowercase() }.toSet()

        candidateMembers.filter { candidate ->
            val notAlreadyFriend = candidate.id !in existingIds && candidate.tag?.lowercase() !in existingTags
            val matches = searchQuery.isBlank() ||
                    candidate.fullName.contains(searchQuery, ignoreCase = true) ||
                    candidate.username.contains(searchQuery, ignoreCase = true) ||
                    (candidate.tag?.contains(searchQuery, ignoreCase = true) == true)
            notAlreadyFriend && matches
        }
    }

    // Filter phone contacts
    val filteredPhoneContacts = remember(phoneBookContacts, searchQuery) {
        phoneBookContacts.filter { contact ->
            searchQuery.isBlank() ||
                    contact.name.contains(searchQuery, ignoreCase = true) ||
                    contact.phone.contains(searchQuery, ignoreCase = true)
        }
    }

    // Check if search query itself is a valid new tag/username that can be immediately added
    val isSearchDirectTag = searchQuery.trim().startsWith("@") || searchQuery.trim().startsWith("#") || searchQuery.trim().length >= 3

    fun handleAddUser(user: User) {
        val initials = user.fullName.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("").ifBlank { "AR" }
        val newProfile = FriendProfile(
            user = user,
            avatarEmoji = initials,
            balanceAmount = 0.0,
            isCreditor = false,
            isBalanced = true
        )
        onFriendAdded(newProfile)
        NotificationHelper.showSystemNotification(
            context = context,
            title = "Kişi Eklendi",
            message = "${user.fullName} (${user.tag ?: "@${user.username}"}) arkadaş listenize eklendi."
        )
        Toast.makeText(context, "${user.fullName} arkadaş listenize eklendi", Toast.LENGTH_SHORT).show()
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
        ) {
            // =========================================================================
            // 1. TOP APP BAR (iOS Style Header)
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color(0xFFF1F5F9)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp).bounceClick(onClick = onBack)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Arkadaş Ekle",
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                FilledTonalIconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = PrimaryEmeraldContainer,
                        contentColor = PrimaryEmerald
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp).bounceClick(onClick = onBack)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Bitti",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // =========================================================================
            // 2. INTRINSIC LIVE SEARCH BAR (iOS Style)
            // =========================================================================
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

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
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Kullanıcı adı (@dilovan), #tag veya isim...",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                },
                                modifier = Modifier.weight(1f)
                            )

                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Temizle",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .bounceClick { searchQuery = "" }
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Direct Add Prompt when typing a new custom tag/username
            if (isSearchDirectTag && filteredCandidates.none { it.tag.equals(searchQuery.trim(), ignoreCase = true) || it.username.equals(searchQuery.trim().removePrefix("@"), ignoreCase = true) }) {
                val cleanTag = if (searchQuery.trim().startsWith("@") || searchQuery.trim().startsWith("#")) searchQuery.trim() else "@${searchQuery.trim()}"
                Surface(
                    color = PrimaryEmeraldContainer.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PrimaryEmeraldContainer,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = cleanTag, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                Text(text = "Bu kullanıcıyı doğrudan ekle", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }

                        Button(
                            onClick = {
                                val directUser = User(
                                    id = "direct_${System.currentTimeMillis()}",
                                    email = "${searchQuery.trim().removePrefix("@").removePrefix("#").lowercase()}@aradapay.com",
                                    username = searchQuery.trim().removePrefix("@").removePrefix("#").lowercase(),
                                    fullName = searchQuery.trim().removePrefix("@").removePrefix("#").replaceFirstChar { it.uppercase() },
                                    iban = "TR64 0006 2000 0000 " + (1000..9999).random() + " " + (1000..9999).random() + " 22",
                                    tag = cleanTag
                                )
                                handleAddUser(directUser)
                                searchQuery = ""
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.bounceClick { }
                        ) {
                            Text("+ Ekle", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // =========================================================================
            // 3. SCROLLABLE INTRINSIC CONTENT (iOS Inset Grouped Sections)
            // =========================================================================
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECTION 1: ÖNERİLEN ARADAPAY ÜYELERİ
                if (filteredCandidates.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "ARADAPAY'DE BULUNAN KİŞİLER (${filteredCandidates.size})",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                            )

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    filteredCandidates.forEachIndexed { index, user ->
                                        val initials = user.fullName.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("").ifBlank { "AR" }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = Color(0xFFF1F5F9),
                                                    modifier = Modifier.size(44.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = initials,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 15.sp,
                                                            color = Color(0xFF0F172A)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column {
                                                    Text(
                                                        text = user.fullName,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 15.sp,
                                                        color = Color(0xFF0F172A)
                                                    )
                                                    Spacer(modifier = Modifier.height(1.dp))
                                                    Text(
                                                        text = "${user.tag ?: "@${user.username}"} • ArdaBank FAST",
                                                        color = Color(0xFF64748B),
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }

                                            Button(
                                                onClick = { handleAddUser(user) },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                                modifier = Modifier.bounceClick { handleAddUser(user) }
                                            ) {
                                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Ekle", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                        }

                                        if (index < filteredCandidates.lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(start = 68.dp),
                                                color = Color(0xFFF1F5F9),
                                                thickness = 0.8.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // SECTION 2: TELEFON REHBERİ ENTEGRASYONU
                item {
                    Column {
                        Text(
                            text = "TELEFON REHBERİ SENKRONİZASYONU",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (!isContactsPermissionGranted) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.ContactPhone, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Rehberdeki Arkadaşları Bul", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                            Text("AradaPay kullananları otomatik eşleştir", fontSize = 12.sp, color = Color(0xFF64748B))
                                        }
                                    }

                                    FilledTonalButton(
                                        onClick = { contactsPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = PrimaryEmeraldContainer, contentColor = PrimaryEmerald),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.bounceClick { contactsPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS) }
                                    ) {
                                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryEmerald)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Bağla", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryEmerald)
                                    }
                                }
                            } else if (filteredPhoneContacts.isNotEmpty()) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    filteredPhoneContacts.take(8).forEachIndexed { index, contact ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Surface(
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = if (contact.isAradaPayMember) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                                    modifier = Modifier.size(44.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = contact.name.take(2).uppercase(),
                                                            color = if (contact.isAradaPayMember) PrimaryEmerald else Color(0xFF0F172A),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(contact.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                                    Text(contact.phone, fontSize = 12.sp, color = Color(0xFF64748B))
                                                }
                                            }

                                            if (contact.isAradaPayMember) {
                                                Button(
                                                    onClick = {
                                                        val u = User(
                                                            id = "phone_${System.currentTimeMillis()}",
                                                            email = "${contact.name.lowercase().replace(" ", "")}@aradapay.com",
                                                            username = contact.name.lowercase().replace(" ", ""),
                                                            fullName = contact.name,
                                                            iban = "TR64 0006 2000 0000 " + (1000..9999).random() + " " + (1000..9999).random() + " 22",
                                                            tag = contact.memberTag ?: "@${contact.name.split(" ").first().lowercase()}"
                                                        )
                                                        handleAddUser(u)
                                                    },
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                                    modifier = Modifier.bounceClick { }
                                                ) {
                                                    Text("Ekle", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                }
                                            } else {
                                                FilledTonalButton(
                                                    onClick = {
                                                        val inviteMessage = "Selam ${contact.name}! AradaPay ile ortak hesaplarımızı, yemek ve fatura masraflarımızı kolayca bölüşüp FAST ile anında fitleşelim: https://aradapay.com/invite/dilovan"
                                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                            type = "text/plain"
                                                            putExtra(Intent.EXTRA_TEXT, inviteMessage)
                                                        }
                                                        context.startActivity(Intent.createChooser(shareIntent, "AradaPay'e Davet Et"))
                                                    },
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFF1F5F9)),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                    modifier = Modifier.bounceClick { }
                                                ) {
                                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF0F172A))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Davet Et", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                                }
                                            }
                                        }
                                        if (index < filteredPhoneContacts.take(8).lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(start = 68.dp),
                                                color = Color(0xFFF1F5F9),
                                                thickness = 0.8.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // SECTION 3: SAYFA İÇİ İÇKİN MANUEL KİŞİ FORMU
                item {
                    Column {
                        Text(
                            text = "MANUEL KİŞİ OLUŞTUR",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Özel Kişi Kaydı",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF0F172A)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (showManualForm) Color(0xFFF1F5F9) else PrimaryEmeraldContainer,
                                        modifier = Modifier.bounceClick { showManualForm = !showManualForm }
                                    ) {
                                        Text(
                                            text = if (showManualForm) "Gizle" else "+ Formu Aç",
                                            color = if (showManualForm) Color(0xFF64748B) else PrimaryEmerald,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                if (showManualForm) {
                                    Text(
                                        text = "Uygulama veya rehber haricinde bir kişiyi IBAN ve iletişim bilgileriyle kaydedin:",
                                        color = Color(0xFF64748B),
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    OutlinedTextField(
                                        value = manualName,
                                        onValueChange = { manualName = it },
                                        label = { Text("İsim Soyisim *") },
                                        placeholder = { Text("örn: Mehmet Can") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = manualTagOrPhone,
                                        onValueChange = { manualTagOrPhone = it },
                                        label = { Text("Telefon No veya @kullanıcıadı (Opsiyonel)") },
                                        placeholder = { Text("05XX XXX XX XX veya @mehmet#1234") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = manualIban,
                                        onValueChange = { manualIban = it },
                                        label = { Text("FAST / IBAN Numarası (Opsiyonel)") },
                                        placeholder = { Text("TR64 0006 2000 ...") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryEmerald,
                                            unfocusedBorderColor = Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Button(
                                        onClick = {
                                            if (manualName.isNotBlank()) {
                                                val generatedTag = if (manualTagOrPhone.startsWith("@") || manualTagOrPhone.startsWith("#")) {
                                                    manualTagOrPhone
                                                } else {
                                                    "@${manualName.split(" ").first().lowercase()}#${(1000..9999).random()}"
                                                }
                                                val newManualUser = User(
                                                    id = "manual_${System.currentTimeMillis()}",
                                                    email = "${manualName.lowercase().replace(" ", "")}@aradapay.com",
                                                    username = manualName.lowercase().replace(" ", "_"),
                                                    fullName = manualName.trim(),
                                                    iban = manualIban.ifBlank { "TR64 0006 2000 0000 " + (1000..9999).random() + " " + (1000..9999).random() + " 22" },
                                                    tag = generatedTag
                                                )
                                                handleAddUser(newManualUser)
                                                manualName = ""
                                                manualTagOrPhone = ""
                                                manualIban = ""
                                                showManualForm = false
                                            } else {
                                                Toast.makeText(context, "Lütfen isim soyisim giriniz", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .bounceClick { }
                                    ) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Manuel Kişiyi Kaydet", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
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
