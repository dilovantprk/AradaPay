'use client';

import React, { useState, useMemo } from 'react';
import {
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
  ChevronRight,
  Search,
  QrCode,
  Sparkles,
  Phone,
  Mail
} from 'lucide-react';
import { User, Expense, Settlement, Group } from '../types';

interface FriendDetailViewProps {
  friendId: string;
  currentUser: User;
  users: User[];
  expenses: Expense[];
  settlements: Settlement[];
  groups: Group[];
  isLocked: boolean;
  onBack: () => void;
  onOpenSettleUp: (friend: User, amount?: number) => void;
  onOpenNudge: (friend: User) => void;
  onOpenAddExpense: (friend: User) => void;
  onViewExpenseDetail: (expense: Expense) => void;
  onNavigateToGroup: (groupId: string) => void;
}

export const FriendDetailView: React.FC<FriendDetailViewProps> = ({
  friendId,
  currentUser,
  users,
  expenses,
  settlements,
  groups,
  isLocked,
  onBack,
  onOpenSettleUp,
  onOpenNudge,
  onOpenAddExpense,
  onViewExpenseDetail,
  onNavigateToGroup
}) => {
  const [activeTab, setActiveTab] = useState<'timeline' | 'groups' | 'payment'>('timeline');
  const [filter, setFilter] = useState<'all' | 'expenses' | 'settlements'>('all');
  const [copiedIban, setCopiedIban] = useState(false);

  const friend = useMemo(() => users.find((u) => u.id === friendId) || users[0], [users, friendId]);

  if (!friend) {
    return (
      <div className="p-8 text-center space-y-4">
        <p className="text-[#8E8E93]">Arkadaş bulunamadı.</p>
        <button onClick={onBack} className="px-4 py-2 rounded-full bg-[#00875A] text-white font-bold">
          Arkadaşlara Dön
        </button>
      </div>
    );
  }

  // Calculate bilateral balance
  const balance = useMemo(() => {
    let bal = 0;
    expenses.forEach((exp) => {
      if (exp.paidBy === currentUser.id) {
        const split = exp.splits.find((s) => s.userId === friend.id);
        if (split) bal += split.amountOwed;
      } else if (exp.paidBy === friend.id) {
        const split = exp.splits.find((s) => s.userId === currentUser.id);
        if (split) bal -= split.amountOwed;
      }
    });

    settlements.forEach((set) => {
      if (set.payerId === currentUser.id && set.receiverId === friend.id) {
        bal += set.amount;
      } else if (set.payerId === friend.id && set.receiverId === currentUser.id) {
        bal -= set.amount;
      }
    });

    return bal;
  }, [expenses, settlements, currentUser.id, friend.id]);

  const isPositive = balance >= 0;
  const hasBalance = Math.abs(balance) > 0.01;

  // Shared expenses
  const sharedExpenses = useMemo(() => {
    return expenses.filter(
      (exp) =>
        (exp.paidBy === currentUser.id && exp.splits.some((s) => s.userId === friend.id)) ||
        (exp.paidBy === friend.id && exp.splits.some((s) => s.userId === currentUser.id))
    );
  }, [expenses, currentUser.id, friend.id]);

  // Shared settlements
  const sharedSettlements = useMemo(() => {
    return settlements.filter(
      (set) =>
        (set.payerId === currentUser.id && set.receiverId === friend.id) ||
        (set.payerId === friend.id && set.receiverId === currentUser.id)
    );
  }, [settlements, currentUser.id, friend.id]);

  // Shared groups
  const sharedGroups = useMemo(() => {
    return groups.filter(
      (g) => g.members.some((m) => m.id === friend.id) && g.members.some((m) => m.id === currentUser.id)
    );
  }, [groups, currentUser.id, friend.id]);

  // Bank Detection
  const detectBank = (ibanStr?: string | null) => {
    if (!ibanStr) return 'Banka Hesabı';
    const clean = ibanStr.replace(/\s+/g, '');
    if (clean.length < 9) return 'Banka Hesabı';
    const code = clean.substring(4, 9);
    switch (code) {
      case '00062':
        return 'Garanti BBVA';
      case '00064':
        return 'Türkiye İş Bankası';
      case '00046':
        return 'Akbank';
      case '00067':
        return 'Yapı Kredi';
      case '00010':
        return 'Ziraat Bankası';
      case '00111':
        return 'QNB Finansbank';
      default:
        return 'Banka Hesabı';
    }
  };

  const handleCopyIban = () => {
    if (friend.iban) {
      navigator.clipboard.writeText(friend.iban.replace(/\s+/g, ''));
      setCopiedIban(true);
      setTimeout(() => setCopiedIban(false), 2000);
    }
  };

  return (
    <div className="space-y-4 text-left animate-fadeIn">
      {/* Desktop Top Navigation Bar (Hidden on mobile because TopBar handles back & actions) */}
      <div className="hidden md:flex items-center justify-between">
        <button
          onClick={onBack}
          className="px-3.5 py-2 rounded-full bg-white border border-black/[0.08] hover:bg-slate-50 text-[#1C1C1E] text-[13px] font-bold flex items-center gap-1.5 active:scale-95 transition shadow-2xs"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Arkadaşlara Dön</span>
        </button>

        <div className="flex items-center gap-2">
          <button
            onClick={() => onOpenAddExpense(friend)}
            className="px-4 py-2 rounded-full bg-[#00875A] hover:bg-[#00744d] text-white text-[13px] font-bold flex items-center gap-1.5 active:scale-95 transition shadow-sm shadow-emerald-800/20"
          >
            <Plus className="w-4 h-4 stroke-[2.5]" />
            <span>Masraf Bölüş</span>
          </button>
        </div>
      </div>

      {/* Hero Friend Profile & Bilateral Balance Card (Single Flat Layer) */}
      <div className="bg-white rounded-[24px] border border-slate-200/80 p-6 sm:p-7 space-y-6 shadow-sm">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 rounded-[20px] bg-emerald-50 text-[#00875A] border border-emerald-200 flex items-center justify-center font-extrabold text-[22px] shadow-2xs">
              {friend.fullName.slice(0, 2).toUpperCase()}
            </div>

            <div>
              <h1 className="text-[22px] sm:text-[26px] font-extrabold text-[#0F172A] tracking-tight">
                {friend.fullName}
              </h1>
              <p className="text-[13px] font-mono text-[#00875A] font-bold mt-0.5">
                {friend.tag || `@${friend.username}`}
              </p>
            </div>
          </div>

          {/* Action Pill Buttons */}
          <div className="flex items-center gap-2 w-full sm:w-auto">
            <button
              onClick={() => onOpenSettleUp(friend, Math.abs(balance))}
              className="flex-1 sm:flex-initial px-4 py-2.5 rounded-[12px] bg-[#00875A] text-white text-[13px] font-bold flex items-center justify-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-900/10"
            >
              <CreditCard className="w-4 h-4" />
              <span>Ödeş & Masayı Kapat</span>
            </button>

            <button
              onClick={() => onOpenNudge(friend)}
              className="px-4 py-2.5 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] text-[13px] font-bold flex items-center justify-center gap-1.5 active:scale-95 transition"
            >
              <Send className="w-4 h-4 text-[#64748B]" />
              <span>Bi' Dürt (Sinyal Çak)</span>
            </button>
          </div>
        </div>

        {/* Clean Flat Balance Section */}
        <div className="pt-5 border-t border-slate-100 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
          <div>
            <span
              className={`text-[11px] font-bold uppercase tracking-wider block ${
                isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
              }`}
            >
              {!hasBalance
                ? 'TERTEMİZ OLDUK (HESAPLAR TAMAMEN DENK)'
                : isPositive
                ? `${friend.fullName.toUpperCase()}'IN MASADA PAYI VAR`
                : `${friend.fullName.toUpperCase()}'IN MASASINA PAYIN VAR`}
            </span>
            <div
              className={`text-[36px] sm:text-[42px] font-extrabold font-tabular tracking-tight leading-tight ${
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

          {friend.iban && (
            <div className="flex items-center gap-3 pt-2 sm:pt-0">
              <div className="text-left sm:text-right">
                <span className="text-[10px] font-bold text-[#64748B] uppercase block">
                  {detectBank(friend.iban)} • FAST IBAN
                </span>
                <span className="text-[13px] font-mono font-bold text-[#0F172A] select-all">
                  {friend.iban}
                </span>
              </div>

              <button
                onClick={handleCopyIban}
                className="px-3 py-1.5 rounded-[10px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] text-[12px] font-bold flex items-center gap-1.5 transition flex-shrink-0"
              >
                {copiedIban ? <Check className="w-3.5 h-3.5 text-[#00875A]" /> : <Copy className="w-3.5 h-3.5" />}
                <span>{copiedIban ? 'Kopyalandı' : 'Kopyala'}</span>
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Segmented Control Tabs */}
      <div className="grid grid-cols-3 gap-1 p-1 bg-black/5 rounded-[18px] text-[13px] font-bold">
        <button
          onClick={() => setActiveTab('timeline')}
          className={`py-2.5 rounded-[14px] transition flex items-center justify-center gap-1.5 ${
            activeTab === 'timeline' ? 'bg-white text-[#1C1C1E] shadow-apple-sm' : 'text-[#8E8E93]'
          }`}
        >
          <Receipt className="w-3.5 h-3.5" />
          <span>Ortak Geçmiş ({sharedExpenses.length + sharedSettlements.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('groups')}
          className={`py-2.5 rounded-[14px] transition flex items-center justify-center gap-1.5 ${
            activeTab === 'groups' ? 'bg-white text-[#1C1C1E] shadow-apple-sm' : 'text-[#8E8E93]'
          }`}
        >
          <Users className="w-3.5 h-3.5" />
          <span>Ortak Gruplar ({sharedGroups.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('payment')}
          className={`py-2.5 rounded-[14px] transition flex items-center justify-center gap-1.5 ${
            activeTab === 'payment' ? 'bg-white text-[#1C1C1E] shadow-apple-sm' : 'text-[#8E8E93]'
          }`}
        >
          <QrCode className="w-3.5 h-3.5" />
          <span>Ödeme & QR</span>
        </button>
      </div>

      {/* ========================================================================= */}
      {/* TAB 1: SHARED ACTIVITY TIMELINE */}
      {/* ========================================================================= */}
      {activeTab === 'timeline' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between px-1">
            <span className="text-[11px] font-bold text-[#8E8E93] uppercase">
              İKİLİ HESAP HAREKETLERİ
            </span>

            <div className="flex items-center gap-1 p-0.5 bg-black/5 rounded-full text-[11px] font-bold">
              <button
                onClick={() => setFilter('all')}
                className={`inline-flex items-center gap-1 px-3 py-1 rounded-full transition ${
                  filter === 'all' ? 'bg-white text-[#1C1C1E] shadow-2xs' : 'text-[#8E8E93]'
                }`}
              >
                <Sparkles className="w-3 h-3" />
                <span>Tümü</span>
              </button>
              <button
                onClick={() => setFilter('expenses')}
                className={`inline-flex items-center gap-1 px-3 py-1 rounded-full transition ${
                  filter === 'expenses' ? 'bg-white text-[#1C1C1E] shadow-2xs' : 'text-[#8E8E93]'
                }`}
              >
                <Receipt className="w-3 h-3" />
                <span>Harcamalar</span>
              </button>
              <button
                onClick={() => setFilter('settlements')}
                className={`inline-flex items-center gap-1 px-3 py-1 rounded-full transition ${
                  filter === 'settlements' ? 'bg-white text-[#1C1C1E] shadow-2xs' : 'text-[#8E8E93]'
                }`}
              >
                <CreditCard className="w-3 h-3" />
                <span>Fitleşmeler</span>
              </button>
            </div>
          </div>

          {filter !== 'settlements' && sharedExpenses.length > 0 && (
            <div className="apple-card divide-y divide-black/[0.04] overflow-hidden">
              {sharedExpenses.map((exp) => {
                const isPayerMe = exp.paidBy === currentUser.id;
                const mySplit = exp.splits.find((s) => s.userId === currentUser.id);
                const friendSplit = exp.splits.find((s) => s.userId === friend.id);
                const amount = isPayerMe ? friendSplit?.amountOwed || 0 : mySplit?.amountOwed || 0;

                return (
                  <div
                    key={exp.id}
                    onClick={() => onViewExpenseDetail(exp)}
                    className="p-4 flex items-center justify-between hover:bg-black/[0.02] cursor-pointer transition"
                  >
                    <div>
                      <div className="text-[15px] font-bold text-[#1C1C1E]">{exp.description}</div>
                      <div className="text-[12px] text-[#8E8E93] mt-0.5">
                        {exp.date || new Date(exp.createdAt).toLocaleDateString('tr-TR')} • {isPayerMe ? 'Sen Ödedin' : `${friend.fullName} Ödedi`}
                      </div>
                    </div>

                    <div className="text-right flex items-center gap-3">
                      <div>
                        <span
                          className={`text-[15px] font-black font-tabular block ${
                            isPayerMe ? 'text-[#00875A]' : 'text-[#D32F2F]'
                          }`}
                        >
                          {isLocked ? '•••• ₺' : `${isPayerMe ? '+' : '-'}${amount.toFixed(2)} ₺`}
                        </span>
                        <span className="text-[10px] text-[#8E8E93]">
                          Toplam: {exp.amount.toFixed(2)} ₺
                        </span>
                      </div>
                      <ChevronRight className="w-4 h-4 text-[#C7C7CC]" />
                    </div>
                  </div>
                );
              })}
            </div>
          )}

          {filter !== 'expenses' && sharedSettlements.length > 0 && (
            <div className="space-y-2">
              <span className="text-[11px] font-bold text-[#8E8E93] uppercase block px-1">
                FİTLEŞME VE TRANSFER GEÇMİŞİ
              </span>
              <div className="apple-card divide-y divide-black/[0.04] overflow-hidden">
                {sharedSettlements.map((set) => {
                  const isPayerMe = set.payerId === currentUser.id;
                  return (
                    <div key={set.id} className="p-4 flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-full bg-emerald-50 text-[#00875A] flex items-center justify-center">
                          <CheckCircle2 className="w-4 h-4" />
                        </div>
                        <div>
                          <div className="text-[14px] font-bold text-[#1C1C1E]">
                            {isPayerMe ? `${friend.fullName}'a FAST Gönderildi` : `${friend.fullName}'dan FAST Alındı`}
                          </div>
                          <div className="text-[11px] text-[#8E8E93]">
                            {new Date(set.createdAt).toLocaleDateString('tr-TR')} • FAST / Havale
                          </div>
                        </div>
                      </div>

                      <span className="text-[15px] font-black text-[#00875A] font-tabular">
                        {set.amount.toFixed(2)} ₺
                      </span>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {sharedExpenses.length === 0 && sharedSettlements.length === 0 && (
            <div className="apple-card p-8 text-center text-[#8E8E93] text-[13px]">
              Bu arkadaşınızla henüz kayıtlı hesap hareketi bulunmuyor.
            </div>
          )}
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 2: SHARED GROUPS */}
      {/* ========================================================================= */}
      {activeTab === 'groups' && (
        <div className="space-y-3">
          {sharedGroups.length === 0 ? (
            <div className="apple-card p-8 text-center space-y-2">
              <p className="text-[14px] text-[#8E8E93]">
                {friend.fullName} ile ortak bir grubunuz bulunmuyor.
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {sharedGroups.map((g) => (
                <div
                  key={g.id}
                  onClick={() => onNavigateToGroup(g.id)}
                  className="apple-card p-4 hover:border-black/[0.1] active:scale-[0.99] cursor-pointer transition flex items-center justify-between"
                >
                  <div className="flex items-center gap-3.5">
                    <div className="w-12 h-12 rounded-[16px] bg-[#F2F2F7] border border-black/[0.04] flex items-center justify-center text-[24px]">
                      {g.emoji || '👥'}
                    </div>
                    <div>
                      <h4 className="text-[15px] font-bold text-[#1C1C1E]">{g.name}</h4>
                      <p className="text-[12px] text-[#8E8E93]">
                        {g.category} • {g.members.length} Üye
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <span className="text-[12px] font-bold text-[#00875A]">Gruba Git</span>
                    <ChevronRight className="w-4 h-4 text-[#C7C7CC]" />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 3: PAYMENT DETAILS & QR */}
      {/* ========================================================================= */}
      {activeTab === 'payment' && (
        <div className="apple-card p-6 sm:p-8 space-y-6">
          <div className="text-center space-y-2">
            <div className="w-48 h-48 bg-white border-2 border-dashed border-black/15 rounded-[24px] mx-auto flex flex-col items-center justify-center p-4 shadow-apple-sm">
              <QrCode className="w-32 h-32 text-[#1C1C1E]" />
              <span className="text-[10px] font-mono text-[#8E8E93] mt-1">TR-KAREKOD FAST</span>
            </div>
            <p className="text-[13px] font-bold text-[#1C1C1E]">
              {friend.fullName} FAST Karekodu
            </p>
            <p className="text-[12px] text-[#8E8E93]">
              Mobil bankacılık uygulamanızdan okutarak saniyeler içinde transfer yapabilirsiniz.
            </p>
          </div>

          <div className="space-y-3 pt-2">
            <div className="p-3.5 rounded-[16px] bg-[#F2F2F7] flex items-center justify-between text-[13px]">
              <span className="text-[#8E8E93]">Alıcı Adı:</span>
              <span className="font-bold text-[#1C1C1E]">{friend.fullName}</span>
            </div>

            <div className="p-3.5 rounded-[16px] bg-[#F2F2F7] flex items-center justify-between text-[13px]">
              <span className="text-[#8E8E93]">Banka:</span>
              <span className="font-bold text-[#00875A]">{detectBank(friend.iban)}</span>
            </div>

            <div className="p-3.5 rounded-[16px] bg-[#F2F2F7] flex items-center justify-between text-[13px]">
              <span className="text-[#8E8E93]">IBAN:</span>
              <span className="font-mono font-bold text-[#1C1C1E] truncate ml-2">
                {friend.iban || 'Belirtilmedi'}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
