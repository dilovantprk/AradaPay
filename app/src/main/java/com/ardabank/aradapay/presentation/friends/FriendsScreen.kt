package com.ardabank.aradapay.presentation.friends

import android.Manifest
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.presentation.common.BankContactPickerSheet
import com.ardabank.aradapay.presentation.common.ContextualQuickActionSheet
import com.ardabank.aradapay.presentation.common.QrScannerView
import com.ardabank.aradapay.presentation.common.SmartInviteChannelSheet
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.components.hapticCombinedClickable
import com.ardabank.aradapay.presentation.expense.ExpenseParticipant
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.AccentRoseContainer
import com.ardabank.aradapay.presentation.theme.OnAccentRoseContainer
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.presentation.theme.ShinyCyan
import com.ardabank.aradapay.presentation.theme.ShinyCyanContainer
import com.ardabank.aradapay.util.ContactsHelper
import com.ardabank.aradapay.util.NotificationHelper
import com.ardabank.aradapay.util.QrCodeHelper
import com.ardabank.aradapay.util.QrUserData
import kotlin.math.abs

data class FriendProfile(
    val user: User,
    val avatarEmoji: String,
    val balanceAmount: Double,
    val isCreditor: Boolean, // true: sana borçlu, false: sen borçlusun
    val isBalanced: Boolean = false
)

data class PhoneBookContact(
    val name: String,
    val phone: String,
    val isAradaPayMember: Boolean,
    val memberTag: String? = null
)

