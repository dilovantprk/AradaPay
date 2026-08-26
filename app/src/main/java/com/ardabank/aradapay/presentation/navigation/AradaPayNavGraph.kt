package com.ardabank.aradapay.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Currency
import com.ardabank.aradapay.domain.model.Expense
import com.ardabank.aradapay.domain.model.ExpenseCategory
import com.ardabank.aradapay.domain.model.Nudge
import com.ardabank.aradapay.domain.model.SplitMethod
import com.ardabank.aradapay.presentation.activity.ActivityScreen
import com.ardabank.aradapay.presentation.activity.TransactionHistoryScreen
import com.ardabank.aradapay.presentation.dashboard.DashboardScreen
import com.ardabank.aradapay.presentation.expense.AddExpenseScreen
import com.ardabank.aradapay.presentation.friends.FriendDetailScreen
import com.ardabank.aradapay.presentation.friends.FriendsScreen
import com.ardabank.aradapay.presentation.profile.EditProfileScreen
import com.ardabank.aradapay.presentation.profile.ProfileScreen
import com.ardabank.aradapay.presentation.settings.SettingsScreen
import com.ardabank.aradapay.presentation.settle.SettleUpScreen
import com.ardabank.aradapay.presentation.theme.DarkBackground
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.presentation.theme.TextSecondary

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.ardabank.aradapay.presentation.auth.LoginScreen
import com.ardabank.aradapay.presentation.auth.OnboardingHowItWorksScreen
import com.ardabank.aradapay.presentation.auth.RegisterFlowScreen
import com.ardabank.aradapay.presentation.auth.WelcomeScreen
import com.ardabank.aradapay.presentation.components.bounceClick
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.Group
import com.ardabank.aradapay.data.repository.GroupRepository
import com.ardabank.aradapay.presentation.groups.GroupDetailScreen
import com.ardabank.aradapay.presentation.groups.GroupsScreen
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.Person

sealed class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    object Dashboard : NavItem("dashboard", "Ana Sayfa", Icons.Outlined.Home, Icons.Filled.Home)
    object Groups : NavItem("groups", "Gruplar", Icons.Outlined.Group, Icons.Filled.Group)
    object Friends : NavItem("friends", "Kişiler", Icons.Outlined.Person, Icons.Filled.Person)
    object Activity : NavItem("activity", "Hareketler", Icons.AutoMirrored.Outlined.ReceiptLong, Icons.AutoMirrored.Filled.ReceiptLong)
    object Profile : NavItem("profile", "Profil", Icons.Outlined.Person, Icons.Filled.Person)
}

