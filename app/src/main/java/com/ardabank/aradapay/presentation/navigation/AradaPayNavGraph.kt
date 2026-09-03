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
import com.ardabank.aradapay.domain.repository.GroupRepository
import com.ardabank.aradapay.presentation.groups.GroupDetailScreen
import com.ardabank.aradapay.presentation.groups.GroupsScreen
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.Person

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ardabank.aradapay.presentation.auth.AuthViewModel
import com.ardabank.aradapay.presentation.dashboard.DashboardViewModel
import com.ardabank.aradapay.presentation.expense.ExpenseViewModel
import com.ardabank.aradapay.presentation.friends.FriendsViewModel
import com.ardabank.aradapay.presentation.groups.GroupViewModel
import com.ardabank.aradapay.presentation.settle.SettleUpViewModel
import kotlinx.coroutines.launch

sealed class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    object Dashboard : NavItem("dashboard", "Ana Sayfa", Icons.Outlined.Home, Icons.Filled.Home)
    object Groups : NavItem("groups", "Gruplar", Icons.Outlined.PeopleAlt, Icons.Filled.PeopleAlt)
    object Friends : NavItem("friends", "Kişiler", Icons.Outlined.Person, Icons.Filled.Person)
    object Activity : NavItem("activity", "Hareketler", Icons.AutoMirrored.Outlined.ReceiptLong, Icons.AutoMirrored.Filled.ReceiptLong)
    object Profile : NavItem("profile", "Profil", Icons.Outlined.Person, Icons.Filled.Person)
}

