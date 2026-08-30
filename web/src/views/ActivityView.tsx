'use client';

import React, { useState, useMemo } from 'react';
import {
  Receipt,
  Search,
  ArrowUpRight,
  ArrowDownLeft,
  CheckCircle2,
  Send,
  Sparkles,
  ShieldCheck,
  ChevronRight,
  Utensils,
  ShoppingCart,
  Plane,
  Home,
  Film,
  Zap,
  Tag,
  CreditCard,
  History,
  LayoutGrid,
  Bell
} from 'lucide-react';
import { Expense, Settlement, Nudge, User, ExpenseCategory } from '../types';

export type ActivityFilter = 'ALL' | 'RECEIVABLES' | 'PAYABLES' | 'SETTLEMENTS' | 'REQUESTS';

interface ActivityViewProps {
  expenses: Expense[];
  settlements: Settlement[];
  nudges: Nudge[];
  currentUser: User;
  users: User[];
  isLocked: boolean;
  onExpenseClick: (expense: Expense) => void;
  onReceiptClick: (txId: string) => void;
  onSettleClick: (targetUser: User, amount?: number) => void;
}

const getCategoryIcon = (category: ExpenseCategory) => {
  switch (category) {
    case 'DINING':
      return <Utensils className="w-4 h-4 text-[#00875A]" />;
    case 'GROCERIES':
      return <ShoppingCart className="w-4 h-4 text-blue-600" />;
    case 'TRAVEL':
      return <Plane className="w-4 h-4 text-purple-600" />;
    case 'HOUSING':
      return <Home className="w-4 h-4 text-amber-600" />;
    case 'ENTERTAINMENT':
      return <Film className="w-4 h-4 text-pink-600" />;
    case 'UTILITIES':
      return <Zap className="w-4 h-4 text-indigo-600" />;
    default:
      return <Tag className="w-4 h-4 text-[#8E8E93]" />;
  }
};

