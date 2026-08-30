'use client';

import React, { useState, useEffect, useMemo } from 'react';
import {
  ArrowLeft,
  Copy,
  Check,
  CheckCircle2,
  Building2,
  ExternalLink,
  ChevronRight,
  Search,
  CheckCircle
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
  {
    id: 'garanti',
    name: 'Garanti BBVA',
    url: 'https://online.garantibbva.com.tr',
    scheme: 'garantibbva://'
  },
  {
    id: 'isbank',
    name: 'Türkiye İş Bankası',
    url: 'https://www.isbank.com.tr',
    scheme: 'iscep://'
  },
  {
    id: 'akbank',
    name: 'Akbank',
    url: 'https://www.akbank.com',
    scheme: 'akbankdirekt://'
  },
  {
    id: 'yapikredi',
    name: 'Yapı Kredi',
    url: 'https://www.yapikredi.com.tr',
    scheme: 'yapikredi://'
  },
  {
    id: 'ziraat',
    name: 'Ziraat Bankası',
    url: 'https://bireysel.ziraatbank.com.tr',
    scheme: 'ziraatmobil://'
  },
  {
    id: 'qnb',
    name: 'QNB Finansbank',
    url: 'https://www.qnb.com.tr',
    scheme: 'qnbfinansbank://'
  },
  {
    id: 'papara',
    name: 'Papara',
    url: 'https://www.papara.com',
    scheme: 'papara://'
  },
  {
    id: 'enpara',
    name: 'Enpara.com',
    url: 'https://www.enpara.com',
    scheme: 'enpara://'
  }
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
  const otherUsers = useMemo(() => users.filter((u) => u.id !== currentUser.id), [users, currentUser.id]);
  const [selectedUserId, setSelectedUserId] = useState<string>(initialTargetUser?.id || otherUsers[0]?.id || '');
  const [amountText, setAmountText] = useState<string>(initialAmount ? initialAmount.toFixed(2) : '320.00');
  const [selectedBankId, setSelectedBankId] = useState<string>('garanti');
  const [copiedName, setCopiedName] = useState<boolean>(false);
  const [copiedIban, setCopiedIban] = useState<boolean>(false);
  const [bankOpeningStatus, setBankOpeningStatus] = useState<string | null>(null);

  // Sub-screen mode: 'MAIN' or 'SELECT_USER'
  const [screenMode, setScreenMode] = useState<'MAIN' | 'SELECT_USER'>('MAIN');
  const [userSearchQuery, setUserSearchQuery] = useState('');

  useEffect(() => {
    if (initialTargetUser) {
      setSelectedUserId(initialTargetUser.id);
    }
    if (initialAmount !== undefined && initialAmount > 0) {
      setAmountText(initialAmount.toFixed(2));
    }
    setScreenMode('MAIN');
  }, [initialTargetUser, initialAmount, isOpen]);

  if (!isOpen) return null;

  const selectedUser = users.find((u) => u.id === selectedUserId) || otherUsers[0];
  const numAmount = parseFloat(amountText.replace(',', '.')) || 0;
  const iban = selectedUser?.iban || 'TR33 0006 1005 1978 4567 1000 01';

  const filteredUsers = otherUsers.filter(
    (u) =>
      u.fullName.toLowerCase().includes(userSearchQuery.toLowerCase()) ||
      (u.tag && u.tag.toLowerCase().includes(userSearchQuery.toLowerCase())) ||
      (u.phone && u.phone.includes(userSearchQuery))
  );

  const handleCopyName = () => {
    navigator.clipboard.writeText(selectedUser.fullName);
    setCopiedName(true);
    setTimeout(() => setCopiedName(false), 2000);
  };

  const handleCopyIban = () => {
    navigator.clipboard.writeText(iban.replace(/\s+/g, ''));
    setCopiedIban(true);
    setTimeout(() => setCopiedIban(false), 2000);
  };

  const handleQuickAdd = (val: number) => {
    const current = parseFloat(amountText.replace(',', '.')) || 0;
    const next = current + val;
    setAmountText(next % 1 === 0 ? next.toString() : next.toFixed(2));
  };

  const handleOpenBankApp = (bank: typeof SUPPORTED_BANKS[0]) => {
    setSelectedBankId(bank.id);

    // Automatically copy IBAN to clipboard
    navigator.clipboard.writeText(iban.replace(/\s+/g, ''));
    setCopiedIban(true);
    setBankOpeningStatus(`IBAN kopyalandı! ${bank.name} açılıyor...`);

    // Redirect to banking app / portal
    window.open(bank.url, '_blank', 'noopener,noreferrer');

    setTimeout(() => {
      setBankOpeningStatus(null);
    }, 4000);
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
        {/* ========================================================================= */}
        {/* TOP BAR */}
        {/* ========================================================================= */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3 bg-white border-b border-slate-100 flex items-center justify-between flex-shrink-0">
          <button
            onClick={() => {
              if (screenMode === 'SELECT_USER') {
                setScreenMode('MAIN');
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
            {screenMode === 'SELECT_USER' ? 'Ödeme Yapılacak Kişiyi Seç' : 'Fitleş & FAST Ödeme'}
          </h3>

          <div className="w-10" />
        </div>

        {/* ========================================================================= */}
        {/* SUB-SCREEN: SELECT USER PAGE (1:1 with AddExpenseModal) */}
        {/* ========================================================================= */}
        {screenMode === 'SELECT_USER' && (
          <div className="flex-1 overflow-y-auto p-5 space-y-4 text-left">
            {/* Search Input */}
            <div className="relative">
              <Search className="w-4 h-4 text-[#94A3B8] absolute left-3.5 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                autoFocus
                placeholder="Arkadaş ara (Ad, @tag)..."
                value={userSearchQuery}
                onChange={(e) => setUserSearchQuery(e.target.value)}
                className="w-full h-11 pl-10 pr-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[13px] font-medium text-[#0F172A] focus:outline-none focus:border-[#00875A]"
              />
            </div>

            {/* Friends List Stream */}
            <div className="bg-white rounded-[18px] border border-slate-200/80 divide-y divide-slate-100 overflow-hidden">
              {filteredUsers.map((user) => {
                const isSelected = user.id === selectedUserId;
                return (
                  <div
                    key={user.id}
                    onClick={() => {
                      setSelectedUserId(user.id);
                      setScreenMode('MAIN');
                    }}
                    className="p-3.5 hover:bg-slate-50 flex items-center justify-between cursor-pointer transition active:bg-slate-100"
                  >
                    <div className="flex items-center gap-3">
                      <div
                        className={`w-10 h-10 rounded-full flex items-center justify-center text-[13px] font-bold ${
                          isSelected
                            ? 'bg-emerald-100 text-[#00875A] border border-[#00875A]'
                            : 'bg-[#F1F5F9] text-[#0F172A]'
                        }`}
                      >
                        {user.fullName.slice(0, 2).toUpperCase()}
                      </div>
                      <div>
                        <div
                          className={`text-[14px] ${
                            isSelected ? 'font-bold text-[#00875A]' : 'font-semibold text-[#0F172A]'
                          }`}
                        >
                          {user.fullName}
                        </div>
                        <div className="text-[11px] text-[#64748B]">
                          {user.tag || `@${user.username}`}
                        </div>
                      </div>
                    </div>

                    {isSelected && <CheckCircle2 className="w-5 h-5 text-[#00875A]" />}
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* MAIN SETTLE UP FORM */}
        {/* ========================================================================= */}
        {screenMode === 'MAIN' && (
          <>
            <div className="flex-1 overflow-y-auto divide-y divide-slate-100 text-left">
              {/* 1. Ödeme Yapılacak Kişi (Custom Select Button) */}
              <div className="px-5 py-4 space-y-2 bg-white">
                <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                  ÖDEME YAPILACAK KİŞİ
                </span>

                <div
                  onClick={() => setScreenMode('SELECT_USER')}
                  className="p-3 rounded-[14px] bg-[#F8FAFC] border border-slate-200 hover:border-slate-300 flex items-center justify-between cursor-pointer transition active:scale-[0.99]"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-emerald-50 text-[#00875A] border border-emerald-200 flex items-center justify-center font-extrabold text-[13px]">
                      {selectedUser.fullName.slice(0, 2).toUpperCase()}
                    </div>
                    <div>
                      <div className="text-[14px] font-bold text-[#0F172A]">{selectedUser.fullName}</div>
                      <div className="text-[11px] font-mono text-[#64748B]">
                        {selectedUser.tag || `@${selectedUser.username}`}
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-1.5 text-[12px] font-bold text-[#00875A]">
                    <span>Değiştir</span>
                    <ChevronRight className="w-4 h-4 text-[#94A3B8]" />
                  </div>
                </div>
              </div>

              {/* 2. Ödenecek Tutar (Hero Amount) */}
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

              {/* 3. FAST & IBAN Bilgileri (Alıcı Adı Kopyalanabilir) */}
              <div className="px-5 py-4 space-y-3 bg-white">
                <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                  FAST & HAVALE BİLGİLERİ
                </span>

                <div className="divide-y divide-slate-100 text-[13px]">
                  {/* Alıcı Adı (Kopyalanabilir) */}
                  <div className="py-2.5 flex items-center justify-between">
                    <div>
                      <span className="text-[10px] font-bold text-[#64748B] uppercase block">ALICI ADI</span>
                      <span className="text-[13px] font-bold text-[#0F172A] block">{selectedUser.fullName}</span>
                    </div>
                    <button
                      type="button"
                      onClick={handleCopyName}
                      className={`px-3 py-1.5 rounded-[8px] text-[11px] font-bold flex items-center gap-1 transition ${
                        copiedName
                          ? 'bg-emerald-100 text-[#00875A]'
                          : 'bg-[#F1F5F9] text-[#0F172A] hover:bg-slate-200'
                      }`}
                    >
                      {copiedName ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                      <span>{copiedName ? 'Kopyalandı' : 'Kopyala'}</span>
                    </button>
                  </div>

                  {/* IBAN (Kopyalanabilir) */}
                  <div className="py-2.5 flex items-center justify-between">
                    <div>
                      <span className="text-[10px] font-bold text-[#64748B] uppercase block">FAST IBAN</span>
                      <span className="font-mono font-bold text-[#0F172A] tracking-wider block">
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
                </div>
              </div>

              {/* 4. Banka Uygulamasına Git */}
              <div className="px-5 py-4 space-y-2 bg-white">
                <div className="flex items-center justify-between">
                  <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase">
                    BANKA UYGULAMASINA GİT
                  </span>
                  <span className="text-[11px] text-[#00875A] font-semibold flex items-center gap-1">
                    <ExternalLink className="w-3 h-3" />
                    <span>Dokununca IBAN kopyalanır</span>
                  </span>
                </div>

                {bankOpeningStatus && (
                  <div className="p-2.5 rounded-[10px] bg-emerald-50 text-[#00875A] text-[12px] font-bold text-center animate-fadeIn">
                    {bankOpeningStatus}
                  </div>
                )}

                <div className="grid grid-cols-4 gap-2">
                  {SUPPORTED_BANKS.map((b) => (
                    <button
                      key={b.id}
                      type="button"
                      onClick={() => handleOpenBankApp(b)}
                      className={`p-2.5 rounded-[12px] border text-center transition flex flex-col items-center gap-1 active:scale-95 ${
                        selectedBankId === b.id
                          ? 'border-[#00875A] bg-emerald-50 text-[#00875A] font-bold'
                          : 'border-slate-200 bg-[#F8FAFC] text-[#0F172A] hover:border-slate-300'
                      }`}
                      title={`${b.name} uygulamasını aç`}
                    >
                      <Building2 className="w-5 h-5 opacity-80" />
                      <span className="text-[10px] font-semibold truncate w-full">{b.name}</span>
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Bottom Fixed Action Button */}
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
          </>
        )}
      </div>
    </div>
  );
};
