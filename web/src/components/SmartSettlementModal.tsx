'use client';

import React from 'react';
import { ArrowLeft, Sparkles, Check, CheckCircle2, ArrowRight, ShieldCheck } from 'lucide-react';
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
            Akıllı Masa Dengeleme
          </h3>

          <div className="w-10" />
        </div>

        {/* Content Body (Flat, De-nested Layout) */}
        <div className="flex-1 overflow-y-auto divide-y divide-slate-100 text-left">
          {/* 1. Hero Amount (Flat) */}
          <div className="px-6 py-6 text-center space-y-1 bg-white">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              KENDİLİĞİNDEN KAPANACAK MASA TUTARI
            </span>
            <p className="text-[44px] font-black text-[#00875A] tracking-tight font-tabular">
              {offer.cycleAmount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
            </p>
            <p className="text-[12px] text-[#64748B]">
              Hiç kimse para transferi yapmadan masadaki ortak hesaplar kendiliğinden sıfırlanır.
            </p>
          </div>

          {/* 2. Directed Cycle Steps Flow */}
          <div className="px-6 py-4 space-y-3 bg-white">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              TESPİT EDİLEN DÖNGÜSEL ÖDEŞME PLANI
            </span>

            <div className="space-y-2">
              {offer.steps.map((step, idx) => (
                <div
                  key={idx}
                  className="flex items-center justify-between py-2 border-b border-slate-50 last:border-none"
                >
                  <div className="flex items-center gap-2 min-w-0">
                    <span className="w-6 h-6 rounded-full bg-slate-100 flex items-center justify-center text-[11px] font-bold text-[#0F172A]">
                      {idx + 1}
                    </span>
                    <span className="text-[13px] font-bold text-[#0F172A] truncate">
                      {step.fromUserName}
                    </span>
                    <ArrowRight className="w-3.5 h-3.5 text-[#94A3B8] flex-shrink-0" />
                    <span className="text-[13px] font-bold text-[#0F172A] truncate">
                      {step.toUserName}
                    </span>
                  </div>

                  <span className="text-[13px] font-extrabold text-[#00875A] font-tabular flex-shrink-0 ml-2">
                    -{step.amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* 3. Approvals Checklist */}
          <div className="px-6 py-4 space-y-3 bg-white">
            <div className="flex items-center justify-between">
              <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                KATILIMCI ONAYLARI
              </span>
              <span className="text-[12px] font-extrabold text-[#00875A]">
                {approvedCount} / {totalCount} Onaylandı
              </span>
            </div>

            <div className="divide-y divide-slate-50">
              {offer.participants.map((participant) => {
                const isApproved = offer.approvals[participant.id] === true;
                const isMe = participant.id === currentUser.id;

                return (
                  <div
                    key={participant.id}
                    className="py-2.5 flex items-center justify-between"
                  >
                    <div className="flex items-center gap-2.5">
                      <div className="w-8 h-8 rounded-full bg-emerald-50 text-[#00875A] flex items-center justify-center font-bold text-[12px]">
                        {participant.name.substring(0, 2).toUpperCase()}
                      </div>
                      <div>
                        <div className="text-[13px] font-bold text-[#0F172A]">
                          {participant.name} {isMe && '(Sen)'}
                        </div>
                        <div className="text-[10px] text-[#64748B] font-mono">
                          {'@' + participant.name.toLowerCase().replace(/\s+/g, '')}
                        </div>
                      </div>
                    </div>

                    {isApproved ? (
                      <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-emerald-50 text-[#00875A] text-[11px] font-bold">
                        <Check className="w-3 h-3 stroke-[3]" />
                        <span>Onayladı</span>
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-amber-50 text-amber-700 text-[11px] font-bold">
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
        <div className="p-4 border-t border-slate-100 bg-white space-y-2 pb-[max(env(safe-area-inset-bottom),16px)] flex-shrink-0">
          {!isApprovedByMe ? (
            <button
              onClick={handleApprove}
              className="w-full h-12 rounded-[14px] bg-[#00875A] text-white font-bold text-[15px] hover:bg-[#00744d] active:scale-[0.98] transition flex items-center justify-center gap-2 shadow-sm shadow-emerald-900/20"
            >
              <CheckCircle2 className="w-5 h-5" />
              <span>Ödeşmeyi Onayla ({offer.cycleAmount.toFixed(0)} ₺ Düşsün)</span>
            </button>
          ) : (
            <div className="h-12 rounded-[14px] bg-emerald-50 text-[#00875A] text-center font-bold text-[14px] flex items-center justify-center gap-2">
              <Check className="w-5 h-5 stroke-[2.5]" />
              <span>Onayınız Kaydedildi (Diğerleri Bekleniyor)</span>
            </div>
          )}

          <p className="text-[11px] text-center text-[#64748B] flex items-center justify-center gap-1">
            <ShieldCheck className="w-3.5 h-3.5 text-[#00875A]" />
            <span>Hesap geçmişine güvenle kaydedilir</span>
          </p>
        </div>
      </div>
    </div>
  );
};
