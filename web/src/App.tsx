'use client';

import React, { useState, useEffect, useMemo } from 'react';
import { TopBar } from './components/TopBar';
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

  // Modals state
  const [showAddExpense, setShowAddExpense] = useState(false);
  const [showSettleUp, setShowSettleUp] = useState(false);
  const [showRequestMoney, setShowRequestMoney] = useState(false);
  const [showAddFriend, setShowAddFriend] = useState(false);
  const [selectedCrossOffer, setSelectedCrossOffer] = useState<CrossSettlementOffer | null>(null);
  const [selectedReceipt, setSelectedReceipt] = useState<{
    txId: string;
    payerName: string;
    receiverName: string;
    amount: number;
  } | null>(null);

  // Active user (default to CURRENT_USER if null in internal logic)
  const activeUser = currentUser || CURRENT_USER;

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

  const handleCreateGroup = (groupData: { name: string; memberIds: string[] }) => {
    const memberUsers = users.filter((u) => groupData.memberIds.includes(u.id));
    const newGroup: Group = {
      id: `group_${Date.now()}`,
      name: groupData.name,
      emoji: '👥',
      category: 'Genel',
      createdBy: activeUser.id,
      members: memberUsers.map((u) => ({
        id: u.id,
        name: u.fullName,
        avatar: u.fullName.slice(0, 2).toUpperCase(),
        tag: u.tag || `@${u.username}`,
        balanceInGroup: 0
      })),
      createdAt: new Date().toISOString(),
      userBalance: 0,
      totalExpenses: 0
    };
    setGroups((prev) => [newGroup, ...prev]);
    FirestoreService.saveGroup(newGroup).catch(console.error);
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

  // 1. If not launched into web app, show high-converting Landing Page
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

  // Active Pending Cross Offer
  const pendingCrossOffer = crossOffers.find((o) => o.status === 'PENDING');

  // 3. Authenticated 1:1 Android Web App Layout
  return (
    <div className="min-h-screen bg-[#F8FAFC] text-textPrimary flex flex-col font-sans selection:bg-primaryEmeraldContainer selection:text-primaryEmerald">
      {/* Top Banner: Native Android APK Prompter */}
      {!dismissAppBanner && (
        <div className="bg-slate-900 text-white px-4 py-2.5 flex items-center justify-between text-[12px] shadow-sm z-30">
          <div className="flex items-center gap-2 max-w-xl truncate">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse flex-shrink-0" />
            <span className="font-semibold truncate">
              🚀 <strong>AradaPay Android:</strong> 100ms Kamera QR & Parmak İzi Kasası için uygulamayı yükleyin!
            </span>
          </div>

          <div className="flex items-center gap-2 flex-shrink-0">
            <a
              href="/AradaPay.apk"
              download="AradaPay.apk"
              className="px-3 py-1 rounded-full bg-primaryEmerald text-white font-bold text-[11px] hover:bg-[#00744d] transition flex items-center gap-1 shadow-2xs"
            >
              <Download className="w-3 h-3" />
              <span>APK İndir</span>
            </a>
            <button
              onClick={() => setDismissAppBanner(true)}
              className="text-slate-400 hover:text-white px-1.5 py-0.5 text-[14px]"
            >
              ✕
            </button>
          </div>
        </div>
      )}

      {/* Main Container */}
      <div className="flex-1 flex flex-col max-w-3xl mx-auto w-full bg-[#F8FAFC]">
        {/* Top Bar */}
        <TopBar
          user={activeUser}
          onProfileClick={() => setCurrentTab('settings')}
          hasNudges={nudges.length > 0}
        />

        {/* View Routing */}
        <main className="flex-1">
          {currentTab === 'dashboard' && (
            <div className="space-y-4 pb-28">
              {/* Financial Hero Card */}
              <FinancialHeroCard
                netBalance={balanceSummary.netBalance}
                totalReceivable={balanceSummary.totalReceivable}
                totalPayable={balanceSummary.totalPayable}
                isLocked={isLocked}
                onToggleLock={() => setIsLocked(!isLocked)}
              />

              {/* Action Buttons Row */}
              <ActionButtonsRow
                onAddExpenseClick={() => setShowAddExpense(true)}
                onSettleUpClick={() => setShowSettleUp(true)}
                onRequestMoneyClick={() => setShowRequestMoney(true)}
              />

              {/* Smart Settlement Banner (DFS Cycle Detection) */}
              <SmartSettlementBanner
                offers={crossOffers}
                currentUserId={activeUser.id}
                onOpenOffer={(offer) => setSelectedCrossOffer(offer)}
              />

              {/* Transactions List */}
              <TransactionsList
                expenses={expenses}
                currentUserId={activeUser.id}
                isLocked={isLocked}
                onSeeAllClick={() => setCurrentTab('analytics')}
                onExpenseClick={(exp) => {
                  const payer = users.find((u) => u.id === exp.paidBy);
                  setSelectedReceipt({
                    txId: exp.id,
                    payerName: payer?.fullName || 'Ödeyen',
                    receiverName: activeUser.fullName,
                    amount: exp.amount
                  });
                }}
              />
            </div>
          )}

          {currentTab === 'groups' && (
            <GroupsView
              groups={groups}
              currentUser={activeUser}
              users={users}
              isLocked={isLocked}
              onAddExpenseClick={() => setShowAddExpense(true)}
              onSaveGroup={(newGroup) => {
                setGroups((prev) => [newGroup, ...prev]);
                FirestoreService.saveGroup(newGroup).catch(console.error);
              }}
            />
          )}

          {currentTab === 'friends' && (
            <FriendsView
              currentUser={activeUser}
              users={users}
              expenses={expenses}
              settlements={settlements}
              isLocked={isLocked}
              onOpenSettleWithUser={() => setShowSettleUp(true)}
              onOpenNudgeWithUser={() => setShowRequestMoney(true)}
              onAddFriend={handleAddFriend}
            />
          )}

          {currentTab === 'analytics' && (
            <AnalyticsView
              expenses={expenses}
              settlements={settlements}
              currentUserId={activeUser.id}
              isLocked={isLocked}
            />
          )}

          {currentTab === 'settings' && (
            <SettingsView
              currentUser={activeUser}
              isLocked={isLocked}
              onToggleLock={() => setIsLocked(!isLocked)}
              onWipeData={handleWipeData}
              onLogout={handleLogout}
            />
          )}
        </main>
      </div>

      {/* Material 3 Bottom Navigation Bar */}
      <BottomNavBar
        currentTab={currentTab}
        onTabChange={(tab) => setCurrentTab(tab)}
      />

      {/* Modals & Drawers */}
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
        onClose={() => setShowSettleUp(false)}
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
