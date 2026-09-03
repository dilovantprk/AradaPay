'use client';

import React, { useState, useMemo } from 'react';
import {
  ArrowLeft,
  Plus,
  CreditCard,
  Zap,
  Share2,
  Edit3,
  Check,
  X,
  Copy,
  Receipt,
  Archive,
  Trash2,
  UserPlus,
  Search,
  ChevronRight
} from 'lucide-react';
import { Group, GroupMember, User, Expense } from '../types';
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
  onAddMemberToGroup?: (groupId: string, newMember: User) => void;
  onUpdateGroup?: (updatedGroup: Group) => void;
  onDeleteGroup?: (groupId: string) => void;
}

interface CategoryOption {
  id: string;
  name: string;
  emoji: string;
  bgTint: string;
  iconTint: string;
}

const CATEGORY_OPTIONS: CategoryOption[] = [
  { id: 'home', name: 'Ev & Yaşam', emoji: '🏠', bgTint: '#CCFBF1', iconTint: '#0D9488' },
  { id: 'trip', name: 'Seyahat', emoji: '✈️', bgTint: '#E0F2FE', iconTint: '#0284C7' },
  { id: 'food', name: 'Yemek', emoji: '🍔', bgTint: '#FEF3C7', iconTint: '#D97706' },
  { id: 'transport', name: 'Yolculuk', emoji: '🚗', bgTint: '#EDE9FE', iconTint: '#7C3AED' },
  { id: 'event', name: 'Etkinlik', emoji: '🎉', bgTint: '#FCE7F3', iconTint: '#DB2777' },
  { id: 'groceries', name: 'Market', emoji: '🛒', bgTint: '#D1FAE5', iconTint: '#059669' },
  { id: 'other', name: 'Diğer', emoji: '📁', bgTint: '#F1F5F9', iconTint: '#475569' }
];

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
  onAddMemberToGroup,
  onUpdateGroup,
  onDeleteGroup
}) => {
  const [showEditGroupModal, setShowEditGroupModal] = useState(false);
  const [showSimplifyDebtsModal, setShowSimplifyDebtsModal] = useState(false);
  const [showAddMemberModal, setShowAddMemberModal] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [memberToDelete, setMemberToDelete] = useState<GroupMember | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const group = useMemo(() => groups.find((g) => g.id === groupId) || groups[0], [groups, groupId]);

  const [editName, setEditName] = useState(group?.name || '');
  const [editCategory, setEditCategory] = useState(group?.category || 'Genel');
  const [editEmoji, setEditEmoji] = useState(group?.emoji || '👥');
  const [showCategoryDropdown, setShowCategoryDropdown] = useState(false);
  const [simplifyDebtsToggle, setSimplifyDebtsToggle] = useState(true);
  const [searchMemberQuery, setSearchMemberQuery] = useState('');
  const [expenseSearchQuery, setExpenseSearchQuery] = useState('');

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  if (!group) {
    return (
      <div className="p-10 text-center space-y-4 bg-white rounded-[24px] border border-slate-200">
        <p className="text-[#64748B] font-medium">Grup bulunamadı.</p>
        <button onClick={onBack} className="px-5 py-2.5 rounded-[12px] bg-[#00875A] text-white font-bold text-[14px]">
          Gruplara Dön
        </button>
      </div>
    );
  }

  const groupExpenses = useMemo(() => {
    return expenses
      .filter((e) => e.groupId === group.id)
      .filter((e) =>
        expenseSearchQuery
          ? e.description.toLowerCase().includes(expenseSearchQuery.toLowerCase()) ||
            e.category.toLowerCase().includes(expenseSearchQuery.toLowerCase())
          : true
      );
  }, [expenses, group.id, expenseSearchQuery]);

  const totalGroupSpend = useMemo(() => {
    return expenses.filter((e) => e.groupId === group.id).reduce((sum, e) => sum + e.amount, 0);
  }, [expenses, group.id]);

  const memberBalances = useMemo(() => {
    const map = new Map<string, number>();
    group.members.forEach((m) => map.set(m.id, 0));
    expenses.filter((e) => e.groupId === group.id).forEach((exp) => {
      map.set(exp.paidBy, (map.get(exp.paidBy) || 0) + exp.amount);
      exp.splits.forEach((split) => {
        map.set(split.userId, (map.get(split.userId) || 0) - split.amountOwed);
      });
    });
    return map;
  }, [expenses, group]);

  const myGroupBalance = memberBalances.get(currentUser.id) || 0;
  const simplifiedTransactions: SimplifiedTransaction[] = useMemo(() => {
    return DebtSimplifierEngine.simplifyDebts(memberBalances);
  }, [memberBalances]);

  const handleShareSummary = async () => {
    const mySteps = simplifiedTransactions.filter(
      (s) => s.debtorId === currentUser.id || s.creditorId === currentUser.id
    );
    const sign = myGroupBalance >= 0 ? '+' : '';
    const status = myGroupBalance >= 0 ? 'Masadan Payın Var' : 'Masaya Payın Var';
    let text = `🏖️ ${group.name} - AradaPay Masa Özeti\n`;
    text += `Toplam Masaya Bırakılan: ${totalGroupSpend.toFixed(2)} ₺\n\n`;
    text += `📊 Masa Durumun: ${sign}${myGroupBalance.toFixed(2)} ₺ (${status})\n`;
    if (mySteps.length > 0) {
      text += `\n⚡ Senin FAST Ödeşmelerin:\n`;
      mySteps.forEach((s) => {
        const fromUser = group.members.find((m) => m.id === s.debtorId)?.name || 'Arkadaş';
        const toUser = group.members.find((m) => m.id === s.creditorId)?.name || 'Arkadaş';
        text += `👉 ${fromUser} -> ${toUser}: ${s.amount.toFixed(2)} ₺\n`;
      });
    }
    text += `\nAradaPay ile hesaplar tertemiz oldu ✨\nhttps://arada.whatevervedoneididitfor.fun/join/${group.id}`;
    if (navigator.share) {
      try { await navigator.share({ title: `${group.name} - Masa Özeti`, text }); showToast('Paylaşıldı'); return; } catch {}
    }
    await navigator.clipboard.writeText(text);
    showToast('Panoya kopyalandı 📋');
  };

  const candidateFriendsToAdd = useMemo(() => {
    return users.filter((u) => !group.members.some((m) => m.id === u.id) && 
      (searchMemberQuery ? u.fullName.toLowerCase().includes(searchMemberQuery.toLowerCase()) : true));
  }, [users, group.members, searchMemberQuery]);

  const handleSaveGroupEdit = () => {
    if (!editName.trim()) { showToast('Lütfen isim girin'); return; }
    onUpdateGroup?.({ ...group, name: editName.trim(), emoji: editEmoji, category: editCategory });
    setShowEditGroupModal(false);
    showToast('Güncellendi ✨');
  };

  if (showEditGroupModal) {
    return (
      <div className="space-y-5 text-left animate-fadeIn">
        <div className="bg-white rounded-[20px] border border-slate-200/80 px-5 py-3.5 flex items-center justify-between shadow-sm">
          <div className="flex items-center gap-3">
            <button onClick={() => setShowEditGroupModal(false)} className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center"><ArrowLeft className="w-5 h-5" /></button>
            <h2 className="text-[18px] font-bold">Masa Düzenle</h2>
          </div>
          <button onClick={handleSaveGroupEdit} className="w-10 h-10 rounded-full bg-[#E6F4EA] text-[#00875A] flex items-center justify-center"><Check className="w-5 h-5" /></button>
        </div>
        <div className="bg-white rounded-[20px] border border-slate-200/80 p-5 space-y-4">
          <div className="flex items-center gap-3">
            <button onClick={() => setShowCategoryDropdown(!showCategoryDropdown)} className="w-12 h-12 rounded-[14px] bg-[#F1F5F9] text-[22px]">{editEmoji}</button>
            <input type="text" value={editName} onChange={(e) => setEditName(e.target.value)} className="flex-1 h-12 px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200" />
          </div>
          {showCategoryDropdown && (
            <div className="flex items-center gap-2 overflow-x-auto">
              {CATEGORY_OPTIONS.map((cat) => (
                <button key={cat.id} onClick={() => { setEditCategory(cat.name); setEditEmoji(cat.emoji); setShowCategoryDropdown(false); }} className={`px-3.5 py-1.5 rounded-[12px] text-[12px] font-bold ${editCategory === cat.name ? 'bg-[#00875A] text-white' : 'bg-[#F1F5F9]'}`}>{cat.emoji} {cat.name}</button>
              ))}
            </div>
          )}
        </div>
        <div className="bg-white rounded-[20px] border border-slate-200/80 p-5 space-y-4 shadow-sm">
          <span className="text-[11px] font-bold text-[#64748B] uppercase">GRUP ÜYELERİ</span>
          {group.members.map((member) => (
            <div key={member.id} className="py-3 flex items-center justify-between">
              <div className="flex items-center gap-3.5">
                <div className={`w-10 h-10 rounded-[12px] flex items-center justify-center ${member.id === currentUser.id ? 'bg-[#E6F4EA] text-[#00875A]' : 'bg-[#F1F5F9]'}`}>{member.name.slice(0, 2).toUpperCase()}</div>
                <span className="text-[14px] font-bold">{member.name}</span>
              </div>
              {member.id !== currentUser.id && group.members.length > 2 && (
                <button onClick={() => setMemberToDelete(member)} className="w-8 h-8 rounded-full bg-rose-50 text-[#DC2626] flex items-center justify-center"><X className="w-4 h-4" /></button>
              )}
            </div>
          ))}
        </div>
        <div className="bg-white rounded-[20px] border border-slate-200/80 p-5 space-y-4">
          <div onClick={() => setShowDeleteConfirm(true)} className="flex items-center justify-between cursor-pointer text-[#DC2626]">
            <div className="flex items-center gap-3"><Trash2 className="w-5 h-5" /> <span>Masayı Sil</span></div>
          </div>
        </div>
        {showDeleteConfirm && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
            <div className="bg-white max-w-sm w-full rounded-[24px] p-6 space-y-4">
              <h3 className="text-[17px] font-bold text-[#DC2626]">Masayı Sil</h3>
              <p className="text-[13px]">Emin misiniz?</p>
              <div className="flex justify-end gap-3"><button onClick={() => setShowDeleteConfirm(false)} className="text-[13px] font-bold">Vazgeç</button><button onClick={() => { setShowDeleteConfirm(false); onDeleteGroup?.(group.id); }} className="text-[13px] font-bold text-[#DC2626]">Sil</button></div>
            </div>
          </div>
        )}
      </div>
    );
  }

  if (showSimplifyDebtsModal) {
    const myTransferSteps = simplifiedTransactions.filter((s) => s.debtorId === currentUser.id || s.creditorId === currentUser.id);
    return (
      <div className="space-y-5 text-left animate-fadeIn">
        <div className="bg-white rounded-[20px] border border-slate-200/80 px-5 py-3.5 flex items-center gap-3">
          <button onClick={() => setShowSimplifyDebtsModal(false)} className="w-10 h-10 rounded-[12px] bg-[#F1F5F9]"><ArrowLeft className="w-5 h-5" /></button>
          <h2 className="text-[18px] font-bold">Akıllı Masa Dengeleme</h2>
        </div>
        <div className="bg-white rounded-[20px] border border-slate-200/80 p-5 space-y-4">
          {(myTransferSteps.length > 0 ? myTransferSteps : simplifiedTransactions).map((step, idx) => (
            <div key={idx} className="py-3.5 flex items-center justify-between">
              <span className="text-[14px] font-bold">{group.members.find(m => m.id === step.debtorId)?.name} ➔ {group.members.find(m => m.id === step.creditorId)?.name}</span>
              <span className="text-[14px] font-black text-[#00875A]">{step.amount.toFixed(2)} ₺</span>
            </div>
          ))}
        </div>
        <button onClick={() => setShowSimplifyDebtsModal(false)} className="w-full h-[52px] rounded-[16px] bg-[#1E293B] text-white font-bold">Tamam</button>
      </div>
    );
  }

  const settledExpensesCount = groupExpenses.filter((e) => e.splits.every((s) => s.amountOwed === 0)).length;

  return (
    <div className="space-y-4 text-left animate-fadeIn">
      {toastMessage && (
        <div className="fixed top-20 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-[12px] bg-[#0F172A] text-white text-[13px] font-bold shadow-xl animate-applePop">
          {toastMessage}
        </div>
      )}

      {/* Hero Financial & Group Identity Card (Unified Single Layer) */}
      <div className="bg-white rounded-[24px] border border-slate-200/80 p-5 sm:p-6 space-y-4 shadow-sm">
        {/* Top Identity & Action Strip */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3 min-w-0">
            <div className="w-12 h-12 rounded-[14px] bg-[#F1F5F9] flex items-center justify-center text-[24px] shadow-2xs flex-shrink-0">
              {group.emoji || '👥'}
            </div>
            <div className="min-w-0">
              <h3 className="text-[17px] font-bold text-[#0F172A] tracking-tight truncate">{group.name}</h3>
              <p className="text-[12px] text-[#64748B] truncate">
                {group.members.length} Üye • {group.category || 'Genel'}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2 flex-shrink-0">
            <button
              onClick={handleShareSummary}
              className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-90 transition flex items-center justify-center text-[#0F172A]"
              title="Masa Özetini Paylaş"
            >
              <Share2 className="w-4 h-4 text-[#0F172A]" />
            </button>
            <button
              onClick={() => setShowEditGroupModal(true)}
              className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-90 transition flex items-center justify-center text-[#0F172A]"
              title="Masayı Düzenle"
            >
              <Edit3 className="w-4 h-4 text-[#0F172A]" />
            </button>
          </div>
        </div>

        {/* 3-Column Metric Strip */}
        <div className="grid grid-cols-3 divide-x divide-slate-200 bg-[#F8FAFC] rounded-[14px] py-3 text-center">
          <div>
            <span className="text-[17px] font-extrabold text-[#0F172A] font-tabular block">{groupExpenses.length}</span>
            <p className="text-[11px] font-medium text-[#64748B] mt-0.5">Harcama</p>
          </div>
          <div>
            <span className="text-[17px] font-extrabold text-[#0F172A] font-tabular block">
              {totalGroupSpend.toLocaleString('tr-TR', { minimumFractionDigits: 0, maximumFractionDigits: 0 })} ₺
            </span>
            <p className="text-[11px] font-medium text-[#64748B] mt-0.5">Toplam</p>
          </div>
          <div>
            <span className="text-[17px] font-extrabold text-[#00875A] font-tabular block">
              {settledExpensesCount}/{groupExpenses.length}
            </span>
            <p className="text-[11px] font-medium text-[#64748B] mt-0.5">Tertemiz</p>
          </div>
        </div>

        {/* CTA Buttons */}
        <div className="grid grid-cols-2 gap-2.5 pt-1">
          <button
            onClick={() => onAddExpenseInGroup(group)}
            className="h-[46px] rounded-[14px] bg-[#00875A] hover:bg-[#00744d] active:scale-[0.97] text-white font-bold text-[14px] flex items-center justify-center gap-1.5 transition shadow-sm shadow-emerald-900/15"
          >
            <Plus className="w-4 h-4 stroke-[2.5]" />
            <span>Masaya Bırak</span>
          </button>
          <button
            onClick={() => {
              const creditorMember = group.members.find((m) => m.id !== currentUser.id) || group.members[0];
              const targetUser = users.find((u) => u.id === creditorMember?.id) || currentUser;
              onOpenSettleUp(targetUser, Math.abs(myGroupBalance));
            }}
            className="h-[46px] rounded-[14px] bg-[#0F172A] hover:bg-[#1E293B] active:scale-[0.97] text-white font-bold text-[14px] flex items-center justify-center gap-1.5 transition shadow-sm"
          >
            <CreditCard className="w-4 h-4" />
            <span>Ödeş & Kapat</span>
          </button>
        </div>

        {/* Smart Debt Simplification Banner Quick Trigger */}
        <button
          onClick={() => setShowSimplifyDebtsModal(true)}
          className="w-full py-2.5 px-4 rounded-[12px] bg-emerald-50 hover:bg-emerald-100 active:scale-[0.98] text-[#00875A] font-bold text-[12px] flex items-center justify-between transition"
        >
          <span className="flex items-center gap-2">
            <Zap className="w-4 h-4 fill-emerald-600 text-emerald-600" />
            <span>Akıllı Masa Dengelemeyi Gör</span>
          </span>
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>
      <div className="space-y-3">
        <span className="text-[11px] font-bold text-[#64748B] uppercase">MASADAKİ HAREKETLER ({groupExpenses.length})</span>
        {groupExpenses.map((item) => (
          <div key={item.id} onClick={() => onViewExpenseDetail(item)} className="p-4 bg-white rounded-[20px] border border-slate-200/80 flex items-center justify-between cursor-pointer">
            <div className="flex items-center gap-3.5"><div className="w-11 h-11 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center">📦</div><div><h4 className="text-[15px] font-semibold">{item.description}</h4><p className="text-[12px] text-[#64748B]">{item.paidBy === currentUser.id ? 'Masayı sen üstlendin' : 'Arkadaş üstlendi'}</p></div></div>
            <span className={`text-[14px] font-bold ${item.paidBy === currentUser.id ? 'text-[#00875A]' : 'text-[#DC2626]'}`}>{item.amount.toFixed(2)} ₺</span>
          </div>
        ))}
      </div>
      {showAddMemberModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60">
          <div className="bg-white w-full max-w-sm rounded-[24px] p-5 space-y-4 overflow-y-auto">
            <h3 className="text-[17px] font-bold">Üye Ekle</h3>
            {candidateFriendsToAdd.map((u) => (
              <div key={u.id} onClick={() => { onAddMemberToGroup?.(group.id, u); setShowAddMemberModal(false); }} className="flex justify-between items-center py-2 cursor-pointer">
                <span className="text-[14px] font-bold">{u.fullName}</span>
                <span className="text-[12px] font-bold bg-[#00875A] text-white px-3 py-1 rounded-lg">+ Ekle</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
