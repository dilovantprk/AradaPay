'use client';

import React from 'react';
import { X, Sparkles, Check, CheckCircle2, ArrowRight, ShieldCheck } from 'lucide-react';
import confetti from 'canvas-confetti';
import { CrossSettlementOffer, User } from '../types';

interface SmartSettlementModalProps {
  isOpen: boolean;
  onClose: () => void;
  offer: CrossSettlementOffer | null;
  currentUser: User;
  onApproveOffer: (offerId: string) => void;
}

export const SmartSettlementModal: React.FC<SmartSettlementModalProps> = ({
  isOpen,
  onClose,
  offer,
  currentUser,
  onApproveOffer
}) => {
  if (!isOpen || !offer) return null;

  const isApprovedByMe = offer.approvals[currentUser.id] === true;
  const approvedCount = Object.values(offer.approvals).filter(Boolean).length;
  const totalCount = Object.keys(offer.approvals).length;
  const isFullyApproved = approvedCount === totalCount;

  const handleApprove = () => {
    onApproveOffer(offer.id);
    try {
      confetti({
        particleCount: 100,
        spread: 70,
        origin: { y: 0.6 }
      });
    } catch {
      // ignore
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-black/[0.08] overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* Header */}
        <div className="px-5 sm:px-6 pt-[max(env(safe-area-inset-top),16px)] pb-4 border-b border-black/[0.06] flex items-center justify-between bg-gradient-to-r from-[#F0FDF4] to-[#ECFDF5] flex-shrink-0">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-xl bg-[#00875A] text-white shadow-sm shadow-emerald-900/20">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-[17px] font-bold text-[#1C1C1E]">
                DFS Akıllı Mahsuplaşma
              </h2>
              <span className="text-[11px] font-bold text-[#00875A]">
                Sıfır Banka Transferi ile Borç İtfa
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-white border border-black/[0.06] flex items-center justify-center text-[#8E8E93] hover:text-[#1C1C1E] active:scale-95 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-5 sm:p-6 overflow-y-auto flex-1 space-y-5">
          {/* Amount Badge */}
          <div className="text-center py-5 px-4 bg-emerald-50 rounded-[22px] border border-emerald-200">
            <span className="text-[11px] font-bold text-emerald-800 uppercase tracking-wider block">
              OTOMATİK SIFIRLANACAK DÖNGÜSEL TUTAR
            </span>
            <p className="text-[38px] font-black text-[#00875A] tracking-tight mt-0.5 font-tabular">
              {offer.cycleAmount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
            </p>
            <p className="text-[12px] text-emerald-900 font-medium mt-1">
              Hiç kimse para transferi yapmadan döngüsel borçlar karşılıklı düşürülür.
            </p>
          </div>

          {/* Directed Cycle Steps Flow */}
          <div>
            <label className="block text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider mb-2">
              TESPİT EDİLEN BORÇ DÖNGÜSÜ (DFS ZİNCİRİ)
            </label>

            <div className="space-y-2">
              {offer.steps.map((step, idx) => {
                return (
                  <div
                    key={idx}
                    className="p-3.5 rounded-[16px] bg-[#F2F2F7] border border-black/[0.04] flex items-center justify-between"
                  >
                    <div className="flex items-center gap-2 min-w-0">
                      <span className="w-6 h-6 rounded-full bg-white border border-black/10 flex items-center justify-center text-[11px] font-bold text-[#1C1C1E]">
                        {idx + 1}
                      </span>
                      <span className="text-[13px] font-bold text-[#1C1C1E] truncate">
                        {step.fromUserName.split(' ')[0]}
                      </span>
                      <ArrowRight className="w-3.5 h-3.5 text-[#8E8E93] flex-shrink-0" />
                      <span className="text-[13px] font-bold text-[#1C1C1E] truncate">
                        {step.toUserName.split(' ')[0]}
                      </span>
                    </div>

                    <div className="text-right flex-shrink-0 ml-2">
                      <span className="text-[13px] font-black text-[#1C1C1E] font-tabular">
                        {step.amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                      </span>
                      <span className="block text-[10px] text-[#8E8E93]">düşülecek</span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Approvals Checklist */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <label className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider">
                KATILIMCI ONAYLARI
              </label>
              <span className="text-[11px] font-bold text-[#00875A]">
                {approvedCount}/{totalCount} Onaylandı
              </span>
            </div>

            <div className="apple-card divide-y divide-black/[0.04]">
              {offer.participants.map((participant) => {
                const isApproved = offer.approvals[participant.id] === true;
                const isMe = participant.id === currentUser.id;

                return (
                  <div
                    key={participant.id}
                    className="p-3 flex items-center justify-between"
                  >
                    <div className="flex items-center gap-2.5">
                      <div className="w-8 h-8 rounded-full bg-emerald-100 text-[#00875A] flex items-center justify-center font-bold text-[12px]">
                        {participant.name.substring(0, 2).toUpperCase()}
                      </div>
                      <div>
                        <div className="text-[13px] font-bold text-[#1C1C1E]">
                          {participant.name} {isMe && '(Sen)'}
                        </div>
                        <div className="text-[10px] text-[#8E8E93] font-mono">
                          {'@' + participant.name.toLowerCase().replace(/\s+/g, '')}
                        </div>
                      </div>
                    </div>

                    {isApproved ? (
                      <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-emerald-100 text-[#00875A] text-[11px] font-bold">
                        <Check className="w-3 h-3 stroke-[3]" />
                        <span>Onayladı</span>
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-amber-100 text-amber-700 text-[11px] font-bold">
                        <span>Bekliyor</span>
                      </span>
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-5 border-t border-black/[0.06] bg-[#F2F2F7]/50 space-y-2 pb-[max(env(safe-area-inset-bottom),16px)] flex-shrink-0">
          {!isApprovedByMe ? (
            <button
              onClick={handleApprove}
              className="w-full py-3.5 rounded-[16px] bg-[#00875A] text-white font-bold text-[15px] hover:bg-[#00744d] active:scale-[0.98] transition flex items-center justify-center gap-2 shadow-sm shadow-emerald-900/20"
            >
              <CheckCircle2 className="w-5 h-5" />
              <span>Mahsuplaşmayı Onayla ({offer.cycleAmount.toFixed(0)} ₺ Düşsün)</span>
            </button>
          ) : (
            <div className="p-3 rounded-[16px] bg-emerald-100 text-[#00875A] text-center font-bold text-[14px] flex items-center justify-center gap-2">
              <Check className="w-5 h-5" />
              <span>Onayınız Kaydedildi (Diğerleri Bekleniyor)</span>
            </div>
          )}

          <p className="text-[11px] text-center text-[#8E8E93] flex items-center justify-center gap-1">
            <ShieldCheck className="w-3.5 h-3.5 text-[#00875A]" />
            <span>Kriptografik Merkle Tree L2 defterine işlenir</span>
          </p>
        </div>
      </div>
    </div>
  );
};
