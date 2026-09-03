package com.ardabank.aradapay.presentation.groups

import android.widget.Toast
import androidx.activity.compose.BackHandler
import com.ardabank.aradapay.presentation.common.BankContactPickerScreen
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.repository.GroupRepository
import com.ardabank.aradapay.domain.model.Group
import com.ardabank.aradapay.domain.model.GroupMember
import com.ardabank.aradapay.presentation.common.BankContactPickerSheet
import com.ardabank.aradapay.presentation.common.ContextualQuickActionSheet
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.components.hapticCombinedClickable
import com.ardabank.aradapay.presentation.expense.ExpenseParticipant
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import kotlin.math.abs

enum class GroupFilterOption(val title: String) {
    ALL("Tüm Gruplar"),
    OUTSTANDING("Hesabı Açık Kalanlar"),
    YOU_OWE("Payıma Düşen Masalar (-₺)"),
    OWED_TO_YOU("Masayı Üstlendiklerim (+₺)")
}

data class GroupTypeItem(
    val id: String,
    val name: String,
    val categoryKey: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconTint: Color,
    val bgTint: Color
)

data class GroupTypeCategorySection(
    val title: String,
    val items: List<GroupTypeItem>
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    groupRepository: GroupRepository,
    onGroupClick: (groupId: String) -> Unit = {},
    onAddExpenseInGroup: (groupName: String) -> Unit = {},
    onAddExpense: () -> Unit = {},
    onCreateGroupClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(GroupFilterOption.ALL) }
    var showCreateGroupSheet by remember { mutableStateOf(false) }
    var selectedGroupForQuickAction by remember { mutableStateOf<Group?>(null) }

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
                            onClick = {
                                onCreateGroupClick()
                            },
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
                    overallGroupNet > 0 -> "+${String.format(java.util.Locale.US, "%.2f", overallGroupNet)} ₺ Masada Payın Var"
                    overallGroupNet < 0 -> "${String.format(java.util.Locale.US, "%.2f", abs(overallGroupNet))} ₺ Payına Düşen"
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (filteredGroups.isEmpty()) {
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
                                        imageVector = Icons.Default.GroupAdd,
                                        contentDescription = null,
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Masa bomboş.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (searchQuery.isNotBlank() || selectedFilter != GroupFilterOption.ALL) "Arama veya filtre kriterine uygun grup masası bulunamadı." else "Henüz bir grup masası açılmamış. Yeni bir masa kurup arkadaşlarınla bölüşmeye başla!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(filteredGroups, key = { it.id }) { group ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .hapticCombinedClickable(
                                    onClick = { onGroupClick(group.id) },
                                    onLongClick = { selectedGroupForQuickAction = group }
                                )
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (group.emoji != null && group.emoji.isNotBlank()) {
                                            Text(text = group.emoji, fontSize = 22.sp)
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Groups,
                                                contentDescription = null,
                                                tint = Color(0xFF475569),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
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
                                        text = "${group.members.size} Üye • ${group.category}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                if (abs(group.userBalance) < 0.01) {
                                    Text(
                                        text = "ödeştik",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                } else if (group.userBalance > 0) {
                                    Text(
                                        text = "masadan payı var",
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
                                        text = "masaya payın var",
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
        CreateGroupScreen(
            groupRepository = groupRepository,
            onBackClick = { showCreateGroupSheet = false },
            onGroupCreated = { newGroupId ->
                showCreateGroupSheet = false
                onGroupClick(newGroupId)
            }
        )
        return
    }

    // GRUP BASILI TUTMA HIZLI AKSİYON MENÜSÜ
    selectedGroupForQuickAction?.let { group ->
        ContextualQuickActionSheet(
            title = group.name,
            deleteText = "Gruptan Ayrıl / Sil",
            onDismiss = { selectedGroupForQuickAction = null },
            onAddExpense = { onAddExpenseInGroup(group.name) },
            onSettleUp = { onGroupClick(group.id) },
            onViewDetail = { onGroupClick(group.id) },
            onDelete = {
                Toast.makeText(context, "${group.name} grubundan ayrılındı", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
