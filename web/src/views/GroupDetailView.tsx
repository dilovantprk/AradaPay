'use client';

import React, { useState, useMemo } from 'react';
import {
  ArrowLeft,
  Plus,
  CreditCard,
  Sparkles,
  Repeat,
  Receipt,
  UserPlus,
  ChevronRight,
  CheckCircle2,
  Calendar,
  Search,
  PieChart,
  Users,
  Share2,
  MoreVertical,
  Check,
  TrendingUp,
  Tag
} from 'lucide-react';
import { Group, User, Expense, ExpenseCategory } from '../types';
import { DebtSimplifierEngine, SimplifiedTransaction } from '../algorithms/DebtSimplifierEngine';

interface GroupDetailViewProps {
  groupId: string;
  groups: Group[];
  currentUser: User;
  users: User[];
  expenses: Expense[];
  isLocked: boolean;
  onBack: () => void;
  onAddExpenseInGroup: (group: Group) => void;
  onOpenSettleUp: (targetUser: User, amount?: number) => void;
  onViewExpenseDetail: (expense: Expense) => void;
  onAddMemberToGroup: (groupId: string, newMember: User) => void;
}

const CATEGORY_ICONS: Record<ExpenseCategory, { icon: string; name: string; color: string }> = {
  DINING: { icon: '🍽️', name: 'Yemek & Kafe', color: '#00875A' },
  GROCERIES: { icon: '🛒', name: 'Market & Gıda', color: '#0284C7' },
  TRAVEL: { icon: '✈️', name: 'Ulaşım & Seyahat', color: '#8B5CF6' },
  HOUSING: { icon: '🏠', name: 'Ev & Kira', color: '#F59E0B' },
  ENTERTAINMENT: { icon: '🍿', name: 'Eğlence & Sinema', color: '#EC4899' },
  UTILITIES: { icon: '⚡', name: 'Faturalar', color: '#6366F1' },
  SHOPPING: { icon: '🛍️', name: 'Alışveriş', color: '#10B981' },
  OTHER: { icon: '📦', name: 'Diğer', color: '#64748B' }
};