@Composable
fun AradaPayNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val groupRepository = remember { GroupRepository() }

    var isDataLocked by remember { mutableStateOf(false) }
    val firebaseUser = remember { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser }
    val defaultName = firebaseUser?.displayName ?: firebaseUser?.email?.split("@")?.first()?.replaceFirstChar { it.uppercase() } ?: "Mehmet Dilovan"
    var userName by remember { mutableStateOf(defaultName) }
    var userIban by remember { mutableStateOf("TR64 0006 2000 0000 1122 3344 55") }
    var userAvatarEmoji by remember { mutableStateOf("MD") }

    val pendingList = remember {
        mutableStateListOf(
            Expense(
                id = "exp_1",
                paidBy = "user_zeynep",
                description = "Zeynep: Starbucks Kahve & Tatlı",
                amount = 240.0,
                currency = Currency.TRY,
                category = ExpenseCategory.DINING,
                splitMethod = SplitMethod.EQUAL,
                status = ApprovalStatus.PENDING,
                createdAt = "2026-08-22"
            ),
            Expense(
                id = "exp_2",
                paidBy = "user_ahmet",
                description = "Ahmet: Migros Ortak Mutfak Alışverişi",
                amount = 450.0,
                currency = Currency.TRY,
                category = ExpenseCategory.GROCERIES,
                splitMethod = SplitMethod.EQUAL,
                status = ApprovalStatus.PENDING,
                createdAt = "2026-08-22"
            )
        )
    }

    val demoNudges = remember {
        mutableStateListOf(
            Nudge(
                id = "ndg_1",
                fromUserId = "3",
                toUserId = "me",
                message = "Mert Demir senden kahve ödemesi için 120,00 ₺ bekliyor.",
                createdAt = "2026-08-22"
            )
        )
    }

    val bottomNavItems = listOf(
        NavItem.Dashboard,
        NavItem.Groups,
        NavItem.Friends,
        NavItem.Activity
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavItems.map { it.route }) {
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color(0xFFEEF0F1)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            bottomNavItems.forEach { item ->
                                val isSelected = currentRoute == item.route

                                val iconTint by animateColorAsState(
                                    targetValue = if (isSelected) Color(0xFF0B8659) else Color(0xFF6B7480),
                                    label = "TabIconTint"
                                )
                                val textTint by animateColorAsState(
                                    targetValue = if (isSelected) Color(0xFF0B8659) else Color(0xFF6B7480),
                                    label = "TabTextTint"
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .bounceClick(scaleDown = 0.92f) {
                                            if (currentRoute != item.route) {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.icon,
                                            contentDescription = item.title,
                                            tint = iconTint,
                                            modifier = Modifier.size(22.dp)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = item.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = textTint
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFFAFAF9)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    currentUserName = userName,
                    onLoginSuccess = { loggedInName ->
                        userName = loggedInName
                        navController.navigate(NavItem.Dashboard.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onSwitchUser = { newName ->
                        userName = newName
                    },
                    onNavigateToWelcome = {
                        navController.navigate("welcome")
                    }
                )
            }

            composable("welcome") {
                WelcomeScreen(
                    onNavigateToRegister = { navController.navigate("register_flow") },
                    onNavigateToLogin = { navController.navigate("login") },
                    onGoogleSignInClick = { navController.navigate("register_flow") }
                )
            }

            composable("register_flow") {
                RegisterFlowScreen(
                    onBackClick = { navController.popBackStack() },
                    onRegisterSuccess = { createdUser ->
                        userName = createdUser.fullName
                        navController.navigate("onboarding_guide") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }

            composable("onboarding_guide") {
                OnboardingHowItWorksScreen(
                    userName = userName,
                    onComplete = {
                        navController.navigate(NavItem.Dashboard.route) {
                            popUpTo("onboarding_guide") { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = NavItem.Dashboard.route,
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://dashboard" })
            ) {
                DashboardScreen(
                    userName = userName,
                    avatarEmoji = userAvatarEmoji,
                    isLocked = isDataLocked,
                    nudges = demoNudges,
                    pendingExpenses = pendingList,
                    groupRepository = groupRepository,
                    onApproveExpense = { id -> pendingList.removeAll { it.id == id } },
                    onRejectExpense = { id -> pendingList.removeAll { it.id == id } },
                    onToggleLock = { isDataLocked = !isDataLocked },
                    onProfileClick = { navController.navigate("profile") },
                    onAddExpenseClick = { navController.navigate("add_expense") },
                    onSettleUpClick = { navController.navigate("settle_up") },
                    onAnalyticsClick = { navController.navigate("analytics") },
                    onSavingsReportClick = { navController.navigate("savings_report") },
                    onGroupClick = { groupId -> navController.navigate("group_detail/$groupId") },
                    onSeeAllGroupsClick = {
                        navController.navigate(NavItem.Groups.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSeeAllActivityClick = {
                        navController.navigate(NavItem.Activity.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(
                route = NavItem.Groups.route,
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://groups" })
            ) {
                GroupsScreen(
                    groupRepository = groupRepository,
                    onGroupClick = { groupId -> navController.navigate("group_detail/$groupId") },
                    onAddExpenseInGroup = { groupName -> navController.navigate("add_expense?initialGroupName=$groupName") },
                    onAddExpense = { navController.navigate("add_expense") }
                )
            }

            composable(
                route = "group_detail/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: "grp_1"
                GroupDetailScreen(
                    groupId = groupId,
                    groupRepository = groupRepository,
                    onBackClick = { navController.popBackStack() },
                    onAddExpenseInGroup = { groupName, grpId -> navController.navigate("add_expense?initialGroupId=$grpId&initialGroupName=$groupName") }
                )
            }

            composable(
                route = NavItem.Friends.route,
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://friends" })
            ) {
                FriendsScreen(
                    onFriendClick = { friendId -> navController.navigate("friend_detail/$friendId") },
                    onAddExpense = { navController.navigate("add_expense") },
                    onSettleUp = { navController.navigate("settle_up") }
                )
            }

            composable(
                route = "friend_detail/{friendId}",
                arguments = listOf(navArgument("friendId") { type = NavType.StringType })
            ) { backStackEntry ->
                val friendId = backStackEntry.arguments?.getString("friendId") ?: "1"
                FriendDetailScreen(
                    friendId = friendId,
                    groupRepository = groupRepository,
                    onBackClick = { navController.popBackStack() },
                    onAddExpenseWithFriend = { friendName -> navController.navigate("add_expense?initialFriendId=$friendId&initialFriendName=$friendName") },
                    onSettleUpWithFriend = { navController.navigate("settle_up") },
                    onGroupClick = { grpId -> navController.navigate("group_detail/$grpId") }
                )
            }

            composable(
                route = NavItem.Activity.route,
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://activity" })
            ) {
                ActivityScreen(
                    isLocked = isDataLocked,
                    onNavigateToHistory = { navController.navigate("transaction_history") },
                    onExpenseClick = { expenseId -> navController.navigate("expense_detail/$expenseId") }
                )
            }

            composable(
                route = "expense_detail/{expenseId}",
                arguments = listOf(navArgument("expenseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getString("expenseId") ?: "1"
                com.ardabank.aradapay.presentation.expense.ExpenseDetailScreen(
                    expenseId = expenseId,
                    onBackClick = { navController.popBackStack() },
                    onSettleUp = { navController.navigate("settle_up") }
                )
            }

            composable("profile") {
                ProfileScreen(
                    userName = userName,
                    userIban = userIban,
                    avatarEmoji = userAvatarEmoji,
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = { navController.navigate("settings") },
                    onEditProfileClick = { navController.navigate("edit_profile") },
                    onAnalyticsClick = { navController.navigate("analytics") },
                    onSavingsReportClick = { navController.navigate("savings_report") }
                )
            }

            composable("edit_profile") {
                EditProfileScreen(
                    currentName = userName,
                    currentIban = userIban,
                    currentAvatarEmoji = userAvatarEmoji,
                    onBackClick = { navController.popBackStack() },
                    onSaveProfile = { newName, newIban, newAvatar ->
                        userName = newName
                        userIban = newIban
                        userAvatarEmoji = newAvatar
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "transaction_history",
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://history" })
            ) {
                TransactionHistoryScreen(
                    isLocked = isDataLocked,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "notifications",
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://notifications" })
            ) {
                ActivityScreen(
                    isLocked = isDataLocked,
                    onNavigateToHistory = { navController.navigate("transaction_history") },
                    onNavigateToSettleUp = { navController.navigate("settle_up") }
                )
            }

            composable(
                route = "add_expense?initialFriendId={initialFriendId}&initialFriendName={initialFriendName}&initialGroupId={initialGroupId}&initialGroupName={initialGroupName}",
                arguments = listOf(
                    navArgument("initialFriendId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("initialFriendName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("initialGroupId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("initialGroupName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                ),
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://add_expense" })
            ) { backStackEntry ->
                val initialFriendId = backStackEntry.arguments?.getString("initialFriendId")
                val initialFriendName = backStackEntry.arguments?.getString("initialFriendName")
                val initialGroupId = backStackEntry.arguments?.getString("initialGroupId")
                val initialGroupName = backStackEntry.arguments?.getString("initialGroupName")
                AddExpenseScreen(
                    initialFriendId = initialFriendId,
                    initialFriendName = initialFriendName,
                    initialGroupId = initialGroupId,
                    initialGroupName = initialGroupName,
                    groupRepository = groupRepository,
                    onCancel = { navController.popBackStack() },
                    onSaveExpense = { _, _, _, _, _ -> navController.popBackStack() }
                )
            }

            composable(
                route = "settle_up",
                deepLinks = listOf(
                    navDeepLink { uriPattern = "aradapay://settle_up" },
                    navDeepLink { uriPattern = "aradapay://qr_scan" }
                )
            ) {
                SettleUpScreen(
                    creditorUser = null,
                    onCancel = { navController.popBackStack() },
                    onConfirmSettlement = { _, _ -> navController.popBackStack() }
                )
            }

            composable(
                route = "analytics",
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://analytics" })
            ) {
                com.ardabank.aradapay.presentation.analytics.AnalyticsScreen(
                    onBackClick = { navController.popBackStack() },
                    onFriendClick = { friendId -> navController.navigate("friend_detail/$friendId") }
                )
            }

            composable(
                route = "savings_report",
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://savings_report" })
            ) {
                com.ardabank.aradapay.presentation.settlement.SmartSettlementReportScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToSettleUp = { navController.navigate("settle_up") }
                )
            }
        }
    }
}
