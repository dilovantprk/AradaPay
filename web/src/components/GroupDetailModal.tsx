'use client';

import React, { useState } from 'react';
import {
  X,
  ArrowLeft,
  Users,
  Plus,
  CreditCard,
  Sparkles,
  Repeat,
  Receipt,
  UserPlus,
  ChevronRight,
  CheckCircle2,
  Calendar
} from 'lucide-react';
import { Group, User, Expense, GroupExpenseItem } from '../types';
import { DebtSimplifierEngine, SimplifiedTransaction } from '../algorithms/DebtSimplifierEngine';

interface GroupDetailModalProps {
  isOpen: boolean;
  onClose: () => void;
  group: Group | null;
  currentUser: User;
  users: User[];
  expenses: Expense[];
  isLocked: boolean;
  onOpenAddExpenseInGroup: (group: Group) => void;
  onOpenSettleUp: (targetUser: User, amount?: number) => void;
  onViewExpenseDetail: (expense: Expense) => void;
  onAddMemberToGroup: (groupId: string, newMember: User) => void;
}

export const GroupDetailModal: React.FC<GroupDetailModalProps> = ({
  isOpen,
  onClose,
  group,
  currentUser,
  users,
  expenses,
  isLocked,
  onOpenAddExpenseInGroup,
  onOpenSettleUp,
  onViewExpenseDetail,
  onAddMemberToGroup
}) => {
  const [activeTab, setActiveTab] = useState<'expenses' | 'members' | 'simplify'>('expenses');
  const [showAddMember, setShowAddMember] = useState(false);

  if (!isOpen || !group) return null;

  // Filter expenses for this group
  const groupExpenses = expenses.filter((e) => e.groupId === group.id);
  const totalGroupSpend = groupExpenses.reduce((sum, e) => sum + e.amount, 0);

  // Compute individual balances inside this group
  const memberBalances = new Map<string, number>();
  group.members.forEach((m) => memberBalances.set(m.id, 0));

  groupExpenses.forEach((exp) => {
    const currentPayer = memberBalances.get(exp.paidBy) || 0;
    memberBalances.set(exp.paidBy, currentPayer + exp.amount);

    exp.splits.forEach((split) => {
      const current = memberBalances.get(split.userId) || 0;
      memberBalances.set(split.userId, current - split.amountOwed);
    });
  });

  const myGroupBalance = memberBalances.get(currentUser.id) || 0;
  const isMyBalancePositive = myGroupBalance >= 0;

  // Run DebtSimplifierEngine on this group
  const simplifiedTransactions: SimplifiedTransaction[] = DebtSimplifierEngine.simplifyDebts(memberBalances);

  // Available users to add to group
  const nonMembers = users.filter((u) => !group.members.some((m) => m.id === u.id));

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
            Grup Detayı
          </h3>

          <button
            onClick={() => onOpenAddExpenseInGroup(group)}
            className="w-9 h-9 rounded-full bg-emerald-50 text-[#00875A] flex items-center justify-center hover:bg-emerald-100 active:scale-95 transition"
            title="Gruba Masraf Ekle"
          >
            <Plus className="w-4 h-4 stroke-[2.5]" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-5 sm:p-6 overflow-y-auto flex-1 space-y-5 text-left">
          {/* Group Header Hero Card */}
          <div className="p-6 rounded-[24px] bg-[#F2F2F7] border border-black/[0.04] text-center space-y-3">
            <div className="w-16 h-16 rounded-[20px] bg-white border border-black/[0.06] flex items-center justify-center text-[32px] mx-auto shadow-apple-sm">
              {group.emoji || '👥'}
            </div>

            <div>
              <h4 className="text-[20px] font-extrabold text-[#1C1C1E]">
                {group.name}
              </h4>
              <span className="text-[11px] font-bold px-2.5 py-0.5 rounded-full bg-white text-[#8E8E93] border border-black/[0.04] inline-block mt-1">
                {group.category || 'Genel'} • {group.members.length} Üye
              </span>
            </div>

            {/* Total spend & user group balance */}
            <div className="grid grid-cols-2 gap-3 pt-2">
              <div className="p-3 rounded-[16px] bg-white border border-black/[0.04] text-left">
                <span className="text-[10px] font-bold text-[#8E8E93] uppercase block">
                  TOPLAM HARCAMA
                </span>
                <span className="text-[16px] font-black text-[#1C1C1E] font-tabular mt-0.5 block">
                  {totalGroupSpend.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                </span>
              </div>

              <div className="p-3 rounded-[16px] bg-white border border-black/[0.04] text-left">
                <span className="text-[10px] font-bold text-[#8E8E93] uppercase block">
                  GRUP BAKİYEN
                </span>
                <span
                  className={`text-[16px] font-black font-tabular mt-0.5 block ${
                    isMyBalancePositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                  }`}
                >
                  {isLocked
                    ? '•••• ₺'
                    : `${isMyBalancePositive ? '+' : ''}${myGroupBalance.toLocaleString('tr-TR', {
                        minimumFractionDigits: 2
                      })} ₺`}
                </span>
              </div>
            </div>
          </div>

          {/* Tab Navigation (Segmented Control) */}
          <div className="grid grid-cols-3 gap-1 p-1 bg-black/5 rounded-full text-[12px] font-bold">
            <button
              onClick={() => setActiveTab('expenses')}
              className={`py-2 rounded-full transition ${
                activeTab === 'expenses' ? 'bg-white text-[#1C1C1E] shadow-apple-sm' : 'text-[#8E8E93]'
              }`}
            >
              Harcamalar ({groupExpenses.length})
            </button>
            <button
              onClick={() => setActiveTab('members')}
              className={`py-2 rounded-full transition ${
                activeTab === 'members' ? 'bg-white text-[#1C1C1E] shadow-apple-sm' : 'text-[#8E8E93]'
              }`}
            >
              Üyeler ({group.members.length})
            </button>
            <button
              onClick={() => setActiveTab('simplify')}
              className={`py-2 rounded-full transition flex items-center justify-center gap-1 ${
                activeTab === 'simplify' ? 'bg-white text-[#00875A] shadow-apple-sm' : 'text-[#8E8E93]'
              }`}
            >
              <Repeat className="w-3.5 h-3.5" />
              <span>Sadeleştir</span>
            </button>
          </div>

          {/* TAB 1: Expenses */}
          {activeTab === 'expenses' && (
            <div className="space-y-3">
              {groupExpenses.length === 0 ? (
                <div className="apple-card p-8 text-center space-y-3">
                  <p className="text-[14px] text-[#8E8E93]">Bu grupta henüz harcama kaydedilmedi.</p>
                  <button
                    onClick={() => onOpenAddExpenseInGroup(group)}
                    className="px-4 py-2 rounded-full bg-[#00875A] text-white text-[13px] font-bold inline-flex items-center gap-1.5"
                  >
                    <Plus className="w-4 h-4" />
                    <span>İlk Harcamayı Ekle</span>
                  </button>
                </div>
              ) : (
                <div className="apple-card divide-y divide-black/[0.04] overflow-hidden">
                  {groupExpenses.map((exp) => {
                    const payer = users.find((u) => u.id === exp.paidBy);
                    return (
                      <div
                        key={exp.id}
                        onClick={() => onViewExpenseDetail(exp)}
                        className="p-3.5 flex items-center justify-between hover:bg-black/[0.02] cursor-pointer transition"
                      >
                        <div>
                          <div className="text-[14px] font-bold text-[#1C1C1E]">
                            {exp.description}
                          </div>
                          <div className="text-[11px] text-[#8E8E93]">
                            {exp.date || new Date(exp.createdAt).toLocaleDateString('tr-TR')} • {payer?.fullName || 'Bilinmiyor'} ödedi
                          </div>
                        </div>

                        <div className="text-right flex items-center gap-2">
                          <span className="text-[14px] font-black text-[#1C1C1E] font-tabular">
                            {exp.amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                          </span>
                          <ChevronRight className="w-3.5 h-3.5 text-[#C7C7CC]" />
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          )}

          {/* TAB 2: Members */}
          {activeTab === 'members' && (
            <div className="space-y-3">
              <div className="flex items-center justify-between px-1">
                <span className="text-[11px] font-bold text-[#8E8E93] uppercase">
                  GRUP ÜYELERİ VE BAKİYELERİ
                </span>
                <button
                  onClick={() => setShowAddMember(!showAddMember)}
                  className="text-[12px] font-bold text-[#00875A] hover:underline flex items-center gap-1"
                >
                  <UserPlus className="w-3.5 h-3.5" />
                  <span>Üye Ekle</span>
                </button>
              </div>

              {/* Add Member Dropdown/List */}
              {showAddMember && nonMembers.length > 0 && (
                <div className="p-3 rounded-[16px] bg-emerald-50/70 border border-emerald-200 space-y-2 animate-fadeIn">
                  <span className="text-[11px] font-bold text-[#00875A] block">
                    ARKADAŞLARINDAN GRUBA EKLE:
                  </span>
                  <div className="flex items-center gap-2 overflow-x-auto pb-1">
                    {nonMembers.map((user) => (
                      <button
                        key={user.id}
                        onClick={() => {
                          onAddMemberToGroup(group.id, user);
                          setShowAddMember(false);
                        }}
                        className="px-3 py-1.5 rounded-full bg-white border border-emerald-300 text-[12px] font-bold text-[#1C1C1E] hover:bg-emerald-100 flex items-center gap-1.5 flex-shrink-0 transition"
                      >
                        <Plus className="w-3 h-3 text-[#00875A]" />
                        <span>{user.fullName}</span>
                      </button>
                    ))}
                  </div>
                </div>
              )}

              <div className="apple-card divide-y divide-black/[0.04] overflow-hidden">
                {group.members.map((member) => {
                  const bal = memberBalances.get(member.id) || 0;
                  const isPos = bal >= 0;

                  return (
                    <div
                      key={member.id}
                      className="p-3.5 flex items-center justify-between hover:bg-black/[0.02] transition"
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-full bg-emerald-100 text-[#00875A] font-bold text-[13px] flex items-center justify-center">
                          {member.name.slice(0, 2).toUpperCase()}
                        </div>
                        <div>
                          <div className="text-[14px] font-bold text-[#1C1C1E]">
                            {member.id === currentUser.id ? `Ben (${member.name})` : member.name}
                          </div>
                          <div className="text-[11px] text-[#8E8E93] font-mono">
                            {member.tag || `@${member.name.toLowerCase().replace(/\s+/g, '')}`}
                          </div>
                        </div>
                      </div>

                      <div className="text-right">
                        <span
                          className={`text-[13px] font-black font-tabular block ${
                            isPos ? 'text-[#00875A]' : 'text-[#D32F2F]'
                          }`}
                        >
                          {isLocked
                            ? '•••• ₺'
                            : `${isPos ? '+' : ''}${bal.toLocaleString('tr-TR', {
                                minimumFractionDigits: 2
                              })} ₺`}
                        </span>
                        <span className="text-[10px] text-[#8E8E93] font-medium">
                          {Math.abs(bal) < 0.01 ? 'fitleşildi' : isPos ? 'alacaklı' : 'borçlu'}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* TAB 3: Debt Simplification Engine */}
          {activeTab === 'simplify' && (
            <div className="space-y-4">
              <div className="p-4 rounded-[20px] bg-gradient-to-br from-emerald-50 to-emerald-100/50 border border-emerald-200 space-y-2 text-left">
                <div className="flex items-center gap-2 text-[#00875A] font-bold text-[13px]">
                  <Sparkles className="w-4 h-4" />
                  <span>Greedy Flow Borç Sadeleştirme</span>
                </div>
                <p className="text-[12px] text-[#1C1C1E] leading-relaxed">
                  Grup üyeleri arasındaki çapraz borçlar matematiksel olarak optimize edildi. Karmaşık onlarca transfer yerine sadece aşağıdaki <strong>{simplifiedTransactions.length} transfer</strong> ile herkes aynı anda fitleşebilir!
                </p>
              </div>

              <div className="space-y-2">
                <span className="text-[11px] font-bold text-[#8E8E93] uppercase block px-1">
                  ÖNERİLEN EN KISA TRANSFER ADIMLARI
                </span>

                {simplifiedTransactions.length === 0 ? (
                  <div className="apple-card p-6 text-center text-[#8E8E93] text-[13px]">
                    Grupta ödenecek borç bulunmuyor. Tüm hesaplar denk! 🎉
                  </div>
                ) : (
                  <div className="space-y-2">
                    {simplifiedTransactions.map((tx, idx) => {
                      const debtor = users.find((u) => u.id === tx.debtorId);
                      const creditor = users.find((u) => u.id === tx.creditorId);
                      const isDebtorMe = tx.debtorId === currentUser.id;

                      return (
                        <div
                          key={idx}
                          className="apple-card p-4 flex items-center justify-between"
                        >
                          <div>
                            <div className="text-[13px] font-bold text-[#1C1C1E]">
                              {isDebtorMe ? 'Sen' : debtor?.fullName || tx.debtorId} ➔ {creditor?.fullName || tx.creditorId}
                            </div>
                            <div className="text-[11px] text-[#8E8E93]">
                              {tx.amount.toFixed(2)} ₺ FAST / Havale ile ödenecek
                            </div>
                          </div>

                          {isDebtorMe ? (
                            <button
                              onClick={() => {
                                onClose();
                                if (creditor) onOpenSettleUp(creditor, tx.amount);
                              }}
                              className="px-3.5 py-1.5 rounded-full bg-[#00875A] text-white text-[12px] font-bold hover:bg-[#00744d] active:scale-95 transition shadow-2xs"
                            >
                              Öde ({tx.amount.toFixed(2)} ₺)
                            </button>
                          ) : (
                            <span className="text-[14px] font-black text-[#00875A] font-tabular">
                              {tx.amount.toFixed(2)} ₺
                            </span>
                          )}
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            </div>
          )}
        </div>

        {/* Footer Actions */}
        <div className="p-4 bg-white border-t border-black/[0.06] flex items-center gap-3">
          <button
            onClick={() => onOpenAddExpenseInGroup(group)}
            className="w-full h-12 rounded-[16px] bg-[#00875A] hover:bg-[#00744d] text-white font-bold text-[14px] flex items-center justify-center gap-2 active:scale-[0.98] transition shadow-sm shadow-emerald-800/20"
          >
            <Plus className="w-4 h-4 stroke-[2.5]" />
            <span>Gruba Masraf Ekle</span>
          </button>
        </div>
      </div>
    </div>
  );
};
