'use client';

import React, { useState } from 'react';
import {
  ArrowLeft,
  Check,
  X,
  ChevronDown,
  Search,
  CheckCircle2,
  Circle,
  Utensils,
  ShoppingCart,
  Plane,
  Home,
  Film,
  Zap,
  Tag,
  Receipt
} from 'lucide-react';
import { User, Group, ExpenseCategory, SplitMethod, Expense } from '../types';

interface AddExpenseModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: User;
  users: User[];
  groups: Group[];
  onAddExpense: (expense: Expense) => void;
}

interface CategoryOption {
  key: ExpenseCategory;
  name: string;
  icon: React.ReactNode;
  bgTint: string;
  iconTint: string;
}

const CATEGORY_OPTIONS: CategoryOption[] = [
  { key: 'DINING', name: 'Yemek & Restoran', icon: <Utensils className="w-5 h-5 text-[#00875A]" />, bgTint: 'bg-emerald-50', iconTint: '#00875A' },
  { key: 'GROCERIES', name: 'Market & Alışveriş', icon: <ShoppingCart className="w-5 h-5 text-blue-600" />, bgTint: 'bg-blue-50', iconTint: '#2563EB' },
  { key: 'TRAVEL', name: 'Ulaşım & Seyahat', icon: <Plane className="w-5 h-5 text-purple-600" />, bgTint: 'bg-purple-50', iconTint: '#9333EA' },
  { key: 'HOUSING', name: 'Ev & Kira', icon: <Home className="w-5 h-5 text-amber-600" />, bgTint: 'bg-amber-50', iconTint: '#D97706' },
  { key: 'ENTERTAINMENT', name: 'Eğlence & Etkinlik', icon: <Film className="w-5 h-5 text-pink-600" />, bgTint: 'bg-pink-50', iconTint: '#DB2777' },
  { key: 'UTILITIES', name: 'Faturalar & Aidat', icon: <Zap className="w-5 h-5 text-indigo-600" />, bgTint: 'bg-indigo-50', iconTint: '#4F46E5' },
  { key: 'SHOPPING', name: 'Giyim & Aksesuar', icon: <Tag className="w-5 h-5 text-teal-600" />, bgTint: 'bg-teal-50', iconTint: '#0D9488' },
  { key: 'OTHER', name: 'Diğer Masraflar', icon: <Receipt className="w-5 h-5 text-slate-600" />, bgTint: 'bg-slate-100', iconTint: '#475569' }
];

