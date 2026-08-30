'use client';

import React, { useState, useMemo } from 'react';
import {
  ArrowLeft,
  Check,
  X,
  ChevronDown,
  ChevronUp,
  Search,
  CheckCircle2,
  Circle,
  Plus,
  Utensils,
  ShoppingCart,
  Plane,
  Home,
  Film,
  Zap,
  Tag,
  Receipt,
  Percent,
  DollarSign,
  Users
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
  const [selectedFriendIds, setSelectedFriendIds] = useState<string[]>([users[1]?.id || 'u2']);
  const [includeMyselfInSplit, setIncludeMyselfInSplit] = useState(true);
  const [participantSearchQuery, setParticipantSearchQuery] = useState('');

  // Custom percentages / exact amounts
  const [customPercentages, setCustomPercentages] = useState<{ [userId: string]: string }>({});
  const [customExacts, setCustomExacts] = useState<{ [userId: string]: string }>({});

  if (!isOpen) return null;

  const numAmount = parseFloat(amountText.replace(',', '.')) || 0;

  // All participants including "me" if selected
  const allParticipantIds = [
    ...(includeMyselfInSplit ? [currentUser.id] : []),
    ...selectedFriendIds
  ];

  const isValid = description.trim().length > 0 && numAmount > 0 && selectedFriendIds.length > 0;

  // Selected Category Info
  const activeCategoryInfo = CATEGORY_OPTIONS.find((c) => c.key === selectedCategory) || CATEGORY_OPTIONS[0];

  // Payer display name
  const payerUser = users.find((u) => u.id === selectedPayerId);
  const payerName = selectedPayerId === currentUser.id ? 'Sen' : payerUser?.fullName || 'Arkadaş';

  // Per person equal amount
  const perPersonEqual = allParticipantIds.length > 0 ? numAmount / allParticipantIds.length : 0;

  const handleQuickAdd = (val: number) => {
    const current = parseFloat(amountText.replace(',', '.')) || 0;
    const next = current + val;
    setAmountText(next % 1 === 0 ? next.toString() : next.toFixed(2));
  };

  const toggleFriendSelection = (userId: string) => {
    if (selectedFriendIds.includes(userId)) {
      if (selectedFriendIds.length > 1) {
        setSelectedFriendIds(selectedFriendIds.filter((id) => id !== userId));
        if (selectedPayerId === userId) setSelectedPayerId(currentUser.id);
      }
    } else {
      setSelectedFriendIds([...selectedFriendIds, userId]);
    }
  };

  const calculateUserAmount = (userId: string): number => {
    if (splitMethod === 'EQUAL') {
      return parseFloat(perPersonEqual.toFixed(2));
    }
    if (splitMethod === 'PERCENTAGE') {
      const pct = parseFloat(customPercentages[userId] || '0') || 100 / allParticipantIds.length;
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

    const splits = allParticipantIds.map((userId) => ({
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

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* ========================================================================= */}
        {/* 1. TOP BAR (1:1 Android AddExpenseScreen.kt) */}
        {/* ========================================================================= */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3 bg-white border-b border-slate-100 flex items-center justify-between flex-shrink-0">
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
            {screenMode === 'CATEGORY'
              ? 'Kategori Seç'
              : screenMode === 'PARTICIPANT'
              ? 'Kişi Ekle'
              : screenMode === 'SPLIT'
              ? 'Ödeyen ve Bölüşüm'
              : 'Harcama Ekle'}
          </h3>

          {screenMode !== 'NORMAL' ? (
            <button
              onClick={() => setScreenMode('NORMAL')}
              className="w-10 h-10 rounded-[12px] bg-emerald-100 text-[#00875A] flex items-center justify-center active:scale-95 transition"
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
        {/* 4. NORMAL HARCAMA FORMU (1:1 Android AddExpenseScreen.kt Normal Mode) */}
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
                  {selectedFriendIds.map((id) => {
                    const f = users.find((u) => u.id === id);
                    if (!f) return null;
                    return (
                      <div
                        key={id}
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
                            toggleFriendSelection(id);
                          }}
                          className="w-4 h-4 rounded-full hover:bg-emerald-200 flex items-center justify-center ml-0.5"
                        >
                          <X className="w-3 h-3 stroke-[2.5]" />
                        </button>
                      </div>
                    );
                  })}

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
                    placeholder="0.00"
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
                    placeholder="Ne için harcandı? (örn: Akşam Yemeği, Kahveler)"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    className="flex-1 h-12 px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] font-semibold text-[#0F172A] placeholder:text-slate-400 focus:outline-none focus:border-[#00875A] transition"
                  />
                </div>
              </div>

              {/* D. Ödemeyi Yapan Kişi */}
              <div className="px-5 py-4 space-y-2 bg-white">
                <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                  ÖDEMEYİ YAPAN KİŞİ
                </span>

                <div className="relative">
                  <select
                    value={selectedPayerId}
                    onChange={(e) => setSelectedPayerId(e.target.value)}
                    className="w-full h-12 px-4 pr-10 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] font-bold text-[#0F172A] focus:outline-none focus:border-[#00875A] appearance-none"
                  >
                    <option value={currentUser.id}>Sen ({currentUser.fullName}) ödedin</option>
                    {selectedFriendIds.map((id) => {
                      const u = users.find((usr) => usr.id === id);
                      if (!u) return null;
                      return (
                        <option key={u.id} value={u.id}>
                          {u.fullName} ödedi
                        </option>
                      );
                    })}
                  </select>
                  <ChevronDown className="w-4 h-4 text-[#8E8E93] absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none" />
                </div>
              </div>

              {/* E. Bölüşüm Yöntemi Segmented Control */}
              <div className="px-5 py-4 space-y-3 bg-white">
                <div className="flex items-center justify-between">
                  <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase">
                    BÖLÜŞÜM YÖNTEMİ
                  </span>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={includeMyselfInSplit}
                      onChange={(e) => setIncludeMyselfInSplit(e.target.checked)}
                      className="rounded text-[#00875A] focus:ring-0"
                    />
                    <span className="text-[12px] font-semibold text-[#64748B]">Beni de dahil et</span>
                  </label>
                </div>

                <div className="grid grid-cols-3 gap-1.5 p-1 bg-[#F1F5F9] rounded-[14px]">
                  <button
                    type="button"
                    onClick={() => setSplitMethod('EQUAL')}
                    className={`py-2 rounded-[10px] text-[12px] font-bold transition ${
                      splitMethod === 'EQUAL'
                        ? 'bg-white text-[#0F172A] shadow-xs'
                        : 'text-[#64748B] hover:text-[#0F172A]'
                    }`}
                  >
                    Eşit Bölüşüm
                  </button>
                  <button
                    type="button"
                    onClick={() => setSplitMethod('PERCENTAGE')}
                    className={`py-2 rounded-[10px] text-[12px] font-bold transition ${
                      splitMethod === 'PERCENTAGE'
                        ? 'bg-white text-[#0F172A] shadow-xs'
                        : 'text-[#64748B] hover:text-[#0F172A]'
                    }`}
                  >
                    % Yüzdelik
                  </button>
                  <button
                    type="button"
                    onClick={() => setSplitMethod('EXACT')}
                    className={`py-2 rounded-[10px] text-[12px] font-bold transition ${
                      splitMethod === 'EXACT'
                        ? 'bg-white text-[#0F172A] shadow-xs'
                        : 'text-[#64748B] hover:text-[#0F172A]'
                    }`}
                  >
                    $ Özel Tutar
                  </button>
                </div>
              </div>

              {/* F. Masrafı Paylaşanlar Listesi (Kişi Başı Tutar) */}
              <div className="px-5 py-4 space-y-2 bg-[#F8FAFC]">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase">
                    MASRAFI PAYLAŞANLAR ({allParticipantIds.length})
                  </span>
                  {splitMethod === 'EQUAL' && (
                    <span className="text-[11px] font-extrabold text-[#00875A]">
                      Kişi Başı: {perPersonEqual.toFixed(2)} ₺
                    </span>
                  )}
                </div>

                <div className="space-y-2">
                  {allParticipantIds.map((userId) => {
                    const isMe = userId === currentUser.id;
                    const u = isMe ? currentUser : users.find((usr) => usr.id === userId);
                    if (!u) return null;

                    return (
                      <div
                        key={userId}
                        className="p-3 rounded-[14px] bg-white border border-slate-200 flex items-center justify-between shadow-2xs"
                      >
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-full bg-emerald-100 text-[#00875A] text-[11px] font-extrabold flex items-center justify-center">
                            {u.fullName.slice(0, 2).toUpperCase()}
                          </div>
                          <div>
                            <div className="text-[13px] font-bold text-[#0F172A]">
                              {isMe ? 'Sen' : u.fullName}
                            </div>
                            <div className="text-[10px] text-[#64748B]">
                              {u.tag || `@${u.username}`}
                            </div>
                          </div>
                        </div>

                        <div className="text-right">
                          {splitMethod === 'EQUAL' ? (
                            <span className="text-[13px] font-extrabold font-tabular text-[#00875A]">
                              {perPersonEqual.toFixed(2)} ₺
                            </span>
                          ) : splitMethod === 'PERCENTAGE' ? (
                            <div className="flex items-center gap-1">
                              <input
                                type="number"
                                placeholder="%"
                                value={customPercentages[userId] || ''}
                                onChange={(e) =>
                                  setCustomPercentages({
                                    ...customPercentages,
                                    [userId]: e.target.value
                                  })
                                }
                                className="w-16 h-8 text-right px-2 rounded-[8px] bg-slate-100 font-bold text-[12px] focus:outline-none focus:ring-1 focus:ring-[#00875A]"
                              />
                              <span className="text-[12px] font-bold text-[#64748B]">%</span>
                            </div>
                          ) : (
                            <div className="flex items-center gap-1">
                              <input
                                type="number"
                                placeholder="0.00"
                                value={customExacts[userId] || ''}
                                onChange={(e) =>
                                  setCustomExacts({
                                    ...customExacts,
                                    [userId]: e.target.value
                                  })
                                }
                                className="w-20 h-8 text-right px-2 rounded-[8px] bg-slate-100 font-bold text-[12px] focus:outline-none focus:ring-1 focus:ring-[#00875A]"
                              />
                              <span className="text-[12px] font-bold text-[#64748B]">₺</span>
                            </div>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>

            {/* G. Bottom Fixed Action Button (1:1 Android) */}
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
                    ? `Harcamayı Kaydet (${numAmount.toFixed(2)} ₺)`
                    : 'Harcamayı Kaydet'}
                </span>
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
