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
import { SmartSettlementReportModal } from './components/SmartSettlementReportModal';
import { EditProfileModal } from './components/EditProfileModal';
import { LandingPage } from './components/LandingPage';
import { AuthScreen } from './components/AuthScreen';
import { GroupsView } from './views/GroupsView';
import { FriendsView } from './views/FriendsView';
import { GroupDetailView } from './views/GroupDetailView';
import { FriendDetailView } from './views/FriendDetailView';
import { AnalyticsView } from './views/AnalyticsView';
import { SettingsView } from './views/SettingsView';
import { Download, Bell, Plus, CreditCard, Send, Sparkles, Shield, Eye, EyeOff } from 'lucide-react';

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
  const [selectedGroupId, setSelectedGroupId] = useState<string | null>(null);
  const [selectedFriendId, setSelectedFriendId] = useState<string | null>(null);

  const [isLocked, setIsLocked] = useState<boolean>(false);

  // Active user (default to CURRENT_USER if null in internal logic)
  const activeUser = currentUser || CURRENT_USER;

  // Modals state
  const [showAddExpense, setShowAddExpense] = useState(false);
  const [preselectedGroupForExpense, setPreselectedGroupForExpense] = useState<Group | undefined>(undefined);
  const [showSettleUp, setShowSettleUp] = useState(false);
  const [settlePreselectedUser, setSettlePreselectedUser] = useState<User | null>(null);
  const [settleInitialAmount, setSettleInitialAmount] = useState<number | undefined>(undefined);
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
  const handleTabChange = (tab: NavTab) => {
    setSelectedGroupId(null);
    setSelectedFriendId(null);
    setCurrentTab(tab);
  };

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

  const handleAddMemberToGroup = (groupId: string, newMember: User) => {
    setGroups((prev) =>
      prev.map((g) => {
        if (g.id === groupId) {
          const updated: Group = {
            ...g,
            members: [
              ...g.members,
              {
                id: newMember.id,
                name: newMember.fullName,
                avatar: newMember.fullName.slice(0, 2).toUpperCase(),
                tag: newMember.tag || '',
                balanceInGroup: 0
              }
            ]
          };
          FirestoreService.saveGroup(updated).catch(console.error);
          return updated;
        }
        return g;
      })
    );
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
    <div className="min-h-screen bg-[#F2F2F7] text-[#1C1C1E] flex flex-row font-sans selection:bg-emerald-100 selection:text-emerald-900 overflow-x-hidden">
      {/* ========================================================================= */}
      {/* A. DESKTOP macOS SIDEBAR (Visible on md: >= 768px) */}
      {/* ========================================================================= */}
      <DesktopSidebar
        currentTab={currentTab}
        onTabChange={handleTabChange}
        currentUser={activeUser}
        onOpenAddExpense={() => {
          setPreselectedGroupForExpense(undefined);
          setShowAddExpense(true);
        }}
        onOpenSettleUp={() => {
          setSettlePreselectedUser(null);
          setSettleInitialAmount(undefined);
          setShowSettleUp(true);
        }}
        onLogout={handleLogout}
      />

      {/* ========================================================================= */}
      {/* B. MAIN VIEWPORT & WORKSPACE */}
      {/* ========================================================================= */}
      <div className="flex-1 flex flex-col min-w-0 min-h-screen bg-[#F6F6F6]">
        {/* Mobile iOS Top Bar (Visible only on mobile < 768px) */}
        <div className="md:hidden">
          <TopBar
            user={activeUser}
            onProfileClick={() => handleTabChange('settings')}
            hasNudges={nudges.length > 0}
          />
        </div>

        {/* Desktop macOS Toolbar (Visible only on md: >= 768px) */}
        <header className="hidden md:flex items-center justify-between px-8 py-3 bg-[#FFFFFF]/80 backdrop-blur-2xl border-b border-black/[0.08] sticky top-0 z-30 select-none">
          {/* Breadcrumb Navigation */}
          <div className="flex items-center gap-2 text-[13px]">
            <span className="font-semibold text-[#8E8E93]">AradaPay</span>
            <span className="text-[#C7C7CC]">/</span>
            <span className="font-bold text-[#1C1C1E]">
              {selectedGroupId
                ? `Gruplar / ${groups.find((g) => g.id === selectedGroupId)?.name || 'Grup'}`
                : selectedFriendId
                ? `Arkadaşlar / ${users.find((u) => u.id === selectedFriendId)?.fullName || 'Arkadaş'}`
                : currentTab === 'dashboard'
                ? 'Ana Panel'
                : currentTab === 'groups'
                ? 'Gruplarım'
                : currentTab === 'friends'
                ? 'Arkadaşlarım'
                : currentTab === 'analytics'
                ? 'Finansal Analiz & DFS Raporu'
                : 'Ayarlar & Güvenlik Kasası'}
            </span>
          </div>

          {/* macOS Desktop Toolbar Actions */}
          <div className="flex items-center gap-3">
            {/* Quick Action Button */}
            <button
              onClick={() => {
                setPreselectedGroupForExpense(undefined);
                setShowAddExpense(true);
              }}
              className="px-3.5 py-1.5 rounded-[8px] bg-[#00875A] hover:bg-[#00744d] text-white text-[12px] font-bold flex items-center gap-1.5 transition shadow-sm shadow-emerald-900/10"
            >
              <Plus className="w-3.5 h-3.5 stroke-[2.5]" />
              <span>Harcama Ekle</span>
            </button>

            <button
              onClick={() => {
                setSettlePreselectedUser(null);
                setSettleInitialAmount(undefined);
                setShowSettleUp(true);
              }}
              className="px-3.5 py-1.5 rounded-[8px] bg-white border border-black/[0.1] hover:bg-slate-50 text-[#1C1C1E] text-[12px] font-bold flex items-center gap-1.5 transition shadow-2xs"
            >
              <CreditCard className="w-3.5 h-3.5 text-[#00875A]" />
              <span>Öde & Fitleş</span>
            </button>

            {/* Privacy Eye Toggle */}
            <button
              onClick={() => setIsLocked(!isLocked)}
              className="px-3 py-1.5 rounded-[8px] bg-black/5 hover:bg-black/10 text-[#1C1C1E] text-[12px] font-bold flex items-center gap-1.5 transition"
              title="Bakiye Maskesini Aç/Kapat"
            >
              {isLocked ? <EyeOff className="w-3.5 h-3.5 text-[#8E8E93]" /> : <Eye className="w-3.5 h-3.5 text-[#00875A]" />}
              <span>{isLocked ? 'Gizli' : 'Görünür'}</span>
            </button>

            {/* Notification Badge */}
            <button
              onClick={() => handleTabChange('analytics')}
              className="relative p-2 rounded-[8px] bg-black/5 hover:bg-black/10 text-[#1C1C1E] transition"
              title="Dürtmeler ve Bildirimler"
            >
              {nudges.length > 0 && (
                <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-[#D32F2F] ring-2 ring-white animate-pulse" />
              )}
              <Bell className="w-3.5 h-3.5 text-[#1C1C1E]" />
            </button>

            {/* Profile Avatar Capsule */}
            <div
              onClick={() => handleTabChange('settings')}
              className="flex items-center gap-2 px-2.5 py-1 rounded-[8px] bg-black/5 hover:bg-black/10 cursor-pointer transition select-none"
            >
              <div className="w-6 h-6 rounded-full bg-[#00875A] text-white font-extrabold text-[10px] flex items-center justify-center">
                {activeUser.fullName.slice(0, 2).toUpperCase()}
              </div>
              <span className="text-[12px] font-bold text-[#1C1C1E]">
                {activeUser.fullName.split(' ')[0]}
              </span>
            </div>
          </div>
        </header>

        {/* Main Content Area */}
        <main className="flex-1 w-full max-w-6xl mx-auto px-4 sm:px-8 py-6 pb-28 md:pb-12 space-y-6">
          {/* 1. DEDICATED GROUP DETAIL PAGE */}
          {selectedGroupId ? (
            <GroupDetailView
              groupId={selectedGroupId}
              groups={groups}
              currentUser={activeUser}
              users={users}
              expenses={expenses}
              isLocked={isLocked}
              onBack={() => setSelectedGroupId(null)}
              onAddExpenseInGroup={(g) => {
                setPreselectedGroupForExpense(g);
                setShowAddExpense(true);
              }}
              onOpenSettleUp={(targetUser, amount) => {
                setSettlePreselectedUser(targetUser);
                setSettleInitialAmount(amount);
                setShowSettleUp(true);
              }}
              onViewExpenseDetail={(exp) => setSelectedExpenseForDetail(exp)}
              onAddMemberToGroup={handleAddMemberToGroup}
            />
          ) : selectedFriendId ? (
            /* 2. DEDICATED FRIEND DETAIL PAGE */
            <FriendDetailView
              friendId={selectedFriendId}
              currentUser={activeUser}
              users={users}
              expenses={expenses}
              settlements={settlements}
              groups={groups}
              isLocked={isLocked}
              onBack={() => setSelectedFriendId(null)}
              onOpenSettleUp={(friend, amount) => {
                setSettlePreselectedUser(friend);
                setSettleInitialAmount(amount);
                setShowSettleUp(true);
              }}
              onOpenNudge={() => setShowRequestMoney(true)}
              onOpenAddExpense={() => setShowAddExpense(true)}
              onViewExpenseDetail={(exp) => setSelectedExpenseForDetail(exp)}
              onNavigateToGroup={(groupId) => {
                setSelectedFriendId(null);
                setSelectedGroupId(groupId);
                setCurrentTab('groups');
              }}
            />
          ) : (
            /* 3. PRIMARY TABS */
            <>
              {currentTab === 'dashboard' && (
                <div className="space-y-6">
                  {/* macOS 2-Column Responsive Layout */}
                  <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
                    {/* Left Column: Balance, Actions, DFS Smart Settlement */}
                    <div className="md:col-span-7 space-y-6">
                      {/* Financial Hero Card */}
                      <FinancialHeroCard
                        netBalance={balanceSummary.netBalance}
                        totalReceivable={balanceSummary.totalReceivable}
                        totalPayable={balanceSummary.totalPayable}
                        isLocked={isLocked}
                        onToggleLock={() => setIsLocked(!isLocked)}
                      />

                      {/* Action Buttons Row (Mobile only) */}
                      <div className="md:hidden">
                        <ActionButtonsRow
                          onAddExpenseClick={() => {
                            setPreselectedGroupForExpense(undefined);
                            setShowAddExpense(true);
                          }}
                          onSettleUpClick={() => {
                            setSettlePreselectedUser(null);
                            setSettleInitialAmount(undefined);
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

                    {/* Right Column: Recent Transactions & Proofs */}
                    <div className="md:col-span-5 space-y-6">
                      <TransactionsList
                        expenses={expenses}
                        currentUserId={activeUser.id}
                        isLocked={isLocked}
                        onSeeAllClick={() => handleTabChange('analytics')}
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
                  onSelectGroup={(g) => setSelectedGroupId(g.id)}
                  onAddExpenseClick={(g) => {
                    setPreselectedGroupForExpense(g);
                    setShowAddExpense(true);
                  }}
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
                  groups={groups}
                  isLocked={isLocked}
                  onSelectFriend={(f) => setSelectedFriendId(f.id)}
                  onOpenSettleWithUser={(user, amount) => {
                    setSettlePreselectedUser(user);
                    setSettleInitialAmount(amount);
                    setShowSettleUp(true);
                  }}
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
            </>
          )}
        </main>
      </div>

      {/* ========================================================================= */}
      {/* C. MOBILE iOS BOTTOM TAB BAR (Visible ONLY on mobile < 768px) */}
      {/* ========================================================================= */}
      <BottomNavBar
        currentTab={currentTab}
        onTabChange={handleTabChange}
      />

      {/* ========================================================================= */}
      {/* D. APPLE HIG MODALS & DRAWERS */}
      {/* ========================================================================= */}
      <AddExpenseModal
        isOpen={showAddExpense}
        onClose={() => {
          setShowAddExpense(false);
          setPreselectedGroupForExpense(undefined);
        }}
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
          setSettleInitialAmount(undefined);
        }}
        currentUser={activeUser}
        users={users}
        initialTargetUser={settlePreselectedUser || undefined}
        initialAmount={settleInitialAmount}
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