export const AddExpenseModal: React.FC<AddExpenseModalProps> = ({
  isOpen,
  onClose,
  currentUser,
  users,
  groups,
  onAddExpense
}) => {
  // Screen sub-modes: 'NORMAL' | 'CATEGORY' | 'PARTICIPANT' | 'SPLIT'
  const [screenMode, setScreenMode] = useState<'NORMAL' | 'CATEGORY' | 'PARTICIPANT' | 'SPLIT'>('NORMAL');

  const [description, setDescription] = useState('');
  const [amountText, setAmountText] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<ExpenseCategory>('DINING');
  const [selectedPayerId, setSelectedPayerId] = useState<string>(currentUser.id);
  const [splitMethod, setSplitMethod] = useState<SplitMethod>('EQUAL');

  // Initial friend: first non-current user
  const initialFriend = users.find((u) => u.id !== currentUser.id);
  const [selectedFriendIds, setSelectedFriendIds] = useState<string[]>(
    initialFriend ? [initialFriend.id] : []
  );
  const [excludedFriendIds, setExcludedFriendIds] = useState<string[]>([]);
  const [includeMyselfInSplit, setIncludeMyselfInSplit] = useState(true);
  const [participantSearchQuery, setParticipantSearchQuery] = useState('');

  // Custom percentages / exact amounts
  const [customPercentages, setCustomPercentages] = useState<{ [userId: string]: string }>({});
  const [customExacts, setCustomExacts] = useState<{ [userId: string]: string }>({});

  if (!isOpen) return null;

  const numAmount = parseFloat(amountText.replace(',', '.')) || 0;

  // Selected friends (excluding currentUser)
  const selectedFriends = users.filter(
    (u) => u.id !== currentUser.id && selectedFriendIds.includes(u.id)
  );

  // Active participants who are included in the bill
  const activeFriendIds = selectedFriendIds.filter((id) => !excludedFriendIds.includes(id));
  const allActiveUserIds = [
    ...(includeMyselfInSplit ? [currentUser.id] : []),
    ...activeFriendIds
  ];

  const isValid = description.trim().length > 0 && numAmount > 0 && selectedFriendIds.length > 0;

  // Selected Category Info
  const activeCategoryInfo = CATEGORY_OPTIONS.find((c) => c.key === selectedCategory) || CATEGORY_OPTIONS[0];

  // Payer display name
  const payerUser = users.find((u) => u.id === selectedPayerId);
  const payerName = selectedPayerId === currentUser.id ? 'Sen' : payerUser?.fullName || 'Arkadaş';
  const payerShortName = selectedPayerId === currentUser.id ? 'Sen' : payerUser?.fullName?.split(' ')[0] || 'Arkadaş';

  // Per person equal amount
  const perPersonEqual = allActiveUserIds.length > 0 ? numAmount / allActiveUserIds.length : 0;

  const handleQuickAdd = (val: number) => {
    const current = parseFloat(amountText.replace(',', '.')) || 0;
    const next = current + val;
    setAmountText(next % 1 === 0 ? next.toString() : next.toFixed(2));
  };

  const toggleFriendSelection = (userId: string) => {
    if (selectedFriendIds.includes(userId)) {
      if (selectedFriendIds.length > 1) {
        setSelectedFriendIds(selectedFriendIds.filter((id) => id !== userId));
        setExcludedFriendIds(excludedFriendIds.filter((id) => id !== userId));
        if (selectedPayerId === userId) setSelectedPayerId(currentUser.id);
      }
    } else {
      setSelectedFriendIds([...selectedFriendIds, userId]);
    }
  };

  const calculateUserAmount = (userId: string): number => {
    if (!allActiveUserIds.includes(userId)) return 0;
    if (splitMethod === 'EQUAL') {
      return parseFloat(perPersonEqual.toFixed(2));
    }
    if (splitMethod === 'PERCENTAGE') {
      const pct = parseFloat(customPercentages[userId] || '0') || (allActiveUserIds.length > 0 ? 100 / allActiveUserIds.length : 0);
      return parseFloat(((numAmount * pct) / 100).toFixed(2));
    }
    if (splitMethod === 'EXACT') {
      return parseFloat(customExacts[userId] || '0') || perPersonEqual;
    }
    return 0;
  };

  const handleSubmit = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!isValid) return;

    const splits = allActiveUserIds.map((userId) => ({
      id: `split_${Date.now()}_${userId}`,
      expenseId: '',
      userId,
      amountOwed: calculateUserAmount(userId),
      status: 'APPROVED' as const
    }));

    const newExpense: Expense = {
      id: `exp_${Date.now()}`,
      groupId: null,
      paidBy: selectedPayerId,
      amount: numAmount,
      currency: 'TRY',
      description: description.trim(),
      category: selectedCategory,
      splitMethod,
      createdAt: new Date().toISOString(),
      date: new Date().toLocaleDateString('tr-TR', { day: 'numeric', month: 'long', year: 'numeric' }),
      status: 'APPROVED',
      splits
    };

    onAddExpense(newExpense);
    onClose();
  };

  const filteredUsers = users.filter((u) => {
    if (u.id === currentUser.id) return false;
    return (
      u.fullName.toLowerCase().includes(participantSearchQuery.toLowerCase()) ||
      (u.tag && u.tag.toLowerCase().includes(participantSearchQuery.toLowerCase()))
    );
  });

  // Mode title
  const getHeaderTitle = () => {
    switch (screenMode) {
      case 'CATEGORY':
        return 'Kategori Seç';
      case 'PARTICIPANT':
        return 'Kişi veya Grup Ekle';
      case 'SPLIT':
        return 'Ödeyen ve Bölüşüm';
      default:
        return 'Harcama Ekle';
    }
  };

  const splitModeLabel = splitMethod === 'EQUAL' ? 'Eşit' : splitMethod === 'PERCENTAGE' ? 'Yüzdelik' : 'Özel tutarlarla';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-[640px] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* ========================================================================= */}
        {/* 1. TOP BAR (1:1 Android AddExpenseScreen.kt) */}
        {/* ========================================================================= */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3 bg-white border-b border-slate-100 flex items-center justify-between flex-shrink-0 h-[64px]">
          <button
            onClick={() => {
              if (screenMode !== 'NORMAL') {
                setScreenMode('NORMAL');
              } else {
                onClose();
              }
            }}
            className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center text-[#0F172A] hover:bg-slate-200 active:scale-95 transition"
            title="Geri"
          >
            <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
          </button>

          <h3 className="text-[17px] font-bold text-[#0F172A] tracking-tight">
            {getHeaderTitle()}
          </h3>

          {screenMode !== 'NORMAL' ? (
            <button
              onClick={() => setScreenMode('NORMAL')}
              className="w-10 h-10 rounded-[12px] bg-emerald-100 text-[#00875A] flex items-center justify-center active:scale-95 transition"
              title="Tamamla"
            >
              <Check className="w-5 h-5 stroke-[2.5]" />
            </button>
          ) : (
            <div className="w-10" />
          )}
        </div>

        {/* ========================================================================= */}
        {/* 2. SUB-MODE: KATEGORİ SEÇİM MODU */}
        {/* ========================================================================= */}
        {screenMode === 'CATEGORY' && (
          <div className="flex-1 overflow-y-auto p-5 space-y-4 animate-fadeIn">
            <div>
              <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase">
                TÜM KATEGORİLER
              </span>
              <p className="text-[13px] text-[#8E8E93] mt-0.5">
                Harcamaya en uygun kategoriyi seçin
              </p>
            </div>

            <div className="space-y-2">
              {CATEGORY_OPTIONS.map((cat) => {
                const isSelected = selectedCategory === cat.key;
                return (
                  <div
                    key={cat.key}
                    onClick={() => {
                      setSelectedCategory(cat.key);
                      if (!description) setDescription(cat.name);
                      setScreenMode('NORMAL');
                    }}
                    className={`p-3.5 rounded-[16px] border flex items-center justify-between cursor-pointer transition active:scale-[0.99] ${
                      isSelected
                        ? 'bg-emerald-50/70 border-[#00875A] text-[#00875A]'
                        : 'bg-white border-slate-100 hover:border-slate-300 text-[#0F172A]'
                    }`}
                  >
                    <div className="flex items-center gap-3.5">
                      <div
                        className={`w-10 h-10 rounded-[12px] flex items-center justify-center ${cat.bgTint}`}
                      >
                        {cat.icon}
                      </div>
                      <span className="text-[15px] font-bold">{cat.name}</span>
                    </div>

                    {isSelected ? (
                      <CheckCircle2 className="w-5 h-5 text-[#00875A]" />
                    ) : (
                      <Circle className="w-5 h-5 text-slate-300" />
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* 3. SUB-MODE: KATILIMCI EKLE MODU */}
        {/* ========================================================================= */}
        {screenMode === 'PARTICIPANT' && (
          <div className="flex-1 overflow-y-auto p-5 space-y-4 animate-fadeIn">
            {/* Search Input */}
            <div className="relative">
              <Search className="w-4 h-4 text-[#8E8E93] absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                placeholder="Arkadaş ara (İsim veya @tag)..."
                value={participantSearchQuery}
                onChange={(e) => setParticipantSearchQuery(e.target.value)}
                className="w-full h-11 pl-10 pr-4 rounded-[14px] bg-[#F1F5F9] border-none text-[13px] font-medium text-[#0F172A] focus:outline-none focus:ring-2 focus:ring-[#00875A]/20"
              />
            </div>

            <div className="space-y-1">
              <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase px-1">
                ARKADAŞ LİSTESİ ({filteredUsers.length})
              </span>

              {filteredUsers.map((user) => {
                const isSelected = selectedFriendIds.includes(user.id);
                return (
                  <div
                    key={user.id}
                    onClick={() => toggleFriendSelection(user.id)}
                    className="p-3 rounded-[14px] hover:bg-slate-50 flex items-center justify-between cursor-pointer transition active:scale-[0.99]"
                  >
                    <div className="flex items-center gap-3">
                      <div
                        className={`w-10 h-10 rounded-[12px] flex items-center justify-center text-[13px] font-bold ${
                          isSelected
                            ? 'bg-emerald-100 text-[#00875A]'
                            : 'bg-[#F1F5F9] text-[#0F172A]'
                        }`}
                      >
                        {user.fullName.slice(0, 2).toUpperCase()}
                      </div>
                      <div>
                        <div
                          className={`text-[14px] ${
                            isSelected
                              ? 'font-bold text-[#00875A]'
                              : 'font-semibold text-[#0F172A]'
                          }`}
                        >
                          {user.fullName}
                        </div>
                        <div className="text-[11px] text-[#64748B]">
                          {user.tag || `@${user.username}`}
                        </div>
                      </div>
                    </div>

                    {isSelected ? (
                      <CheckCircle2 className="w-5 h-5 text-[#00875A]" />
                    ) : (
                      <Circle className="w-5 h-5 text-slate-300" />
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* 4. SUB-MODE: ÖDEYEN VE BÖLÜŞÜM AÇILIR MENÜSÜ (1:1 Android Split Screen) */}
        {/* ========================================================================= */}
        {screenMode === 'SPLIT' && (
          <div className="flex-1 overflow-y-auto p-5 space-y-5 animate-fadeIn flex flex-col justify-between">
            <div className="space-y-5">
              {/* 1. ÖDEYEN KİŞİ KAPSÜLLERİ */}
              <div className="space-y-2.5">
                <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                  MASAYI ÜSTLENEN KİŞİ
                </span>

                <div className="flex items-center gap-2 overflow-x-auto py-1">
                  {/* Sen Butonu */}
                  <button
                    type="button"
                    onClick={() => setSelectedPayerId(currentUser.id)}
                    className={`px-3.5 py-2 rounded-[12px] flex items-center gap-2 text-[13px] font-bold transition active:scale-95 flex-shrink-0 ${
                      selectedPayerId === currentUser.id
                        ? 'bg-emerald-50 border border-[#00875A] text-[#00875A]'
                        : 'bg-[#F1F5F9] text-[#0F172A]'
                    }`}
                  >
                    <div
                      className={`w-5 h-5 rounded-full text-[9px] font-black flex items-center justify-center ${
                        selectedPayerId === currentUser.id
                          ? 'bg-[#00875A] text-white'
                          : 'bg-[#CBD5E1] text-[#0F172A]'
                      }`}
                    >
                      SEN
                    </div>
                    <span>Sen</span>
                  </button>

                  {/* Diğer Katılımcılar */}
                  {selectedFriends.map((f) => {
                    const isPayer = selectedPayerId === f.id;
                    return (
                      <button
                        key={f.id}
                        type="button"
                        onClick={() => setSelectedPayerId(f.id)}
                        className={`px-3.5 py-2 rounded-[12px] flex items-center gap-2 text-[13px] font-bold transition active:scale-95 flex-shrink-0 ${
                          isPayer
                            ? 'bg-emerald-50 border border-[#00875A] text-[#00875A]'
                            : 'bg-[#F1F5F9] text-[#0F172A]'
                        }`}
                      >
                        <div
                          className={`w-5 h-5 rounded-full text-[9px] font-black flex items-center justify-center ${
                            isPayer
                              ? 'bg-[#00875A] text-white'
                              : 'bg-[#CBD5E1] text-[#0F172A]'
                          }`}
                        >
                          {f.fullName.slice(0, 2).toUpperCase()}
                        </div>
                        <span>{f.fullName.split(' ')[0]}</span>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* 2. BÖLÜŞÜM YÖNTEMİ SEGMENTED CONTROL */}
              <div className="space-y-2.5">
                <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                  BÖLÜŞÜM YÖNTEMİ
                </span>

                <div className="grid grid-cols-3 gap-1.5 p-1 bg-[#F1F5F9] rounded-[14px]">
                  <button
                    type="button"
                    onClick={() => setSplitMethod('EQUAL')}
                    className={`py-2 rounded-[10px] text-[13px] font-bold transition ${
                      splitMethod === 'EQUAL'
                        ? 'bg-white text-[#0F172A] shadow-xs'
                        : 'text-[#64748B] hover:text-[#0F172A]'
                    }`}
                  >
                    = Eşit
                  </button>
                  <button
                    type="button"
                    onClick={() => setSplitMethod('EXACT')}
                    className={`py-2 rounded-[10px] text-[13px] font-bold transition ${
                      splitMethod === 'EXACT'
                        ? 'bg-white text-[#0F172A] shadow-xs'
                        : 'text-[#64748B] hover:text-[#0F172A]'
                    }`}
                  >
                    123 Tutar
                  </button>
                  <button
                    type="button"
                    onClick={() => setSplitMethod('PERCENTAGE')}
                    className={`py-2 rounded-[10px] text-[13px] font-bold transition ${
                      splitMethod === 'PERCENTAGE'
                        ? 'bg-white text-[#0F172A] shadow-xs'
                        : 'text-[#64748B] hover:text-[#0F172A]'
                    }`}
                  >
                    % Yüzde
                  </button>
                </div>
              </div>

              {/* 3. KATILIMCI PAY LİSTESİ */}
              <div className="space-y-2.5">
                <div className="flex items-center justify-between">
                  <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase">
                    MASRAFA DAHİL OLANLAR ({allActiveUserIds.length})
                  </span>
                  {splitMethod === 'EQUAL' && numAmount > 0 && (
                    <span className="text-[12px] font-bold text-[#00875A]">
                      Kişi Başı: {perPersonEqual.toFixed(2)} ₺
                    </span>
                  )}
                </div>

                <div className="divide-y divide-slate-100 rounded-[16px] border border-slate-200 overflow-hidden bg-white">
                  {/* Sen Satırı */}
                  <div
                    onClick={() => {
                      if (splitMethod === 'EQUAL') {
                        setIncludeMyselfInSplit(!includeMyselfInSplit);
                      }
                    }}
                    className={`px-4 py-3 flex items-center justify-between transition ${
                      splitMethod === 'EQUAL' ? 'cursor-pointer hover:bg-slate-50' : ''
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <div
                        className={`w-8 h-8 rounded-full flex items-center justify-center text-[11px] font-bold ${
                          includeMyselfInSplit
                            ? 'bg-emerald-100 text-[#00875A]'
                            : 'bg-[#F1F5F9] text-[#94A3B8]'
                        }`}
                      >
                        SEN
                      </div>
                      <span className="text-[14px] font-bold text-[#0F172A]">
                        Sen ({currentUser.fullName.split(' ')[0]})
                      </span>
                    </div>

                    <div className="flex items-center gap-3">
                      {splitMethod === 'EQUAL' && (
                        <>
                          {includeMyselfInSplit && numAmount > 0 && (
                            <span className="text-[14px] font-extrabold text-[#00875A] font-tabular">
                              {perPersonEqual.toFixed(2)} ₺
                            </span>
                          )}
                          {includeMyselfInSplit ? (
                            <CheckCircle2 className="w-5 h-5 text-[#00875A]" />
                          ) : (
                            <Circle className="w-5 h-5 text-slate-300" />
                          )}
                        </>
                      )}

                      {splitMethod === 'EXACT' && (
                        <div className="flex items-center gap-1.5">
                          <input
                            type="text"
                            inputMode="decimal"
                            placeholder={perPersonEqual.toFixed(2)}
                            value={customExacts[currentUser.id] || ''}
                            onChange={(e) =>
                              setCustomExacts({
                                ...customExacts,
                                [currentUser.id]: e.target.value
                              })
                            }
                            className="w-24 h-9 text-right px-2.5 rounded-[10px] bg-[#F1F5F9] font-bold text-[13px] text-[#0F172A] focus:outline-none focus:ring-2 focus:ring-[#00875A]/20"
                          />
                          <span className="text-[13px] font-bold text-[#64748B]">₺</span>
                        </div>
                      )}

                      {splitMethod === 'PERCENTAGE' && (
                        <div className="flex items-center gap-1.5">
                          <input
                            type="text"
                            inputMode="decimal"
                            placeholder={(100 / (selectedFriends.length + 1)).toFixed(0)}
                            value={customPercentages[currentUser.id] || ''}
                            onChange={(e) =>
                              setCustomPercentages({
                                ...customPercentages,
                                [currentUser.id]: e.target.value
                              })
                            }
                            className="w-16 h-9 text-right px-2.5 rounded-[10px] bg-[#F1F5F9] font-bold text-[13px] text-[#0F172A] focus:outline-none focus:ring-2 focus:ring-[#00875A]/20"
                          />
                          <span className="text-[13px] font-bold text-[#64748B]">%</span>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Arkadaş Satırları */}
                  {selectedFriends.map((f) => {
                    const isIncluded = !excludedFriendIds.includes(f.id);
                    return (
                      <div
                        key={f.id}
                        onClick={() => {
                          if (splitMethod === 'EQUAL') {
                            if (isIncluded) {
                              setExcludedFriendIds([...excludedFriendIds, f.id]);
                            } else {
                              setExcludedFriendIds(excludedFriendIds.filter((id) => id !== f.id));
                            }
                          }
                        }}
                        className={`px-4 py-3 flex items-center justify-between transition ${
                          splitMethod === 'EQUAL' ? 'cursor-pointer hover:bg-slate-50' : ''
                        }`}
                      >
                        <div className="flex items-center gap-3">
                          <div
                            className={`w-8 h-8 rounded-full flex items-center justify-center text-[11px] font-bold ${
                              isIncluded
                                ? 'bg-emerald-100 text-[#00875A]'
                                : 'bg-[#F1F5F9] text-[#94A3B8]'
                            }`}
                          >
                            {f.fullName.slice(0, 2).toUpperCase()}
                          </div>
                          <div>
                            <span className="text-[14px] font-bold text-[#0F172A] block leading-tight">
                              {f.fullName}
                            </span>
                            <span className="text-[11px] text-[#64748B] block leading-tight">
                              {f.tag || `@${f.username}`}
                            </span>
                          </div>
                        </div>

                        <div className="flex items-center gap-3">
                          {splitMethod === 'EQUAL' && (
                            <>
                              {isIncluded && numAmount > 0 && (
                                <span className="text-[14px] font-extrabold text-[#00875A] font-tabular">
                                  {perPersonEqual.toFixed(2)} ₺
                                </span>
                              )}
                              {isIncluded ? (
                                <CheckCircle2 className="w-5 h-5 text-[#00875A]" />
                              ) : (
                                <Circle className="w-5 h-5 text-slate-300" />
                              )}
                            </>
                          )}

                          {splitMethod === 'EXACT' && (
                            <div className="flex items-center gap-1.5">
                              <input
                                type="text"
                                inputMode="decimal"
                                placeholder={perPersonEqual.toFixed(2)}
                                value={customExacts[f.id] || ''}
                                onChange={(e) =>
                                  setCustomExacts({
                                    ...customExacts,
                                    [f.id]: e.target.value
                                  })
                                }
                                className="w-24 h-9 text-right px-2.5 rounded-[10px] bg-[#F1F5F9] font-bold text-[13px] text-[#0F172A] focus:outline-none focus:ring-2 focus:ring-[#00875A]/20"
                              />
                              <span className="text-[13px] font-bold text-[#64748B]">₺</span>
                            </div>
                          )}

                          {splitMethod === 'PERCENTAGE' && (
                            <div className="flex items-center gap-1.5">
                              <input
                                type="text"
                                inputMode="decimal"
                                placeholder={(100 / (selectedFriends.length + 1)).toFixed(0)}
                                value={customPercentages[f.id] || ''}
                                onChange={(e) =>
                                  setCustomPercentages({
                                    ...customPercentages,
                                    [f.id]: e.target.value
                                  })
                                }
                                className="w-16 h-9 text-right px-2.5 rounded-[10px] bg-[#F1F5F9] font-bold text-[13px] text-[#0F172A] focus:outline-none focus:ring-2 focus:ring-[#00875A]/20"
                              />
                              <span className="text-[13px] font-bold text-[#64748B]">%</span>
                            </div>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>

            {/* Modal Onayla Butonu */}
            <div className="pt-4">
              <button
                type="button"
                onClick={() => setScreenMode('NORMAL')}
                className="w-full h-12 rounded-[14px] bg-[#00875A] hover:bg-[#00744d] active:scale-[0.98] text-white font-bold text-[14px] flex items-center justify-center gap-2 transition shadow-sm"
              >
                <Check className="w-4 h-4 stroke-[2.5]" />
                <span>Bölüşümü Onayla</span>
              </button>
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* 5. NORMAL HARCAMA FORMU (1:1 Android AddExpenseScreen.kt Normal Mode) */}
        {/* ========================================================================= */}
        {screenMode === 'NORMAL' && (
          <div className="flex-1 overflow-y-auto flex flex-col justify-between">
            <div className="space-y-0 divide-y divide-slate-100">
              {/* A. Katılımcı Seçim Barı ("Seninle ve:" + Kapsül Çipler + Buton) */}
              <div className="px-5 py-3 flex items-center justify-between gap-2 bg-white">
                <span className="text-[14px] font-bold text-[#0F172A] flex-shrink-0">
                  Seninle ve:
                </span>

                <div className="flex-1 flex items-center gap-1.5 overflow-x-auto py-1">
                  {selectedFriends.map((f) => (
                    <div
                      key={f.id}
                      className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-emerald-50 border border-[#00875A] text-[#00875A] text-[12px] font-bold flex-shrink-0"
                    >
                      <div className="w-5 h-5 rounded-full bg-[#00875A] text-white text-[9px] font-black flex items-center justify-center">
                        {f.fullName.slice(0, 2).toUpperCase()}
                      </div>
                      <span>{f.fullName.split(' ')[0]}</span>
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          toggleFriendSelection(f.id);
                        }}
                        className="w-4 h-4 rounded-full hover:bg-emerald-200 flex items-center justify-center ml-0.5"
                      >
                        <X className="w-3 h-3 stroke-[2.5]" />
                      </button>
                    </div>
                  ))}

                  <button
                    type="button"
                    onClick={() => setScreenMode('PARTICIPANT')}
                    className="text-[13px] text-[#94A3B8] hover:text-[#00875A] font-medium px-2 py-1 flex-shrink-0"
                  >
                    + Kişi Ekle...
                  </button>
                </div>

                <button
                  type="button"
                  onClick={() => setScreenMode('PARTICIPANT')}
                  className="w-8 h-8 rounded-[10px] bg-[#F1F5F9] flex items-center justify-center text-[#0F172A] hover:bg-slate-200 active:scale-95 transition flex-shrink-0"
                >
                  <ChevronDown className="w-4 h-4" />
                </button>
              </div>

              {/* B. Harcama Tutarı (Büyük Kahraman Tutar + Hızlı Artırma Çipleri) */}
              <div className="px-5 py-4 space-y-3 bg-white">
                <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                  HARCAMA TUTARI
                </span>

                <div className="flex items-center">
                  <input
                    type="text"
                    inputMode="decimal"
                    placeholder="0,00"
                    value={amountText}
                    onChange={(e) => {
                      const val = e.target.value.replace(/[^0-9.,]/g, '');
                      setAmountText(val);
                    }}
                    className="text-[40px] font-extrabold text-[#00875A] bg-transparent border-none focus:outline-none w-48 font-tabular placeholder:text-slate-200 tracking-tight"
                  />
                  <span className="text-[32px] font-bold text-[#00875A] ml-1">₺</span>
                </div>

                {/* Hızlı Artırma Çipleri */}
                <div className="flex items-center gap-2 pt-1">
                  {[50, 100, 250, 500].map((val) => (
                    <button
                      key={val}
                      type="button"
                      onClick={() => handleQuickAdd(val)}
                      className="px-3.5 py-1.5 rounded-[10px] bg-[#F1F5F9] hover:bg-slate-200 text-[#475569] text-[12px] font-bold active:scale-95 transition"
                    >
                      +{val} ₺
                    </button>
                  ))}
                </div>
              </div>

              {/* C. Açıklama & Kategori Seçimi (1:1 Android) */}
              <div className="px-5 py-4 space-y-2 bg-white">
                <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                  AÇIKLAMA & KATEGORİ
                </span>

                <div className="flex items-center gap-3">
                  {/* Kategori İkon Butonu */}
                  <button
                    type="button"
                    onClick={() => setScreenMode('CATEGORY')}
                    className={`w-12 h-12 rounded-[14px] ${activeCategoryInfo.bgTint} border border-emerald-600/20 flex items-center justify-center shadow-2xs hover:scale-105 active:scale-95 transition flex-shrink-0`}
                    title="Kategoriyi Değiştir"
                  >
                    {activeCategoryInfo.icon}
                  </button>

                  {/* Açıklama Inputu */}
                  <input
                    type="text"
                    placeholder="Ne için harcandı? (örn: Akşam Yemeği, Kahveler...)"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    className="flex-1 h-12 px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] font-semibold text-[#0F172A] placeholder:text-slate-400 focus:outline-none focus:border-[#00875A] transition"
                  />
                </div>
              </div>

              {/* D. ÖDEYEN VE BÖLÜŞÜM ŞEKLİ (1:1 Android Mobildeki Gibi Tek Satır Kapsül / Açılır Menü) */}
              <div className="px-5 py-4 space-y-2 bg-white">
                <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                  ÖDEYEN VE BÖLÜŞÜM ŞEKLİ
                </span>

                <div
                  onClick={() => setScreenMode('SPLIT')}
                  className="p-3.5 rounded-[16px] bg-[#F8FAFC] hover:bg-slate-100 border border-slate-200/80 flex items-center justify-between cursor-pointer transition active:scale-[0.99]"
                >
                  <div className="min-w-0 pr-2">
                    <div className="text-[14px] font-bold text-[#0F172A] truncate">
                      Ödeyen: <span className="text-[#00875A]">{payerName}</span> • {splitModeLabel} bölüşülecek
                    </div>
                    {numAmount > 0 ? (
                      <div
                        className={`text-[12px] font-semibold mt-0.5 ${
                          selectedPayerId === currentUser.id
                            ? 'text-[#00875A]'
                            : 'text-[#DC2626]'
                        }`}
                      >
                        {selectedPayerId === currentUser.id
                          ? `Sen ödedin, +${(
                              numAmount - calculateUserAmount(currentUser.id)
                            ).toFixed(2)} ₺ alacağın var`
                          : `${payerShortName} ödedi${
                              includeMyselfInSplit
                                ? `, -${calculateUserAmount(currentUser.id).toFixed(2)} ₺ borcun düştü`
                                : ''
                            }`}
                      </div>
                    ) : (
                      <div className="text-[12px] text-[#64748B] mt-0.5">
                        Ödeyen ve payları düzenlemek için dokunun
                      </div>
                    )}
                  </div>

                  <div className="w-8 h-8 rounded-[10px] bg-white border border-slate-200 flex items-center justify-center text-[#0F172A] flex-shrink-0">
                    <ChevronDown className="w-4 h-4" />
                  </div>
                </div>
              </div>
            </div>

            {/* Bottom Fixed Action Button (1:1 Android) */}
            <div className="p-4 bg-white border-t border-slate-100 pb-[max(env(safe-area-inset-bottom),16px)]">
              <button
                type="button"
                onClick={() => handleSubmit()}
                disabled={!isValid}
                className={`w-full h-12 rounded-[14px] font-bold text-[15px] flex items-center justify-center gap-2 transition active:scale-[0.98] shadow-sm ${
                  isValid
                    ? 'bg-[#00875A] hover:bg-[#00744d] text-white shadow-emerald-900/20'
                    : 'bg-slate-100 text-slate-400 cursor-not-allowed'
                }`}
              >
                <Check className="w-4 h-4 stroke-[2.5]" />
                <span>
                  {numAmount > 0
                    ? `Harcama Ekle (${numAmount.toFixed(2)} ₺)`
                    : 'Harcama Ekle'}
                </span>
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
