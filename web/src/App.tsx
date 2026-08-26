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
import { LandingPage } from './components/LandingPage';
import { GroupsView } from './views/GroupsView';
import { FriendsView } from './views/FriendsView';
import { AnalyticsView } from './views/AnalyticsView';
import { SettingsView } from './views/SettingsView';
import { Smartphone, Download, ArrowLeft } from 'lucide-react';

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
  const [inWebApp, setInWebApp] = useState<boolean>(() => {
    // If URL has ?app=true or previously opened, default to web app, else show landing page
    if (typeof window !== 'undefined') {
      const params = new URLSearchParams(window.location.search);
      return params.get('app') === 'true';
    }
    return false;
  });

  const [currentUser] = useState<User>(CURRENT_USER);
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

    const unsubNudges = FirestoreService.subscribeNudges(currentUser.id, (liveNudges) => {
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
  }, [currentUser.id]);

  // Live calculation of net balance
  const netBalances = useMemo(() => {
    return NetBalanceCalculator.calculateNetBalances(expenses, settlements);
  }, [expenses, settlements]);

  const myNetBalance = netBalances.get(currentUser.id) || 0;

  // Real-time DFS Cycle detection
  useEffect(() => {
    const pairwiseMatrix = new Map<string, number>();

    expenses.forEach((exp) => {
      if (exp.status === 'REJECTED') return;
      const payerId = exp.paidBy;
      exp.splits?.forEach((s) => {
        if (s.userId !== payerId) {
          const key = `${s.userId}_${payerId}`;
          pairwiseMatrix.set(key, (pairwiseMatrix.get(key) || 0) + s.amountOwed);
        }
      });
    });

    settlements.forEach((s) => {
      if (s.status === 'REJECTED') return;
      const key = `${s.payerId}_${s.receiverId}`;
      pairwiseMatrix.set(key, Math.max(0, (pairwiseMatrix.get(key) || 0) - s.amount));
    });

    const usersMap = new Map(users.map((u) => [u.id, u]));
    const detectedOffers = CrossSettlementDfsEngine.detectCrossSettlementCycles(usersMap, pairwiseMatrix);

    if (detectedOffers.length > 0) {
      setCrossOffers((prev) => {
        const existingIds = new Set(prev.map((o) => o.id));
        const newOnes = detectedOffers.filter((o) => !existingIds.has(o.id));
        return [...prev, ...newOnes];
      });
    }
  }, [expenses, settlements, users]);

  // Handlers
  const handleDirectDownload = () => {
    const link = document.createElement('a');
    link.href = '/AradaPay.apk';
    link.download = 'AradaPay.apk';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleAddExpense = async (newExpense: Expense) => {
    setExpenses((prev) => [newExpense, ...prev]);
    await FirestoreService.addExpense(newExpense);
  };

  const handleConfirmSettlement = async (settlement: Settlement) => {
    setSettlements((prev) => [settlement, ...prev]);
    await FirestoreService.addSettlement(settlement);
  };

  const handleShowReceiptForSettlement = (settlement: Settlement) => {
    const payer = users.find((u) => u.id === settlement.payerId);
    const receiver = users.find((u) => u.id === settlement.receiverId);
    setSelectedReceipt({
      txId: settlement.id,
      payerName: payer?.fullName || 'Dilovan Toprak',
      receiverName: receiver?.fullName || 'Alıcı',
      amount: settlement.amount
    });
  };

  const handleExpenseClick = (expense: Expense) => {
    const payer = users.find((u) => u.id === expense.paidBy);
    setSelectedReceipt({
      txId: expense.id,
      payerName: payer?.fullName || 'Dilovan Toprak',
      receiverName: `${expense.splits.length} Katılımcı`,
      amount: expense.amount
    });
  };

  const handleApproveCrossOffer = async (offerId: string) => {
    setCrossOffers((prev) =>
      prev.map((o) => {
        if (o.id === offerId) {
          const newApprovals = { ...o.approvals, [currentUser.id]: true };
          const allApproved = Object.values(newApprovals).every(Boolean);
          return {
            ...o,
            approvals: newApprovals,
            status: allApproved ? 'APPROVED' : 'PENDING'
          };
        }
        return o;
      })
    );
    await FirestoreService.updateCrossApproval(offerId, currentUser.id, true);
  };

  const handleSendNudge = async (nudge: Nudge) => {
    setNudges((prev) => [nudge, ...prev]);
    await FirestoreService.sendNudge(nudge);
  };

  const handleSaveGroup = async (group: Group) => {
    setGroups((prev) => [group, ...prev]);
    await FirestoreService.saveGroup(group);
  };

  const handleWipeData = () => {
    setExpenses([]);
    setSettlements([]);
    setGroups([]);
    setNudges([]);
    localStorage.clear();
    alert('Verileriniz KVKK m.11 kapsamında başarıyla sıfırlandı.');
    setCurrentTab('dashboard');
  };

  // If user is on landing page
  if (!inWebApp) {
    return <LandingPage onLaunchWebApp={() => setInWebApp(true)} />;
  }

  return (
    <div className="min-h-screen bg-[#F8FAFC] text-textPrimary flex flex-col font-sans">
      {/* Top Banner: Native Android App Download Prompt */}
      {!dismissAppBanner && (
        <div className="bg-primaryEmerald text-white px-4 py-2 text-[12px] flex items-center justify-between shadow-xs">
          <div className="max-w-2xl mx-auto flex items-center justify-between w-full">
            <div className="flex items-center gap-2">
              <Smartphone className="w-4 h-4 flex-shrink-0" />
              <span className="font-medium">
                En iyi deneyim için <strong className="font-bold">Android Uygulamasını</strong> indirin
              </span>
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={handleDirectDownload}
                className="px-2.5 py-1 bg-white text-primaryEmerald rounded-lg font-bold text-[11px] flex items-center gap-1 hover:bg-slate-100 active:scale-95 transition"
              >
                <Download className="w-3 h-3" />
                <span>APK İndir</span>
              </button>
              <button
                onClick={() => setDismissAppBanner(true)}
                className="text-white/80 hover:text-white text-[14px] px-1"
                title="Kapat"
              >
                ✕
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Top Bar Header */}
      <TopBar
        user={currentUser}
        onProfileClick={() => setCurrentTab('settings')}
        onNotificationClick={() => setShowRequestMoney(true)}
        hasNudges={nudges.length > 0}
      />

      {/* Back to Landing Page floating button */}
      <div className="max-w-2xl mx-auto w-full px-5 pt-2 flex items-center justify-between">
        <button
          onClick={() => setInWebApp(false)}
          className="text-[12px] text-textSecondary hover:text-primaryEmerald font-semibold flex items-center gap-1 py-1"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>Tanıtım & İndirme Sayfasına Dön</span>
        </button>

        <button
          onClick={handleDirectDownload}
          className="text-[12px] text-primaryEmerald font-bold hover:underline flex items-center gap-1 py-1"
        >
          <Download className="w-3.5 h-3.5" />
          <span>AradaPay.apk</span>
        </button>
      </div>

      {/* Main Tab Content */}
      <main className="flex-1 w-full max-w-2xl mx-auto">
        {currentTab === 'dashboard' && (
          <div className="pb-24">
            {/* 1. Net Bakiye Hero */}
            <FinancialHeroCard
              netBalance={myNetBalance}
              isLocked={isLocked}
              onToggleLock={() => setIsLocked(!isLocked)}
            />

            {/* 2. Harcama Ekle & Öde Butonları */}
            <ActionButtonsRow
              onAddExpenseClick={() => setShowAddExpense(true)}
              onSettleUpClick={() => setShowSettleUp(true)}
              onRequestMoneyClick={() => setShowRequestMoney(true)}
            />

            {/* 3. DFS Akıllı Mahsuplaşma Teklifi Varsa Göster */}
            <SmartSettlementBanner
              offers={crossOffers}
              currentUserId={currentUser.id}
              onOpenOffer={(offer) => setSelectedCrossOffer(offer)}
            />

            {/* 4. Son Hareketler Listesi */}
            <TransactionsList
              expenses={expenses}
              currentUserId={currentUser.id}
              isLocked={isLocked}
              onSeeAllClick={() => setCurrentTab('analytics')}
              onExpenseClick={handleExpenseClick}
            />
          </div>
        )}

        {currentTab === 'groups' && (
          <GroupsView
            groups={groups}
            currentUser={currentUser}
            users={users}
            isLocked={isLocked}
            onAddExpenseClick={() => setShowAddExpense(true)}
            onSaveGroup={handleSaveGroup}
          />
        )}

        {currentTab === 'friends' && (
          <FriendsView
            currentUser={currentUser}
            users={users}
            expenses={expenses}
            settlements={settlements}
            isLocked={isLocked}
            onOpenSettleWithUser={(u) => setShowSettleUp(true)}
            onOpenNudgeWithUser={(u) => setShowRequestMoney(true)}
            onAddFriend={(tag) => {
              const newUser: User = {
                id: `user_${Date.now()}`,
                email: `${tag.toLowerCase().replace('#', '')}@ardabank.com`,
                username: tag.split('#')[0].toLowerCase(),
                fullName: tag.split('#')[0],
                avatarUrl: '',
                tag,
                createdAt: new Date().toISOString()
              };
              setUsers((prev) => [...prev, newUser]);
            }}
          />
        )}

        {currentTab === 'analytics' && (
          <AnalyticsView
            expenses={expenses}
            settlements={settlements}
            currentUserId={currentUser.id}
            isLocked={isLocked}
          />
        )}

        {currentTab === 'settings' && (
          <SettingsView
            currentUser={currentUser}
            isLocked={isLocked}
            onToggleLock={() => setIsLocked(!isLocked)}
            onWipeData={handleWipeData}
          />
        )}
      </main>

      {/* Bottom Navigation Bar */}
      <BottomNavBar currentTab={currentTab} onTabChange={setCurrentTab} />

      {/* Modals & Dialogs */}
      <AddExpenseModal
        isOpen={showAddExpense}
        onClose={() => setShowAddExpense(false)}
        currentUser={currentUser}
        users={users}
        groups={groups}
        onAddExpense={handleAddExpense}
      />

      <SettleUpModal
        isOpen={showSettleUp}
        onClose={() => setShowSettleUp(false)}
        currentUser={currentUser}
        users={users}
        onConfirmSettlement={handleConfirmSettlement}
        onShowReceipt={handleShowReceiptForSettlement}
      />

      <RequestMoneyDrawer
        isOpen={showRequestMoney}
        onClose={() => setShowRequestMoney(false)}
        currentUser={currentUser}
        users={users}
        onSendNudge={handleSendNudge}
      />

      <SmartSettlementModal
        isOpen={!!selectedCrossOffer}
        onClose={() => setSelectedCrossOffer(null)}
        offer={selectedCrossOffer}
        currentUser={currentUser}
        onApproveOffer={handleApproveCrossOffer}
      />

      <MerkleReceiptModal
        isOpen={!!selectedReceipt}
        onClose={() => setSelectedReceipt(null)}
        txId={selectedReceipt?.txId}
        payerName={selectedReceipt?.payerName}
        receiverName={selectedReceipt?.receiverName}
        amount={selectedReceipt?.amount}
      />
    </div>
  );
}

export default App;
