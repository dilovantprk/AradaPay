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

private enum class CreateGroupScreenMode {
    NORMAL,
    TYPE,
    PARTICIPANT
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
        val groupSections = remember {
            listOf(
                GroupTypeCategorySection(
                    title = "Ev & Yaşam",
                    items = listOf(
                        GroupTypeItem("home", "Ev & Yaşam", "Ev & Yaşam", Icons.Outlined.Home, Color(0xFF0D9488), Color(0xFFCCFBF1)),
                        GroupTypeItem("groceries", "Market & Ortak Alışveriş", "Ev & Yaşam", Icons.Default.ShoppingCart, Color(0xFF059669), Color(0xFFD1FAE5)),
                        GroupTypeItem("pets", "Evcil Hayvan", "Ev & Yaşam", Icons.Default.Pets, Color(0xFFEA580C), Color(0xFFFFEDD5)),
                        GroupTypeItem("family", "Aile & Çocuk", "Ev & Yaşam", Icons.Default.ChildCare, Color(0xFF0D9488), Color(0xFFCCFBF1))
                    )
                ),
                GroupTypeCategorySection(
                    title = "Seyahat & Ulaşım",
                    items = listOf(
                        GroupTypeItem("trip", "Seyahat & Tatil", "Seyahat", Icons.Outlined.Flight, Color(0xFF0284C7), Color(0xFFE0F2FE)),
                        GroupTypeItem("transport", "Yolculuk & Ulaşım", "Yolculuk", Icons.Outlined.DirectionsCar, Color(0xFF7C3AED), Color(0xFFEDE9FE)),
                        GroupTypeItem("hotel", "Otel & Konaklama", "Seyahat", Icons.Default.Hotel, Color(0xFF0891B2), Color(0xFFCFFAFE))
                    )
                ),
                GroupTypeCategorySection(
                    title = "Sosyal & Eğlence",
                    items = listOf(
                        GroupTypeItem("food", "Yemek & Restoran", "Yemek", Icons.Outlined.Restaurant, Color(0xFFD97706), Color(0xFFFEF3C7)),
                        GroupTypeItem("drinks", "Gece Hayatı & Bar", "Yemek", Icons.Default.LocalBar, Color(0xFFE11D48), Color(0xFFFFE4E6)),
                        GroupTypeItem("event", "Etkinlik & Parti", "Etkinlik", Icons.Outlined.Celebration, Color(0xFFDB2777), Color(0xFFFCE7F3)),
                        GroupTypeItem("music", "Konser & Festival", "Etkinlik", Icons.Default.MusicNote, Color(0xFF9333EA), Color(0xFFF3E8FF)),
                        GroupTypeItem("cinema", "Sinema & Dizi Gecesi", "Etkinlik", Icons.Default.Movie, Color(0xFFBE185D), Color(0xFFFCE7F3)),
                        GroupTypeItem("gifts", "Hediye & Kutlama", "Etkinlik", Icons.Default.CardGiftcard, Color(0xFFEC4899), Color(0xFFFCE7F3))
                    )
                ),
                GroupTypeCategorySection(
                    title = "Aktivite & Spor",
                    items = listOf(
                        GroupTypeItem("sports", "Spor & Halı Saha", "Etkinlik", Icons.Default.SportsSoccer, Color(0xFF16A34A), Color(0xFFDCFCE7)),
                        GroupTypeItem("fitness", "Fitness & Sağlık", "Diğer", Icons.Default.FitnessCenter, Color(0xFF0284C7), Color(0xFFE0F2FE)),
                        GroupTypeItem("gaming", "Oyun & Espor", "Etkinlik", Icons.Default.SportsEsports, Color(0xFF6366F1), Color(0xFFE0E7FF))
                    )
                ),
                GroupTypeCategorySection(
                    title = "İş & Diğer",
                    items = listOf(
                        GroupTypeItem("project", "Proje & Girişim", "Diğer", Icons.Outlined.Folder, Color(0xFF4F46E5), Color(0xFFE0E7FF)),
                        GroupTypeItem("work", "İş & Ofis Masrafları", "Diğer", Icons.Default.Work, Color(0xFF2563EB), Color(0xFFDBEAFE)),
                        GroupTypeItem("school", "Okul & Kurs", "Diğer", Icons.Default.School, Color(0xFFD97706), Color(0xFFFEF3C7)),
                        GroupTypeItem("other", "Diğer", "Diğer", Icons.Default.Category, Color(0xFF475569), Color(0xFFF1F5F9))
                    )
                )
            )
        }
        val groupTypes = remember(groupSections) { groupSections.flatMap { it.items } }

