'use client';

import React, { useState } from 'react';
import { X, Check, Users, Calculator, Percent, DollarSign, Plus, ArrowLeft } from 'lucide-react';
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
  { key: 'TRAVEL', label: 'Ulaşım & Seyahat', icon: '✈️' },
  { key: 'HOUSING', label: 'Ev & Kira', icon: '🏠' },
  { key: 'ENTERTAINMENT', label: 'Eğlence & Konser', icon: '🍿' },
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
  const [paidBy, setPaidBy] = useState<string>(currentUser.id);
  const [category, setCategory] = useState<ExpenseCategory>('DINING');
  const [splitMethod, setSplitMethod] = useState<SplitMethod>('EQUAL');
  const [selectedGroupId, setSelectedGroupId] = useState<string>('');
  const [selectedUserIds, setSelectedUserIds] = useState<string[]>([currentUser.id, users[1]?.id || 'u2']);

  // Custom Split percentage and exact amounts maps
  const [customPercentages, setCustomPercentages] = useState<{ [userId: string]: number }>({});
  const [customAmounts, setCustomAmounts] = useState<{ [userId: string]: number }>({});

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

  const calculateUserSplit = (userId: string): number => {
    if (splitMethod === 'EQUAL') {
      return parseFloat((numAmount / selectedUserIds.length).toFixed(2));
    }
    if (splitMethod === 'PERCENTAGE') {
      const pct = customPercentages[userId] ?? 100 / selectedUserIds.length;
      return parseFloat(((numAmount * pct) / 100).toFixed(2));
    }
    if (splitMethod === 'EXACT') {
      return customAmounts[userId] ?? parseFloat((numAmount / selectedUserIds.length).toFixed(2));
    }
    return 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!isValid) return;

    const splits = selectedUserIds.map((userId) => ({
      id: `split_${Date.now()}_${userId}`,
      expenseId: '',
      userId,
      amountOwed: calculateUserSplit(userId),
      status: 'APPROVED' as const
    }));

    const newExpense: Expense = {
      id: `exp_${Date.now()}`,
      groupId: selectedGroupId || null,
      paidBy: paidBy,
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
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
      <div className="bg-white w-full max-w-lg rounded-[28px] shadow-2xl border border-slate-200 overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="px-5 py-4 bg-white border-b border-slate-200 flex items-center justify-between">
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-[12px] bg-slate-100 flex items-center justify-center text-slate-800 hover:bg-slate-200 active:scale-95 transition"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>

          <h3 className="text-[18px] font-black text-textPrimary tracking-tight">
            Harcama Ekle
          </h3>

          <button
            onClick={handleSubmit}
            disabled={!isValid}
            className={`w-10 h-10 rounded-[12px] flex items-center justify-center transition active:scale-95 ${
              isValid
                ? 'bg-primaryEmerald text-white hover:bg-[#00744d]'
                : 'bg-slate-100 text-slate-400 cursor-not-allowed'
            }`}
          >
            <Check className="w-5 h-5 stroke-[2.5]" />
          </button>
        </div>

        {/* Scrollable Form Body */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-5 space-y-5">
          {/* Tutar & Açıklama */}
          <div className="p-5 rounded-[22px] bg-[#F8FAFC] border border-slate-200 text-center space-y-3">
            <span className="text-[11px] font-black text-slate-400 uppercase tracking-wider block">
              HARCAMA TUTARI (₺)
            </span>

            <div className="flex items-center justify-center">
              <input
                type="number"
                step="0.01"
                required
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="0.00"
                className="text-[36px] font-black text-center text-textPrimary bg-transparent border-none focus:outline-none w-48 placeholder:text-slate-300 font-tabular"
              />
              <span className="text-[28px] font-bold text-slate-400 ml-1">₺</span>
            </div>

            {/* Quick Inc buttons */}
            <div className="flex items-center justify-center gap-2 pt-1">
              {[50, 100, 250, 500].map((val) => (
                <button
                  key={val}
                  type="button"
                  onClick={() => handleQuickAdd(val)}
                  className="px-3 py-1 rounded-[10px] bg-white border border-slate-200 text-slate-700 text-[12px] font-bold hover:border-emerald-400 hover:text-primaryEmerald active:scale-95 transition shadow-2xs"
                >
                  +{val} ₺
                </button>
              ))}
            </div>

            {/* Description */}
            <div className="pt-2">
              <input
                type="text"
                required
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Ne için harcandı? (örn: Akşam Yemeği, Kahveler)"
                className="w-full h-11 px-4 rounded-[14px] bg-white border border-slate-200 text-[14px] text-center text-textPrimary placeholder:text-slate-400 focus:outline-none focus:border-primaryEmerald transition"
              />
            </div>
          </div>

          {/* Kategori Seçici */}
          <div className="space-y-2">
            <span className="text-[11px] font-black text-slate-500 uppercase tracking-wider block">
              KATEGORİ
            </span>
            <div className="grid grid-cols-4 gap-2">
              {CATEGORIES.map((cat) => (
                <button
                  key={cat.key}
                  type="button"
                  onClick={() => setCategory(cat.key)}
                  className={`p-2.5 rounded-[14px] border text-center transition flex flex-col items-center gap-1 active:scale-95 ${
                    category === cat.key
                      ? 'bg-emerald-50 border-primaryEmerald text-primaryEmerald font-bold shadow-2xs'
                      : 'bg-[#F8FAFC] border-slate-200 text-slate-700 hover:bg-slate-100'
                  }`}
                >
                  <span className="text-[20px]">{cat.icon}</span>
                  <span className="text-[11px] leading-tight truncate w-full">{cat.label}</span>
                </button>
              ))}
            </div>
          </div>

          {/* Ödeyen Kişi Seçici */}
          <div className="space-y-2">
            <span className="text-[11px] font-black text-slate-500 uppercase tracking-wider block">
              ÖDEMEYİ YAPAN KİŞİ
            </span>
            <select
              value={paidBy}
              onChange={(e) => setPaidBy(e.target.value)}
              className="w-full h-11 px-3.5 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] font-bold text-textPrimary focus:outline-none focus:border-primaryEmerald"
            >
              {users.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.id === currentUser.id ? `Ben (${u.fullName})` : u.fullName}
                </option>
              ))}
            </select>
          </div>

          {/* Bölüşüm Tipi (M3 Segmented Control) */}
          <div className="space-y-2">
            <span className="text-[11px] font-black text-slate-500 uppercase tracking-wider block">
              BÖLÜŞÜM YÖNTEMİ
            </span>
            <div className="grid grid-cols-3 gap-2 p-1 bg-slate-100 rounded-[16px]">
              <button
                type="button"
                onClick={() => setSplitMethod('EQUAL')}
                className={`py-2 rounded-[12px] text-[12px] font-bold transition flex items-center justify-center gap-1.5 ${
                  splitMethod === 'EQUAL'
                    ? 'bg-white text-textPrimary shadow-sm'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <Calculator className="w-3.5 h-3.5" />
                <span>Eşit Bölüşüm</span>
              </button>

              <button
                type="button"
                onClick={() => setSplitMethod('PERCENTAGE')}
                className={`py-2 rounded-[12px] text-[12px] font-bold transition flex items-center justify-center gap-1.5 ${
                  splitMethod === 'PERCENTAGE'
                    ? 'bg-white text-textPrimary shadow-sm'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <Percent className="w-3.5 h-3.5" />
                <span>Yüzdelik %</span>
              </button>

              <button
                type="button"
                onClick={() => setSplitMethod('EXACT')}
                className={`py-2 rounded-[12px] text-[12px] font-bold transition flex items-center justify-center gap-1.5 ${
                  splitMethod === 'EXACT'
                    ? 'bg-white text-textPrimary shadow-sm'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <DollarSign className="w-3.5 h-3.5" />
                <span>Özel Tutar</span>
              </button>
            </div>
          </div>

          {/* Katılımcı Listesi ve Kişi Başı Pay */}
          <div className="space-y-2.5">
            <div className="flex items-center justify-between">
              <span className="text-[11px] font-black text-slate-500 uppercase tracking-wider">
                MASRAFI PAYLAŞANLAR ({selectedUserIds.length})
              </span>
              <span className="text-[11px] text-primaryEmerald font-bold">
                Kişi Başı: {(numAmount / Math.max(selectedUserIds.length, 1)).toFixed(2)} ₺
              </span>
            </div>

            <div className="space-y-2">
              {users.map((user) => {
                const isSelected = selectedUserIds.includes(user.id);
                return (
                  <div
                    key={user.id}
                    onClick={() => toggleUser(user.id)}
                    className={`p-3 rounded-[16px] border flex items-center justify-between cursor-pointer transition active:scale-[0.99] ${
                      isSelected
                        ? 'bg-emerald-50/60 border-primaryEmerald/50'
                        : 'bg-[#F8FAFC] border-slate-200 text-slate-400'
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <div
                        className={`w-9 h-9 rounded-xl flex items-center justify-center font-bold text-[13px] ${
                          isSelected
                            ? 'bg-primaryEmerald text-white'
                            : 'bg-slate-200 text-slate-600'
                        }`}
                      >
                        {user.fullName.slice(0, 2).toUpperCase()}
                      </div>
                      <div>
                        <div
                          className={`text-[14px] font-bold ${
                            isSelected ? 'text-textPrimary' : 'text-slate-500'
                          }`}
                        >
                          {user.id === currentUser.id ? 'Sen' : user.fullName}
                        </div>
                        <div className="text-[11px] text-slate-500 font-mono">
                          {user.tag || `@${user.username}`}
                        </div>
                      </div>
                    </div>

                    <div className="text-right">
                      <div className="text-[13px] font-black text-textPrimary font-tabular">
                        {calculateUserSplit(user.id)} ₺
                      </div>
                      <div className="text-[10px] text-slate-500 font-medium">
                        {splitMethod === 'EQUAL' ? 'Eşit Pay' : 'Pay'}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </form>

        {/* Submit Button in Footer */}
        <div className="p-4 bg-white border-t border-slate-200">
          <button
            onClick={handleSubmit}
            disabled={!isValid}
            className={`w-full h-12 rounded-[16px] font-black text-[15px] flex items-center justify-center gap-2 transition shadow-sm ${
              isValid
                ? 'bg-primaryEmerald text-white hover:bg-[#00744d] active:scale-95'
                : 'bg-slate-100 text-slate-400 cursor-not-allowed'
            }`}
          >
            <Check className="w-5 h-5 stroke-[2.5]" />
            <span>Harcamayı Kaydet ({numAmount.toFixed(2)} ₺)</span>
          </button>
        </div>
      </div>
    </div>
  );
};