export const GroupDetailView: React.FC<GroupDetailViewProps> = ({
  groupId,
  groups,
  currentUser,
  users,
  expenses,
  isLocked,
  onBack,
  onAddExpenseInGroup,
  onOpenSettleUp,
  onViewExpenseDetail,
  onAddMemberToGroup
}) => {
  const [activeTab, setActiveTab] = useState<'expenses' | 'members' | 'simplify' | 'analytics'>('expenses');
  const [searchQuery, setSearchQuery] = useState('');
  const [showAddMemberModal, setShowAddMemberModal] = useState(false);

  const group = useMemo(() => groups.find((g) => g.id === groupId) || groups[0], [groups, groupId]);

  if (!group) {
    return (
      <div className="p-8 text-center space-y-4">
        <p className="text-[#8E8E93]">Grup bulunamadı.</p>
        <button onClick={onBack} className="px-4 py-2 rounded-full bg-[#00875A] text-white font-bold">
          Gruplara Dön
        </button>
      </div>
    );
  }

  // Filter expenses for this group
  const groupExpenses = useMemo(() => {
    return expenses
      .filter((e) => e.groupId === group.id)
      .filter((e) =>
        searchQuery
          ? e.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
            e.category.toLowerCase().includes(searchQuery.toLowerCase())
          : true
      );
  }, [expenses, group.id, searchQuery]);

  const totalGroupSpend = useMemo(() => {
    return expenses.filter((e) => e.groupId === group.id).reduce((sum, e) => sum + e.amount, 0);
  }, [expenses, group.id]);

  // Compute individual balances inside this group
  const memberBalances = useMemo(() => {
    const map = new Map<string, number>();
    group.members.forEach((m) => map.set(m.id, 0));

    const allGroupExps = expenses.filter((e) => e.groupId === group.id);
    allGroupExps.forEach((exp) => {
      const currentPayer = map.get(exp.paidBy) || 0;
      map.set(exp.paidBy, currentPayer + exp.amount);

      exp.splits.forEach((split) => {
        const current = map.get(split.userId) || 0;
        map.set(split.userId, current - split.amountOwed);
      });
    });

    return map;
  }, [expenses, group]);

  const myGroupBalance = memberBalances.get(currentUser.id) || 0;
  const isMyBalancePositive = myGroupBalance >= 0;

  // Run DebtSimplifierEngine on this group
  const simplifiedTransactions: SimplifiedTransaction[] = useMemo(() => {
    return DebtSimplifierEngine.simplifyDebts(memberBalances);
  }, [memberBalances]);

  // Group Category Breakdown
  const categoryBreakdown = useMemo(() => {
    const counts: Partial<Record<ExpenseCategory, number>> = {};
    expenses
      .filter((e) => e.groupId === group.id)
      .forEach((e) => {
        counts[e.category] = (counts[e.category] || 0) + e.amount;
      });
    return counts;
  }, [expenses, group.id]);

  // Non-members to invite
  const nonMembers = users.filter((u) => !group.members.some((m) => m.id === u.id));

  return (
    <div className="space-y-6 text-left animate-fadeIn">
      {/* Top Navigation Bar */}
      <div className="flex items-center justify-between">
        <button
          onClick={onBack}
          className="px-3.5 py-2 rounded-full bg-white border border-black/[0.08] hover:bg-slate-50 text-[#1C1C1E] text-[13px] font-bold flex items-center gap-1.5 active:scale-95 transition shadow-2xs"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Gruplara Dön</span>
        </button>

        <div className="flex items-center gap-2">
          <button
            onClick={() => onAddExpenseInGroup(group)}
            className="px-4 py-2 rounded-full bg-[#00875A] hover:bg-[#00744d] text-white text-[13px] font-bold flex items-center gap-1.5 active:scale-95 transition shadow-sm shadow-emerald-800/20"
          >
            <Plus className="w-4 h-4 stroke-[2.5]" />
            <span>Masraf Ekle</span>
          </button>
        </div>
      </div>

      {/* Hero Group Profile & Financial Card */}
      <div className="apple-card p-6 sm:p-8 space-y-6 bg-white/90">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 sm:w-20 sm:h-20 rounded-[22px] bg-[#F2F2F7] border border-black/[0.06] flex items-center justify-center text-[34px] sm:text-[40px] shadow-apple-sm">
              {group.emoji || '👥'}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-[24px] sm:text-[28px] font-extrabold text-[#1C1C1E] tracking-tight">
                  {group.name}
                </h1>
                <span className="px-2.5 py-0.5 rounded-full bg-emerald-50 text-[#00875A] border border-emerald-200 text-[11px] font-bold">
                  {group.category || 'Genel'}
                </span>
              </div>
              <p className="text-[13px] text-[#8E8E93] mt-1">
                {group.members.length} Katılımcı • {expenses.filter((e) => e.groupId === group.id).length} Harcama Kaydı
              </p>
            </div>
          </div>

          <button
            onClick={() => setShowAddMemberModal(true)}
            className="px-3.5 py-2 rounded-full bg-black/5 hover:bg-black/10 text-[#1C1C1E] text-[12px] font-bold flex items-center gap-1.5 active:scale-95 transition"
          >
            <UserPlus className="w-3.5 h-3.5" />
            <span>Üye Ekle</span>
          </button>
        </div>

        {/* Balance Metric Highlights */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
          {/* Total Spend */}
          <div className="p-4 rounded-[20px] bg-[#F2F2F7] border border-black/[0.04] space-y-1">
            <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider block">
              TOPLAM GRUP HARCAMASI
            </span>
            <div className="text-[26px] sm:text-[30px] font-black text-[#1C1C1E] font-tabular tracking-tight">
              {totalGroupSpend.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
            </div>
            <span className="text-[11px] text-[#8E8E93]">Tüm grup üyelerinin ortak harcamaları</span>
          </div>

          {/* User Net Balance in Group */}
          <div className="p-4 rounded-[20px] bg-[#F2F2F7] border border-black/[0.04] space-y-1">
            <span
              className={`text-[11px] font-bold uppercase tracking-wider block ${
                isMyBalancePositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
              }`}
            >
              SENİN GRUP BAKİYEN
            </span>
            <div
              className={`text-[26px] sm:text-[30px] font-black font-tabular tracking-tight ${
                isMyBalancePositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
              }`}
            >
              {isLocked
                ? '•••• ₺'
                : `${isMyBalancePositive ? '+' : ''}${myGroupBalance.toLocaleString('tr-TR', {
                    minimumFractionDigits: 2
                  })} ₺`}
            </div>
            <span className="text-[11px] text-[#8E8E93]">
              {Math.abs(myGroupBalance) < 0.01
                ? 'Grupta tüm hesapların denk (0 ₺)'
                : isMyBalancePositive
                ? 'Gruptan alacağın var'
                : 'Gruba ödemen gereken borcun var'}
            </span>
          </div>
        </div>
      </div>

      {/* Segmented Control Tabs */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-1 p-1 bg-black/5 rounded-[18px] text-[13px] font-bold">
        <button
          onClick={() => setActiveTab('expenses')}
          className={`py-2.5 rounded-[14px] transition ${
            activeTab === 'expenses' ? 'bg-white text-[#1C1C1E] shadow-apple-sm' : 'text-[#8E8E93]'
          }`}
        >
          Harcamalar ({groupExpenses.length})
        </button>

        <button
          onClick={() => setActiveTab('members')}
          className={`py-2.5 rounded-[14px] transition ${
            activeTab === 'members' ? 'bg-white text-[#1C1C1E] shadow-apple-sm' : 'text-[#8E8E93]'
          }`}
        >
          Üyeler ({group.members.length})
        </button>

        <button
          onClick={() => setActiveTab('simplify')}
          className={`py-2.5 rounded-[14px] transition flex items-center justify-center gap-1.5 ${
            activeTab === 'simplify' ? 'bg-white text-[#00875A] shadow-apple-sm' : 'text-[#8E8E93]'
          }`}
        >
          <Repeat className="w-4 h-4" />
          <span>Sadeleştir ({simplifiedTransactions.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('analytics')}
          className={`py-2.5 rounded-[14px] transition flex items-center justify-center gap-1.5 ${
            activeTab === 'analytics' ? 'bg-white text-[#1C1C1E] shadow-apple-sm' : 'text-[#8E8E93]'
          }`}
        >
          <PieChart className="w-4 h-4" />
          <span>Analiz</span>
        </button>
      </div>

      {/* ========================================================================= */}
      {/* TAB 1: GROUP EXPENSES FEED */}
      {/* ========================================================================= */}
      {activeTab === 'expenses' && (
        <div className="space-y-4">
          {/* Search Bar */}
          <div className="relative">
            <Search className="w-4 h-4 text-[#8E8E93] absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Grup harcamalarında ara..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full h-11 pl-10 pr-4 rounded-[14px] bg-white border border-black/[0.08] text-[13px] font-medium text-[#1C1C1E] focus:outline-none focus:border-[#00875A]"
            />
          </div>

          {groupExpenses.length === 0 ? (
            <div className="apple-card p-10 text-center space-y-3">
              <p className="text-[14px] text-[#8E8E93]">Bu grupta henüz kayıtlı harcama bulunmuyor.</p>
              <button
                onClick={() => onAddExpenseInGroup(group)}
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
                const isPayerMe = exp.paidBy === currentUser.id;
                const catInfo = CATEGORY_ICONS[exp.category] || CATEGORY_ICONS.OTHER;

                return (
                  <div
                    key={exp.id}
                    onClick={() => onViewExpenseDetail(exp)}
                    className="p-4 flex items-center justify-between hover:bg-black/[0.02] cursor-pointer transition"
                  >
                    <div className="flex items-center gap-3.5">
                      <div className="w-11 h-11 rounded-[14px] bg-[#F2F2F7] border border-black/[0.04] flex items-center justify-center text-[20px] flex-shrink-0">
                        {catInfo.icon}
                      </div>
                      <div>
                        <div className="text-[15px] font-bold text-[#1C1C1E]">{exp.description}</div>
                        <div className="text-[12px] text-[#8E8E93] flex items-center gap-2 mt-0.5">
                          <span>{exp.date || new Date(exp.createdAt).toLocaleDateString('tr-TR')}</span>
                          <span>•</span>
                          <span>{isPayerMe ? 'Sen ödedin' : `${payer?.fullName || 'Bilinmiyor'} ödedi`}</span>
                          <span>•</span>
                          <span className="text-[#00875A] font-semibold">{exp.splits.length} kişi</span>
                        </div>
                      </div>
                    </div>

                    <div className="text-right flex items-center gap-3">
                      <div>
                        <span className="text-[16px] font-black text-[#1C1C1E] font-tabular block">
                          {exp.amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                        </span>
                        <span className="text-[11px] text-[#8E8E93]">
                          {exp.splitMethod === 'EQUAL' ? 'Eşit pay' : 'Özel pay'}
                        </span>
                      </div>
                      <ChevronRight className="w-4 h-4 text-[#C7C7CC]" />
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 2: GROUP MEMBERS & BALANCES */}
      {/* ========================================================================= */}
      {activeTab === 'members' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between px-1">
            <span className="text-[11px] font-bold text-[#8E8E93] uppercase">
              GRUP ÜYELERİ ({group.members.length})
            </span>
            <button
              onClick={() => setShowAddMemberModal(true)}
              className="text-[12px] font-bold text-[#00875A] hover:underline flex items-center gap-1"
            >
              <UserPlus className="w-3.5 h-3.5" />
              <span>Yeni Üye Ekle</span>
            </button>
          </div>

          <div className="apple-card divide-y divide-black/[0.04] overflow-hidden">
            {group.members.map((member) => {
              const bal = memberBalances.get(member.id) || 0;
              const isPos = bal >= 0;
              const isMe = member.id === currentUser.id;

              return (
                <div key={member.id} className="p-4 flex items-center justify-between hover:bg-black/[0.02] transition">
                  <div className="flex items-center gap-3.5">
                    <div className="w-11 h-11 rounded-full bg-emerald-100 text-[#00875A] border border-emerald-300 font-extrabold text-[14px] flex items-center justify-center">
                      {member.name.slice(0, 2).toUpperCase()}
                    </div>
                    <div>
                      <div className="text-[15px] font-bold text-[#1C1C1E]">
                        {isMe ? `Ben (${member.name})` : member.name}
                      </div>
                      <div className="text-[12px] text-[#8E8E93] font-mono">
                        {member.tag || `@${member.name.toLowerCase().replace(/\s+/g, '')}`}
                      </div>
                    </div>
                  </div>

                  <div className="text-right">
                    <span
                      className={`text-[15px] font-black font-tabular block ${
                        isPos ? 'text-[#00875A]' : 'text-[#D32F2F]'
                      }`}
                    >
                      {isLocked
                        ? '•••• ₺'
                        : `${isPos ? '+' : ''}${bal.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺`}
                    </span>
                    <span className="text-[11px] text-[#8E8E93] font-medium">
                      {Math.abs(bal) < 0.01 ? 'Fitleşildi' : isPos ? 'Gruptan Alacaklı' : 'Gruba Borçlu'}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 3: DEBT SIMPLIFICATION ENGINE */}
      {/* ========================================================================= */}
      {activeTab === 'simplify' && (
        <div className="space-y-4">
          <div className="p-5 rounded-[22px] bg-gradient-to-br from-emerald-50 via-[#F0FDF4] to-emerald-100/50 border border-emerald-200 space-y-2">
            <div className="flex items-center gap-2 text-[#00875A] font-bold text-[14px]">
              <Sparkles className="w-4 h-4" />
              <span>Greedy Flow Borç Sadeleştirici (Optimal Graph)</span>
            </div>
            <p className="text-[13px] text-[#1C1C1E] leading-relaxed">
              Gruptaki tüm üyeler arasındaki karşılıklı ve döngüsel borçlar çözüldü. Onlarca ayrı FAST transferi yerine sadece <strong>{simplifiedTransactions.length} adımda</strong> tüm grup sıfırlanabilir!
            </p>
          </div>

          <div className="space-y-2.5">
            <span className="text-[11px] font-bold text-[#8E8E93] uppercase block px-1">
              ÖNERİLEN EN DÜŞÜK TRANSFER ADIMLARI
            </span>

            {simplifiedTransactions.length === 0 ? (
              <div className="apple-card p-8 text-center text-[#8E8E93] text-[13px]">
                Grupta ödenecek borç bulunmuyor. Tüm hesaplar denk! 🎉
              </div>
            ) : (
              <div className="space-y-2.5">
                {simplifiedTransactions.map((tx, idx) => {
                  const debtor = users.find((u) => u.id === tx.debtorId);
                  const creditor = users.find((u) => u.id === tx.creditorId);
                  const isDebtorMe = tx.debtorId === currentUser.id;

                  return (
                    <div key={idx} className="apple-card p-4 sm:p-5 flex items-center justify-between">
                      <div className="space-y-0.5">
                        <div className="text-[14px] font-bold text-[#1C1C1E] flex items-center gap-2">
                          <span>{isDebtorMe ? 'Sen' : debtor?.fullName || tx.debtorId}</span>
                          <span className="text-[#00875A]">➔</span>
                          <span>{creditor?.fullName || tx.creditorId}</span>
                        </div>
                        <div className="text-[12px] text-[#8E8E93]">
                          {tx.amount.toFixed(2)} ₺ doğrudan FAST transferi ile kapatılabilir
                        </div>
                      </div>

                      {isDebtorMe ? (
                        <button
                          onClick={() => {
                            if (creditor) onOpenSettleUp(creditor, tx.amount);
                          }}
                          className="px-4 py-2 rounded-full bg-[#00875A] text-white text-[13px] font-bold hover:bg-[#00744d] active:scale-95 transition shadow-sm"
                        >
                          Öde ({tx.amount.toFixed(2)} ₺)
                        </button>
                      ) : (
                        <span className="text-[16px] font-black text-[#00875A] font-tabular">
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

      {/* ========================================================================= */}
      {/* TAB 4: GROUP CATEGORY ANALYTICS */}
      {/* ========================================================================= */}
      {activeTab === 'analytics' && (
        <div className="apple-card p-6 space-y-4">
          <h3 className="text-[13px] font-bold text-[#8E8E93] uppercase tracking-wider">
            GRUP KATEGORİ DAĞILIMI
          </h3>

          <div className="space-y-4">
            {(Object.keys(categoryBreakdown) as ExpenseCategory[]).map((catKey) => {
              const amount = categoryBreakdown[catKey] || 0;
              const percentage = totalGroupSpend > 0 ? Math.round((amount / totalGroupSpend) * 100) : 0;
              const catInfo = CATEGORY_ICONS[catKey] || CATEGORY_ICONS.OTHER;

              return (
                <div key={catKey} className="space-y-1.5">
                  <div className="flex items-center justify-between text-[13px]">
                    <div className="flex items-center gap-2">
                      <span>{catInfo.icon}</span>
                      <span className="font-bold text-[#1C1C1E]">{catInfo.name}</span>
                    </div>
                    <span className="font-black text-[#1C1C1E] font-tabular">
                      {amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺ ({percentage}%)
                    </span>
                  </div>

                  <div className="h-2 rounded-full bg-[#F2F2F7] overflow-hidden">
                    <div
                      className="h-full rounded-full transition-all duration-500"
                      style={{
                        width: `${percentage}%`,
                        backgroundColor: catInfo.color
                      }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Add Member Modal */}
      {showAddMemberModal && (
        <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
          <div className="bg-white w-full max-w-md rounded-t-[32px] sm:rounded-[28px] shadow-apple-modal border border-black/[0.08] overflow-hidden flex flex-col max-h-[85vh] animate-appleSheet">
            <div className="w-12 h-1.5 bg-black/15 rounded-full mx-auto mt-3 sm:hidden" />

            <div className="px-5 py-3.5 border-b border-black/[0.06] flex items-center justify-between">
              <button
                onClick={() => setShowAddMemberModal(false)}
                className="w-9 h-9 rounded-full bg-black/5 flex items-center justify-center text-[#1C1C1E]"
              >
                <ArrowLeft className="w-4 h-4" />
              </button>
              <h3 className="text-[17px] font-bold text-[#1C1C1E]">Gruba Üye Ekle</h3>
              <div className="w-9" />
            </div>

            <div className="p-5 space-y-3 overflow-y-auto">
              <p className="text-[13px] text-[#8E8E93]">
                Rehberinizdeki arkadaşlarınızı <strong>{group.name}</strong> grubuna dahil edin:
              </p>

              {nonMembers.length === 0 ? (
                <div className="p-6 text-center text-[#8E8E93] text-[13px]">
                  Tüm arkadaşlarınız zaten bu grupta ekli!
                </div>
              ) : (
                <div className="apple-card divide-y divide-black/[0.04]">
                  {nonMembers.map((user) => (
                    <div
                      key={user.id}
                      className="p-3.5 flex items-center justify-between hover:bg-black/[0.02] transition"
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-full bg-emerald-100 text-[#00875A] font-bold text-[13px] flex items-center justify-center">
                          {user.fullName.slice(0, 2).toUpperCase()}
                        </div>
                        <div>
                          <div className="text-[14px] font-bold text-[#1C1C1E]">{user.fullName}</div>
                          <div className="text-[11px] text-[#8E8E93] font-mono">{user.tag || `@${user.username}`}</div>
                        </div>
                      </div>

                      <button
                        onClick={() => {
                          onAddMemberToGroup(group.id, user);
                          setShowAddMemberModal(false);
                        }}
                        className="px-3.5 py-1.5 rounded-full bg-[#00875A] text-white text-[12px] font-bold hover:bg-[#00744d] active:scale-95 transition"
                      >
                        + Ekle
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
