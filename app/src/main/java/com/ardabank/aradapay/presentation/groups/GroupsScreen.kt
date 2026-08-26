package com.ardabank.aradapay.presentation.groups

import android.widget.Toast
import androidx.activity.compose.BackHandler
import com.ardabank.aradapay.presentation.common.BankContactPickerScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.ardabank.aradapay.data.repository.GroupRepository
import com.ardabank.aradapay.domain.model.Group
import com.ardabank.aradapay.domain.model.GroupMember
import com.ardabank.aradapay.presentation.common.BankContactPickerSheet
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.expense.ExpenseParticipant
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import kotlin.math.abs

enum class GroupFilterOption(val title: String) {
    ALL("Tüm gruplar"),
    OUTSTANDING("Açık bakiyeli gruplar"),
    YOU_OWE("Borçlu olduğun gruplar"),
    OWED_TO_YOU("Alacaklı olduğun gruplar")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    groupRepository: GroupRepository,
    onGroupClick: (groupId: String) -> Unit = {},
    onAddExpenseInGroup: (groupName: String) -> Unit = {},
    onAddExpense: () -> Unit = {}
) {
    val context = LocalContext.current
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(GroupFilterOption.ALL) }
    var showCreateGroupSheet by remember { mutableStateOf(false) }

    val groups by groupRepository.groups.collectAsState()

    val totalGroupReceivables = groups.filter { it.userBalance > 0 }.sumOf { it.userBalance }
    val totalGroupPayables = groups.filter { it.userBalance < 0 }.sumOf { abs(it.userBalance) }
    val overallGroupNet = totalGroupReceivables - totalGroupPayables

    val filteredGroups = remember(groups, searchQuery, selectedFilter) {
        groups.filter { group ->
            val matchesSearch = searchQuery.isBlank() ||
                    group.name.contains(searchQuery, ignoreCase = true) ||
                    group.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                GroupFilterOption.ALL -> true
                GroupFilterOption.OUTSTANDING -> group.userBalance != 0.0
                GroupFilterOption.YOU_OWE -> group.userBalance < 0
                GroupFilterOption.OWED_TO_YOU -> group.userBalance > 0
            }

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onAddExpense() },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = {
                    Text(
                        text = "Harcama ekle",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                },
                containerColor = PrimaryEmerald,
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
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
            // 1. TOP APP BAR (Title + Action Buttons)
            // =========================================================================
            if (isSearchActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        isSearchActive = false
                        searchQuery = ""
                    }) {
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
                                text = "Grup adı veya kategori ara...",
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
                        text = "Gruplar",
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
                                containerColor = if (selectedFilter != GroupFilterOption.ALL) PrimaryEmeraldContainer else Color(0xFFF1F5F9)
                            ),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filtrele",
                                tint = if (selectedFilter != GroupFilterOption.ALL) PrimaryEmerald else Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        FilledTonalIconButton(
                            onClick = { showCreateGroupSheet = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GroupAdd,
                                contentDescription = "Yeni Grup",
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
                    text = "TOPLAM GRUP BAKİYESİ",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                val netStatusColor = when {
                    overallGroupNet > 0 -> PrimaryEmerald
                    overallGroupNet < 0 -> AccentRose
                    else -> Color(0xFF64748B)
                }

                val netText = when {
                    overallGroupNet > 0 -> "+${String.format(java.util.Locale.US, "%.2f", overallGroupNet)} ₺ Alacak"
                    overallGroupNet < 0 -> "${String.format(java.util.Locale.US, "%.2f", abs(overallGroupNet))} ₺ Borç"
                    else -> "Dengede"
                }

                Text(
                    text = netText,
                    color = netStatusColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(filteredGroups, key = { _, group -> group.id }) { index, group ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGroupClick(group.id) }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            val categoryIcon = when (group.category) {
                                "Ev & Yaşam" -> Icons.Outlined.Home
                                "Seyahat" -> Icons.Outlined.Flight
                                "Yemek" -> Icons.Outlined.Restaurant
                                "Yolculuk" -> Icons.Outlined.DirectionsCar
                                "Etkinlik" -> Icons.Outlined.Celebration
                                else -> Icons.Outlined.Folder
                            }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = categoryIcon,
                                        contentDescription = group.category,
                                        tint = Color(0xFF0F172A),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = group.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${group.members.size} üye • ${group.category}",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            if (group.userBalance == 0.0) {
                                Text(
                                    text = "fitleşildi",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            } else if (group.userBalance > 0) {
                                Text(
                                    text = "sana borçlu",
                                    color = PrimaryEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "+${String.format(java.util.Locale.US, "%.2f", group.userBalance)} ₺",
                                    color = PrimaryEmerald,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "sen borçlusun",
                                    color = AccentRose,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.2f", abs(group.userBalance))} ₺",
                                    color = AccentRose,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (index < filteredGroups.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 78.dp),
                            color = Color(0xFFF1F5F9),
                            thickness = 0.8.dp
                        )
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
                            text = "Grupları Filtrele",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    if (selectedFilter != GroupFilterOption.ALL) {
                        TextButton(
                            onClick = {
                                selectedFilter = GroupFilterOption.ALL
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

                GroupFilterOption.entries.forEach { option ->
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
                                text = option.title,
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

    if (showCreateGroupSheet) {
        BackHandler { showCreateGroupSheet = false }

        var groupNameInput by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("Ev & Yaşam") }
        val categoryIcons = listOf(
            "Ev & Yaşam" to Icons.Outlined.Home,
            "Seyahat" to Icons.Outlined.Flight,
            "Yemek" to Icons.Outlined.Restaurant,
            "Yolculuk" to Icons.Outlined.DirectionsCar,
            "Etkinlik" to Icons.Outlined.Celebration,
            "Diğer" to Icons.Outlined.Folder
        )

        val candidateFriends = listOf(
            ExpenseParticipant("1", "Ahmet Yılmaz", "Ahmet#7821", "AY"),
            ExpenseParticipant("2", "Zeynep Kaya", "Zeynep#3412", "ZK"),
            ExpenseParticipant("3", "Mert Demir", "Mert#9015", "MD"),
            ExpenseParticipant("4", "Elif Şahin", "Elif#4420", "EŞ"),
            ExpenseParticipant("5", "Burak Öztürk", "Burak#6108", "BÖ"),
            ExpenseParticipant("6", "Selin Aydın", "Selin#2839", "SA"),
            ExpenseParticipant("7", "Caner Erkin", "Caner#1903", "CE"),
            ExpenseParticipant("8", "Deniz Çelik", "Deniz#5522", "DÇ")
        )
        val selectedFriendIds = remember { mutableStateListOf("1", "2") }
        var isParticipantDropdownOpen by remember { mutableStateOf(false) }
        var participantSearchQuery by remember { mutableStateOf("") }

        val filteredCandidateFriends = remember(candidateFriends, participantSearchQuery) {
            if (participantSearchQuery.isBlank()) candidateFriends
            else candidateFriends.filter {
                it.name.contains(participantSearchQuery, ignoreCase = true) ||
                it.tag.contains(participantSearchQuery, ignoreCase = true)
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                            onClick = { showCreateGroupSheet = false },
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
                            text = "Yeni Grup Oluştur",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (groupNameInput.isNotBlank()) PrimaryEmerald else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .size(38.dp)
                                .bounceClick {
                                    if (groupNameInput.isNotBlank()) {
                                        val newMembers = listOf(
                                            GroupMember(id = "me", name = "Sen", avatar = "Sen", tag = "@me", balanceInGroup = 0.0)
                                        ) + selectedFriendIds.map { id ->
                                            val f = candidateFriends.firstOrNull { it.id == id }
                                            GroupMember(
                                                id = id,
                                                name = f?.name ?: "Arkadaş",
                                                avatar = f?.avatar ?: f?.name?.take(2)?.uppercase() ?: "AR",
                                                tag = f?.tag ?: "@arkadas",
                                                balanceInGroup = 0.0
                                            )
                                        }
                                        val created = groupRepository.createGroup(
                                            name = groupNameInput.trim(),
                                            emoji = "",
                                            category = selectedCategory,
                                            members = newMembers
                                        )
                                        showCreateGroupSheet = false
                                        Toast.makeText(context, "'${created.name}' grubu oluşturuldu", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Lütfen grup adı girin", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Kaydet",
                                    tint = if (groupNameInput.isNotBlank()) Color.White else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // 2. KİMLERLE BÖLÜŞÜLECEK / KATILIMCILAR (Seninle ve: + Kapsül Çipler + Arama + Aç/Kapat Butonu)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Seninle ve:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontSize = 15.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Admin Çipi (Sen)
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = "Sen", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Sen", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Seçili Katılımcı Kapsül Çipleri
                            val selectedFriends = candidateFriends.filter { selectedFriendIds.contains(it.id) }
                            selectedFriends.forEach { p ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = PrimaryEmeraldContainer,
                                    border = BorderStroke(1.dp, PrimaryEmerald),
                                    modifier = Modifier.bounceClick {
                                        isParticipantDropdownOpen = !isParticipantDropdownOpen
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = PrimaryEmerald,
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = p.avatar,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
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

                                        Spacer(modifier = Modifier.width(4.dp))

                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Kaldır",
                                            tint = PrimaryEmerald,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable {
                                                    selectedFriendIds.remove(p.id)
                                                }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            BasicTextField(
                                value = participantSearchQuery,
                                onValueChange = {
                                    participantSearchQuery = it
                                    if (it.isNotEmpty()) isParticipantDropdownOpen = true
                                },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(PrimaryEmerald),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (participantSearchQuery.isEmpty()) {
                                            Text(
                                                text = if (selectedFriendIds.isEmpty()) "Gruba kişi ekle..." else "Kişi ara...",
                                                fontSize = 14.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            FilledTonalIconButton(
                                onClick = { isParticipantDropdownOpen = !isParticipantDropdownOpen },
                                shape = RoundedCornerShape(10.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = if (isParticipantDropdownOpen) PrimaryEmeraldContainer else Color(0xFFF1F5F9)
                                ),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (isParticipantDropdownOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Kişi Seç",
                                    tint = if (isParticipantDropdownOpen) PrimaryEmerald else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // ALTA DOĞRU GENİŞLEYEN KİŞİLER LİSTESİ (AnimatedVisibility)
                        AnimatedVisibility(
                            visible = isParticipantDropdownOpen,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "KİŞİLER (${candidateFriends.size})",
                                    color = Color(0xFF64748B),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )

                                filteredCandidateFriends.forEach { contact ->
                                    val isChecked = selectedFriendIds.contains(contact.id)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .bounceClick {
                                                if (isChecked) {
                                                    selectedFriendIds.remove(contact.id)
                                                } else {
                                                    selectedFriendIds.add(contact.id)
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
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (isChecked) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                                modifier = Modifier.size(38.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = contact.avatar,
                                                        color = if (isChecked) PrimaryEmerald else Color(0xFF0F172A),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Text(
                                                    text = contact.name,
                                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.SemiBold,
                                                    fontSize = 14.sp,
                                                    color = if (isChecked) PrimaryEmerald else Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = contact.tag,
                                                    color = Color(0xFF64748B),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        if (isChecked) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Seçili",
                                                tint = PrimaryEmerald,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Outlined.Circle,
                                                contentDescription = null,
                                                tint = Color(0xFFCBD5E1),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // 3. GRUP ADI
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "GRUP ADI",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        OutlinedTextField(
                            value = groupNameInput,
                            onValueChange = { groupNameInput = it },
                            placeholder = { Text("örn: Kaş Tatili 2026", color = Color(0xFF94A3B8)) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryEmerald,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // 4. GRUP TÜRÜ
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "GRUP TÜRÜ",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoryIcons.forEach { (cat, iconVector) ->
                                val isSelected = selectedCategory == cat
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                    border = if (isSelected) BorderStroke(1.dp, PrimaryEmerald) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .bounceClick { selectedCategory = cat }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = iconVector,
                                            contentDescription = cat,
                                            tint = if (isSelected) PrimaryEmerald else Color(0xFF0F172A),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. BOTTOM SUBMIT BUTTON
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = {
                            if (groupNameInput.isNotBlank()) {
                                val newMembers = listOf(
                                    GroupMember(id = "me", name = "Sen", avatar = "Sen", tag = "@me", balanceInGroup = 0.0)
                                ) + selectedFriendIds.map { id ->
                                    val f = candidateFriends.firstOrNull { it.id == id }
                                    GroupMember(
                                        id = id,
                                        name = f?.name ?: "Arkadaş",
                                        avatar = f?.avatar?.ifBlank { f.name.take(2).uppercase() } ?: "AR",
                                        tag = f?.tag ?: "@arkadas",
                                        balanceInGroup = 0.0
                                    )
                                }
                                val created = groupRepository.createGroup(
                                    name = groupNameInput.trim(),
                                    emoji = "",
                                    category = selectedCategory,
                                    members = newMembers
                                )
                                showCreateGroupSheet = false
                                Toast.makeText(context, "'${created.name}' grubu oluşturuldu", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Lütfen grup adı girin", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Grubu Oluştur", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
        return
    }
}