export const ActivityView: React.FC<ActivityViewProps> = ({
  expenses,
  settlements,
  nudges,
  currentUser,
  users,
  isLocked,
  onExpenseClick,
  onReceiptClick,
  onSettleClick
}) => {
  const [filter, setFilter] = useState<ActivityFilter>('ALL');
  const [searchQuery, setSearchQuery] = useState('');

  // Normalize all activities into a unified timeline
  const activities = useMemo(() => {
    const list: {
      id: string;
      type: 'EXPENSE' | 'SETTLEMENT' | 'NUDGE';
      date: string;
      title: string;
      subtitle: string;
      amount: number;
      isPositive: boolean;
      statusText: string;
      category?: ExpenseCategory;
      rawExpense?: Expense;
      rawSettlement?: Settlement;
      rawNudge?: Nudge;
      userRef?: User;
    }[] = [];

    // 1. Expenses
    expenses.forEach((exp) => {
      const payer = users.find((u) => u.id === exp.paidBy);
      const isPayerMe = exp.paidBy === currentUser.id;
      const mySplit = exp.splits.find((s) => s.userId === currentUser.id);

      let myAmount = 0;
      let isPos = false;
      let status = '';

      if (isPayerMe) {
        myAmount = exp.amount - (mySplit?.amountOwed || 0);
        isPos = true;
        status = 'alacaklısın';
      } else {
        myAmount = mySplit?.amountOwed || 0;
        isPos = false;
        status = 'sen borçlusun';
      }

      list.push({
        id: exp.id,
        type: 'EXPENSE',
        date: exp.date || exp.createdAt,
        title: exp.description,
        subtitle: `${isPayerMe ? 'Sen ödedin' : `${payer?.fullName || 'Arkadaş'} ödedi`} • ${exp.splits.length} kişi`,
        amount: myAmount,
        isPositive: isPos,
        statusText: status,
        category: exp.category,
        rawExpense: exp
      });
    });

    // 2. Settlements
    settlements.forEach((set) => {
      const isPayerMe = set.payerId === currentUser.id;
      const otherUser = users.find((u) => u.id === (isPayerMe ? set.receiverId : set.payerId));

      list.push({
        id: set.id,
        type: 'SETTLEMENT',
        date: set.createdAt,
        title: isPayerMe ? `Ödeme Yapıldı: ${otherUser?.fullName || 'Arkadaş'}` : `Ödeme Alındı: ${otherUser?.fullName || 'Arkadaş'}`,
        subtitle: 'FAST / Havale ile fitleşildi • Dekont hazır',
        amount: set.amount,
        isPositive: !isPayerMe,
        statusText: isPayerMe ? 'ödendi' : 'tahsil edildi',
        rawSettlement: set,
        userRef: otherUser
      });
    });

    // 3. Nudges
    nudges.forEach((nudge) => {
      const isFromMe = nudge.fromUserId === currentUser.id;
      const otherUser = users.find((u) => u.id === (isFromMe ? nudge.toUserId : nudge.fromUserId));

      list.push({
        id: nudge.id,
        type: 'NUDGE',
        date: nudge.createdAt,
        title: isFromMe ? `İstek Gönderildi: ${otherUser?.fullName || 'Arkadaş'}` : `Para İsteği Geldi: ${otherUser?.fullName || 'Arkadaş'}`,
        subtitle: nudge.message,
        amount: 0,
        isPositive: isFromMe,
        statusText: isFromMe ? 'hatırlatma' : 'ödeme bekleniyor',
        rawNudge: nudge,
        userRef: otherUser
      });
    });

    // Sort by date descending
    return list.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  }, [expenses, settlements, nudges, currentUser.id, users]);

  // Apply filters and search
  const filteredActivities = useMemo(() => {
    return activities.filter((act) => {
      const matchesSearch = searchQuery
        ? act.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
          act.subtitle.toLowerCase().includes(searchQuery.toLowerCase())
        : true;

      let matchesFilter = true;
      if (filter === 'RECEIVABLES') {
        matchesFilter = act.type === 'EXPENSE' && act.isPositive;
      } else if (filter === 'PAYABLES') {
        matchesFilter = act.type === 'EXPENSE' && !act.isPositive;
      } else if (filter === 'SETTLEMENTS') {
        matchesFilter = act.type === 'SETTLEMENT';
      } else if (filter === 'REQUESTS') {
        matchesFilter = act.type === 'NUDGE';
      }

      return matchesSearch && matchesFilter;
    });
  }, [activities, filter, searchQuery]);

  return (
    <div className="space-y-5 text-left animate-fadeIn">
      {/* Header */}
      <div className="px-1">
        <h2 className="text-[28px] font-extrabold text-[#1C1C1E] tracking-tight">Hareketler</h2>
        <p className="text-[13px] text-[#8E8E93]">Tüm harcama, FAST fitleşme ve ödeme hatırlatma geçmişi</p>
      </div>

      {/* Search & Filter Bar (With Clean Icons on all Chips) */}
      <div className="space-y-3">
        {/* Search */}
        <div className="relative">
          <Search className="w-4 h-4 text-[#8E8E93] absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="İşlem veya kişi ara..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full h-11 pl-10 pr-4 rounded-[14px] bg-white border border-black/[0.08] text-[13px] font-medium text-[#1C1C1E] focus:outline-none focus:border-[#00875A]"
          />
        </div>

        {/* Filter Pills with Icons and zero visible scrollbars */}
        <div className="flex items-center gap-2 overflow-x-auto pb-1 [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
          <button
            onClick={() => setFilter('ALL')}
            className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
              filter === 'ALL'
                ? 'bg-[#00875A] text-white shadow-2xs'
                : 'bg-white border border-black/[0.08] text-[#1C1C1E] hover:bg-slate-50'
            }`}
          >
            <LayoutGrid className="w-3.5 h-3.5" />
            <span>Tümü ({activities.length})</span>
          </button>

          <button
            onClick={() => setFilter('RECEIVABLES')}
            className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
              filter === 'RECEIVABLES'
                ? 'bg-[#00875A] text-white shadow-2xs'
                : 'bg-white border border-black/[0.08] text-[#00875A] hover:bg-slate-50'
            }`}
          >
            <ArrowDownLeft className="w-3.5 h-3.5" />
            <span>Alacaklar (+₺)</span>
          </button>

          <button
            onClick={() => setFilter('PAYABLES')}
            className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
              filter === 'PAYABLES'
                ? 'bg-[#D32F2F] text-white shadow-2xs'
                : 'bg-white border border-black/[0.08] text-[#D32F2F] hover:bg-slate-50'
            }`}
          >
            <ArrowUpRight className="w-3.5 h-3.5" />
            <span>Borçlar (-₺)</span>
          </button>

          <button
            onClick={() => setFilter('SETTLEMENTS')}
            className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
              filter === 'SETTLEMENTS'
                ? 'bg-[#00875A] text-white shadow-2xs'
                : 'bg-white border border-black/[0.08] text-[#1C1C1E] hover:bg-slate-50'
            }`}
          >
            <CreditCard className="w-3.5 h-3.5 text-[#00875A]" />
            <span>Fitleşmeler (FAST)</span>
          </button>

          <button
            onClick={() => setFilter('REQUESTS')}
            className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
              filter === 'REQUESTS'
                ? 'bg-[#6366F1] text-white shadow-2xs'
                : 'bg-white border border-black/[0.08] text-[#6366F1] hover:bg-slate-50'
            }`}
          >
            <Bell className="w-3.5 h-3.5 text-[#6366F1]" />
            <span>İstek & Onay</span>
          </button>
        </div>
      </div>

      {/* Activity Timeline Items */}
      <div className="space-y-3">
        {filteredActivities.length === 0 ? (
          <div className="apple-card p-10 text-center space-y-2">
            <History className="w-8 h-8 text-[#8E8E93] mx-auto opacity-50" />
            <p className="text-[14px] font-semibold text-[#1C1C1E]">Kayıtlı hareket bulunamadı</p>
            <p className="text-[12px] text-[#8E8E93]">Seçilen filtre kriterlerine uygun işlem kaydı yok.</p>
          </div>
        ) : (
          filteredActivities.map((act) => {
            const formattedDate = new Date(act.date).toLocaleDateString('tr-TR', {
              day: 'numeric',
              month: 'short',
              hour: '2-digit',
              minute: '2-digit'
            });

            return (
              <div
                key={act.id}
                onClick={() => {
                  if (act.rawExpense) {
                    onExpenseClick(act.rawExpense);
                  } else if (act.rawSettlement) {
                    onReceiptClick(act.rawSettlement.id);
                  }
                }}
                className="apple-card p-4 hover:border-black/[0.1] active:scale-[0.99] cursor-pointer transition flex items-center justify-between"
              >
                <div className="flex items-center gap-3.5">
                  <div className="w-12 h-12 rounded-[16px] bg-[#F2F2F7] border border-black/[0.04] flex items-center justify-center text-[18px] shadow-2xs flex-shrink-0">
                    {act.type === 'EXPENSE' ? (
                      getCategoryIcon(act.category || 'OTHER')
                    ) : act.type === 'SETTLEMENT' ? (
                      <CreditCard className="w-5 h-5 text-[#00875A]" />
                    ) : (
                      <Send className="w-5 h-5 text-indigo-600" />
                    )}
                  </div>

                  <div>
                    <h3 className="text-[15px] font-bold text-[#1C1C1E] line-clamp-1">{act.title}</h3>
                    <p className="text-[12px] text-[#8E8E93] mt-0.5 line-clamp-1">
                      {formattedDate} • {act.subtitle}
                    </p>
                  </div>
                </div>

                <div className="text-right flex items-center gap-3 flex-shrink-0 ml-2">
                  <div>
                    {act.type !== 'NUDGE' ? (
                      <>
                        <span
                          className={`text-[15px] font-black font-tabular block ${
                            act.isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                          }`}
                        >
                          {isLocked
                            ? '•••• ₺'
                            : `${act.isPositive ? '+' : '-'}${act.amount.toLocaleString('tr-TR', {
                                minimumFractionDigits: 2,
                                maximumFractionDigits: 2
                              })} ₺`}
                        </span>
                        <span className="text-[10px] text-[#8E8E93] block capitalize">
                          {act.statusText}
                        </span>
                      </>
                    ) : (
                      <span className="px-2.5 py-1 rounded-full bg-indigo-100 text-indigo-700 text-[11px] font-bold">
                        Dürtme
                      </span>
                    )}
                  </div>
                  <ChevronRight className="w-4 h-4 text-[#C7C7CC]" />
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