enum class FriendFilterOption(val label: String) {
    ALL("Tüm Arkadaşlar"),
    OUTSTANDING("Hesabı Açık Kalanlar"),
    YOU_OWE("Payıma Düşenler (-₺)"),
    OWED_TO_YOU("Masayı Üstlendiklerim (+₺)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    remoteFriends: List<User> = emptyList(),
    userTag: String = "#1453",
    onFriendClick: (friendId: String) -> Unit = {},
    onAddExpense: () -> Unit = {},
    onSendNudge: (userId: String) -> Unit = {},
    onSettleUp: (userId: String) -> Unit = {},
    onAddFriend: (User) -> Unit = {}
) {
    val context = LocalContext.current
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FriendFilterOption.ALL) }
    var showFilterSheet by remember { mutableStateOf(false) }

    var showAddFriendsScreen by remember { mutableStateOf(false) }
    var selectedFriendForQuickAction by remember { mutableStateOf<FriendProfile?>(null) }
    var selectedNonMemberForInvite by remember { mutableStateOf<PhoneBookContact?>(null) }

    val friendsList = remember(remoteFriends) {
        val mapped = remoteFriends.map { user ->
            val initials = user.fullName.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("").ifBlank { "AR" }
            FriendProfile(
                user = user,
                avatarEmoji = initials,
                balanceAmount = 0.0,
                isCreditor = false,
                isBalanced = true
            )
        }
        mutableStateListOf(*mapped.toTypedArray())
    }

    // Overall Balance Calculation
    val totalReceivables = friendsList.filter { it.isCreditor && !it.isBalanced }.sumOf { it.balanceAmount }
    val totalPayables = friendsList.filter { !it.isCreditor && !it.isBalanced }.sumOf { it.balanceAmount }
    val overallNet = totalReceivables - totalPayables

    // Filtered Friends List
    val filteredFriends = remember(friendsList.toList(), searchQuery, selectedFilter) {
        friendsList.filter { friend ->
            val matchesSearch = searchQuery.isBlank() ||
                    friend.user.fullName.contains(searchQuery, ignoreCase = true) ||
                    friend.user.username.contains(searchQuery, ignoreCase = true) ||
                    (friend.user.tag?.contains(searchQuery, ignoreCase = true) == true)

            val matchesFilter = when (selectedFilter) {
                FriendFilterOption.ALL -> true
                FriendFilterOption.OUTSTANDING -> !friend.isBalanced && friend.balanceAmount > 0
                FriendFilterOption.YOU_OWE -> !friend.isCreditor && !friend.isBalanced && friend.balanceAmount > 0
                FriendFilterOption.OWED_TO_YOU -> friend.isCreditor && !friend.isBalanced && friend.balanceAmount > 0
            }

            matchesSearch && matchesFilter
        }
    }

    // =========================================================================
    // INTRINSIC ADD FRIEND SCREEN (Tamamen İçkin & Structure Uyumlu)
    // =========================================================================
    if (showAddFriendsScreen) {
        BackHandler {
            showAddFriendsScreen = false
        }
        AddFriendScreen(
            existingFriends = friendsList,
            onBack = { showAddFriendsScreen = false },
            onFriendAdded = { newProfile ->
                if (friendsList.none { it.user.id == newProfile.user.id || it.user.tag.equals(newProfile.user.tag, ignoreCase = true) }) {
                    friendsList.add(0, newProfile)
                    onAddFriend(newProfile.user)
                }
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddExpense,
                containerColor = PrimaryEmerald,
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = "Harcama Ekle",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = {
                    Text(
                        text = "Harcama ekle",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // =========================================================================
            // 1. TOP APP BAR (Search & Add Friend Icons)
            // =========================================================================
            if (isSearchActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            isSearchActive = false
                            searchQuery = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Kişi veya kullanıcı adı ara...",
                                color = Color(0xFF94A3B8),
                                fontSize = 15.sp
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color(0xFF0F172A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(PrimaryEmerald),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Temizle",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kişiler",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        fontSize = 28.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilledTonalIconButton(
                            onClick = { isSearchActive = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Ara",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FilledTonalIconButton(
                            onClick = { showFilterSheet = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = if (selectedFilter != FriendFilterOption.ALL) PrimaryEmeraldContainer else Color(0xFFF1F5F9)
                            ),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filtrele",
                                tint = if (selectedFilter != FriendFilterOption.ALL) PrimaryEmerald else Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FilledTonalIconButton(
                            onClick = { showAddFriendsScreen = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Arkadaş Ekle",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // =========================================================================
            // 2. UNBOXED 1-LINE SUMMARY STRIP
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOPLAM KİŞİSEL BAKİYE",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                val netStatusColor = when {
                    overallNet > 0 -> PrimaryEmerald
                    overallNet < 0 -> AccentRose
                    else -> Color(0xFF64748B)
                }

                val netText = when {
                    overallNet > 0 -> "+${String.format(java.util.Locale.US, "%.2f", overallNet)} ₺ Masada Payın Var"
                    overallNet < 0 -> "${String.format(java.util.Locale.US, "%.2f", abs(overallNet))} ₺ Payına Düşen"
                    else -> "Tertemiz (Dengede)"
                }

                Text(
                    text = netText,
                    color = netStatusColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // =========================================================================
            // 3. FRIENDS LIST
            // =========================================================================
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (filteredFriends.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Masa bomboş.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (searchQuery.isNotBlank() || selectedFilter != FriendFilterOption.ALL) "Arama veya filtre kriterine uygun arkadaş bulunamadı." else "Henüz kimse eklenmedi. Arkadaşlarını ekleyip başlayabilirsin!",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(filteredFriends, key = { it.user.id }) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .hapticCombinedClickable(
                                    onClick = { onFriendClick(friend.user.id) },
                                    onLongClick = { selectedFriendForQuickAction = friend }
                                )
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left: Avatar & Name
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = PrimaryEmeraldContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = friend.avatarEmoji,
                                            color = PrimaryEmerald,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = friend.user.fullName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            // Right: Status & Amount
                            Column(horizontalAlignment = Alignment.End) {
                                if (friend.isBalanced || friend.balanceAmount == 0.0) {
                                    Text(
                                        text = "ödeştik",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                } else if (friend.isCreditor) {
                                    Text(
                                        text = "masadan payı var",
                                        color = PrimaryEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = "+${String.format(java.util.Locale.US, "%.2f", friend.balanceAmount)} ₺",
                                        color = PrimaryEmerald,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = "masaya payın var",
                                        color = AccentRose,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%.2f", abs(friend.balanceAmount))} ₺",
                                        color = AccentRose,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // =========================================================================
    // FILTER FULL-PAGE INTRINSIC SCREEN
    // =========================================================================
    if (showFilterSheet) {
        BackHandler { showFilterSheet = false }

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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalIconButton(
                            onClick = { showFilterSheet = false },
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
                            text = "Kişileri Filtrele",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    if (selectedFilter != FriendFilterOption.ALL) {
                        TextButton(
                            onClick = {
                                selectedFilter = FriendFilterOption.ALL
                                showFilterSheet = false
                            }
                        ) {
                            Text(
                                text = "Sıfırla",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                FriendFilterOption.entries.forEach { option ->
                    val isSelected = selectedFilter == option
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) PrimaryEmeraldContainer else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isSelected) PrimaryEmerald else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .bounceClick {
                                selectedFilter = option
                                showFilterSheet = false
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.label,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryEmerald else Color(0xFF0F172A)
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        return
    }


    // HAPTIC TOUCH / BASILI TUTMA HIZLI AKSİYON MENÜSÜ
    selectedFriendForQuickAction?.let { friend ->
        ContextualQuickActionSheet(
            title = friend.user.fullName,
            subtitle = friend.user.tag ?: friend.user.email,
            avatarText = friend.avatarEmoji,
            iban = friend.user.iban,
            onDismiss = { selectedFriendForQuickAction = null },
            onAddExpense = onAddExpense,
            onSettleUp = { onSettleUp(friend.user.id) },
            onViewDetail = { onFriendClick(friend.user.id) },
            onSendNudge = {
                onSendNudge(friend.user.id)
                Toast.makeText(context, "${friend.user.fullName} kişisine ödeme hatırlatması iletildi", Toast.LENGTH_SHORT).show()
            },
            onDelete = {
                friendsList.removeAll { it.user.id == friend.user.id }
                Toast.makeText(context, "${friend.user.fullName} listeden silindi", Toast.LENGTH_SHORT).show()
            }
        )
    }

    selectedNonMemberForInvite?.let { contact ->
        SmartInviteChannelSheet(
            contactName = contact.name,
            contactPhone = contact.phone,
            onDismiss = { selectedNonMemberForInvite = null }
        )
    }
}
