'use client';

import React, { useState, useEffect, useMemo } from 'react';
import { TopBar } from './components/TopBar';
import { DesktopSidebar } from './components/DesktopSidebar';
import { FinancialHeroCard } from './components/FinancialHeroCard';
import { ActionButtonsRow } from './components/ActionButtonsRow';
import { SmartSettlementBanner } from './components/SmartSettlementBanner';
import { TransactionsList } from './components/TransactionsList';
import { BottomNavBar, NavTab } from './components/BottomNavBar';
import { AddExpenseModal } from './components/AddExpenseModal';
import { SettleUpModal } from './components/SettleUpModal';
import { RequestMoneyDrawer } from './components/RequestMoneyDrawer';
import { SmartSettlementModal } from './components/SmartSettlementModal';
import { MerkleReceiptModal } from './components/MerkleReceiptModal';
import { AddFriendModal } from './components/AddFriendModal';
import { ExpenseDetailModal } from './components/ExpenseDetailModal';
import { FriendDetailModal } from './components/FriendDetailModal';
import { GroupDetailModal } from './components/GroupDetailModal';
import { SmartSettlementReportModal } from './components/SmartSettlementReportModal';
import { EditProfileModal } from './components/EditProfileModal';
import { LandingPage } from './components/LandingPage';
import { AuthScreen } from './components/AuthScreen';
import { GroupsView } from './views/GroupsView';
import { FriendsView } from './views/FriendsView';
import { AnalyticsView } from './views/AnalyticsView';
import { SettingsView } from './views/SettingsView';
import { Download } from 'lucide-react';

import {
  User,
  Expense,
  Group,
  Settlement,
  CrossSettlementOffer,
  Nudge
} from './types';
import {
  CURRENT_USER,
  INITIAL_USERS,
  INITIAL_GROUPS,
  INITIAL_EXPENSES,
  INITIAL_CROSS_OFFERS
} from './services/mockData';
import { FirestoreService } from './services/firestoreService';
import { NetBalanceCalculator } from './algorithms/NetBalanceCalculator';
import { CrossSettlementDfsEngine } from './algorithms/CrossSettlementDfsEngine';

