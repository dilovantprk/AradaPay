'use client';

import React, { useState } from 'react';
import {
  X,
  ArrowLeft,
  Sparkles,
  Repeat,
  ShieldCheck,
  CheckCircle2,
  Receipt,
  Download,
  Copy,
  Check
} from 'lucide-react';
import { CrossSettlementOffer, User } from '../types';

interface SmartSettlementReportModalProps {
  isOpen: boolean;
  onClose: () => void;
  offer: CrossSettlementOffer | null;
  currentUser: User;
  onOpenReceipt: (txId: string) => void;
}

export const SmartSettlementReportModal: React.FC<SmartSettlementReportModalProps> = ({
  isOpen,
  onClose,
  offer,
  currentUser,
  onOpenReceipt
}) => {
  const [copiedHash, setCopiedHash] = useState(false);

  if (!isOpen || !offer) return null;

  const mockHash = `0x7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069`;
  const savedFee = (offer.participants.length * 15).toFixed(2); // ~15 TL per FAST transfer

  const handleCopyHash = () => {
    navigator.clipboard.writeText(mockHash);
    setCopiedHash(true);
    setTimeout(() => setCopiedHash(false), 2000);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full max-w-lg rounded-t-[32px] sm:rounded-[28px] shadow-apple-modal border border-black/[0.08] overflow-hidden flex flex-col max-h-[92vh] animate-appleSheet">
        {/* Mobile iOS Drag Handle */}
        <div className="w-12 h-1.5 bg-black/15 rounded-full mx-auto mt-3 sm:hidden" />

        {/* Top App Bar */}
        <div className="px-5 py-3.5 border-b border-black/[0.06] flex items-center justify-between bg-white/80 backdrop-blur-md">
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-black/5 flex items-center justify-center text-[#1C1C1E] hover:bg-black/10 active:scale-95 transition"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>

          <h3 className="text-[17px] font-bold text-[#1C1C1E] tracking-tight">
            Akıllı Mahsuplaşma Raporu
          </h3>

          <div className="w-9" />
        </div>

        {/* Modal Body */}
        <div className="p-5 sm:p-6 overflow-y-auto flex-1 space-y-5 text-left">
          {/* Savings Hero Banner */}
          <div className="p-6 rounded-[24px] bg-gradient-to-br from-emerald-50 via-[#F0FDF4] to-emerald-100/50 border border-emerald-200 text-center space-y-3">
            <div className="w-14 h-14 rounded-2xl bg-[#00875A] text-white flex items-center justify-center mx-auto shadow-md shadow-emerald-900/20">
              <Repeat className="w-7 h-7" />
            </div>

            <div>
              <span className="text-[11px] font-bold text-[#00875A] uppercase tracking-wider block">
                SIFIR TRANSFERLE BORÇ İTFASI
              </span>
              <div className="text-[34px] font-black text-[#1C1C1E] font-tabular tracking-tight">
                {offer.cycleAmount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
              </div>
              <p className="text-[13px] text-[#8E8E93]">
                Kapalı döngü algoritmasıyla tüm borçlar tek hamlede sıfırlandı.
              </p>
            </div>

            {/* Savings stats */}
            <div className="grid grid-cols-2 gap-3 pt-2 text-left">
              <div className="p-3 rounded-[16px] bg-white border border-emerald-200/80">
                <span className="text-[10px] font-bold text-[#8E8E93] uppercase block">
                  ÖNLENEN TRANSFER
                </span>
                <span className="text-[16px] font-black text-[#00875A] block mt-0.5">
                  {offer.participants.length} Adet (0'a indi)
                </span>
              </div>

              <div className="p-3 rounded-[16px] bg-white border border-emerald-200/80">
                <span className="text-[10px] font-bold text-[#8E8E93] uppercase block">
                  TASARRUF EDİLEN FAST
                </span>
                <span className="text-[16px] font-black text-[#00875A] block mt-0.5">
                  ~{savedFee} ₺
                </span>
              </div>
            </div>
          </div>

          {/* Döngü Rota Adımları */}
          <div className="space-y-2.5">
            <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider block px-1">
              DÖNGÜ ŞEMASI VE KATILIMCILAR
            </span>

            <div className="apple-card divide-y divide-black/[0.04] overflow-hidden">
              {offer.steps.map((step, idx) => (
                <div key={idx} className="p-4 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-7 h-7 rounded-full bg-black/5 flex items-center justify-center text-[12px] font-bold text-[#1C1C1E]">
                      {idx + 1}
                    </div>
                    <div>
                      <div className="text-[13px] font-bold text-[#1C1C1E]">
                        {step.fromUserName} ➔ {step.toUserName}
                      </div>
                      <div className="text-[11px] text-[#8E8E93]">
                        {step.amount.toFixed(2)} ₺ karşılıklı borç
                      </div>
                    </div>
                  </div>

                  <span className="px-2.5 py-1 rounded-full bg-emerald-100 text-[#00875A] text-[11px] font-bold">
                    Sıfırlandı ✓
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Kriptografik Merkle Özeti */}
          <div className="apple-card p-4 space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-4 h-4 text-[#00875A]" />
                <span className="text-[12px] font-bold text-[#1C1C1E]">
                  SHA-256 Merkle Ağacı İspatı
                </span>
              </div>
              <button
                onClick={handleCopyHash}
                className="text-[11px] font-bold text-[#00875A] hover:underline flex items-center gap-1"
              >
                {copiedHash ? <Check className="w-3 h-3" /> : <Copy className="w-3 h-3" />}
                <span>{copiedHash ? 'Kopyalandı' : 'Kodu Al'}</span>
              </button>
            </div>

            <div className="p-2.5 rounded-[12px] bg-[#F2F2F7] font-mono text-[11px] text-[#8E8E93] select-all break-all">
              {mockHash}
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-4 bg-white border-t border-black/[0.06]">
          <button
            onClick={() => {
              onClose();
              onOpenReceipt(offer.id);
            }}
            className="w-full h-12 rounded-[16px] bg-[#00875A] hover:bg-[#00744d] text-white font-bold text-[14px] flex items-center justify-center gap-2 active:scale-[0.98] transition shadow-sm"
          >
            <Receipt className="w-4 h-4" />
            <span>Resmi Mahsuplaşma Dekontunu Aç</span>
          </button>
        </div>
      </div>
    </div>
  );
};
