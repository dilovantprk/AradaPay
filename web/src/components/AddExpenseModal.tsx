import React, { useState } from 'react';
import { X, Check } from 'lucide-react';
import { User, Group, ExpenseCategory, SplitMethod, Expense } from '../types';

interface AddExpenseModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: User;
  users: User[];
  groups: Group[];
  onAddExpense: (expense: Expense) => void;
}

const CATEGORIES: { key: ExpenseCategory; label: string; icon: string }[] = [
  { key: 'DINING', label: 'Yemek & Kafe', icon: '🍽️' },
  { key: 'GROCERIES', label: 'Market', icon: '🛒' },
  { key: 'TRAVEL', label: 'Seyahat', icon: '✈️' },
  { key: 'HOUSING', label: 'Ev & Yaşam', icon: '🏠' },
  { key: 'ENTERTAINMENT', label: 'Eğlence', icon: '🍿' },
  { key: 'UTILITIES', label: 'Faturalar', icon: '⚡' },
  { key: 'SHOPPING', label: 'Alışveriş', icon: '🛍️' },
  { key: 'OTHER', label: 'Diğer', icon: '📦' }
];

export const AddExpenseModal: React.FC<AddExpenseModalProps> = ({
  isOpen,
  onClose,
  currentUser,
  users,
  groups,
  onAddExpense
}) => {
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [category, setCategory] = useState<ExpenseCategory>('DINING');
  const [splitMethod, setSplitMethod] = useState<SplitMethod>('EQUAL');
  const [selectedGroupId, setSelectedGroupId] = useState<string>('');
  const [selectedUserIds, setSelectedUserIds] = useState<string[]>([currentUser.id]);

  if (!isOpen) return null;

  const numAmount = parseFloat(amount.replace(',', '.')) || 0;
  const isValid = description.trim().length > 0 && numAmount > 0 && selectedUserIds.length > 0;

  const handleQuickAdd = (inc: number) => {
    const current = parseFloat(amount.replace(',', '.')) || 0;
    setAmount((current + inc).toString());
  };

  const toggleUser = (userId: string) => {
    if (selectedUserIds.includes(userId)) {
      if (selectedUserIds.length > 1) {
        setSelectedUserIds(selectedUserIds.filter((id) => id !== userId));
      }
    } else {
      setSelectedUserIds([...selectedUserIds, userId]);
    }
  };

  const handleGroupSelect = (groupId: string) => {
    setSelectedGroupId(groupId);
    if (groupId) {
      const group = groups.find((g) => g.id === groupId);
      if (group) {
        setSelectedUserIds(group.members.map((m) => m.id));
      }
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isValid) return;

    // Calculate splits
    const splitCount = selectedUserIds.length;
    const shareAmount = parseFloat((numAmount / splitCount).toFixed(2));

    const splits = selectedUserIds.map((userId) => ({
      id: `split_${Date.now()}_${userId}`,
      expenseId: '',
      userId,
      amountOwed: shareAmount,
      status: 'APPROVED' as const
    }));

    const newExpense: Expense = {
      id: `exp_${Date.now()}`,
      groupId: selectedGroupId || null,
      paidBy: currentUser.id,
      amount: numAmount,
      currency: 'TRY',
      description: description.trim(),
      category,
      splitMethod,
      createdAt: new Date().toISOString(),
      date: new Date().toLocaleDateString('tr-TR', { day: 'numeric', month: 'long', year: 'numeric' }),
      status: 'APPROVED',
      splits
    };

    onAddExpense(newExpense);
    onClose();
    // Reset form
    setDescription('');
    setAmount('');
    setSelectedGroupId('');
    setSelectedUserIds([currentUser.id]);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
      <div className="bg-surfaceWhite w-full max-w-lg rounded-[24px] shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="px-6 py-4 border-b border-surfaceBorder flex items-center justify-between">
          <h2 className="text-[18px] font-bold text-textPrimary">Harcama Ekle</h2>
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-surfaceContainerLow flex items-center justify-center text-textSecondary hover:bg-slate-200 active:scale-95 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Scrollable Form */}
        <form onSubmit={handleSubmit} className="p-6 overflow-y-auto flex-1 space-y-5">
          {/* Big Amount Input Card */}
          <div className="flex flex-col items-center justify-center py-4 px-3 bg-surfaceContainerLow/50 rounded-[20px]">
            <span className="text-[11px] font-semibold text-textSecondary tracking-wider uppercase mb-1">
              TOPLAM TUTAR
            </span>
            <div className="flex items-center justify-center gap-2">
              <span className="text-[32px] font-bold text-primaryEmerald">₺</span>
              <input
                type="text"
                placeholder="0,00"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                autoFocus
                className="w-48 text-[38px] font-extrabold text-textPrimary bg-transparent border-none outline-none text-center focus:ring-0 placeholder:text-slate-300"
              />
            </div>

            {/* Quick Increment Chips */}
            <div className="flex items-center gap-2 mt-3 flex-wrap justify-center">
              {[50, 100, 250, 500].map((inc) => (
                <button
                  key={inc}
                  type="button"
                  onClick={() => handleQuickAdd(inc)}
                  className="px-3 py-1 rounded-full bg-white border border-slate-200 text-textSecondary text-[12px] font-bold hover:border-primaryEmerald hover:text-primaryEmerald active:scale-95 transition shadow-2xs"
                >
                  +{inc} ₺
                </button>
              ))}
            </div>
          </div>

          {/* Description Input */}
          <div>
            <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-1.5">
              AÇIKLAMA
            </label>
            <input
              type="text"
              placeholder="Örn: Akşam Yemeği, Market, Uber..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full px-4 py-3 rounded-[14px] bg-white border border-slate-200 text-textPrimary text-[14px] font-medium outline-none focus:border-primaryEmerald focus:ring-2 focus:ring-primaryEmeraldContainer transition"
            />
          </div>

          {/* Group Selector (Optional) */}
          {groups.length > 0 && (
            <div>
              <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-1.5">
                GRUP (OPSİYONEL)
              </label>
              <select
                value={selectedGroupId}
                onChange={(e) => handleGroupSelect(e.target.value)}
                className="w-full px-4 py-3 rounded-[14px] bg-white border border-slate-200 text-textPrimary text-[14px] font-medium outline-none focus:border-primaryEmerald focus:ring-2 focus:ring-primaryEmeraldContainer transition"
              >
                <option value="">Bireysel / Arkadaşlar Arası</option>
                {groups.map((g) => (
                  <option key={g.id} value={g.id}>
                    {g.emoji} {g.name}
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Category Chips */}
          <div>
            <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-1.5">
              KATEGORİ
            </label>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
              {CATEGORIES.map((cat) => {
                const isSelected = category === cat.key;
                return (
                  <button
                    key={cat.key}
                    type="button"
                    onClick={() => setCategory(cat.key)}
                    className={`px-3 py-2 rounded-[12px] text-[12px] font-semibold flex items-center gap-1.5 border transition ${
                      isSelected
                        ? 'bg-primaryEmeraldContainer border-primaryEmerald text-primaryEmerald font-bold'
                        : 'bg-white border-slate-200 text-textSecondary hover:bg-slate-50'
                    }`}
                  >
                    <span>{cat.icon}</span>
                    <span className="truncate">{cat.label}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Participants Selector */}
          <div>
            <div className="flex items-center justify-between mb-1.5">
              <label className="text-[12px] font-bold text-textSecondary uppercase tracking-wider">
                KİMLERLE BÖLÜŞÜLECEK ({selectedUserIds.length} kişi)
              </label>
              {numAmount > 0 && selectedUserIds.length > 0 && (
                <span className="text-[12px] font-bold text-primaryEmerald">
                  Kişi Başı: {(numAmount / selectedUserIds.length).toFixed(2)} ₺
                </span>
              )}
            </div>

            <div className="flex flex-wrap gap-2 max-h-36 overflow-y-auto p-1">
              {users.map((u) => {
                const isSelected = selectedUserIds.includes(u.id);
                const isMe = u.id === currentUser.id;
                return (
                  <button
                    key={u.id}
                    type="button"
                    onClick={() => toggleUser(u.id)}
                    className={`px-3 py-1.5 rounded-full text-[13px] font-semibold flex items-center gap-2 border transition active:scale-95 ${
                      isSelected
                        ? 'bg-primaryEmerald text-white border-primaryEmerald shadow-sm'
                        : 'bg-white text-textSecondary border-slate-200 hover:border-slate-300'
                    }`}
                  >
                    <span className="w-5 h-5 rounded-full bg-white/20 flex items-center justify-center text-[10px] font-bold">
                      {u.fullName.substring(0, 2).toUpperCase()}
                    </span>
                    <span>{isMe ? 'Sen (Ödeyen)' : u.fullName.split(' ')[0]}</span>
                    {isSelected && <Check className="w-3.5 h-3.5" />}
                  </button>
                );
              })}
            </div>
          </div>
        </form>

        {/* Footer CTA */}
        <div className="p-4 border-t border-surfaceBorder bg-surfaceWhite">
          <button
            type="button"
            onClick={handleSubmit}
            disabled={!isValid}
            className={`w-full h-[52px] rounded-[16px] font-bold text-[15px] flex items-center justify-center gap-2 transition shadow-sm ${
              isValid
                ? 'bg-primaryEmerald text-white hover:bg-[#00744d] active:scale-[0.98]'
                : 'bg-slate-100 text-slate-400 cursor-not-allowed'
            }`}
          >
            <Check className="w-5 h-5" />
            <span>Harcamayı Kaydet & Böl</span>
          </button>
        </div>
      </div>
    </div>
  );
};