export function App() {
  // Navigation / View states
  const [inWebApp, setInWebApp] = useState<boolean>(false);
  const [currentUser, setCurrentUser] = useState<User | null>(null);

  const [users, setUsers] = useState<User[]>(INITIAL_USERS);
  const [groups, setGroups] = useState<Group[]>(INITIAL_GROUPS);
  const [expenses, setExpenses] = useState<Expense[]>(INITIAL_EXPENSES);
  const [settlements, setSettlements] = useState<Settlement[]>([]);
  const [crossOffers, setCrossOffers] = useState<CrossSettlementOffer[]>(INITIAL_CROSS_OFFERS);
  const [nudges, setNudges] = useState<Nudge[]>([]);

  const [currentTab, setCurrentTab] = useState<NavTab>('dashboard');
  const [isLocked, setIsLocked] = useState<boolean>(false);
  const [dismissAppBanner, setDismissAppBanner] = useState(false);

  // Active user (default to CURRENT_USER if null in internal logic)
  const activeUser = currentUser || CURRENT_USER;

  // Modals state
  const [showAddExpense, setShowAddExpense] = useState(false);
  const [showSettleUp, setShowSettleUp] = useState(false);
  const [settlePreselectedUser, setSettlePreselectedUser] = useState<User | null>(null);
  const [showRequestMoney, setShowRequestMoney] = useState(false);
  const [showAddFriend, setShowAddFriend] = useState(false);

  const [selectedExpenseForDetail, setSelectedExpenseForDetail] = useState<Expense | null>(null);
  const [selectedCrossOffer, setSelectedCrossOffer] = useState<CrossSettlementOffer | null>(null);
  const [selectedReceipt, setSelectedReceipt] = useState<{
    txId: string;
    payerName: string;
    receiverName: string;
    amount: number;
  } | null>(null);

  // Real-time Firestore Subscriptions
  useEffect(() => {
    const unsubExpenses = FirestoreService.subscribeExpenses((liveExpenses) => {
      if (liveExpenses && liveExpenses.length > 0) {
        setExpenses(liveExpenses);
      }
    });

    const unsubSettlements = FirestoreService.subscribeSettlements((liveSettlements) => {
      if (liveSettlements && liveSettlements.length > 0) {
        setSettlements(liveSettlements);
      }
    });

    const unsubGroups = FirestoreService.subscribeGroups((liveGroups) => {
      if (liveGroups && liveGroups.length > 0) {
        setGroups(liveGroups);
      }
    });

    const unsubUsers = FirestoreService.subscribeAllUsers((liveUsers) => {
      if (liveUsers && liveUsers.length > 0) {
        setUsers(liveUsers);
      }
    });

    const unsubCross = FirestoreService.subscribeCrossOffers((liveOffers) => {
      if (liveOffers && liveOffers.length > 0) {
        setCrossOffers(liveOffers);
      }
    });

    const unsubNudges = FirestoreService.subscribeNudges(activeUser.id, (liveNudges) => {
      setNudges(liveNudges);
    });

    return () => {
      unsubExpenses();
      unsubSettlements();
      unsubGroups();
      unsubUsers();
      unsubCross();
      unsubNudges();
    };
  }, [activeUser.id]);

  // Run DFS Graph Algorithm to detect debt cycles dynamically
  useEffect(() => {
    const usersMap = new Map<string, User>(users.map((u) => [u.id, u]));
    const pairwiseMatrix = new Map<string, number>();

    // Compute bilateral debts from expenses
    expenses.forEach((exp) => {
      exp.splits.forEach((split) => {
        if (split.userId !== exp.paidBy && split.amountOwed > 0) {
          const key = `${split.userId}_${exp.paidBy}`;
          pairwiseMatrix.set(key, (pairwiseMatrix.get(key) || 0) + split.amountOwed);
        }
      });
    });

    // Reduce by settlements
    settlements.forEach((set) => {
      const key = `${set.payerId}_${set.receiverId}`;
      const current = pairwiseMatrix.get(key) || 0;
      pairwiseMatrix.set(key, Math.max(0, current - set.amount));
    });

    const detectedOffers = CrossSettlementDfsEngine.detectCrossSettlementCycles(
      usersMap,
      pairwiseMatrix
    );

    if (detectedOffers.length > 0) {
      setCrossOffers((prev) => {
        const newOnes = detectedOffers.filter(
          (no) => !prev.some((po) => po.id === no.id || po.cycleAmount === no.cycleAmount)
        );
        return [...newOnes, ...prev];
      });
    }
  }, [expenses, settlements, users, activeUser.id]);

  // Calculate Net Balances
  const balanceSummary = useMemo(() => {
    const netMap = NetBalanceCalculator.calculateNetBalances(expenses, settlements);
    const myBalance = netMap.get(activeUser.id) || 0;

    let receivable = 0;
    let payable = 0;
    if (myBalance > 0) {
      receivable = myBalance;
    } else {
      payable = Math.abs(myBalance);
    }

    return {
      netBalance: myBalance,
      totalReceivable: receivable,
      totalPayable: payable
    };
  }, [activeUser.id, expenses, settlements]);

  // Handlers
  const handleAddExpense = (newExpense: Expense) => {
    setExpenses((prev) => [newExpense, ...prev]);
    FirestoreService.addExpense(newExpense).catch(console.error);
  };

  const handleConfirmSettlement = (newSettlement: Settlement) => {
    setSettlements((prev) => [newSettlement, ...prev]);
    FirestoreService.addSettlement(newSettlement).catch(console.error);
  };

  const handleAddFriend = (newFriendUser: User) => {
    setUsers((prev) => {
      if (!prev.some((u) => u.id === newFriendUser.id)) {
        return [...prev, newFriendUser];
      }
      return prev;
    });
    FirestoreService.saveUser(newFriendUser).catch(console.error);
  };

  const handleApproveCrossOffer = (offerId: string) => {
    setCrossOffers((prev) =>
      prev.map((offer) => {
        if (offer.id === offerId) {
          const updatedApprovals = {
            ...offer.approvals,
            [activeUser.id]: true
          };
          const allApproved = offer.participants.every((p) => updatedApprovals[p.id]);

          return {
            ...offer,
            approvals: updatedApprovals,
            status: allApproved ? 'APPROVED' : 'PENDING'
          };
        }
        return offer;
      })
    );
  };

  const handleSendNudge = (nudge: Nudge) => {
    setNudges((prev) => [nudge, ...prev]);
    FirestoreService.sendNudge(nudge).catch(console.error);
  };

  const handleSaveProfile = (updatedUser: User) => {
    setCurrentUser(updatedUser);
    setUsers((prev) => prev.map((u) => (u.id === updatedUser.id ? updatedUser : u)));
    FirestoreService.saveUser(updatedUser).catch(console.error);
  };

  const handleWipeData = () => {
    setExpenses([]);
    setSettlements([]);
    setCrossOffers([]);
    setNudges([]);
    localStorage.clear();
  };

  const handleLogout = () => {
    setCurrentUser(null);
    setInWebApp(false);
  };

  // 1. If not launched into web app, show high-converting Apple HIG Landing Page
  if (!inWebApp) {
    return <LandingPage onLaunchWebApp={() => setInWebApp(true)} />;
  }

  // 2. If user clicked web app but not logged in, show AuthScreen (Welcome / Login / PIN pad / Demo selector)
  if (!currentUser) {
    return (
      <AuthScreen
        onLoginSuccess={(user) => {
          setCurrentUser(user);
        }}
        onBackToLanding={() => setInWebApp(false)}
      />
    );
  }

  // 3. Authenticated Apple HIG macOS / iOS Web App Layout
  return (
    <div className="min-h-screen bg-[#F2F2F7] text-[#1C1C1E] flex flex-row font-sans selection:bg-emerald-100 selection:text-emerald-900">
      {/* ========================================================================= */}
      {/* A. DESKTOP macOS SIDEBAR (Hidden on mobile < 1024px) */}
      {/* ========================================================================= */}
      <DesktopSidebar
        currentTab={currentTab}
        onTabChange={(tab) => setCurrentTab(tab)}
        currentUser={activeUser}
        onOpenAddExpense={() => setShowAddExpense(true)}
        onOpenSettleUp={() => {
          setSettlePreselectedUser(null);
          setShowSettleUp(true);
        }}
        onLogout={handleLogout}
      />

      {/* ========================================================================= */}
      {/* B. MAIN VIEWPORT & WORKSPACE */}
      {/* ========================================================================= */}
      <div className="flex-1 flex flex-col min-w-0 min-h-screen">
        {/* Top Banner (Native APK Prompter) */}
        {!dismissAppBanner && (
          <div className="bg-[#1C1C1E] text-white px-4 sm:px-8 py-2 flex items-center justify-between text-[12px] z-30">
            <div className="flex items-center gap-2 truncate">
              <span className="w-2 h-2 rounded-full bg-[#00875A] animate-pulse flex-shrink-0" />
              <span className="font-semibold truncate text-[12px]">
                📱 <strong>AradaPay Android:</strong> 100ms Kamera QR & Parmak İzi Kasası için uygulamayı yükleyin!
              </span>
            </div>

            <div className="flex items-center gap-2 flex-shrink-0">
              <a
                href="/AradaPay.apk"
                download="AradaPay.apk"
                className="px-3 py-1 rounded-full bg-[#00875A] text-white font-bold text-[11px] hover:bg-[#00744d] transition flex items-center gap-1 shadow-apple-sm"
              >
                <Download className="w-3 h-3" />
                <span>APK İndir</span>
              </a>
              <button
                onClick={() => setDismissAppBanner(true)}
                className="text-[#8E8E93] hover:text-white px-1.5 py-0.5 text-[14px]"
              >
                ✕
              </button>
            </div>
          </div>
        )}

        {/* Mobile Top Bar */}
        <div className="lg:hidden">
          <TopBar
            user={activeUser}
            onProfileClick={() => setCurrentTab('settings')}
            hasNudges={nudges.length > 0}
          />
        </div>

        {/* Main Content Area */}
        <main className="flex-1 max-w-5xl mx-auto w-full px-4 sm:px-8 py-6 pb-28 lg:pb-12 space-y-6">
          {currentTab === 'dashboard' && (
            <div className="space-y-6">
              {/* Responsive Grid on Desktop */}
              <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
                {/* Left Column: Balance & Primary Actions */}
                <div className="lg:col-span-7 space-y-6">
                  {/* Financial Hero Card */}
                  <FinancialHeroCard
                    netBalance={balanceSummary.netBalance}
                    totalReceivable={balanceSummary.totalReceivable}
                    totalPayable={balanceSummary.totalPayable}
                    isLocked={isLocked}
                    onToggleLock={() => setIsLocked(!isLocked)}
                  />

                  {/* Action Buttons Row */}
                  <div className="lg:hidden">
                    <ActionButtonsRow
                      onAddExpenseClick={() => setShowAddExpense(true)}
                      onSettleUpClick={() => {
                        setSettlePreselectedUser(null);
                        setShowSettleUp(true);
                      }}
                      onRequestMoneyClick={() => setShowRequestMoney(true)}
                    />
                  </div>

                  {/* Smart Settlement Banner (DFS Cycle Detection) */}
                  <SmartSettlementBanner
                    offers={crossOffers}
                    currentUserId={activeUser.id}
                    onOpenOffer={(offer) => setSelectedCrossOffer(offer)}
                  />
                </div>

                {/* Right Column: Transactions List */}
                <div className="lg:col-span-5 space-y-6">
                  <TransactionsList
                    expenses={expenses}
                    currentUserId={activeUser.id}
                    isLocked={isLocked}
                    onSeeAllClick={() => setCurrentTab('analytics')}
                    onExpenseClick={(exp) => setSelectedExpenseForDetail(exp)}
                  />
                </div>
              </div>
            </div>
          )}

          {currentTab === 'groups' && (
            <GroupsView
              groups={groups}
              currentUser={activeUser}
              users={users}
              expenses={expenses}
              isLocked={isLocked}
              onAddExpenseClick={() => setShowAddExpense(true)}
              onSaveGroup={(newGroup) => {
                setGroups((prev) => [newGroup, ...prev]);
                FirestoreService.saveGroup(newGroup).catch(console.error);
              }}
              onOpenSettleUp={(targetUser) => {
                setSettlePreselectedUser(targetUser);
                setShowSettleUp(true);
              }}
              onViewExpenseDetail={(exp) => setSelectedExpenseForDetail(exp)}
            />
          )}

          {currentTab === 'friends' && (
            <FriendsView
              currentUser={activeUser}
              users={users}
              expenses={expenses}
              settlements={settlements}
              groups={groups}
              isLocked={isLocked}
              onOpenSettleWithUser={(user) => {
                setSettlePreselectedUser(user);
                setShowSettleUp(true);
              }}
              onOpenNudgeWithUser={() => setShowRequestMoney(true)}
              onOpenAddExpenseWithUser={() => setShowAddExpense(true)}
              onViewExpenseDetail={(exp) => setSelectedExpenseForDetail(exp)}
              onAddFriend={handleAddFriend}
            />
          )}

          {currentTab === 'analytics' && (
            <AnalyticsView
              expenses={expenses}
              settlements={settlements}
              currentUserId={activeUser.id}
              isLocked={isLocked}
              crossOffers={crossOffers}
              currentUser={activeUser}
              onOpenReceipt={(txId) => {
                setSelectedReceipt({
                  txId,
                  payerName: activeUser.fullName,
                  receiverName: 'Mahsuplaşma Grubu',
                  amount: crossOffers[0]?.cycleAmount || 0
                });
              }}
            />
          )}

          {currentTab === 'settings' && (
            <SettingsView
              currentUser={activeUser}
              isLocked={isLocked}
              onToggleLock={() => setIsLocked(!isLocked)}
              onWipeData={handleWipeData}
              onLogout={handleLogout}
              onSaveProfile={handleSaveProfile}
            />
          )}
        </main>
      </div>

      {/* ========================================================================= */}
      {/* C. MOBILE iOS BOTTOM TAB BAR (Hidden on desktop lg:hidden) */}
      {/* ========================================================================= */}
      <BottomNavBar
        currentTab={currentTab}
        onTabChange={(tab) => setCurrentTab(tab)}
      />

      {/* ========================================================================= */}
      {/* D. APPLE HIG MODALS & DRAWERS (100% Mobile Android Parity) */}
      {/* ========================================================================= */}
      <AddExpenseModal
        isOpen={showAddExpense}
        onClose={() => setShowAddExpense(false)}
        currentUser={activeUser}
        users={users}
        groups={groups}
        onAddExpense={handleAddExpense}
      />

      <SettleUpModal
        isOpen={showSettleUp}
        onClose={() => {
          setShowSettleUp(false);
          setSettlePreselectedUser(null);
        }}
        currentUser={activeUser}
        users={users}
        onConfirmSettlement={handleConfirmSettlement}
        onShowReceipt={(settlement) => {
          const receiver = users.find((u) => u.id === settlement.receiverId);
          setSelectedReceipt({
            txId: settlement.id,
            payerName: activeUser.fullName,
            receiverName: receiver?.fullName || 'Alıcı',
            amount: settlement.amount
          });
        }}
      />

      <RequestMoneyDrawer
        isOpen={showRequestMoney}
        onClose={() => setShowRequestMoney(false)}
        currentUser={activeUser}
        users={users}
        onSendNudge={handleSendNudge}
      />

      <ExpenseDetailModal
        isOpen={selectedExpenseForDetail !== null}
        onClose={() => setSelectedExpenseForDetail(null)}
        expense={selectedExpenseForDetail}
        currentUser={activeUser}
        users={users}
        onOpenReceipt={(exp) => {
          const payer = users.find((u) => u.id === exp.paidBy);
          setSelectedReceipt({
            txId: exp.id,
            payerName: payer?.fullName || 'Ödeyen',
            receiverName: activeUser.fullName,
            amount: exp.amount
          });
        }}
        onOpenSettleUp={(exp) => {
          const payer = users.find((u) => u.id === exp.paidBy);
          if (payer) setSettlePreselectedUser(payer);
          setShowSettleUp(true);
        }}
        onOpenNudge={() => setShowRequestMoney(true)}
      />

      <SmartSettlementModal
        isOpen={selectedCrossOffer !== null}
        onClose={() => setSelectedCrossOffer(null)}
        offer={selectedCrossOffer}
        currentUser={activeUser}
        onApproveOffer={handleApproveCrossOffer}
      />

      <MerkleReceiptModal
        isOpen={selectedReceipt !== null}
        onClose={() => setSelectedReceipt(null)}
        txId={selectedReceipt?.txId}
        payerName={selectedReceipt?.payerName}
        receiverName={selectedReceipt?.receiverName}
        amount={selectedReceipt?.amount}
      />

      <AddFriendModal
        isOpen={showAddFriend}
        onClose={() => setShowAddFriend(false)}
        existingFriends={users}
        onFriendAdded={handleAddFriend}
      />
    </div>
  );
}
export default App;
