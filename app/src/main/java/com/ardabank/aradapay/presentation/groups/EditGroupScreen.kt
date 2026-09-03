package com.ardabank.aradapay.presentation.groups

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.domain.repository.GroupRepository
import com.ardabank.aradapay.domain.model.GroupMember
import com.ardabank.aradapay.presentation.common.BankContactPickerScreen
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.expense.ExpenseParticipant
import com.ardabank.aradapay.presentation.receipt.PdfReceiptGenerator
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import kotlin.math.abs

data class CategoryTag(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val bgTint: Color,
    val iconTint: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroupScreen(
    groupId: String,
    groupRepository: GroupRepository,
    onBackClick: () -> Unit = {},
    onGroupDeleted: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    val groups by groupRepository.groups.collectAsState()
    val groupExpensesMap by groupRepository.groupExpenses.collectAsState()

    val group = groups.find { it.id == groupId }
    if (group == null) {
        onBackClick()
        return
    }
    val groupExpenses = groupExpensesMap[group.id] ?: emptyList()

    val categories = remember {
        listOf(
            CategoryTag("home", "Ev & Yaşam", Icons.Outlined.Home, Color(0xFFCCFBF1), Color(0xFF0D9488)),
            CategoryTag("trip", "Seyahat", Icons.Outlined.Flight, Color(0xFFE0F2FE), Color(0xFF0284C7)),
            CategoryTag("food", "Yemek", Icons.Outlined.Restaurant, Color(0xFFFEF3C7), Color(0xFFD97706)),
            CategoryTag("transport", "Yolculuk", Icons.Outlined.DirectionsCar, Color(0xFFEDE9FE), Color(0xFF7C3AED)),
            CategoryTag("event", "Etkinlik", Icons.Outlined.Celebration, Color(0xFFFCE7F3), Color(0xFFDB2777)),
            CategoryTag("groceries", "Market", Icons.Default.ShoppingCart, Color(0xFFD1FAE5), Color(0xFF059669)),
            CategoryTag("other", "Diğer", Icons.Default.Category, Color(0xFFF1F5F9), Color(0xFF475569))
        )
    }

    var groupNameInput by remember(group.name) { mutableStateOf(group.name) }
    var selectedCategory by remember(group.category) {
        mutableStateOf(categories.find { it.name == group.category } ?: categories[0])
    }
    var showCategorySelector by remember { mutableStateOf(false) }
    var isSimplifyDebtsEnabled by remember { mutableStateOf(true) }

    var showAddMemberModal by remember { mutableStateOf(false) }
    var memberToDelete by remember { mutableStateOf<GroupMember?>(null) }
    var showDeleteGroupDialog by remember { mutableStateOf(false) }

    val candidateFriendsToAdd = listOf(
        "4" to "Elif Şahin",
        "5" to "Burak Öztürk",
        "6" to "Selin Aydın",
        "7" to "Caner Erkin",
        "8" to "Deniz Çelik",
        "9" to "Buse Demir",
        "10" to "Kaan Kaya"
    ).filter { candidate -> group.members.none { it.id == candidate.first } }

    BackHandler { onBackClick() }

    fun saveGroupChanges() {
        if (groupNameInput.isBlank()) {
            Toast.makeText(context, "Lütfen geçerli bir grup adı girin", Toast.LENGTH_SHORT).show()
            return
        }
        groupRepository.updateGroup(
            groupId = group.id,
            name = groupNameInput.trim(),
            emoji = group.emoji,
            category = selectedCategory.name
        )
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        Toast.makeText(context, "Grup ayarları güncellendi", Toast.LENGTH_SHORT).show()
        onBackClick()
    }

    if (showAddMemberModal) {
        val candidateParticipants = candidateFriendsToAdd.map { (id, name) ->
            ExpenseParticipant(
                id = id,
                name = name,
                tag = "@${name.lowercase().replace(" ", "")}",
                avatar = name.take(2).uppercase()
            )
        }
        BankContactPickerScreen(
            title = "Kişi Ekle",
            allFriends = candidateParticipants,
            selectedIds = emptySet(),
            onDismiss = { showAddMemberModal = false },
            onConfirmSelection = { selectedIds, updatedFriends ->
                selectedIds.forEach { memberId ->
                    val found = updatedFriends.find { it.id == memberId }
                    if (found != null) {
                        groupRepository.addMemberToGroup(
                            groupId = group.id,
                            member = GroupMember(
                                id = found.id,
                                name = found.name,
                                avatar = found.avatar,
                                tag = found.tag,
                                balanceInGroup = 0.0
                            )
                        )
                    }
                }
                showAddMemberModal = false
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                Toast.makeText(context, "Katılımcılar eklendi", Toast.LENGTH_SHORT).show()
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // =========================================================================
            // 1. TOP APP BAR
            // =========================================================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = onBackClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(0xFFF1F5F9)),
                        modifier = Modifier.size(40.dp).bounceClick(onClick = onBackClick)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Grup Düzenle",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )

                    // Top-Right Green Check Save Button
                    Surface(
                        shape = CircleShape,
                        color = PrimaryEmeraldContainer,
                        modifier = Modifier
                            .size(40.dp)
                            .bounceClick { saveGroupChanges() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Kaydet",
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // =========================================================================
            // 2. SECTION: GRUP ADI & KATEGORİ (From Reference Screenshot 1)
            // =========================================================================
            item {
                Text(
                    text = "GRUP ADI & KATEGORİ",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.bounceClick { showCategorySelector = !showCategorySelector }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = selectedCategory.bgTint,
                            border = BorderStroke(1.5.dp, selectedCategory.iconTint),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = selectedCategory.icon,
                                    contentDescription = selectedCategory.name,
                                    tint = selectedCategory.iconTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = selectedCategory.iconTint,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
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
                                        text = "Örn: ${selectedCategory.name}, Kaş Tatili 2026...",
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

                // Category selector expansion row
                AnimatedVisibility(
                    visible = showCategorySelector,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSel = cat.id == selectedCategory.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                                border = if (isSel) BorderStroke(1.dp, PrimaryEmerald) else null,
                                modifier = Modifier.bounceClick {
                                    selectedCategory = cat
                                    showCategorySelector = false
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(cat.icon, contentDescription = null, tint = cat.iconTint, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cat.name,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) PrimaryEmerald else Color(0xFF0F172A)
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(top = 10.dp))
            }

            // Quick Participant Strip (Direct Sen and members chips)
            item {
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
                        group.members.forEach { m ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = PrimaryEmeraldContainer,
                                border = BorderStroke(1.dp, PrimaryEmerald)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Surface(shape = CircleShape, color = PrimaryEmerald, modifier = Modifier.size(22.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = m.avatar, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = m.name.split(" ").first(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryEmerald
                                    )
                                    if (m.id != "me" && group.members.size > 2) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Kaldır",
                                            tint = PrimaryEmerald,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { memberToDelete = m }
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .height(32.dp)
                                .bounceClick { showAddMemberModal = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = "Ek...",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // =========================================================================
            // 3. SECTION: KATILIMCILAR (From Reference Screenshot 2)
            // =========================================================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KATILIMCILAR (${group.members.size})",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Text(
                        text = "+ Kişi Ekle",
                        color = PrimaryEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.bounceClick { showAddMemberModal = true }
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // Member Rows
            items(group.members, key = { it.id }) { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (member.id == "me") PrimaryEmeraldContainer else Color(0xFFF1F5F9),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = member.avatar,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (member.id == "me") PrimaryEmerald else Color(0xFF0F172A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = member.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                                if (member.id == "me") {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PrimaryEmeraldContainer
                                    ) {
                                        Text(
                                            text = "Sen",
                                            color = PrimaryEmerald,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (member.balanceInGroup > 0) {
                                    "+${String.format(java.util.Locale.US, "%.2f", member.balanceInGroup)} ₺ masada payı var"
                                } else if (member.balanceInGroup < 0) {
                                    "-${String.format(java.util.Locale.US, "%.2f", abs(member.balanceInGroup))} ₺ masaya payı var"
                                } else {
                                    "0,00 ₺ ödeştik"
                                },
                                fontSize = 13.sp,
                                color = if (member.balanceInGroup > 0) PrimaryEmerald else if (member.balanceInGroup < 0) AccentRose else Color(0xFF64748B)
                            )
                        }
                    }

                    if (member.id != "me" && group.members.size > 2) {
                        FilledTonalIconButton(
                            onClick = { memberToDelete = member },
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFFFFF1F2)
                            ),
                            modifier = Modifier.size(32.dp).bounceClick { memberToDelete = member }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kaldır",
                                tint = AccentRose,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(start = 76.dp))
            }

            // =========================================================================
            // 4. SECTION: TERCİHLER & AYARLAR (From Reference Screenshot 3)
            // =========================================================================
            item {
                Text(
                    text = "TERCİHLER & AYARLAR",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
                )
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // Borçları Sadeleştir Toggle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Akıllı Masayı Dengele",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Grup içi transfer sayısını en aza indirip hesabı kolaylaştırır",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    Switch(
                        checked = isSimplifyDebtsEnabled,
                        onCheckedChange = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isSimplifyDebtsEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryEmerald,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1)
                        )
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // Davet Bağlantısını Kopyala
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            val inviteLink = "https://aradapay.com/join/${group.id}"
                            clipboardManager.setText(AnnotatedString(inviteLink))
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast.makeText(context, "Davet bağlantısı panoya kopyalandı", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Davet Bağlantısını Paylaş",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "aradapay.com/join/${group.id}",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryEmeraldContainer,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = PrimaryEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Kopyala",
                                color = PrimaryEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // Finansal Rapor (PDF)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            try {
                                PdfReceiptGenerator.shareGroupReportPdf(
                                    context = context,
                                    group = group,
                                    expenses = groupExpenses
                                )
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Rapor oluşturulurken hata: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Finansal Rapor (PDF)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Detaylı harcama ve bakiye özeti",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // =========================================================================
            // 5. SECTION: HESAP & TEHLİKELİ BÖLGE (From Reference Screenshot 3 bottom)
            // =========================================================================
            item {
                Text(
                    text = "HESAP & TEHLİKELİ BÖLGE",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
                )
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // Grubu Arşivle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast.makeText(context, "Grup arşivlendi", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Grubu Arşivle",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Geçmiş harcamaları koru ancak grubu dondur",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }

            // Grubu Tamamen Sil
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick { showDeleteGroupDialog = true }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF1F2),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = null,
                                    tint = AccentRose,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Grubu Tamamen Sil",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AccentRose
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tüm harcamalar ve bakiye kayıtları silinir",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = AccentRose,
                        modifier = Modifier.size(14.dp)
                    )
                }
                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            }
        }
    }

    // =========================================================================
    // DIALOGS
    // =========================================================================

    // Katılımcıyı Çıkar Dialog
    memberToDelete?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToDelete = null },
            title = {
                Text(
                    text = "Katılımcıyı Çıkar",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = "${member.name} gruptan çıkarılsın mı? Üyenin mevcut bakiye durumu sıfırlanacaktır.",
                    color = Color(0xFF475569),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        groupRepository.removeMemberFromGroup(groupId = group.id, memberId = member.id)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        Toast.makeText(context, "${member.name} gruptan çıkarıldı", Toast.LENGTH_SHORT).show()
                        memberToDelete = null
                    }
                ) {
                    Text("Çıkar", color = AccentRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToDelete = null }) {
                    Text("İptal", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Grubu Sil Dialog
    if (showDeleteGroupDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteGroupDialog = false },
            title = {
                Text(
                    text = "Grubu Sil",
                    fontWeight = FontWeight.Bold,
                    color = AccentRose
                )
            },
            text = {
                Text(
                    text = "'${group.name}' grubunu ve tüm harcama geçmişini kalıcı olarak silmek istediğinize emin misiniz? Bu işlem geri alınamaz.",
                    color = Color(0xFF475569),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteGroupDialog = false
                        groupRepository.deleteGroup(group.id)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        Toast.makeText(context, "Grup silindi", Toast.LENGTH_SHORT).show()
                        onGroupDeleted()
                    }
                ) {
                    Text("Evet, Sil", color = AccentRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteGroupDialog = false }) {
                    Text("Vazgeç", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
