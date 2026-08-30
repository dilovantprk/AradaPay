'use client';

import React, { useState } from 'react';
import {
  ArrowLeft,
  Sparkles,
  Repeat,
  ShieldCheck,
  CheckCircle2,
  Receipt,
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
  const savedFee = (offer.participants.length * 15).toFixed(2);

  const handleCopyHash = () => {
    navigator.clipboard.writeText(mockHash);
    setCopiedHash(true);
    setTimeout(() => setCopiedHash(false), 2000);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* Top App Bar (1:1 Android Style) */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3 bg-white border-b border-slate-100 flex items-center justify-between flex-shrink-0">
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center text-[#0F172A] hover:bg-slate-200 active:scale-95 transition"
            title="Geri"
          >
            <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
          </button>

          <h3 className="text-[17px] font-bold text-[#0F172A] tracking-tight">
            Akıllı Mahsuplaşma Raporu
          </h3>

          <div className="w-10" />
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
              <div className="text-[34px] font-extrabold text-[#0F172A] font-tabular tracking-tight">
                {offer.cycleAmount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
              </div>
              <p className="text-[13px] text-[#64748B]">
                {offer.participants.length} kişi arasındaki döngüsel borç tamamen sıfırlandı.
              </p>
            </div>

            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-200/60 text-[#00875A] text-[11px] font-bold">
              <Sparkles className="w-3.5 h-3.5" />
              <span>Tasarruf Edilen EFT/FAST Komisyonu: ~{savedFee} ₺</span>
            </div>
          </div>

          {/* Mahsuplaşma Katılımcıları */}
          <div className="space-y-2">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block px-1">
              MAHSUPLAŞMAYA DAHİL KİŞİLER
            </span>

            <div className="space-y-2">
              {offer.participants.map((p) => (
                <div
                  key={p.id}
                  className="p-3.5 rounded-[16px] bg-[#F8FAFC] border border-slate-200 flex items-center justify-between"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-9 h-9 rounded-[12px] bg-white border border-slate-200 text-[#00875A] font-extrabold text-[12px] flex items-center justify-center">
                      {p.name.slice(0, 2).toUpperCase()}
                    </div>
                    <div>
                      <div className="text-[14px] font-bold text-[#0F172A]">
                        {p.name} {p.id === currentUser.id && '(Sen)'}
                      </div>
                      <div className="text-[11px] text-[#64748B]">
                        Net Düşen: -{offer.cycleAmount.toFixed(2)} ₺
                      </div>
                    </div>
                  </div>

                  <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-emerald-100 text-[#00875A] text-[11px] font-bold">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    <span>Onaylandı</span>
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Cryptographic L2 Proof Certificate */}
          <div className="p-4 rounded-[18px] bg-[#F8FAFC] border border-slate-200 space-y-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-4 h-4 text-[#00875A]" />
                <span className="text-[12px] font-bold text-[#0F172A]">
                  Graph DFS & Merkle L2 İspatı
                </span>
              </div>

              <button
                onClick={handleCopyHash}
                className="text-[11px] font-bold text-[#00875A] hover:underline flex items-center gap-1"
              >
                {copiedHash ? <Check className="w-3 h-3" /> : <Copy className="w-3 h-3" />}
                <span>{copiedHash ? 'Kopyalandı' : 'Kopyala'}</span>
              </button>
            </div>

            <p className="text-[11px] text-[#64748B]">
              Bu mahsuplaşma, DFS Directed Cycle Reduction algoritması ile çözülmüş ve AradaPay L2 kriptografik defterine değişmez olarak işlenmiştir.
            </p>

            <div className="p-2.5 rounded-[10px] bg-slate-900 text-[#34C759] font-mono text-[10px] break-all select-all">
              {mockHash}
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 bg-white border-t border-slate-100 flex items-center gap-3 pb-[max(env(safe-area-inset-bottom),16px)]">
          <button
            onClick={() => {
              onClose();
              onOpenReceipt(mockHash);
            }}
            className="flex-1 h-12 rounded-[14px] bg-[#00875A] hover:bg-[#00744d] text-white font-bold text-[14px] flex items-center justify-center gap-2 active:scale-[0.98] transition shadow-sm shadow-emerald-900/20"
          >
            <Receipt className="w-4 h-4" />
            <span>Kriptografik Dekontu Görüntüle</span>
          </button>

          <button
            onClick={onClose}
            className="px-5 h-12 rounded-[14px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] font-bold text-[14px] active:scale-[0.98] transition"
          >
            Kapat
          </button>
        </div>
      </div>
    </div>
  );
};