@Composable
fun AradaPayNavGraph(
    authViewModel: AuthViewModel = hiltViewModel(),
    groupViewModel: GroupViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    friendsViewModel: FriendsViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    settleUpViewModel: SettleUpViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val groupRepository = groupViewModel.groupRepository

    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val storedUserName by authViewModel.userNameFlow.collectAsStateWithLifecycle()
    val storedUserIban by authViewModel.userIbanFlow.collectAsStateWithLifecycle()
    val storedAvatarUrl by authViewModel.avatarUrlFlow.collectAsStateWithLifecycle()
    val storedAvatarEmoji by authViewModel.avatarEmojiFlow.collectAsStateWithLifecycle()
    val isDataLocked by authViewModel.isDataLockedFlow.collectAsStateWithLifecycle()

    val financialSummary by dashboardViewModel.financialSummary.collectAsStateWithLifecycle()
    val nudgesList by dashboardViewModel.nudges.collectAsStateWithLifecycle()
    val pendingList by dashboardViewModel.pendingExpenses.collectAsStateWithLifecycle()
    val friendsList by friendsViewModel.friends.collectAsStateWithLifecycle()

    var customUserName by remember { mutableStateOf("") }
    val effectiveUserName = when {
        customUserName.isNotBlank() -> customUserName
        currentUser != null && currentUser!!.fullName.isNotBlank() -> currentUser!!.fullName
        storedUserName.isNotBlank() && storedUserName != "Kullanıcı" -> storedUserName
        else -> "Kullanıcı"
    }

    val effectiveIban = if (currentUser?.iban?.isNotBlank() == true) currentUser!!.iban!! else storedUserIban
    val effectiveAvatarUrl = if (currentUser?.avatarUrl?.isNotBlank() == true) currentUser!!.avatarUrl else storedAvatarUrl
    val effectiveEmoji = if (effectiveUserName.length >= 2) {
        val parts = effectiveUserName.trim().split(" ").filter { it.isNotBlank() }
        if (parts.size >= 2) "${parts[0].first()}${parts[1].first()}".uppercase()
        else effectiveUserName.take(2).uppercase()
    } else storedAvatarEmoji

    val bottomNavItems = listOf(
        NavItem.Dashboard,
        NavItem.Groups,
        NavItem.Friends
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
            startDestination = "welcome",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    currentUserName = effectiveUserName,
                    viewModel = authViewModel,
                    onLoginSuccess = { loggedInName ->
                        customUserName = loggedInName
                        navController.navigate(NavItem.Dashboard.route) {
                            popUpTo("welcome") { inclusive = true }
                        }
                    },
                    onSwitchUser = { newName ->
                        customUserName = newName
                    },
                    onNavigateToRegister = {
                        navController.navigate("register_flow")
                    },
                    onNavigateToWelcome = {
                        navController.navigate("welcome") {
                            popUpTo("login") { inclusive = true }
                        }
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
                    viewModel = authViewModel,
                    onBackClick = { navController.popBackStack() },
                    onRegisterSuccess = { createdUser ->
                        customUserName = createdUser.fullName
                        navController.navigate("onboarding_guide") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }

            composable("onboarding_guide") {
                OnboardingHowItWorksScreen(
                    userName = effectiveUserName,
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
                    userName = effectiveUserName,
                    avatarEmoji = effectiveEmoji,
                    avatarUrl = effectiveAvatarUrl,
                    isLocked = isDataLocked,
                    netBalance = financialSummary.netBalance,
                    totalReceivable = financialSummary.alacakTotal,
                    totalPayable = financialSummary.borcTotal,
                    nudges = nudgesList,
                    pendingExpenses = pendingList,
                    groupRepository = groupRepository,
                    onApproveExpense = { id -> dashboardViewModel.approveExpense(id) },
                    onRejectExpense = { id -> dashboardViewModel.rejectExpense(id) },
                    onToggleLock = { authViewModel.toggleDataLock(!isDataLocked) },
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
                    onAddExpense = { navController.navigate("add_expense") },
                    onCreateGroupClick = { navController.navigate("create_group") }
                )
            }

            composable(route = "create_group") {
                com.ardabank.aradapay.presentation.groups.CreateGroupScreen(
                    groupRepository = groupRepository,
                    onBackClick = { navController.popBackStack() },
                    onGroupCreated = { newGroupId ->
                        navController.popBackStack()
                        navController.navigate("group_detail/$newGroupId")
                    }
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
                    onAddExpenseInGroup = { groupName, grpId -> navController.navigate("add_expense?initialGroupId=$grpId&initialGroupName=$groupName") },
                    onNavigateToSettleUp = { amount, _, groupName, grpId ->
                        navController.navigate("settle_up?initialGroupId=$grpId&initialGroupName=$groupName&amount=$amount")
                    }
                )
            }

            composable(
                route = "edit_group/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: "grp_1"
                com.ardabank.aradapay.presentation.groups.EditGroupScreen(
                    groupId = groupId,
                    groupRepository = groupRepository,
                    onBackClick = { navController.popBackStack() },
                    onGroupDeleted = { navController.popBackStack() }
                )
            }

            composable(
                route = NavItem.Friends.route,
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://friends" })
            ) {
                FriendsScreen(
                    remoteFriends = friendsList,
                    onFriendClick = { friendId -> navController.navigate("friend_detail/$friendId") },
                    onAddExpense = { navController.navigate("add_expense") },
                    onSettleUp = { navController.navigate("settle_up") },
                    onAddFriend = { user -> friendsViewModel.addFriend(user) }
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
                val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
                ProfileScreen(
                    userName = effectiveUserName,
                    userIban = effectiveIban,
                    avatarEmoji = effectiveEmoji,
                    avatarUrl = effectiveAvatarUrl,
                    onBackClick = { navController.popBackStack() },
                    onSettingsClick = { navController.navigate("settings") },
                    onEditProfileClick = { navController.navigate("edit_profile") },
                    onAnalyticsClick = { navController.navigate("analytics") },
                    onSavingsReportClick = { navController.navigate("savings_report") },
                    onSignOutClick = {
                        customUserName = ""
                        authViewModel.signOut()
                        navController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("edit_profile") {
                val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
                EditProfileScreen(
                    currentName = effectiveUserName,
                    currentIban = effectiveIban,
                    currentAvatarEmoji = effectiveEmoji,
                    currentAvatarUrl = effectiveAvatarUrl,
                    onBackClick = { navController.popBackStack() },
                    onSaveProfile = { newName, newIban, newEmoji, newAvatarUrl ->
                        customUserName = newName
                        coroutineScope.launch {
                            authViewModel.securityPreferencesManager.saveUserSession(
                                name = newName,
                                iban = newIban,
                                avatarUrl = newAvatarUrl,
                                avatarEmoji = newEmoji
                            )
                        }
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    onEditProfileClick = { navController.navigate("edit_profile") }
                )
            }

            composable(
                route = "notifications",
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://notifications" }, navDeepLink { uriPattern = "aradapay://history" })
            ) {
                ActivityScreen(
                    isLocked = isDataLocked,
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
                    friendsList = friendsList,
                    onCancel = { navController.popBackStack() },
                    onSaveExpense = { amount, description, category, splitMethod, selectedUserIds ->
                        expenseViewModel.saveExpense(
                            amount = amount,
                            description = description,
                            category = category,
                            splitMethod = splitMethod,
                            selectedUserIds = selectedUserIds,
                            groupId = initialGroupId
                        )
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "settle_up?initialGroupId={initialGroupId}&initialGroupName={initialGroupName}&amount={amount}",
                arguments = listOf(
                    navArgument("initialGroupId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("initialGroupName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("amount") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "aradapay://settle_up" },
                    navDeepLink { uriPattern = "aradapay://qr_scan" }
                )
            ) { backStackEntry ->
                val initialGroupId = backStackEntry.arguments?.getString("initialGroupId")
                val initialGroupName = backStackEntry.arguments?.getString("initialGroupName")
                val amountStr = backStackEntry.arguments?.getString("amount")
                val amountDouble = amountStr?.toDoubleOrNull() ?: 120.0

                SettleUpScreen(
                    creditorUser = null,
                    owedAmount = amountDouble,
                    initialGroupId = initialGroupId,
                    initialGroupName = initialGroupName,
                    onCancel = { navController.popBackStack() },
                    onConfirmSettlement = { amount, note ->
                        settleUpViewModel.confirmSettlement(
                            receiverId = initialGroupId ?: "friend",
                            amount = amount,
                            note = note,
                            isCash = false,
                            groupId = initialGroupId
                        )
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "analytics?tab={tab}",
                arguments = listOf(
                    navArgument("tab") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = "overview"
                    }
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "aradapay://analytics" }
                )
            ) {
                com.ardabank.aradapay.presentation.analytics.AnalyticsScreen(
                    onBackClick = { navController.popBackStack() },
                    onFriendClick = { friendId -> navController.navigate("friend_detail/$friendId") },
                    onNavigateToSettleUp = { navController.navigate("settle_up") }
                )
            }

            composable(
                route = "savings_report",
                deepLinks = listOf(navDeepLink { uriPattern = "aradapay://savings_report" })
            ) {
                com.ardabank.aradapay.presentation.analytics.AnalyticsScreen(
                    onBackClick = { navController.popBackStack() },
                    onFriendClick = { friendId -> navController.navigate("friend_detail/$friendId") },
                    onNavigateToSettleUp = { navController.navigate("settle_up") }
                )
            }
        }
    }
}
