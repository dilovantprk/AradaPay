'use client';

import React, { useState } from 'react';
import {
  X,
  Copy,
  Check,
  QrCode,
  ArrowRight,
  ShieldCheck,
  CheckCircle2,
  Building2,
  ExternalLink,
  ArrowLeft,
  Sparkles
} from 'lucide-react';
import confetti from 'canvas-confetti';
import { User, Settlement } from '../types';

interface SettleUpModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: User;
  users: User[];
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
  onConfirmSettlement,
  onShowReceipt
}) => {
  const otherUsers = users.filter((u) => u.id !== currentUser.id);
  const [selectedUserId, setSelectedUserId] = useState<string>(otherUsers[0]?.id || '');
  const [amount, setAmount] = useState<string>('320.00');
  const [selectedBankId, setSelectedBankId] = useState<string>('garanti');
  const [copiedIban, setCopiedIban] = useState<boolean>(false);
  const [copiedDesc, setCopiedDesc] = useState<boolean>(false);
  const [activeTab, setActiveTab] = useState<'fast' | 'qr'>('fast');

  if (!isOpen) return null;

  const selectedUser = users.find((u) => u.id === selectedUserId) || otherUsers[0];
  const numAmount = parseFloat(amount.replace(',', '.')) || 0;
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

    // Celebration confetti
    try {
      confetti({
        particleCount: 100,
        spread: 70,
        origin: { y: 0.6 }
      });
    } catch {
      // ignore
    }

    onConfirmSettlement(settlement);
    onShowReceipt(settlement);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
      <div className="bg-white w-full max-w-lg rounded-[28px] shadow-2xl border border-slate-200 overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="px-5 py-4 border-b border-slate-200 flex items-center justify-between bg-white">
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-[12px] bg-slate-100 flex items-center justify-center text-slate-800 hover:bg-slate-200 active:scale-95 transition"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>

          <div className="flex items-center gap-2">
            <h3 className="text-[18px] font-black text-textPrimary tracking-tight">Öde & Fitleş</h3>
            <span className="px-2 py-0.5 rounded-full bg-emerald-100 text-primaryEmerald text-[11px] font-bold">
              FAST 7/24
            </span>
          </div>

          <div className="w-10" />
        </div>

        {/* Tab Switcher: FAST Transfer vs QR Okut */}
        <div className="p-3 bg-[#F8FAFC] border-b border-slate-200">
          <div className="grid grid-cols-2 gap-2 p-1 bg-slate-200/70 rounded-[16px]">
            <button
              onClick={() => setActiveTab('fast')}
              className={`py-2 rounded-[12px] text-[13px] font-bold transition flex items-center justify-center gap-1.5 ${
                activeTab === 'fast' ? 'bg-white text-textPrimary shadow-sm' : 'text-slate-600'
              }`}
            >
              <Building2 className="w-4 h-4" />
              <span>FAST & Havale</span>
            </button>
            <button
              onClick={() => setActiveTab('qr')}
              className={`py-2 rounded-[12px] text-[13px] font-bold transition flex items-center justify-center gap-1.5 ${
                activeTab === 'qr' ? 'bg-white text-textPrimary shadow-sm' : 'text-slate-600'
              }`}
            >
              <QrCode className="w-4 h-4" />
              <span>TR-Karekod</span>
            </button>
          </div>
        </div>

        {/* Modal Body */}
        <div className="p-5 overflow-y-auto flex-1 space-y-5">
          {activeTab === 'fast' ? (
            <>
              {/* Alıcı Seçici */}
              <div className="space-y-2">
                <span className="text-[11px] font-black text-slate-500 uppercase tracking-wider block">
                  KİME ÖDEME YAPACAKSIN?
                </span>
                <select
                  value={selectedUserId}
                  onChange={(e) => setSelectedUserId(e.target.value)}
                  className="w-full h-12 px-3.5 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] font-bold text-textPrimary focus:outline-none focus:border-primaryEmerald"
                >
                  {otherUsers.map((u) => (
                    <option key={u.id} value={u.id}>
                      {u.fullName} ({u.tag || `@${u.username}`})
                    </option>
                  ))}
                </select>
              </div>

              {/* Tutar Girişi */}
              <div className="p-4 rounded-[20px] bg-[#F8FAFC] border border-slate-200 text-center space-y-2">
                <span className="text-[11px] font-black text-slate-400 uppercase tracking-wider block">
                  FİTLEŞME TUTARI (₺)
                </span>
                <div className="flex items-center justify-center">
                  <input
                    type="number"
                    step="0.01"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    className="text-[32px] font-black text-center text-textPrimary bg-transparent border-none focus:outline-none w-44 font-tabular"
                  />
                  <span className="text-[24px] font-bold text-slate-400 ml-1">₺</span>
                </div>
              </div>

              {/* Banka Seçici */}
              <div className="space-y-2">
                <span className="text-[11px] font-black text-slate-500 uppercase tracking-wider block">
                  ALICININ BANKASI
                </span>
                <div className="grid grid-cols-4 gap-2">
                  {SUPPORTED_BANKS.map((b) => (
                    <button
                      key={b.id}
                      type="button"
                      onClick={() => setSelectedBankId(b.id)}
                      className={`p-2.5 rounded-[14px] border text-center transition flex flex-col items-center justify-center ${
                        selectedBankId === b.id
                          ? 'bg-emerald-50 border-primaryEmerald text-primaryEmerald font-bold shadow-2xs'
                          : 'bg-[#F8FAFC] border-slate-200 text-slate-700 hover:bg-slate-100'
                      }`}
                    >
                      <Building2 className="w-4 h-4 mb-1" style={{ color: b.color }} />
                      <span className="text-[10px] font-bold leading-tight truncate w-full">
                        {b.name}
                      </span>
                    </button>
                  ))}
                </div>
              </div>

              {/* IBAN Kopyalama Kartı */}
              <div className="p-4 rounded-[20px] bg-slate-900 text-white space-y-3 shadow-md">
                <div className="flex items-center justify-between text-[11px] text-slate-400 font-bold">
                  <span>ALICI IBAN NUMARASI</span>
                  <span className="text-emerald-400 font-mono">BKM / FAST Uyumlu</span>
                </div>

                <div className="flex items-center justify-between bg-slate-800/80 p-3 rounded-[14px] border border-slate-700">
                  <span className="font-mono font-bold text-[13px] text-emerald-300 tracking-wider select-all truncate mr-2">
                    {iban}
                  </span>
                  <button
                    onClick={handleCopyIban}
                    className="px-3 py-1.5 rounded-[10px] bg-primaryEmerald text-white text-[12px] font-bold flex items-center gap-1 hover:bg-[#00744d] active:scale-95 transition flex-shrink-0"
                  >
                    {copiedIban ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                    <span>{copiedIban ? 'Kopyalandı' : 'Kopyala'}</span>
                  </button>
                </div>

                {/* Açıklama Kodu */}
                <div className="flex items-center justify-between bg-slate-800/80 p-3 rounded-[14px] border border-slate-700">
                  <div className="truncate mr-2">
                    <span className="text-[10px] text-slate-400 block font-semibold">HAVALE AÇIKLAMASI</span>
                    <span className="text-[12px] text-slate-200 font-bold truncate block">
                      {descriptionCode}
                    </span>
                  </div>
                  <button
                    onClick={handleCopyDesc}
                    className="px-3 py-1.5 rounded-[10px] bg-slate-700 text-slate-200 text-[12px] font-bold flex items-center gap-1 hover:bg-slate-600 active:scale-95 transition flex-shrink-0"
                  >
                    {copiedDesc ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                    <span>{copiedDesc ? 'Kopyalandı' : 'Kopyala'}</span>
                  </button>
                </div>
              </div>
            </>
          ) : (
            /* TR-Karekod Sekmesi */
            <div className="text-center space-y-4 py-4">
              <div className="p-4 bg-[#F8FAFC] rounded-[24px] border border-slate-200 inline-block shadow-xs">
                <div className="w-56 h-56 bg-slate-900 rounded-2xl p-4 flex flex-col items-center justify-center text-white">
                  <QrCode className="w-36 h-36 text-white stroke-[1.5]" />
                  <span className="text-[10px] font-mono text-emerald-400 mt-2">
                    FAST://{iban.replace(/\s+/g, '')}/{numAmount}TRY
                  </span>
                </div>
              </div>
              <div>
                <h4 className="text-[15px] font-bold text-textPrimary">
                  {selectedUser?.fullName} için Karekod
                </h4>
                <p className="text-[12px] text-slate-500 max-w-xs mx-auto mt-1">
                  Banka uygulamanızın QR/Karekod menüsünden bu kodu okutarak <strong>{numAmount} ₺</strong> tutarını anında transfer edin.
                </p>
              </div>
            </div>
          )}
        </div>

        {/* Footer Actions */}
        <div className="p-4 bg-white border-t border-slate-200 flex items-center gap-3">
          <button
            onClick={handleConfirm}
            className="w-full h-12 rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm"
          >
            <Sparkles className="w-4 h-4" />
            <span>Ödemeyi Bildir & Dekont Al</span>
          </button>
        </div>
      </div>
    </div>
  );
};
