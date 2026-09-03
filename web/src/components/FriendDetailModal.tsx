'use client';

import React, { useState } from 'react';
import {
  X,
  ArrowLeft,
  Copy,
  Check,
  Send,
  CreditCard,
  Plus,
  Receipt,
  Users,
  ShieldCheck,
  CheckCircle2,
  Building2,
  ChevronRight
} from 'lucide-react';
import { User, Expense, Settlement, Group } from '../types';

interface FriendDetailModalProps {
  isOpen: boolean;
  onClose: () => void;
  friend: User | null;
  currentUser: User;
  expenses: Expense[];
  settlements: Settlement[];
  groups: Group[];
  isLocked: boolean;
  onOpenSettleUp: (friend: User) => void;
  onOpenNudge: (friend: User) => void;
  onOpenAddExpense: (friend: User) => void;
  onViewExpenseDetail: (expense: Expense) => void;
}

export const FriendDetailModal: React.FC<FriendDetailModalProps> = ({
  isOpen,
  onClose,
  friend,
  currentUser,
  expenses,
  settlements,
  groups,
  isLocked,
  onOpenSettleUp,
  onOpenNudge,
  onOpenAddExpense,
  onViewExpenseDetail
}) => {
  const [filter, setFilter] = useState<'all' | 'expenses' | 'settlements'>('all');
  const [copiedIban, setCopiedIban] = useState(false);

  if (!isOpen || !friend) return null;

  // Calculate bilateral balance with this friend
  let balance = 0;
  expenses.forEach((exp) => {
    if (exp.paidBy === currentUser.id) {
      const split = exp.splits.find((s) => s.userId === friend.id);
      if (split) balance += split.amountOwed;
    } else if (exp.paidBy === friend.id) {
      const split = exp.splits.find((s) => s.userId === currentUser.id);
      if (split) balance -= split.amountOwed;
    }
  });

  settlements.forEach((set) => {
    if (set.payerId === currentUser.id && set.receiverId === friend.id) {
      balance += set.amount;
    } else if (set.payerId === friend.id && set.receiverId === currentUser.id) {
      balance -= set.amount;
    }
  });

  const isPositive = balance >= 0;
  const hasBalance = Math.abs(balance) > 0.01;

  // Shared expenses
  const sharedExpenses = expenses.filter(
    (exp) =>
      (exp.paidBy === currentUser.id && exp.splits.some((s) => s.userId === friend.id)) ||
      (exp.paidBy === friend.id && exp.splits.some((s) => s.userId === currentUser.id))
  );

  // Shared settlements
  const sharedSettlements = settlements.filter(
    (set) =>
      (set.payerId === currentUser.id && set.receiverId === friend.id) ||
      (set.payerId === friend.id && set.receiverId === currentUser.id)
  );

  // Shared groups
  const sharedGroups = groups.filter((g) =>
    g.members.some((m) => m.id === friend.id) && g.members.some((m) => m.id === currentUser.id)
  );

  const handleCopyIban = () => {
    if (friend.iban) {
      navigator.clipboard.writeText(friend.iban.replace(/\s+/g, ''));
      setCopiedIban(true);
      setTimeout(() => setCopiedIban(false), 2000);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full max-w-lg rounded-t-[32px] sm:rounded-[28px] shadow-apple-modal border border-black/[0.08] overflow-hidden flex flex-col max-h-[92vh] animate-appleSheet">
        {/* Mobile iOS Drag Handle */}
        <div className="w-12 h-1.5 bg-black/15 rounded-full mx-auto mt-3 sm:hidden" />

        {/* Top App Bar */}
        <div className="px-5 py-3.5 border-b border-black/[0.06] flex items-center justify-between bg-white/80 backdrop-blur-md">
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-black/5 flex items-center justify-center text-[#1C1C1E] hover:bg-black/10 active:scale-95 transition"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>

          <h3 className="text-[17px] font-bold text-[#1C1C1E] tracking-tight">
            Arkadaş Detayı
          </h3>

          <div className="w-9" />
        </div>

        {/* Modal Body */}
        <div className="p-5 sm:p-6 overflow-y-auto flex-1 space-y-5 text-left">
          {/* Friend Profile & Bilateral Balance Card */}
          <div className="p-6 rounded-[24px] bg-[#F2F2F7] border border-black/[0.04] text-center space-y-3">
            <div className="w-16 h-16 rounded-full bg-emerald-100 text-[#00875A] border-2 border-emerald-300 flex items-center justify-center font-extrabold text-[20px] mx-auto shadow-apple-sm">
              {friend.fullName.slice(0, 2).toUpperCase()}
            </div>

            <div>
              <h4 className="text-[20px] font-extrabold text-[#1C1C1E]">
                {friend.fullName}
              </h4>
              <p className="text-[12px] font-mono text-[#00875A] font-bold">
                {friend.tag || `@${friend.username}`}
              </p>
            </div>

            {/* Bilateral Balance Display */}
            <div className="pt-2">
              <span
                className={`text-[11px] font-bold uppercase tracking-wider block ${
                  isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                }`}
              >
                {!hasBalance
                  ? 'FİTLEŞİLDİ (HESAPLAR DENK)'
                  : isPositive
                  ? 'SANA BORÇLU'
                  : 'SEN BORÇLUSUN'}
              </span>

              <div
                className={`text-[36px] font-black font-tabular tracking-tight ${
                  isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                }`}
              >
                {isLocked
                  ? '•••• ₺'
                  : `${isPositive ? '+' : ''}${balance.toLocaleString('tr-TR', {
                      minimumFractionDigits: 2,
                      maximumFractionDigits: 2
                    })} ₺`}
              </div>
            </div>

            {/* IBAN Copy Pill */}
            {friend.iban && (
              <div className="p-2.5 rounded-[14px] bg-white border border-black/[0.06] flex items-center justify-between mt-2">
                <div className="truncate text-left mr-2">
                  <span className="text-[9px] font-bold text-[#8E8E93] uppercase block">
                    FAST IBAN
                  </span>
                  <span className="text-[12px] font-mono font-bold text-[#1C1C1E] truncate block">
                    {friend.iban}
                  </span>
                </div>
                <button
                  onClick={handleCopyIban}
                  className="px-2.5 py-1 rounded-[10px] bg-black/5 hover:bg-black/10 text-[#1C1C1E] text-[11px] font-bold flex items-center gap-1 transition flex-shrink-0"
                >
                  {copiedIban ? <Check className="w-3 h-3 text-[#00875A]" /> : <Copy className="w-3 h-3" />}
                  <span>{copiedIban ? 'Kopyalandı' : 'Kopyala'}</span>
                </button>
              </div>
            )}
          </div>

          {/* Quick Action Buttons Row */}
          <div className="grid grid-cols-3 gap-2.5">
            <button
              onClick={() => {
                onClose();
                onOpenSettleUp(friend);
              }}
              className="p-3 rounded-[16px] bg-[#00875A] text-white flex flex-col items-center justify-center gap-1 text-[12px] font-bold hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-800/20"
            >
              <CreditCard className="w-4 h-4" />
              <span>Fitleş</span>
            </button>

            <button
              onClick={() => {
                onClose();
                onOpenNudge(friend);
              }}
              className="p-3 rounded-[16px] bg-white border border-black/[0.08] text-[#1C1C1E] flex flex-col items-center justify-center gap-1 text-[12px] font-bold hover:bg-slate-50 active:scale-95 transition shadow-apple-sm"
            >
              <Send className="w-4 h-4 text-[#8E8E93]" />
              <span>Dürt</span>
            </button>

            <button
              onClick={() => {
                onClose();
                onOpenAddExpense(friend);
              }}
              className="p-3 rounded-[16px] bg-white border border-black/[0.08] text-[#1C1C1E] flex flex-col items-center justify-center gap-1 text-[12px] font-bold hover:bg-slate-50 active:scale-95 transition shadow-apple-sm"
            >
              <Plus className="w-4 h-4 text-[#8E8E93]" />
              <span>Masraf Bölüş</span>
            </button>
          </div>

          {/* Shared Groups */}
          {sharedGroups.length > 0 && (
            <div className="space-y-2">
              <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider block px-1">
                ORTAK GRUPLAR ({sharedGroups.length})
              </span>
              <div className="flex items-center gap-2 overflow-x-auto pb-1">
                {sharedGroups.map((g) => (
                  <div
                    key={g.id}
                    className="px-3 py-1.5 rounded-full bg-white border border-black/[0.06] text-[12px] font-bold text-[#1C1C1E] flex items-center gap-1.5 flex-shrink-0 shadow-2xs"
                  >
                    <span>{g.emoji}</span>
                    <span>{g.name}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Shared Transactions Feed */}
          <div className="space-y-2.5">
            <div className="flex items-center justify-between px-1">
              <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider">
                ORTAK HESAP GEÇMİŞİ
              </span>

              <div className="flex items-center gap-1 p-0.5 bg-black/5 rounded-full text-[11px] font-bold">
                <button
                  onClick={() => setFilter('all')}
                  className={`px-2 py-0.5 rounded-full transition ${
                    filter === 'all' ? 'bg-white text-[#1C1C1E] shadow-2xs' : 'text-[#8E8E93]'
                  }`}
                >
                  Tümü
                </button>
                <button
                  onClick={() => setFilter('expenses')}
                  className={`px-2 py-0.5 rounded-full transition ${
                    filter === 'expenses' ? 'bg-white text-[#1C1C1E] shadow-2xs' : 'text-[#8E8E93]'
                  }`}
                >
                  Harcamalar
                </button>
              </div>
            </div>

            <div className="apple-card divide-y divide-black/[0.04] overflow-hidden">
              {sharedExpenses.length === 0 ? (
                <div className="p-6 text-center text-[#8E8E93] text-[13px]">
                  Bu arkadaşınızla henüz kayıtlı ortak harcama bulunmuyor.
                </div>
              ) : (
                sharedExpenses.map((expense) => {
                  const isPayerMe = expense.paidBy === currentUser.id;
                  const mySplit = expense.splits.find((s) => s.userId === currentUser.id);
                  const amount = isPayerMe
                    ? expense.amount - (mySplit?.amountOwed || 0)
                    : mySplit?.amountOwed || 0;

                  return (
                    <div
                      key={expense.id}
                      onClick={() => onViewExpenseDetail(expense)}
                      className="p-3.5 flex items-center justify-between hover:bg-black/[0.02] cursor-pointer transition"
                    >
                      <div>
                        <div className="text-[14px] font-bold text-[#1C1C1E]">
                          {expense.description}
                        </div>
                        <div className="text-[11px] text-[#8E8E93]">
                          {expense.date || new Date(expense.createdAt).toLocaleDateString('tr-TR')} • {isPayerMe ? 'Sen Ödedin' : `${friend.fullName} Ödedi`}
                        </div>
                      </div>

                      <div className="text-right flex items-center gap-2">
                        <div>
                          <span
                            className={`text-[13px] font-black font-tabular block ${
                              isPayerMe ? 'text-[#00875A]' : 'text-[#D32F2F]'
                            }`}
                          >
                            {isLocked ? '•••• ₺' : `${isPayerMe ? '+' : '-'}${amount.toFixed(2)} ₺`}
                          </span>
                        </div>
                        <ChevronRight className="w-3.5 h-3.5 text-[#C7C7CC]" />
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
