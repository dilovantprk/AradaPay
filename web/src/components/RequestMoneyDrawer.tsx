'use client';

import React, { useState } from 'react';
import { ArrowLeft, Send, Search, CheckCircle2, ChevronDown, ChevronUp, Check, X } from 'lucide-react';
import { User, Nudge } from '../types';

interface RequestMoneyDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: User;
  users: User[];
  onSendNudge: (nudge: Nudge) => void;
}

export const RequestMoneyDrawer: React.FC<RequestMoneyDrawerProps> = ({
  isOpen,
  onClose,
  currentUser,
  users,
  onSendNudge
}) => {
  const otherUsers = users.filter((u) => u.id !== currentUser.id);
  const [selectedUser, setSelectedUser] = useState<User | null>(otherUsers[0] || null);
  const [searchQuery, setSearchQuery] = useState('');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [amountText, setAmountText] = useState('150.00');
  const [note, setNote] = useState('');
  const [sentSuccess, setSentSuccess] = useState(false);

  if (!isOpen) return null;

  const filteredUsers = otherUsers.filter(
    (u) =>
      u.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (u.tag && u.tag.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  const numAmount = parseFloat(amountText.replace(',', '.')) || 0;
  const isValid = selectedUser && numAmount > 0;

  const handleQuickAdd = (val: number) => {
    const current = parseFloat(amountText.replace(',', '.')) || 0;
    const next = current + val;
    setAmountText(next % 1 === 0 ? next.toString() : next.toFixed(2));
  };

  const handleSend = () => {
    if (!isValid || !selectedUser) return;

    const nudge: Nudge = {
      id: `nudge_${Date.now()}`,
      fromUserId: currentUser.id,
      toUserId: selectedUser.id,
      message: `${currentUser.fullName} senden ${numAmount.toFixed(2)} ₺ talep etti: "${note || 'AradaPay ödemesi'}"`,
      createdAt: new Date().toISOString(),
      isRead: false
    };

    onSendNudge(nudge);
    setSentSuccess(true);
    setTimeout(() => {
      setSentSuccess(false);
      onClose();
    }, 1200);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* Top Bar (1:1 Android Style) */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3 bg-white border-b border-slate-100 flex items-center justify-between flex-shrink-0">
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center text-[#0F172A] hover:bg-slate-200 active:scale-95 transition"
            title="Geri"
          >
            <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
          </button>

          <h3 className="text-[17px] font-bold text-[#0F172A] tracking-tight">
            Para İste & Hatırlat (Dürt)
          </h3>

          <div className="w-10" />
        </div>

        {/* Content Body */}
        <div className="flex-1 overflow-y-auto divide-y divide-slate-100">
          {/* 1. Kimden İstenecek */}
          <div className="px-5 py-4 space-y-2 bg-white">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              KİMDEN İSTENECEK?
            </span>

            <div className="relative">
              <select
                value={selectedUser?.id || ''}
                onChange={(e) => {
                  const u = otherUsers.find((user) => user.id === e.target.value);
                  setSelectedUser(u || null);
                }}
                className="w-full h-12 px-4 pr-10 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] font-bold text-[#0F172A] focus:outline-none focus:border-[#00875A] appearance-none"
              >
                {otherUsers.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.fullName} ({u.tag || `@${u.username}`})
                  </option>
                ))}
              </select>
              <ChevronDown className="w-4 h-4 text-[#8E8E93] absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none" />
            </div>
          </div>

          {/* 2. Talep Tutarı */}
          <div className="px-5 py-4 space-y-3 bg-white">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              TALEP TUTARI
            </span>

            <div className="flex items-center">
              <input
                type="text"
                inputMode="decimal"
                value={amountText}
                onChange={(e) => {
                  const val = e.target.value.replace(/[^0-9.,]/g, '');
                  setAmountText(val);
                }}
                className="text-[40px] font-extrabold text-[#00875A] bg-transparent border-none focus:outline-none w-48 font-tabular tracking-tight placeholder:text-slate-200"
              />
              <span className="text-[32px] font-bold text-[#00875A] ml-1">₺</span>
            </div>

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

          {/* 3. Açıklama & Not */}
          <div className="px-5 py-4 space-y-2 bg-white">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              HATIRLATMA MESAJI (OPSİYONEL)
            </span>

            <input
              type="text"
              placeholder="örn: Dünkü yemek payı, kahve ücreti..."
              value={note}
              onChange={(e) => setNote(e.target.value)}
              className="w-full h-12 px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] font-semibold text-[#0F172A] placeholder:text-slate-400 focus:outline-none focus:border-[#00875A] transition"
            />
          </div>
        </div>

        {/* Footer Action Button */}
        <div className="p-4 bg-white border-t border-slate-100 pb-[max(env(safe-area-inset-bottom),16px)]">
          <button
            onClick={handleSend}
            disabled={!isValid}
            className={`w-full h-12 rounded-[14px] font-bold text-[15px] flex items-center justify-center gap-2 transition active:scale-[0.98] shadow-sm ${
              isValid
                ? 'bg-[#00875A] hover:bg-[#00744d] text-white shadow-emerald-900/20'
                : 'bg-slate-100 text-slate-400 cursor-not-allowed'
            }`}
          >
            {sentSuccess ? (
              <>
                <Check className="w-5 h-5" />
                <span>İstek Gönderildi ✓</span>
              </>
            ) : (
              <>
                <Send className="w-4 h-4" />
                <span>
                  {numAmount > 0
                    ? `Dürt & Para İste (${numAmount.toFixed(2)} ₺)`
                    : 'Dürt & Para İste'}
                </span>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
