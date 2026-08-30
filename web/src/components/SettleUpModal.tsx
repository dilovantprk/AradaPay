'use client';

import React, { useState, useEffect } from 'react';
import {
  ArrowLeft,
  Copy,
  Check,
  QrCode,
  CheckCircle2,
  Building2,
  ExternalLink,
  Sparkles,
  ChevronDown
} from 'lucide-react';
import confetti from 'canvas-confetti';
import { User, Settlement } from '../types';

interface SettleUpModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: User;
  users: User[];
  initialTargetUser?: User;
  initialAmount?: number;
  onConfirmSettlement: (settlement: Settlement) => void;
  onShowReceipt: (settlement: Settlement) => void;
}

const SUPPORTED_BANKS = [
  { id: 'garanti', name: 'Garanti BBVA', code: '0062', color: '#1B5E20' },
  { id: 'isbank', name: 'Türkiye İş Bankası', code: '0064', color: '#0D47A1' },
  { id: 'akbank', name: 'Akbank', code: '0046', color: '#B71C1C' },
  { id: 'yapikredi', name: 'Yapı Kredi', code: '0067', color: '#01579B' },
  { id: 'ziraat', name: 'Ziraat Bankası', code: '0010', color: '#C62828' },
  { id: 'qnb', name: 'QNB Finansbank', code: '0111', color: '#4A148C' },
  { id: 'papara', name: 'Papara', code: '8001', color: '#6A1B9A' },
  { id: 'enpara', name: 'Enpara.com', code: '0111', color: '#6A1B9A' }
];

