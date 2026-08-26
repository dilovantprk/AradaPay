package com.ardabank.aradapay.presentation.expense

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Circle
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

    val availableGroups: List<Group> = groupRepository?.groups?.collectAsState()?.value ?: emptyList()

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
                availableGroups.firstOrNull()
            }
        )
    }

    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.DINING) }
    var selectedSplitMethod by remember { mutableStateOf(SplitMethod.EQUAL) }
    var includeMyselfInSplit by remember { mutableStateOf(true) }
    var selectedPayerId by remember { mutableStateOf("me") } // "me" or participant id
    var showPayerSheet by remember { mutableStateOf(false) }
    var showSplitOptionsSheet by remember { mutableStateOf(false) }
    var splitTabMode by remember { mutableStateOf(0) } // 0: Equal, 1: Exact, 2: Percentage, 3: Shares
    val excludedInEqualSplit = remember { mutableStateListOf<String>() }
    val exactAmountsMap = remember { mutableStateMapOf<String, String>() }
    val percentageMap = remember { mutableStateMapOf<String, String>() }
    val sharesMap = remember { mutableStateMapOf<String, Int>() }

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

    val currentPayerName = remember(selectedPayerId, allFriends.size) {
        if (selectedPayerId == "me") "Sen" else allFriends.find { it.id == selectedPayerId }?.name ?: "Arkadaş"
    }
    val currentPayerShortName = remember(selectedPayerId, allFriends.size) {
        if (selectedPayerId == "me") "Sen" else allFriends.find { it.id == selectedPayerId }?.name?.split(" ")?.first() ?: "Arkadaş"
    }

    val amountValue = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0

    val allParticipantIdsWithMe = remember(selectedFriendIds.toList(), includeMyselfInSplit) {
        (if (includeMyselfInSplit) listOf("me") else emptyList()) + selectedFriendIds
    }

    val includedInEqualCount = remember(allParticipantIdsWithMe, excludedInEqualSplit.toList()) {
        allParticipantIdsWithMe.filter { !excludedInEqualSplit.contains(it) }.size.coerceAtLeast(1)
    }
    val dynamicEqualShare = if (amountValue > 0 && includedInEqualCount > 0) amountValue / includedInEqualCount else 0.0

    var selectedDetailedCategory by remember { mutableStateOf(ExpenseCategoryCatalog.findDefaultItemForCategory(selectedCategory)) }

    val isExpenseFormValid = amountValue > 0.0 && description.isNotBlank() && selectedFriendIds.isNotEmpty()

    val executeSaveExpense: () -> Unit = {
        if (amountValue <= 0) {
            Toast.makeText(context, "Lütfen harcama tutarı giriniz", Toast.LENGTH_SHORT).show()
        } else if (description.isBlank()) {
            Toast.makeText(context, "Lütfen harcama açıklaması giriniz", Toast.LENGTH_SHORT).show()
        } else if (selectedFriendIds.isEmpty()) {
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
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 1. TOP BAR (Matching Dashboard & App Standard)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = onCancel,
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

                Text(
                    text = "Harcama Ekle",
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isExpenseFormValid) PrimaryEmerald else Color(0xFFF1F5F9),
                    modifier = Modifier
                        .size(38.dp)
                        .bounceClick { if (isExpenseFormValid) executeSaveExpense() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Kaydet",
                            tint = if (isExpenseFormValid) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // 2. KATILIMCI SEÇİM BARI (Seninle ve: + Kapsül Çipler + Arama + Aç/Kapat Butonu)
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

                    // Seçili Katılımcı Kapsül Çipleri
                    val selectedFriends = allFriends.filter { selectedFriendIds.contains(it.id) }
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
                                            if (selectedPayerId == p.id) selectedPayerId = "me"
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
                                        text = if (selectedFriendIds.isEmpty()) "Kişi seç veya ara..." else "Kişi ara...",
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
                    val filteredFriends = allFriends.filter {
                        participantSearchQuery.isBlank() ||
                        it.name.contains(participantSearchQuery, ignoreCase = true) ||
                        it.tag.contains(participantSearchQuery, ignoreCase = true)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "KİŞİLER (${allFriends.size})",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        filteredFriends.forEach { contact ->
                            val isChecked = selectedFriendIds.contains(contact.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .bounceClick {
                                        if (isChecked) {
                                            selectedFriendIds.remove(contact.id)
                                            if (selectedPayerId == contact.id) selectedPayerId = "me"
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

            // 2. HERO AMOUNT SECTION
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = amountText,
                        onValueChange = { input ->
                            val clean = input.replace(",", ".")
                            if (clean.isEmpty() || clean.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                amountText = input
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        ),
                        cursorBrush = SolidColor(PrimaryEmerald),
                        decorationBox = { innerTextField ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (amountText.isEmpty()) {
                                    Text(
                                        text = "0,00",
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                                innerTextField()
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "₺",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryEmerald
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    listOf(50, 100, 250, 500).forEach { inc ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.bounceClick {
                                val current = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0
                                val updated = current + inc
                                amountText = if (updated % 1.0 == 0.0) updated.toLong().toString() else String.format(java.util.Locale.US, "%.2f", updated)
                            }
                        ) {
                            Text(
                                text = "+$inc ₺",
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

            // 3. AÇIKLAMA & KATEGORİ SEÇİCİ (Genişleme Animasyonlu & Zengin Kategori Kataloğu)
            var showCategoryExpansion by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AÇIKLAMA & KATEGORİ",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Text(
                        text = selectedDetailedCategory.name,
                        color = PrimaryEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (showCategoryExpansion) PrimaryEmeraldContainer else selectedDetailedCategory.bgTint,
                        border = if (showCategoryExpansion) BorderStroke(1.dp, PrimaryEmerald) else null,
                        modifier = Modifier
                            .size(46.dp)
                            .bounceClick { showCategoryExpansion = !showCategoryExpansion }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = selectedDetailedCategory.icon,
                                contentDescription = selectedDetailedCategory.name,
                                tint = if (showCategoryExpansion) PrimaryEmerald else selectedDetailedCategory.iconTint,
                                modifier = Modifier.size(24.dp)
                            )
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

                // Alta Doğru Genişleyen Zengin Kategori Seçici (AnimatedVisibility)
                AnimatedVisibility(
                    visible = showCategoryExpansion,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ExpenseCategoryCatalog.groups.forEach { group ->
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Grup Başlığı (Fotoğraftaki gibi sade ve net)
                                    Text(
                                        text = group.englishTitle,
                                        color = Color(0xFF64748B),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp,
                                        modifier = Modifier.padding(start = 2.dp, top = 4.dp, bottom = 2.dp)
                                    )

                                    // Grup İçindeki Kategoriler
                                    group.items.forEachIndexed { idx, item ->
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
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // =========================================================================
            // 4. SPLITWISE INTERAKTİF BÖLÜŞÜM ÇUBUĞU ("Ödeyen [Sen] ve [Eşit] bölüşülecek")
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ÖDEYEN VE BÖLÜŞÜM ŞEKLİ",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                // Splitwise Cümle Butonu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Ödeyen ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F172A)
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryEmeraldContainer,
                        border = BorderStroke(1.dp, PrimaryEmerald),
                        modifier = Modifier.bounceClick { showPayerSheet = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (selectedPayerId == "me") "Sen" else currentPayerShortName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = " ve ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    val splitMethodSummary = when (splitTabMode) {
                        0 -> if (excludedInEqualSplit.isEmpty()) "Eşit" else "Eşit (${includedInEqualCount} kişi)"
                        1 -> "Tam Tutarlarla"
                        2 -> "Yüzdelerle"
                        3 -> "Paylarla"
                        else -> "Eşit"
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryEmeraldContainer,
                        border = BorderStroke(1.dp, PrimaryEmerald),
                        modifier = Modifier.bounceClick { showSplitOptionsSheet = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = splitMethodSummary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "bölüşülecek.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F172A)
                    )
                }

                // Splitwise Net Bakiye Özeti
                if (amountValue > 0 && selectedFriendIds.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
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
                            val isMePaying = selectedPayerId == "me"
                            val statusColor = if (isMePaying) PrimaryEmerald else AccentRose

                            val summaryText = if (isMePaying) {
                                if (splitTabMode == 0) {
                                    val otherCount = selectedFriendIds.filter { !excludedInEqualSplit.contains(it) }.size
                                    val totalReceivable = dynamicEqualShare * otherCount
                                    "Sen ödedin, ${String.format(java.util.Locale.US, "%.2f", totalReceivable)} ₺ alacağın var"
                                } else {
                                    "Sen ödedin, ortaklar borçlandı"
                                }
                            } else {
                                val myShare = if (splitTabMode == 0) {
                                    if (excludedInEqualSplit.contains("me")) 0.0 else dynamicEqualShare
                                } else 0.0
                                if (myShare > 0) "$currentPayerShortName ödedi, ${String.format(java.util.Locale.US, "%.2f", myShare)} ₺ borcun var" else "$currentPayerShortName ödedi"
                            }

                            Text(
                                text = summaryText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )

                            if (splitTabMode == 0 && !excludedInEqualSplit.contains("me") && dynamicEqualShare > 0) {
                                Text(
                                    text = "(${String.format(java.util.Locale.US, "%.2f", dynamicEqualShare)} ₺ / kişi)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))
        }

        // FIXED BOTTOM CTA BUTTON
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

    // =========================================================================
    // SPLITWISE MODAL 1: ÖDEYEN KİŞİ SEÇİMİ
    // =========================================================================
    if (showPayerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPayerSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Harcamayı Kim Ödedi?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // 1. Sen
                val isMeSelected = selectedPayerId == "me"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            selectedPayerId = "me"
                            showPayerSheet = false
                        }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isMeSelected) PrimaryEmerald else Color(0xFFF1F5F9),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Sen", color = if (isMeSelected) Color.White else Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Sen (Tümünü sen ödedin)", fontWeight = if (isMeSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 15.sp, color = Color(0xFF0F172A))
                    }
                    if (isMeSelected) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(22.dp))
                    }
                }

                HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                // 2. Katılımcılar
                selectedFriendIds.forEach { friendId ->
                    val friend = allFriends.find { it.id == friendId }
                    if (friend != null) {
                        val isFriendSelected = selectedPayerId == friend.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick {
                                    selectedPayerId = friend.id
                                    showPayerSheet = false
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isFriendSelected) PrimaryEmerald else Color(0xFFF1F5F9),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(friend.avatar, color = if (isFriendSelected) Color.White else Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = "${friend.name} (Tümünü ödedi)", fontWeight = if (isFriendSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 15.sp, color = Color(0xFF0F172A))
                            }
                            if (isFriendSelected) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(22.dp))
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)
                    }
                }
            }
        }
    }

    // =========================================================================
    // SPLITWISE MODAL 2: BÖLÜŞÜM SEÇENEKLERİ (4 SEKME: =, 123, %, ||)
    // =========================================================================
    if (showSplitOptionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSplitOptionsSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1)) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Bölüşüm Seçenekleri",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                // iOS Segmented Control for Split Tabs: =, 123, %, ||
                IOSSegmentedControl(
                    items = listOf(
                        0 to "= Eşit",
                        1 to "123 Tutar",
                        2 to "% Yüzde",
                        3 to "|| Paylar"
                    ),
                    selectedItem = splitTabMode,
                    onItemSelected = { index ->
                        splitTabMode = index
                        when (index) {
                            0 -> selectedSplitMethod = SplitMethod.EQUAL
                            1 -> selectedSplitMethod = SplitMethod.EXACT
                            2 -> selectedSplitMethod = SplitMethod.PERCENTAGE
                            3 -> selectedSplitMethod = SplitMethod.EQUAL
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFFF1F5F9)
                )

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // TAB 0: = EŞİT BÖLÜŞÜM (Splitwise Checkbox List)
                if (splitTabMode == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isMeIncluded) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = if (isMeIncluded) PrimaryEmerald else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Sen", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            }
                            if (isMeIncluded && dynamicEqualShare > 0) {
                                Text("${String.format(java.util.Locale.US, "%.2f", dynamicEqualShare)} ₺", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryEmerald)
                            }
                        }

                        // 2. Arkadaşlar
                        selectedFriendIds.forEach { friendId ->
                            val friend = allFriends.find { it.id == friendId }
                            if (friend != null) {
                                val isFriendIncluded = !excludedInEqualSplit.contains(friendId)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .bounceClick {
                                            if (isFriendIncluded) {
                                                excludedInEqualSplit.add(friendId)
                                            } else {
                                                excludedInEqualSplit.remove(friendId)
                                            }
                                        }
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isFriendIncluded) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                            contentDescription = null,
                                            tint = if (isFriendIncluded) PrimaryEmerald else Color(0xFFCBD5E1),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(friend.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                    }
                                    if (isFriendIncluded && dynamicEqualShare > 0) {
                                        Text("${String.format(java.util.Locale.US, "%.2f", dynamicEqualShare)} ₺", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryEmerald)
                                    }
                                }
                            }
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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sen", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.width(100.dp).height(38.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BasicTextField(
                                        value = exactAmountsMap["me"] ?: "",
                                        onValueChange = { exactAmountsMap["me"] = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("₺", fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Arkadaşlar
                        selectedFriendIds.forEach { friendId ->
                            val friend = allFriends.find { it.id == friendId }
                            if (friend != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(friend.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.width(100.dp).height(38.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            BasicTextField(
                                                value = exactAmountsMap[friendId] ?: "",
                                                onValueChange = { exactAmountsMap[friendId] = it },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                singleLine = true,
                                                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text("₺", fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sen", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.width(80.dp).height(38.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BasicTextField(
                                        value = percentageMap["me"] ?: "",
                                        onValueChange = { percentageMap["me"] = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("%", fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Arkadaşlar
                        selectedFriendIds.forEach { friendId ->
                            val friend = allFriends.find { it.id == friendId }
                            if (friend != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(friend.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.width(80.dp).height(38.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            BasicTextField(
                                                value = percentageMap[friendId] ?: "",
                                                onValueChange = { percentageMap[friendId] = it },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text("%", fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 3: || PAYLARLA BÖLÜŞÜM
                if (splitTabMode == 3) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Her kişinin pay adedini belirleyin:",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )

                        // Sen
                        val myShares = sharesMap["me"] ?: 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sen", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.size(28.dp).bounceClick {
                                        if (myShares > 0) sharesMap["me"] = myShares - 1
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) { Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("$myShares Pay", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Spacer(modifier = Modifier.width(10.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.size(28.dp).bounceClick {
                                        sharesMap["me"] = myShares + 1
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) { Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }

                        // Arkadaşlar
                        selectedFriendIds.forEach { friendId ->
                            val friend = allFriends.find { it.id == friendId }
                            if (friend != null) {
                                val fShares = sharesMap[friendId] ?: 1
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(friend.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF0F172A))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.size(28.dp).bounceClick {
                                                if (fShares > 0) sharesMap[friendId] = fShares - 1
                                            }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) { Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("$fShares Pay", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.size(28.dp).bounceClick {
                                                sharesMap[friendId] = fShares + 1
                                            }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) { Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { showSplitOptionsSheet = false },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Bölüşümü Kaydet", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                }
            }
        }
    }
}

