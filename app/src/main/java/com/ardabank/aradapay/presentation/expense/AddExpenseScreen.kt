package com.ardabank.aradapay.presentation.expense

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import com.ardabank.aradapay.presentation.components.bounceClick
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.data.repository.GroupRepository
import com.ardabank.aradapay.domain.model.ExpenseCategory
import com.ardabank.aradapay.domain.model.Group
import com.ardabank.aradapay.domain.model.SplitMethod
import com.ardabank.aradapay.presentation.common.BankContactPickerScreen
import com.ardabank.aradapay.presentation.common.BankContactPickerSheet
import com.ardabank.aradapay.presentation.components.ExpenseSplitPresetSelector
import com.ardabank.aradapay.presentation.common.IOSSegmentedControl
import com.ardabank.aradapay.presentation.common.applePressEffect
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer

data class CategoryUiItem(
    val category: ExpenseCategory,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class ExpenseParticipant(
    val id: String,
    val name: String,
    val tag: String,
    val avatar: String
)

class ItemizedBillEntry(
    val id: String,
    name: String,
    amount: String,
    val selectedMemberIds: androidx.compose.runtime.snapshots.SnapshotStateList<String>
) {
    var name by mutableStateOf(name)
    var amount by mutableStateOf(amount)
}

private enum class ExpenseScreenMode {
    NORMAL,
    CATEGORY,
    PARTICIPANT,
    SPLIT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    initialFriendName: String? = null,
    initialFriendId: String? = null,
    initialGroupId: String? = null,
    initialGroupName: String? = null,
    groupRepository: GroupRepository? = null,
    onSaveExpense: (amount: Double, description: String, category: ExpenseCategory, splitMethod: SplitMethod, selectedUserIds: List<String>) -> Unit = { _, _, _, _, _ -> },
    onCancel: () -> Unit = {}
) {
    val context = LocalContext.current
    var isParticipantDropdownOpen by remember { mutableStateOf(false) }
    var participantSearchQuery by remember { mutableStateOf("") }
    var showCategoryExpansion by remember { mutableStateOf(false) }
    var showSplitExpansion by remember { mutableStateOf(false) }

    BackHandler(enabled = isParticipantDropdownOpen || showCategoryExpansion || showSplitExpansion) {
        if (showCategoryExpansion) {
            showCategoryExpansion = false
        } else if (isParticipantDropdownOpen) {
            isParticipantDropdownOpen = false
        } else if (showSplitExpansion) {
            showSplitExpansion = false
        }
    }

    val allFriends = remember {
        mutableStateListOf(
            ExpenseParticipant("1", "Ahmet Yılmaz", "Ahmet#7821", "AY"),
            ExpenseParticipant("2", "Zeynep Kaya", "Zeynep#3412", "ZK"),
            ExpenseParticipant("3", "Mert Demir", "Mert#9015", "MD"),
            ExpenseParticipant("4", "Elif Şahin", "Elif#4420", "EŞ"),
            ExpenseParticipant("5", "Burak Öztürk", "Burak#6108", "BÖ"),
            ExpenseParticipant("6", "Selin Aydın", "Selin#2839", "SA"),
            ExpenseParticipant("7", "Caner Erkin", "Caner#1903", "CE"),
            ExpenseParticipant("8", "Deniz Çelik", "Deniz#5522", "DÇ"),
            ExpenseParticipant("9", "Adem Bal", "Adem#8440", "AD"),
            ExpenseParticipant("10", "Adil Kupan", "Adil#9120", "AK"),
            ExpenseParticipant("11", "Aslı Çelik", "Asli#3310", "AÇ")
        )
    }

    val loadedGroups = groupRepository?.groups?.collectAsState()?.value ?: emptyList()
    val availableGroups: List<Group> = remember(loadedGroups) {
        if (loadedGroups.isNotEmpty()) loadedGroups else listOf(
            Group(
                id = "group_demo_1",
                name = "Kaş Tatili 2026",
                category = "Seyahat",
                emoji = "🏖️",
                members = listOf(
                    com.ardabank.aradapay.domain.model.GroupMember("me", "Sen", "AP", "Sen"),
                    com.ardabank.aradapay.domain.model.GroupMember("1", "Ahmet Yılmaz", "AY", "Ahmet#7821"),
                    com.ardabank.aradapay.domain.model.GroupMember("2", "Zeynep Kaya", "ZK", "Zeynep#3412"),
                    com.ardabank.aradapay.domain.model.GroupMember("3", "Mert Demir", "MD", "Mert#9015")
                )
            ),
            Group(
                id = "group_demo_2",
                name = "Ev & Yaşam",
                category = "Konut",
                emoji = "🏠",
                members = listOf(
                    com.ardabank.aradapay.domain.model.GroupMember("me", "Sen", "AP", "Sen"),
                    com.ardabank.aradapay.domain.model.GroupMember("4", "Elif Şahin", "EŞ", "Elif#4420"),
                    com.ardabank.aradapay.domain.model.GroupMember("5", "Burak Öztürk", "BÖ", "Burak#6108")
                )
            ),
            Group(
                id = "group_demo_3",
                name = "Kadıköy Ekibi",
                category = "Eğlence",
                emoji = "🍕",
                members = listOf(
                    com.ardabank.aradapay.domain.model.GroupMember("me", "Sen", "AP", "Sen"),
                    com.ardabank.aradapay.domain.model.GroupMember("6", "Selin Aydın", "SA", "Selin#2839"),
                    com.ardabank.aradapay.domain.model.GroupMember("7", "Caner Erkin", "CE", "Caner#1903")
                )
            )
        )
    }

    val isInitiallyGroup = !initialGroupId.isNullOrBlank() ||
            !initialGroupName.isNullOrBlank() ||
            (initialFriendName != null && availableGroups.any { it.name.equals(initialFriendName, ignoreCase = true) })

    var splitMode by remember { mutableStateOf(if (isInitiallyGroup) 1 else 0) }

    var selectedGroup by remember {
        mutableStateOf<Group?>(
            if (!initialGroupId.isNullOrBlank()) {
                availableGroups.find { it.id == initialGroupId }
            } else if (!initialGroupName.isNullOrBlank()) {
                availableGroups.find { it.name.equals(initialGroupName, ignoreCase = true) }
            } else if (initialFriendName != null) {
                availableGroups.find { it.name.equals(initialFriendName, ignoreCase = true) }
            } else {
                null
            }
        )
    }

    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.DINING) }
    var selectedSplitMethod by remember { mutableStateOf(SplitMethod.EQUAL) }
    var includeMyselfInSplit by remember { mutableStateOf(true) }
    var selectedPayerId by remember { mutableStateOf("me") } // "me" or participant id
    var splitTabMode by remember { mutableStateOf(0) } // 0: Equal, 1: Exact, 2: Percentage, 3: Itemized
    val excludedInEqualSplit = remember { mutableStateListOf<String>() }
    val exactAmountsMap = remember { mutableStateMapOf<String, String>() }
    val percentageMap = remember { mutableStateMapOf<String, String>() }
    val itemizedBillList = remember {
        mutableStateListOf(
            ItemizedBillEntry(id = "item_1", name = "Ana Yemek", amount = "", selectedMemberIds = mutableStateListOf("me")),
            ItemizedBillEntry(id = "item_2", name = "İçecek & Meze", amount = "", selectedMemberIds = mutableStateListOf("me"))
        )
    }

    val initialSelected = remember {
        val set = mutableSetOf<String>()
        if (splitMode == 1 && selectedGroup != null) {
            set.addAll(selectedGroup!!.members.filter { it.id != "me" }.map { it.id })
        } else {
            if (!initialFriendId.isNullOrBlank()) {
                set.add(initialFriendId)
            } else if (!initialFriendName.isNullOrBlank() && !isInitiallyGroup) {
                val match = allFriends.find { it.name.contains(initialFriendName, ignoreCase = true) }
                if (match != null) set.add(match.id)
            }
        }
        set
    }

    val selectedFriendIds = remember { mutableStateListOf<String>().apply { addAll(initialSelected) } }

    val splitParticipants: List<ExpenseParticipant> = remember(selectedGroup, selectedFriendIds.toList(), allFriends.toList()) {
        val list = mutableListOf<ExpenseParticipant>()
        val seenIds = mutableSetOf<String>()

        // 1. Seçili gruptaki üyeler (varsa)
        if (selectedGroup != null) {
            selectedGroup!!.members.forEach { m ->
                if (m.id != "me" && !seenIds.contains(m.id)) {
                    seenIds.add(m.id)
                    val existing = allFriends.find { it.id == m.id }
                    if (existing != null) {
                        list.add(existing)
                    } else {
                        val newP = ExpenseParticipant(id = m.id, name = m.name, tag = m.tag, avatar = m.avatar)
                        list.add(newP)
                    }
                }
            }
        }

        // 2. Bireysel seçilen arkadaşlar (duplicate olmadan)
        selectedFriendIds.forEach { fId ->
            if (fId != "me" && !seenIds.contains(fId)) {
                seenIds.add(fId)
                val existing = allFriends.find { it.id == fId }
                if (existing != null) {
                    list.add(existing)
                }
            }
        }
        list
    }

    val currentPayerName = remember(selectedPayerId, splitParticipants, allFriends.size) {
        if (selectedPayerId == "me") "Sen" else (splitParticipants.find { it.id == selectedPayerId } ?: allFriends.find { it.id == selectedPayerId })?.name ?: "Arkadaş"
    }
    val currentPayerShortName = remember(selectedPayerId, splitParticipants, allFriends.size) {
        if (selectedPayerId == "me") "Sen" else (splitParticipants.find { it.id == selectedPayerId } ?: allFriends.find { it.id == selectedPayerId })?.name?.split(" ")?.first() ?: "Arkadaş"
    }

    val amountValue = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0

    val allParticipantIdsWithMe = remember(splitParticipants, includeMyselfInSplit) {
        (if (includeMyselfInSplit) listOf("me") else emptyList()) + splitParticipants.map { it.id }
    }

    val includedInEqualCount = remember(allParticipantIdsWithMe, excludedInEqualSplit.toList()) {
        allParticipantIdsWithMe.filter { !excludedInEqualSplit.contains(it) }.size.coerceAtLeast(1)
    }
    val dynamicEqualShare = if (amountValue > 0 && includedInEqualCount > 0) amountValue / includedInEqualCount else 0.0

    var selectedDetailedCategory by remember { mutableStateOf(ExpenseCategoryCatalog.findDefaultItemForCategory(selectedCategory)) }

    val currentMode = when {
        showCategoryExpansion -> ExpenseScreenMode.CATEGORY
        isParticipantDropdownOpen -> ExpenseScreenMode.PARTICIPANT
        showSplitExpansion -> ExpenseScreenMode.SPLIT
        else -> ExpenseScreenMode.NORMAL
    }

    val categoryChevronRotation by animateFloatAsState(
        targetValue = if (showCategoryExpansion) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "CategoryChevronRotation"
    )

    val isExpenseFormValid = amountValue > 0.0 && description.isNotBlank() && (splitParticipants.isNotEmpty() || selectedFriendIds.isNotEmpty())

    val executeSaveExpense: () -> Unit = {
        if (amountValue <= 0) {
            Toast.makeText(context, "Lütfen harcama tutarı giriniz", Toast.LENGTH_SHORT).show()
        } else if (description.isBlank()) {
            Toast.makeText(context, "Lütfen harcama açıklaması giriniz", Toast.LENGTH_SHORT).show()
        } else if (splitParticipants.isEmpty() && selectedFriendIds.isEmpty()) {
            Toast.makeText(context, "Lütfen en az bir kişi seçiniz", Toast.LENGTH_SHORT).show()
        } else {
            if (splitMode == 1 && selectedGroup != null && groupRepository != null) {
                groupRepository.addExpenseToGroup(
                    groupId = selectedGroup!!.id,
                    title = description.trim(),
                    amount = amountValue,
                    category = selectedCategory,
                    payerId = selectedPayerId,
                    payerName = currentPayerName,
                    participantIds = selectedFriendIds.toList(),
                    includeMyself = includeMyselfInSplit
                )
            }

            onSaveExpense(
                amountValue,
                description.trim(),
                selectedCategory,
                selectedSplitMethod,
                selectedFriendIds.toList()
            )

            Toast.makeText(context, "Harcama başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. TOP BAR (Matching Dashboard & App Standard) - ALWAYS FIXED
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(
                onClick = {
                    if (showCategoryExpansion) {
                        showCategoryExpansion = false
                    } else if (isParticipantDropdownOpen) {
                        isParticipantDropdownOpen = false
                    } else if (showSplitExpansion) {
                        showSplitExpansion = false
                    } else {
                        onCancel()
                    }
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color(0xFFF1F5F9)
                ),
                modifier = Modifier.size(40.dp).bounceClick {
                    if (showCategoryExpansion) {
                        showCategoryExpansion = false
                    } else if (isParticipantDropdownOpen) {
                        isParticipantDropdownOpen = false
                    } else if (showSplitExpansion) {
                        showSplitExpansion = false
                    } else {
                        onCancel()
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
                label = "TopBarTitleAnimation"
            ) { mode ->
                Text(
                    text = when (mode) {
                        ExpenseScreenMode.CATEGORY -> "Kategori Seç"
                        ExpenseScreenMode.PARTICIPANT -> "Kişi veya Grup Ekle"
                        ExpenseScreenMode.SPLIT -> "Ödeyen ve Bölüşüm"
                        ExpenseScreenMode.NORMAL -> "Harcama Ekle"
                    },
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (currentMode != ExpenseScreenMode.NORMAL) {
                FilledTonalIconButton(
                    onClick = {
                        showCategoryExpansion = false
                        isParticipantDropdownOpen = false
                        showSplitExpansion = false
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

        AnimatedContent(
            targetState = currentMode,
            transitionSpec = {
                if (targetState == ExpenseScreenMode.CATEGORY || targetState == ExpenseScreenMode.PARTICIPANT || targetState == ExpenseScreenMode.SPLIT) {
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
            label = "ExpenseScreenModeContentAnimation"
        ) { mode ->
            when (mode) {
                ExpenseScreenMode.CATEGORY -> {
                    // =========================================================================
                    // KATEGORİ SEÇİM MODU: SABİT BAŞLIK + BAĞIMSIZ KAYAN LİSTE
                    // =========================================================================
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "AÇIKLAMA & KATEGORİ",
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
                                    modifier = Modifier.bounceClick { showCategoryExpansion = false }
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = selectedDetailedCategory.bgTint,
                                        border = BorderStroke(1.5.dp, selectedDetailedCategory.iconTint),
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = selectedDetailedCategory.icon,
                                                contentDescription = selectedDetailedCategory.name,
                                                tint = selectedDetailedCategory.iconTint,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = selectedDetailedCategory.iconTint,
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
                                                    .graphicsLayer { rotationZ = categoryChevronRotation }
                                            )
                                        }
                                    }
                                }

                    Spacer(modifier = Modifier.width(14.dp))

                    BasicTextField(
                        value = description,
                        onValueChange = { description = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        ),
                        cursorBrush = SolidColor(PrimaryEmerald),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (description.isEmpty()) {
                                    Text(
                                        text = "Örn: ${selectedDetailedCategory.name}, Taksi, Akşam Yemeği...",
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

            // YÜKSEK PERFORMANSLI LAZY KATEGORİ LİSTESİ (ANINDA YÜKLENİR)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ExpenseCategoryCatalog.groups.forEach { group ->
                    item(key = "group_header_${group.id}") {
                        Text(
                            text = group.englishTitle,
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            modifier = Modifier.padding(start = 2.dp, top = 10.dp, bottom = 4.dp)
                        )
                    }

                    itemsIndexed(group.items, key = { _, item -> "cat_${item.id}" }) { idx, item ->
                        val isSelected = selectedDetailedCategory.id == item.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick {
                                    selectedDetailedCategory = item
                                    selectedCategory = item.parentCategory
                                    if (description.isBlank()) {
                                        description = item.name
                                    }
                                    showCategoryExpansion = false
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
                                    text = item.englishName,
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

                        if (idx < group.items.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 50.dp),
                                color = Color(0xFFF8FAFC),
                                thickness = 0.8.dp
                            )
                        }
                    }
                }

                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
    ExpenseScreenMode.PARTICIPANT -> {
        // =========================================================================
        // KİŞİ VE GRUP EKLEME TAM EKRAN MODU
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
                                        text = "Kişi veya grup ara (#tag)...",
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

            // 2. SEÇİLEN KİŞİLERİN VE GRUBUN KAPSÜLLERİ
            val selectedFriends = allFriends.filter { selectedFriendIds.contains(it.id) }
            if (selectedGroup != null || selectedFriends.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Seçili Grup Çipi (Grup seçiliyse sadece grup görünür)
                    if (selectedGroup != null) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFF3B82F6))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF3B82F6),
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (selectedGroup!!.emoji.isNotBlank()) selectedGroup!!.emoji else "👥",
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = selectedGroup!!.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Grubu Kaldır",
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            selectedGroup = null
                                            splitMode = 0
                                        }
                                )
                            }
                        }
                    } else {
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
                                        shape = RoundedCornerShape(6.dp),
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
                                                if (selectedPayerId == p.id) selectedPayerId = "me"
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 3. GRUP VE KİŞİ LİSTESİ
            val filteredGroups = availableGroups.filter { g ->
                participantSearchQuery.isBlank() ||
                g.name.contains(participantSearchQuery, ignoreCase = true) ||
                g.category.contains(participantSearchQuery, ignoreCase = true)
            }

            val filteredFriends = allFriends.filter { friend ->
                participantSearchQuery.isBlank() ||
                friend.name.contains(participantSearchQuery, ignoreCase = true) ||
                friend.tag.contains(participantSearchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
            ) {
                // ================= GRUPLAR BÖLÜMÜ =================
                if (filteredGroups.isNotEmpty()) {
                    item {
                        Text(
                            text = "GRUPLAR (${filteredGroups.size})",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                        )
                    }

                    items(filteredGroups, key = { "group_${it.id}" }) { group ->
                        val isGroupSelected = selectedGroup?.id == group.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick {
                                    if (isGroupSelected) {
                                        selectedGroup = null
                                        splitMode = 0
                                    } else {
                                        selectedGroup = group
                                        splitMode = 1
                                        selectedFriendIds.clear()
                                        // Gruptaki tüm üyeleri allFriends listesine ekle
                                        group.members.forEach { m ->
                                            if (m.id != "me" && allFriends.none { it.id == m.id }) {
                                                allFriends.add(ExpenseParticipant(m.id, m.name, m.tag, m.avatar))
                                            }
                                        }
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isGroupSelected) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                    border = if (isGroupSelected) BorderStroke(1.dp, Color(0xFF3B82F6)) else null,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (group.emoji.isNotBlank()) {
                                            Text(text = group.emoji, fontSize = 20.sp)
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Groups,
                                                contentDescription = null,
                                                tint = if (isGroupSelected) Color(0xFF2563EB) else Color(0xFF64748B),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = group.name,
                                        fontWeight = if (isGroupSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = if (isGroupSelected) Color(0xFF1D4ED8) else Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${group.members.size} Katılımcı • ${group.category}",
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            AnimatedContent(
                                targetState = isGroupSelected,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                                },
                                label = "GroupSelectionAnimation"
                            ) { selected ->
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Seçili",
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Circle,
                                        contentDescription = "Seçilmedi",
                                        tint = Color(0xFFCBD5E1),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 56.dp),
                            color = Color(0xFFF8FAFC),
                            thickness = 1.dp
                        )
                    }
                }

                // ================= KİŞİLER BÖLÜMÜ =================
                if (filteredFriends.isNotEmpty()) {
                    item {
                        Text(
                            text = "KİŞİLER (${filteredFriends.size})",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                        )
                    }

                    items(filteredFriends, key = { it.id }) { friend ->
                        val isSelected = selectedFriendIds.contains(friend.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick {
                                    if (isSelected) {
                                        selectedFriendIds.remove(friend.id)
                                        if (selectedPayerId == friend.id) selectedPayerId = "me"
                                    } else {
                                        selectedFriendIds.add(friend.id)
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = friend.avatar,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) PrimaryEmerald else Color(0xFF0F172A)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = friend.name,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = if (isSelected) PrimaryEmerald else Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = friend.tag,
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            AnimatedContent(
                                targetState = isSelected,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                                },
                                label = "SelectionAnimation"
                            ) { selected ->
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Seçili",
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Circle,
                                        contentDescription = "Seçilmedi",
                                        tint = Color(0xFFCBD5E1),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
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
    }
    ExpenseScreenMode.NORMAL -> {
        // =========================================================================
        // NORMAL HARCAMA FORMU MODU
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 2. KATILIMCI SEÇİM BARI (Sen Çipi + Arkadaş Çipleri + Arama/Ekle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Eğer grup seçiliyse SADECE grup çipi görünür
                    if (selectedGroup != null) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFF3B82F6)),
                            modifier = Modifier.bounceClick { isParticipantDropdownOpen = !isParticipantDropdownOpen }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF3B82F6), modifier = Modifier.size(20.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = if (selectedGroup!!.emoji.isNotBlank()) selectedGroup!!.emoji else "👥", fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = selectedGroup!!.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Grubu Kaldır",
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            selectedGroup = null
                                            splitMode = 0
                                        }
                                )
                            }
                        }
                    } else {
                        // Sabit "Sen" Çipi
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = PrimaryEmeraldContainer,
                            border = BorderStroke(1.dp, PrimaryEmerald),
                            modifier = Modifier.bounceClick { isParticipantDropdownOpen = !isParticipantDropdownOpen }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Surface(shape = CircleShape, color = PrimaryEmerald, modifier = Modifier.size(22.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "Sen", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Sen", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)
                            }
                        }

                        val selectedFriends = allFriends.filter { selectedFriendIds.contains(it.id) }
                        selectedFriends.forEach { p ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = PrimaryEmeraldContainer,
                                border = BorderStroke(1.dp, PrimaryEmerald),
                                modifier = Modifier.bounceClick { isParticipantDropdownOpen = !isParticipantDropdownOpen }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Surface(shape = CircleShape, color = PrimaryEmerald, modifier = Modifier.size(22.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = p.avatar, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = p.name.split(" ").first(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Kaldır",
                                        tint = PrimaryEmerald,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable {
                                                selectedFriendIds.remove(p.id)
                                                if (selectedPayerId == p.id) selectedPayerId = "me"
                                            }
                                    )
                                }
                            }
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
                }

                Spacer(modifier = Modifier.width(6.dp))

                FilledTonalIconButton(
                    onClick = { isParticipantDropdownOpen = !isParticipantDropdownOpen },
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

            // =========================================================================
            // 3. TUTAR GİRİŞİ (Kahraman Büyük Tutar & Hızlı Artırma Çipleri)
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "HARCAMA TUTARI",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = amountText,
                        onValueChange = { newText ->
                            val filtered = newText.filter { it.isDigit() || it == '.' || it == ',' }
                            val normalized = filtered.replace(',', '.')
                            if (normalized.count { it == '.' } <= 1) {
                                val parts = normalized.split('.')
                                if (parts.size == 1 || parts[1].length <= 2) {
                                    amountText = normalized
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryEmerald
                        ),
                        cursorBrush = SolidColor(PrimaryEmerald),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (amountText.isEmpty()) {
                                    Text(
                                        text = "0,00",
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier
                            .width(IntrinsicSize.Min)
                            .defaultMinSize(minWidth = if (amountText.isEmpty()) 76.dp else 24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₺",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryEmerald
                    )
                }

                // Hızlı Tutar Ekleme Çipleri
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    listOf(50, 100, 250, 500).forEach { addVal ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.bounceClick {
                                val current = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0
                                val next = current + addVal
                                amountText = if (next % 1.0 == 0.0) next.toLong().toString() else String.format(java.util.Locale.US, "%.2f", next)
                            }
                        ) {
                            Text(
                                text = "+$addVal ₺",
                                color = Color(0xFF475569),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // =========================================================================
            // 4. AÇIKLAMA & KATEGORİ ÇUBUĞU (Normal Görünüm)
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "AÇIKLAMA & KATEGORİ",
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
                            showCategoryExpansion = true
                            isParticipantDropdownOpen = false
                        }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = selectedDetailedCategory.bgTint,
                            border = BorderStroke(1.5.dp, selectedDetailedCategory.iconTint),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = selectedDetailedCategory.icon,
                                    contentDescription = selectedDetailedCategory.name,
                                    tint = selectedDetailedCategory.iconTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = selectedDetailedCategory.iconTint,
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
                                        .graphicsLayer { rotationZ = categoryChevronRotation }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    BasicTextField(
                        value = description,
                        onValueChange = { description = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0F172A)
                        ),
                        cursorBrush = SolidColor(PrimaryEmerald),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (description.isEmpty()) {
                                    Text(
                                        text = "Örn: ${selectedDetailedCategory.name}, Taksi, Akşam Yemeği...",
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

            // =========================================================================
            // 5. SPLITWISE BÖLÜŞÜM ÇUBUĞU (Düz, Kart İçi Kartsız Akış)
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "ÖDEYEN VE BÖLÜŞÜM ŞEKLİ",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showSplitExpansion = true
                            isParticipantDropdownOpen = false
                            showCategoryExpansion = false
                        }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val splitModeLabel = when (splitTabMode) {
                        0 -> "Eşit"
                        1 -> "Tutarlarla"
                        2 -> "Yüzdelerle"
                        3 -> "Hisselerle"
                        4 -> "Kalemlerle"
                        else -> "Eşit"
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ödeyen $currentPayerName • $splitModeLabel bölüşülecek",
                            color = Color(0xFF0F172A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (amountValue > 0 && selectedFriendIds.isNotEmpty()) {
                            val isMePaying = selectedPayerId == "me"
                            val statusColor = if (isMePaying) PrimaryEmerald else AccentRose

                            val summaryText = if (isMePaying) {
                                if (splitTabMode == 0) {
                                    val otherCount = selectedFriendIds.filter { !excludedInEqualSplit.contains(it) }.size
                                    val totalReceivable = dynamicEqualShare * otherCount
                                    "Sen ödedin, +${String.format(java.util.Locale.US, "%.2f", totalReceivable)} ₺ alacağın var"
                                } else {
                                    "Sen ödedin, ortaklar borçlandı"
                                }
                            } else {
                                val myShare = if (splitTabMode == 0) {
                                    if (excludedInEqualSplit.contains("me")) 0.0 else dynamicEqualShare
                                } else 0.0
                                if (myShare > 0) "$currentPayerShortName ödedi, -${String.format(java.util.Locale.US, "%.2f", myShare)} ₺ borcun var" else "$currentPayerShortName ödedi"
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = summaryText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = statusColor
                            )
                        }
                    }

                    FilledTonalIconButton(
                        onClick = {
                            showSplitExpansion = true
                            isParticipantDropdownOpen = false
                            showCategoryExpansion = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Bölüşüm Ayarları",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    ExpenseScreenMode.SPLIT -> {
        // =========================================================================
        // BÖLÜŞÜM MODU: TAM EKRANI KAPLAYAN ORGANİK GENİŞLEYEN GÖRÜNÜM
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. ÖDEYEN KİŞİ SEÇİCİ
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "ÖDEYEN KİŞİ",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sen
                    val isMe = selectedPayerId == "me"
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isMe) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                        border = if (isMe) BorderStroke(1.dp, PrimaryEmerald) else null,
                        modifier = Modifier.bounceClick { selectedPayerId = "me" }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isMe) PrimaryEmerald else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("SEN", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sen",
                                fontSize = 14.sp,
                                fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMe) PrimaryEmerald else Color(0xFF0F172A)
                            )
                        }
                    }

                    // Diğer Katılımcılar
                    splitParticipants.forEach { f ->
                        val isSelected = selectedPayerId == f.id
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                            border = if (isSelected) BorderStroke(1.dp, PrimaryEmerald) else null,
                            modifier = Modifier.bounceClick { selectedPayerId = f.id }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) PrimaryEmerald else Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(f.avatar, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = f.name.split(" ").first(),
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PrimaryEmerald else Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 2. BÖLÜŞÜM YÖNTEMİ
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "BÖLÜŞÜM YÖNTEMİ",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                IOSSegmentedControl(
                    items = listOf(
                        0 to "= Eşit",
                        1 to "123 Tutar",
                        2 to "% Yüzde",
                        3 to "🧾 Kalemler"
                    ),
                    selectedItem = splitTabMode,
                    onItemSelected = { index ->
                        splitTabMode = index
                        when (index) {
                            0 -> selectedSplitMethod = SplitMethod.EQUAL
                            1 -> selectedSplitMethod = SplitMethod.EXACT
                            2 -> selectedSplitMethod = SplitMethod.PERCENTAGE
                            3 -> selectedSplitMethod = SplitMethod.EXACT
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFFF1F5F9),
                    selectedBackgroundColor = Color.White,
                    selectedTextColor = Color(0xFF0F172A),
                    unselectedTextColor = Color(0xFF64748B)
                )

                // TAB 0: = EŞİT BÖLÜŞÜM
                if (splitTabMode == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Harcamaya dahil olan kişileri işaretleyin:",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )

                        // 1. Sen
                        val isMeIncluded = !excludedInEqualSplit.contains("me")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick {
                                    if (isMeIncluded) {
                                        excludedInEqualSplit.add("me")
                                        includeMyselfInSplit = false
                                    } else {
                                        excludedInEqualSplit.remove("me")
                                        includeMyselfInSplit = true
                                    }
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isMeIncluded) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("SEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isMeIncluded) PrimaryEmerald else Color(0xFF64748B))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Sen", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isMeIncluded && dynamicEqualShare > 0) {
                                    Text(
                                        text = "${String.format(java.util.Locale.US, "%.2f", dynamicEqualShare)} ₺",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PrimaryEmerald
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                                Icon(
                                    imageVector = if (isMeIncluded) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = if (isMeIncluded) PrimaryEmerald else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(start = 50.dp), color = Color(0xFFF8FAFC), thickness = 0.8.dp)

                        // 2. Katılımcılar
                        splitParticipants.forEach { friend ->
                            val isFriendIncluded = !excludedInEqualSplit.contains(friend.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .bounceClick {
                                        if (isFriendIncluded) {
                                            excludedInEqualSplit.add(friend.id)
                                        } else {
                                            excludedInEqualSplit.remove(friend.id)
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isFriendIncluded) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(friend.avatar, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isFriendIncluded) PrimaryEmerald else Color(0xFF64748B))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(friend.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isFriendIncluded && dynamicEqualShare > 0) {
                                        Text(
                                            text = "${String.format(java.util.Locale.US, "%.2f", dynamicEqualShare)} ₺",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = PrimaryEmerald
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
                                    Icon(
                                        imageVector = if (isFriendIncluded) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                        contentDescription = null,
                                        tint = if (isFriendIncluded) PrimaryEmerald else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(start = 50.dp), color = Color(0xFFF8FAFC), thickness = 0.8.dp)
                        }
                    }
                }

                // TAB 1: 123 TAM TUTARLAR
                if (splitTabMode == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Her kişinin ödeyeceği tam tutarı girin:",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )

                        // Sen
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("SEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Sen", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.width(110.dp).height(40.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BasicTextField(
                                        value = exactAmountsMap["me"] ?: "",
                                        onValueChange = { exactAmountsMap["me"] = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                        cursorBrush = SolidColor(PrimaryEmerald),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("₺", fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(start = 50.dp), color = Color(0xFFF8FAFC), thickness = 0.8.dp)

                        // Katılımcılar
                        splitParticipants.forEach { friend ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(friend.avatar, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(friend.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.width(110.dp).height(40.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BasicTextField(
                                            value = exactAmountsMap[friend.id] ?: "",
                                            onValueChange = { exactAmountsMap[friend.id] = it },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true,
                                            textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                            cursorBrush = SolidColor(PrimaryEmerald),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text("₺", fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(start = 50.dp), color = Color(0xFFF8FAFC), thickness = 0.8.dp)
                        }
                    }
                }

                // TAB 2: % YÜZDELERLE BÖLÜŞÜM
                if (splitTabMode == 2) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Her kişinin yüzdesini girin (Toplam %100 olmalı):",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )

                        // Sen
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("SEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Sen", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.width(85.dp).height(40.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BasicTextField(
                                        value = percentageMap["me"] ?: "",
                                        onValueChange = { percentageMap["me"] = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                        cursorBrush = SolidColor(PrimaryEmerald),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("%", fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(start = 50.dp), color = Color(0xFFF8FAFC), thickness = 0.8.dp)

                        // Katılımcılar
                        splitParticipants.forEach { friend ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(friend.avatar, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(friend.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.width(85.dp).height(40.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BasicTextField(
                                            value = percentageMap[friend.id] ?: "",
                                            onValueChange = { percentageMap[friend.id] = it },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                            cursorBrush = SolidColor(PrimaryEmerald),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text("%", fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(start = 50.dp), color = Color(0xFFF8FAFC), thickness = 0.8.dp)
                        }
                    }
                }

                // TAB 3: 🧾 KALEM KALEM FİŞ BÖLÜŞÜMÜ (Itemized Bill Split)
                if (splitTabMode == 3) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Fiş Kalemleri (${itemizedBillList.size})",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold
                            )

                            FilledTonalButton(
                                onClick = {
                                    itemizedBillList.add(
                                        ItemizedBillEntry(
                                            id = "item_${System.currentTimeMillis()}",
                                            name = "Kalem ${itemizedBillList.size + 1}",
                                            amount = "",
                                            selectedMemberIds = androidx.compose.runtime.mutableStateListOf("me")
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = PrimaryEmeraldContainer,
                                    contentColor = PrimaryEmerald
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.bounceClick {
                                    itemizedBillList.add(
                                        ItemizedBillEntry(
                                            id = "item_${System.currentTimeMillis()}",
                                            name = "Kalem ${itemizedBillList.size + 1}",
                                            amount = "",
                                            selectedMemberIds = androidx.compose.runtime.mutableStateListOf("me")
                                        )
                                    )
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryEmerald)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kalem Ekle", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)
                            }
                        }

                        itemizedBillList.forEachIndexed { itemIdx, itemEntry ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF8FAFC),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BasicTextField(
                                            value = itemEntry.name,
                                            onValueChange = { itemEntry.name = it },
                                            singleLine = true,
                                            textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                            cursorBrush = SolidColor(PrimaryEmerald),
                                            decorationBox = { inner ->
                                                if (itemEntry.name.isEmpty()) {
                                                    Text("Kalem adı...", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                                }
                                                inner()
                                            },
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White,
                                            modifier = Modifier.width(95.dp).height(36.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                BasicTextField(
                                                    value = itemEntry.amount,
                                                    onValueChange = { itemEntry.amount = it },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                    singleLine = true,
                                                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                                    cursorBrush = SolidColor(PrimaryEmerald),
                                                    decorationBox = { inner ->
                                                        if (itemEntry.amount.isEmpty()) {
                                                            Text("0,00", color = Color(0xFF94A3B8), fontSize = 13.sp)
                                                        }
                                                        inner()
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text("₺", fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        if (itemizedBillList.size > 1) {
                                            FilledTonalIconButton(
                                                onClick = { itemizedBillList.removeAt(itemIdx) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.Transparent),
                                                modifier = Modifier.size(32.dp).padding(start = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Sil", tint = AccentRose, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    Text(
                                        text = "Bu kalemi tüketenler:",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Medium
                                    )

                                    // Katılımcı Çipleri
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        item {
                                            val isAssigned = itemEntry.selectedMemberIds.contains("me")
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isAssigned) PrimaryEmeraldContainer else Color.White,
                                                border = if (isAssigned) BorderStroke(1.dp, PrimaryEmerald) else null,
                                                modifier = Modifier.bounceClick {
                                                    if (isAssigned) itemEntry.selectedMemberIds.remove("me")
                                                    else itemEntry.selectedMemberIds.add("me")
                                                }
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = if (isAssigned) PrimaryEmerald else Color(0xFF94A3B8),
                                                        modifier = Modifier.size(14.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text("S", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Sen",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isAssigned) PrimaryEmerald else Color(0xFF64748B)
                                                    )
                                                }
                                            }
                                        }

                                        items(splitParticipants) { friend ->
                                            val isAssigned = itemEntry.selectedMemberIds.contains(friend.id)
                                            val shortName = friend.name.split(" ").first()
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isAssigned) PrimaryEmeraldContainer else Color.White,
                                                border = if (isAssigned) BorderStroke(1.dp, PrimaryEmerald) else null,
                                                modifier = Modifier.bounceClick {
                                                    if (isAssigned) itemEntry.selectedMemberIds.remove(friend.id)
                                                    else itemEntry.selectedMemberIds.add(friend.id)
                                                }
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = if (isAssigned) PrimaryEmerald else Color(0xFF94A3B8),
                                                        modifier = Modifier.size(14.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(friend.avatar, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = shortName,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isAssigned) PrimaryEmerald else Color(0xFF64748B)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Kalemler toplam hesaplama butonu / özeti
                        val totalItemizedSum = itemizedBillList.sumOf { it.amount.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimaryEmeraldContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick {
                                    if (totalItemizedSum > 0) {
                                        amountText = String.format(java.util.Locale.US, "%.2f", totalItemizedSum)
                                        val memberCalculated = mutableMapOf<String, Double>()
                                        itemizedBillList.forEach { entry ->
                                            val entryAmt = entry.amount.replace(",", ".").toDoubleOrNull() ?: 0.0
                                            val consumers = entry.selectedMemberIds.toList().ifEmpty { listOf("me") }
                                            val perPerson = entryAmt / consumers.size
                                            consumers.forEach { uid ->
                                                memberCalculated[uid] = (memberCalculated[uid] ?: 0.0) + perPerson
                                            }
                                        }
                                        memberCalculated.forEach { (uid, amt) ->
                                            exactAmountsMap[uid] = String.format(java.util.Locale.US, "%.2f", amt)
                                        }
                                        Toast.makeText(context, "Kalemler harcamaya aktarıldı (${String.format(java.util.Locale.US, "%.2f", totalItemizedSum)} ₺)", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Kalemler Toplamı:", fontSize = 11.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${String.format(java.util.Locale.US, "%.2f", totalItemizedSum)} ₺", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryEmerald)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Toplamı Aktar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryEmerald)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}

        // FIXED BOTTOM CTA BUTTON
        AnimatedVisibility(
            visible = currentMode == ExpenseScreenMode.NORMAL,
            enter = fadeIn(animationSpec = tween(200)) + expandVertically(),
            exit = fadeOut(animationSpec = tween(150)) + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isExpenseFormValid) PrimaryEmerald else Color(0xFFE2E8F0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .then(
                            if (isExpenseFormValid) {
                                Modifier.bounceClick { executeSaveExpense() }
                            } else {
                                Modifier.clickable { executeSaveExpense() }
                            }
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isExpenseFormValid) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (amountValue > 0) "Harcamayı Kaydet (${String.format(java.util.Locale.US, "%.2f", amountValue)} ₺)" else "Harcamayı Kaydet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isExpenseFormValid) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}