        var groupNameInput by remember { mutableStateOf("") }
        var selectedGroupType by remember { mutableStateOf(groupTypes.first()) }
        var showGroupTypeExpansion by remember { mutableStateOf(false) }
        var isParticipantDropdownOpen by remember { mutableStateOf(false) }
        var participantSearchQuery by remember { mutableStateOf("") }

        BackHandler(enabled = showGroupTypeExpansion || isParticipantDropdownOpen) {
            if (showGroupTypeExpansion) {
                showGroupTypeExpansion = false
            } else if (isParticipantDropdownOpen) {
                isParticipantDropdownOpen = false
            }
        }

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

        val currentMode = when {
            showGroupTypeExpansion -> CreateGroupScreenMode.TYPE
            isParticipantDropdownOpen -> CreateGroupScreenMode.PARTICIPANT
            else -> CreateGroupScreenMode.NORMAL
        }

        val groupTypeChevronRotation by animateFloatAsState(
            targetValue = if (showGroupTypeExpansion) 180f else 0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
            label = "GroupTypeChevronRotation"
        )

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
                    .imePadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. TOP APP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            if (showGroupTypeExpansion) {
                                showGroupTypeExpansion = false
                            } else if (isParticipantDropdownOpen) {
                                isParticipantDropdownOpen = false
                            } else {
                                showCreateGroupSheet = false
                            }
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier.size(40.dp).bounceClick {
                            if (showGroupTypeExpansion) {
                                showGroupTypeExpansion = false
                            } else if (isParticipantDropdownOpen) {
                                isParticipantDropdownOpen = false
                            } else {
                                showCreateGroupSheet = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AnimatedContent(
                        targetState = currentMode,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(220)) + slideInVertically { -it / 3 })
                                .togetherWith(fadeOut(animationSpec = tween(150)) + slideOutVertically { it / 3 })
                        },
                        label = "CreateGroupTitleAnimation"
                    ) { mode ->
                        Text(
                            text = when (mode) {
                                CreateGroupScreenMode.TYPE -> "Grup Türü Seç"
                                CreateGroupScreenMode.PARTICIPANT -> "Kişi Ekle"
                                CreateGroupScreenMode.NORMAL -> "Yeni Grup Oluştur"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    if (currentMode != CreateGroupScreenMode.NORMAL) {
                        FilledTonalIconButton(
                            onClick = {
                                showGroupTypeExpansion = false
                                isParticipantDropdownOpen = false
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = PrimaryEmeraldContainer
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Tamamla",
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // 2. MAIN EXPANDING/SHRINKING CONTENT
                AnimatedContent(
                    targetState = currentMode,
                    transitionSpec = {
                        if (targetState == CreateGroupScreenMode.TYPE || targetState == CreateGroupScreenMode.PARTICIPANT) {
                            (fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)) +
                                    slideInVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)) { it / 6 } +
                                    expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow), expandFrom = Alignment.Top))
                                .togetherWith(
                                    fadeOut(animationSpec = tween(140)) +
                                    shrinkVertically(animationSpec = tween(140), shrinkTowards = Alignment.Top)
                                )
                        } else {
                            (fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)) +
                                    slideInVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)) { -it / 6 } +
                                    expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow), expandFrom = Alignment.Top))
                                .togetherWith(
                                    fadeOut(animationSpec = tween(140)) +
                                    shrinkVertically(animationSpec = tween(140), shrinkTowards = Alignment.Top)
                                )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    label = "CreateGroupContentAnimation"
                ) { mode ->
                    when (mode) {
                        CreateGroupScreenMode.TYPE -> {
                            // =========================================================================
                            // GRUP TÜRÜ SEÇİM MODU: SABİT BAŞLIK + LİSTE
                            // =========================================================================
                            Column(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "GRUP ADI & KATEGORİ",
                                        color = Color(0xFF64748B),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.bounceClick { showGroupTypeExpansion = false }
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = selectedGroupType.bgTint,
                                                border = BorderStroke(1.5.dp, selectedGroupType.iconTint),
                                                modifier = Modifier.size(46.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = selectedGroupType.icon,
                                                        contentDescription = selectedGroupType.name,
                                                        tint = selectedGroupType.iconTint,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }

                                            Surface(
                                                shape = CircleShape,
                                                color = selectedGroupType.iconTint,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .align(Alignment.BottomEnd)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.KeyboardArrowDown,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .graphicsLayer { rotationZ = groupTypeChevronRotation }
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        BasicTextField(
                                            value = groupNameInput,
                                            onValueChange = { groupNameInput = it },
                                            singleLine = true,
                                            textStyle = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF0F172A)
                                            ),
                                            cursorBrush = SolidColor(PrimaryEmerald),
                                            decorationBox = { innerTextField ->
                                                Box(contentAlignment = Alignment.CenterStart) {
                                                    if (groupNameInput.isEmpty()) {
                                                        Text(
                                                            text = "Örn: ${selectedGroupType.name}, Kaş Tatili 2026...",
                                                            fontSize = 15.sp,
                                                            color = Color(0xFF94A3B8)
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    groupSections.forEach { section ->
                                        item(key = "group_section_${section.title}") {
                                            Text(
                                                text = section.title,
                                                color = Color(0xFF64748B),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.6.sp,
                                                modifier = Modifier.padding(start = 2.dp, top = 10.dp, bottom = 4.dp)
                                            )
                                        }

                                        itemsIndexed(section.items, key = { _, item -> "type_${item.id}" }) { idx, item ->
                                            val isSelected = selectedGroupType.id == item.id
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .bounceClick {
                                                        selectedGroupType = item
                                                        if (groupNameInput.isBlank()) {
                                                            groupNameInput = item.name
                                                        }
                                                        showGroupTypeExpansion = false
                                                    }
                                                    .padding(vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = item.bgTint,
                                                        modifier = Modifier.size(38.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Icon(
                                                                imageVector = item.icon,
                                                                contentDescription = item.name,
                                                                tint = item.iconTint,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.width(12.dp))

                                                    Text(
                                                        text = item.name,
                                                        color = if (isSelected) PrimaryEmerald else Color(0xFF0F172A),
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        fontSize = 15.sp
                                                    )
                                                }

                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "Seçili",
                                                        tint = PrimaryEmerald,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }

                                            if (idx < section.items.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(start = 50.dp),
                                                    color = Color(0xFFF8FAFC),
                                                    thickness = 0.8.dp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        CreateGroupScreenMode.PARTICIPANT -> {
                            // =========================================================================
                            // KATILIMCI SEÇİM MODU: TEMİZ ARAMA + SEÇİLEN KİŞİLER + CHECK LİSTESİ
                            // =========================================================================
                            Column(modifier = Modifier.fillMaxSize()) {
                                // 1. ARAMA ÇUBUĞU
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 10.dp),
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
                                                value = participantSearchQuery,
                                                onValueChange = { participantSearchQuery = it },
                                                singleLine = true,
                                                textStyle = TextStyle(
                                                    color = Color(0xFF0F172A),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                cursorBrush = SolidColor(PrimaryEmerald),
                                                decorationBox = { innerTextField ->
                                                    if (participantSearchQuery.isEmpty()) {
                                                        Text(
                                                            text = "Kişi ara veya #tag yaz...",
                                                            color = Color(0xFF94A3B8),
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                    innerTextField()
                                                },
                                                modifier = Modifier.weight(1f)
                                            )

                                            if (participantSearchQuery.isNotEmpty()) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Temizle",
                                                    tint = Color(0xFF64748B),
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .bounceClick { participantSearchQuery = "" }
                                                )
                                            }
                                        }
                                    }
                                }

                                // 2. SEÇİLEN KİŞİLERİN KAPSÜLLERİ
                                val selectedFriends = candidateFriends.filter { selectedFriendIds.contains(it.id) }
                                if (selectedFriends.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                            .padding(horizontal = 20.dp, vertical = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        selectedFriends.forEach { p ->
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = PrimaryEmeraldContainer,
                                                border = BorderStroke(1.dp, PrimaryEmerald)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = PrimaryEmerald,
                                                        modifier = Modifier.size(20.dp)
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
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                // 3. KİŞİ SEÇİM LİSTESİ
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                                ) {
                                    if (filteredCandidateFriends.isEmpty()) {
                                        item {
                                            Text(
                                                text = "Sonuç bulunamadı",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(vertical = 16.dp)
                                            )
                                        }
                                    }

                                    items(filteredCandidateFriends, key = { it.id }) { friend ->
                                        val isChecked = selectedFriendIds.contains(friend.id)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .bounceClick {
                                                    if (isChecked) {
                                                        selectedFriendIds.remove(friend.id)
                                                    } else {
                                                        selectedFriendIds.add(friend.id)
                                                    }
                                                }
                                                .padding(vertical = 12.dp),
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
                                                            text = friend.avatar,
                                                            color = if (isChecked) PrimaryEmerald else Color(0xFF0F172A),
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(14.dp))
                                                Column {
                                                    Text(
                                                        text = friend.name,
                                                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.SemiBold,
                                                        fontSize = 15.sp,
                                                        color = if (isChecked) PrimaryEmerald else Color(0xFF0F172A)
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = friend.tag,
                                                        color = Color(0xFF64748B),
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }

                                            Icon(
                                                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                                contentDescription = null,
                                                tint = if (isChecked) PrimaryEmerald else Color(0xFFCBD5E1),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        HorizontalDivider(
                                            modifier = Modifier.padding(start = 56.dp),
                                            color = Color(0xFFF8FAFC),
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                        }

                        CreateGroupScreenMode.NORMAL -> {
                            // =========================================================================
                            // NORMAL FORM MODU
                            // =========================================================================
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                // 1. GRUP ADI & KATEGORİ
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "GRUP ADI & KATEGORİ",
                                        color = Color(0xFF64748B),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.bounceClick {
                                                showGroupTypeExpansion = true
                                                isParticipantDropdownOpen = false
                                            }
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = selectedGroupType.bgTint,
                                                border = BorderStroke(1.5.dp, selectedGroupType.iconTint),
                                                modifier = Modifier.size(46.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = selectedGroupType.icon,
                                                        contentDescription = selectedGroupType.name,
                                                        tint = selectedGroupType.iconTint,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }

                                            Surface(
                                                shape = CircleShape,
                                                color = selectedGroupType.iconTint,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .align(Alignment.BottomEnd)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.KeyboardArrowDown,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .graphicsLayer { rotationZ = groupTypeChevronRotation }
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        BasicTextField(
                                            value = groupNameInput,
                                            onValueChange = { groupNameInput = it },
                                            singleLine = true,
                                            textStyle = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF0F172A)
                                            ),
                                            cursorBrush = SolidColor(PrimaryEmerald),
                                            decorationBox = { innerTextField ->
                                                Box(contentAlignment = Alignment.CenterStart) {
                                                    if (groupNameInput.isEmpty()) {
                                                        Text(
                                                            text = "Örn: ${selectedGroupType.name}, Kaş Tatili 2026...",
                                                            fontSize = 15.sp,
                                                            color = Color(0xFF94A3B8)
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                // 2. SENİNLE VE: (KATILIMCILAR)
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

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .horizontalScroll(rememberScrollState()),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val selectedFriends = candidateFriends.filter { selectedFriendIds.contains(it.id) }
                                        selectedFriends.forEach { p ->
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = PrimaryEmeraldContainer,
                                                border = BorderStroke(1.dp, PrimaryEmerald),
                                                modifier = Modifier.bounceClick {
                                                    isParticipantDropdownOpen = !isParticipantDropdownOpen
                                                    showGroupTypeExpansion = false
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
                                        }

                                        BasicTextField(
                                            value = participantSearchQuery,
                                            onValueChange = {
                                                participantSearchQuery = it
                                                if (it.isNotEmpty()) {
                                                    isParticipantDropdownOpen = true
                                                    showGroupTypeExpansion = false
                                                }
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
                                                            text = if (selectedFriendIds.isEmpty()) "Kişi ara..." else "Ekle...",
                                                            fontSize = 14.sp,
                                                            color = Color(0xFF94A3B8)
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            },
                                            modifier = Modifier.defaultMinSize(minWidth = 60.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    FilledTonalIconButton(
                                        onClick = {
                                            isParticipantDropdownOpen = !isParticipantDropdownOpen
                                            showGroupTypeExpansion = false
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = Color(0xFFF1F5F9)
                                        ),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isParticipantDropdownOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Seç",
                                            tint = Color(0xFF0F172A),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                // 3. FIXED BOTTOM CTA BUTTON
                AnimatedVisibility(
                    visible = currentMode == CreateGroupScreenMode.NORMAL,
                    enter = fadeIn(animationSpec = tween(200)) + expandVertically(),
                    exit = fadeOut(animationSpec = tween(150)) + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
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
                                        category = selectedGroupType.categoryKey,
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
                                .height(54.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Grubu Oluştur", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }
        }
        return
    }
}
