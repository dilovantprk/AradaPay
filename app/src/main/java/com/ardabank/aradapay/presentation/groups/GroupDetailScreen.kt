package com.ardabank.aradapay.presentation.groups

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardabank.aradapay.data.repository.GroupRepository
import com.ardabank.aradapay.domain.model.ExpenseCategory
import com.ardabank.aradapay.domain.model.GroupMember
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.common.BankContactPickerScreen
import com.ardabank.aradapay.presentation.expense.ExpenseParticipant
import com.ardabank.aradapay.presentation.receipt.AradaPayReceipt
import com.ardabank.aradapay.presentation.receipt.AradaPayReceiptModalSheet
import com.ardabank.aradapay.presentation.receipt.ReceiptParticipant
import com.ardabank.aradapay.presentation.common.IOSSegmentedControl
import com.ardabank.aradapay.presentation.common.applePressEffect
import com.ardabank.aradapay.presentation.theme.AccentRose
import com.ardabank.aradapay.presentation.theme.AccentRoseContainer
import com.ardabank.aradapay.presentation.theme.LightBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.presentation.theme.SurfaceWhite
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String = "grp_1",
    groupRepository: GroupRepository,
    onBackClick: () -> Unit = {},
    onAddExpenseInGroup: (groupName: String, groupId: String) -> Unit = { _, _ -> },
    onNavigateToSettleUp: (amount: Double, creditorId: String?, groupName: String?, groupId: String?) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    var showMembersManagementModal by remember { mutableStateOf(false) }
    var showSimplifyDebtsModal by remember { mutableStateOf(false) }
    var showAddMemberModal by remember { mutableStateOf(false) }
    var activeReceiptForModal by remember { mutableStateOf<AradaPayReceipt?>(null) }

    val groups by groupRepository.groups.collectAsState()
    val groupExpensesMap by groupRepository.groupExpenses.collectAsState()

    val group = groups.find { it.id == groupId } ?: groups.firstOrNull() ?: return
    val groupExpenses = groupExpensesMap[group.id] ?: emptyList()
    val groupNetBalance = group.userBalance

    val candidateFriendsToAdd = listOf(
        "4" to "Elif Şahin",
        "5" to "Burak Öztürk",
        "6" to "Selin Aydın",
        "7" to "Caner Erkin",
        "8" to "Deniz Çelik"
    ).filter { candidate -> group.members.none { it.id == candidate.first } }

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
            title = "Grup Üyesi Ekle",
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
                Toast.makeText(context, "Grup üyeleri güncellendi", Toast.LENGTH_SHORT).show()
            }
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // 1. TOP BAR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = onBackClick,
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
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalIconButton(
                        onClick = { showMembersManagementModal = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Üyeleri Yönet",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { showSimplifyDebtsModal = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Borç Sadeleştirme",
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 2. HERO: GRUP BİLGİLERİ VE İSTATİSTİKLER
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Grup kimlik satırı: Kategori ikonu + ad + durum rozeti
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val categoryIcon = when (group.category.lowercase()) {
                        "tatil", "seyahat" -> Icons.Default.LocalGasStation
                        "ev", "konut" -> Icons.Default.Home
                        "yemek", "restoran" -> Icons.Default.Fastfood
                        else -> Icons.Default.Group
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Grup adı + kategori + üye sayısı
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = group.name,
                            color = Color(0xFF0F172A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${group.category} • ${group.members.size} Üye",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }

                // Üye avatarları satırı
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    group.members.take(5).forEach { member ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = member.avatar,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                    if (group.members.size > 5) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "+${group.members.size - 5} kişi",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // İstatistik şeridi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${groupExpenses.size}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Harcama",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFE2E8F0)).align(Alignment.CenterVertically))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val totalSpent = groupExpenses.sumOf { it.totalAmount }
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.0f", totalSpent)} ₺",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Toplam",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFE2E8F0)).align(Alignment.CenterVertically))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val settledCount = groupExpenses.count { it.isSettled }
                        Text(
                            text = "$settledCount/${groupExpenses.size}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryEmerald
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Fitleşildi",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // CTA Butonları (Harcama Ekle & Öde / Fitleş)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onAddExpenseInGroup(group.name, group.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryEmerald,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Harcama Ekle",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    FilledTonalButton(
                        onClick = { onNavigateToSettleUp(abs(groupNetBalance), null, group.name, group.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF0F172A),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Öde & Fitleş",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }

        // 3. GRUP HARCAMALARI LİSTESİ
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GRUP HARCAMALARI (${groupExpenses.size})",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        if (groupExpenses.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Henüz harcama eklenmemiş",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onAddExpenseInGroup(group.name, group.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryEmerald,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("İlk Harcamayı Ekle", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        } else {
            itemsIndexed(groupExpenses) { index, item ->
                val icon = when (item.category) {
                    ExpenseCategory.DINING -> Icons.Default.Fastfood
                    ExpenseCategory.GROCERIES -> Icons.Default.ShoppingCart
                    ExpenseCategory.HOUSING, ExpenseCategory.UTILITIES -> Icons.Default.Home
                    ExpenseCategory.TRAVEL -> Icons.Default.LocalGasStation
                    else -> Icons.Default.ShoppingCart
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
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
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = item.title,
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.payerName} • Toplam ${String.format(java.util.Locale.US, "%.0f", item.totalAmount)} ₺",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        if (item.isSettled) {
                            Text(
                                text = "fitleşildi",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else if (item.yourShare > 0) {
                            Text(
                                text = "+${String.format(java.util.Locale.US, "%.2f", item.yourShare)} ₺",
                                color = PrimaryEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "sen verdin",
                                color = PrimaryEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "-${String.format(java.util.Locale.US, "%.2f", abs(item.yourShare))} ₺",
                                color = AccentRose,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "senin payın",
                                color = AccentRose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (index < groupExpenses.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 74.dp),
                        color = Color(0xFFF1F5F9),
                        thickness = 1.dp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // =========================================================================
    // 1. MEMBERS MANAGEMENT FULL-PAGE INTRINSIC SCREEN
    // =========================================================================
    if (showMembersManagementModal) {
        BackHandler { showMembersManagementModal = false }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                // TOP BAR
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalIconButton(
                            onClick = { showMembersManagementModal = false },
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
                            text = "Katılımcılar (${group.members.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryEmeraldContainer,
                            modifier = Modifier.bounceClick {
                                showMembersManagementModal = false
                                showAddMemberModal = true
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Katılımcı Ekle",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryEmerald
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }

                // ÜYE SATIRLARI
                itemsIndexed(group.members) { idx, member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
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
                                        text = member.avatar,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = member.name,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A),
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (member.id == "me" || member.id == "1") "Yönetici • Sen" else member.tag,
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val statusLabel = when {
                                member.balanceInGroup > 0 -> "alacaklı"
                                member.balanceInGroup < 0 -> "borçlu"
                                else -> "fitleşildi"
                            }
                            val balanceColor = when {
                                member.balanceInGroup > 0 -> PrimaryEmerald
                                member.balanceInGroup < 0 -> AccentRose
                                else -> Color(0xFF94A3B8)
                            }
                            val balanceText = when {
                                member.balanceInGroup > 0 -> "+${String.format(java.util.Locale.US, "%.2f", member.balanceInGroup)} ₺"
                                member.balanceInGroup < 0 -> "-${String.format(java.util.Locale.US, "%.2f", abs(member.balanceInGroup))} ₺"
                                else -> "0,00 ₺"
                            }

                            Text(
                                text = statusLabel,
                                color = balanceColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = balanceText,
                                color = balanceColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
        return
    }

    // =========================================================================
    // 2. SIMPLIFY DEBTS FULL-PAGE INTRINSIC SCREEN
    // =========================================================================
    if (showSimplifyDebtsModal) {
        BackHandler { showSimplifyDebtsModal = false }

        val suggestedSteps = remember(group.id, group.members) {
            groupRepository.getSimplifyDebtsSuggestions(group.id)
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { showSimplifyDebtsModal = false },
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
                        text = "Borç Sadeleştirme",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PrimaryEmeraldContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Akıllı Borç Sadeleştirme",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald
                            )
                            Text(
                                text = "Gereksiz ara transferleri sıfırlayan doğrudan FAST transfer planı.",
                                color = Color(0xFF0F172A),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (suggestedSteps.isNotEmpty()) "Sadeleştirilmiş ${suggestedSteps.size} Doğrudan Transfer" else "Grupta sadeleştirilecek borç kalmadı",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    suggestedSteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${step.fromUserName} ➔ ${step.toUserName}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.2f", step.amount)} ₺",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryEmerald,
                                fontSize = 15.sp
                            )
                        }

                        if (index < suggestedSteps.lastIndex) {
                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.8.dp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .bounceClick { showSimplifyDebtsModal = false }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Tamam, Anladım", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                }
            }
        }
        return
    }

    AradaPayReceiptModalSheet(
        receipt = activeReceiptForModal,
        onDismiss = { activeReceiptForModal = null }
    )
}