export const SettleUpModal: React.FC<SettleUpModalProps> = ({
  isOpen,
  onClose,
  currentUser,
  users,
  initialTargetUser,
  initialAmount,
  onConfirmSettlement,
  onShowReceipt
}) => {
  const otherUsers = users.filter((u) => u.id !== currentUser.id);
  const [selectedUserId, setSelectedUserId] = useState<string>(initialTargetUser?.id || otherUsers[0]?.id || '');
  const [amountText, setAmountText] = useState<string>(initialAmount ? initialAmount.toFixed(2) : '320.00');
  const [selectedBankId, setSelectedBankId] = useState<string>('garanti');
  const [copiedIban, setCopiedIban] = useState<boolean>(false);
  const [copiedDesc, setCopiedDesc] = useState<boolean>(false);

  useEffect(() => {
    if (initialTargetUser) {
      setSelectedUserId(initialTargetUser.id);
    }
    if (initialAmount !== undefined && initialAmount > 0) {
      setAmountText(initialAmount.toFixed(2));
    }
  }, [initialTargetUser, initialAmount, isOpen]);

  if (!isOpen) return null;

  const selectedUser = users.find((u) => u.id === selectedUserId) || otherUsers[0];
  const numAmount = parseFloat(amountText.replace(',', '.')) || 0;
  const iban = selectedUser?.iban || 'TR33 0006 1005 1978 4567 1000 01';
  const descriptionCode = `AradaPay ${selectedUser?.fullName || ''} Fitleşme`;

  const handleCopyIban = () => {
    navigator.clipboard.writeText(iban.replace(/\s+/g, ''));
    setCopiedIban(true);
    setTimeout(() => setCopiedIban(false), 2000);
  };

  const handleCopyDesc = () => {
    navigator.clipboard.writeText(descriptionCode);
    setCopiedDesc(true);
    setTimeout(() => setCopiedDesc(false), 2000);
  };

  const handleQuickAdd = (val: number) => {
    const current = parseFloat(amountText.replace(',', '.')) || 0;
    const next = current + val;
    setAmountText(next % 1 === 0 ? next.toString() : next.toFixed(2));
  };

  const handleConfirm = () => {
    if (numAmount <= 0) return;

    const settlement: Settlement = {
      id: `set_${Date.now()}`,
      payerId: currentUser.id,
      receiverId: selectedUser.id,
      amount: numAmount,
      currency: 'TRY',
      createdAt: new Date().toISOString(),
      status: 'APPROVED',
      note: 'FAST / Havale ile ödendi ve fitleşildi'
    };

    try {
      confetti({
        particleCount: 80,
        spread: 70,
        origin: { y: 0.6 },
        colors: ['#00875A', '#34C759', '#30B0C7']
      });
    } catch {
      // ignore
    }

    onConfirmSettlement(settlement);
    onClose();
    onShowReceipt(settlement);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* Top Bar (1:1 Android SettleUpScreen.kt) */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3 bg-white border-b border-slate-100 flex items-center justify-between flex-shrink-0">
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center text-[#0F172A] hover:bg-slate-200 active:scale-95 transition"
            title="Geri"
          >
            <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
          </button>

          <h3 className="text-[17px] font-bold text-[#0F172A] tracking-tight">
            Fitleş & FAST Ödeme
          </h3>

          <div className="w-10" />
        </div>

        {/* Scrollable Form Body */}
        <div className="flex-1 overflow-y-auto divide-y divide-slate-100">
          {/* 1. Kime Ödenecek (Seçici) */}
          <div className="px-5 py-4 space-y-2 bg-white">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              ÖDEME YAPILACAK KİŞİ
            </span>

            <div className="relative">
              <select
                value={selectedUserId}
                onChange={(e) => setSelectedUserId(e.target.value)}
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

          {/* 2. Tutar Girişi (Büyük Kahraman Tutar + Hızlı Çipler) */}
          <div className="px-5 py-4 space-y-3 bg-white">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              ÖDENECEK TUTAR
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

          {/* 3. FAST & IBAN Bilgileri Kartı (1:1 Android) */}
          <div className="px-5 py-4 space-y-3 bg-[#F8FAFC]">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              FAST & HAVALE BİLGİLERİ
            </span>

            <div className="apple-card p-4 space-y-3 bg-white">
              {/* Alıcı Adı */}
              <div className="flex items-center justify-between">
                <span className="text-[12px] text-[#8E8E93]">Alıcı Adı:</span>
                <span className="text-[13px] font-bold text-[#0F172A]">{selectedUser.fullName}</span>
              </div>

              {/* IBAN */}
              <div className="flex items-center justify-between pt-2 border-t border-slate-100">
                <div>
                  <span className="text-[10px] font-bold text-[#8E8E93] uppercase block">FAST IBAN</span>
                  <span className="text-[13px] font-mono font-bold text-[#0F172A] tracking-wider block">
                    {iban}
                  </span>
                </div>
                <button
                  type="button"
                  onClick={handleCopyIban}
                  className={`px-3 py-1.5 rounded-[8px] text-[11px] font-bold flex items-center gap-1 transition ${
                    copiedIban
                      ? 'bg-emerald-100 text-[#00875A]'
                      : 'bg-[#F1F5F9] text-[#0F172A] hover:bg-slate-200'
                  }`}
                >
                  {copiedIban ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                  <span>{copiedIban ? 'Kopyalandı' : 'Kopyala'}</span>
                </button>
              </div>

              {/* Açıklama Kodu */}
              <div className="flex items-center justify-between pt-2 border-t border-slate-100">
                <div>
                  <span className="text-[10px] font-bold text-[#8E8E93] uppercase block">Açıklama</span>
                  <span className="text-[12px] font-semibold text-[#0F172A] block">{descriptionCode}</span>
                </div>
                <button
                  type="button"
                  onClick={handleCopyDesc}
                  className={`px-3 py-1.5 rounded-[8px] text-[11px] font-bold flex items-center gap-1 transition ${
                    copiedDesc
                      ? 'bg-emerald-100 text-[#00875A]'
                      : 'bg-[#F1F5F9] text-[#0F172A] hover:bg-slate-200'
                  }`}
                >
                  {copiedDesc ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                  <span>{copiedDesc ? 'Kopyalandı' : 'Kopyala'}</span>
                </button>
              </div>
            </div>
          </div>

          {/* 4. Banka Seçici */}
          <div className="px-5 py-4 space-y-2 bg-white">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              BANKA UYGULAMASINI SEÇ
            </span>
            <div className="grid grid-cols-4 gap-2">
              {SUPPORTED_BANKS.map((b) => (
                <button
                  key={b.id}
                  type="button"
                  onClick={() => setSelectedBankId(b.id)}
                  className={`p-2.5 rounded-[12px] border text-center transition flex flex-col items-center gap-1 ${
                    selectedBankId === b.id
                      ? 'border-[#00875A] bg-emerald-50 text-[#00875A] font-bold'
                      : 'border-slate-100 bg-[#F8FAFC] text-[#0F172A] hover:border-slate-300'
                  }`}
                >
                  <Building2 className="w-5 h-5 opacity-80" />
                  <span className="text-[10px] font-semibold truncate w-full">{b.name}</span>
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Bottom Fixed Action Button (1:1 Android) */}
        <div className="p-4 bg-white border-t border-slate-100 pb-[max(env(safe-area-inset-bottom),16px)]">
          <button
            type="button"
            onClick={handleConfirm}
            disabled={numAmount <= 0}
            className={`w-full h-12 rounded-[14px] font-bold text-[15px] flex items-center justify-center gap-2 transition active:scale-[0.98] shadow-sm ${
              numAmount > 0
                ? 'bg-[#00875A] hover:bg-[#00744d] text-white shadow-emerald-900/20'
                : 'bg-slate-100 text-slate-400 cursor-not-allowed'
            }`}
          >
            <CheckCircle2 className="w-5 h-5 stroke-[2.2]" />
            <span>Ödemeyi Yaptım, Fitleş ({numAmount.toFixed(2)} ₺)</span>
          </button>
        </div>
      </div>
    </div>
  );
};
