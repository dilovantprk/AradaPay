'use client';

import React from 'react';
import { Sparkles, ArrowRight, CheckCircle2, Repeat } from 'lucide-react';
import { CrossSettlementOffer } from '../types';

interface SmartSettlementBannerProps {
  offers: CrossSettlementOffer[];
  currentUserId: string;
  onOpenOffer: (offer: CrossSettlementOffer) => void;
}

export const SmartSettlementBanner: React.FC<SmartSettlementBannerProps> = ({
  offers,
  currentUserId,
  onOpenOffer
}) => {
  const pendingOffer = offers.find((o) => o.status === 'PENDING');
  if (!pendingOffer) return null;

  const isApprovedByMe = pendingOffer.approvals[currentUserId] === true;
  const approvedCount = Object.values(pendingOffer.approvals).filter(Boolean).length;
  const totalCount = Object.keys(pendingOffer.approvals).length;

  return (
    <section className="px-4 sm:px-0">
      <div className="p-5 rounded-[22px] bg-gradient-to-br from-emerald-50 via-[#F0FDF4] to-emerald-100/50 border border-[#C8E6C9] shadow-apple-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div className="flex items-start gap-3.5">
          <div className="w-10 h-10 rounded-[14px] bg-[#00875A] text-white flex items-center justify-center shadow-sm shadow-emerald-800/20 flex-shrink-0 mt-0.5">
            <Repeat className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-[11px] font-black text-[#00875A] uppercase tracking-wider">
                DFS AKILLI MAHSUPLAŞMA
              </span>
              <span className="text-[10px] font-bold bg-white text-[#00875A] border border-emerald-300 px-2 py-0.5 rounded-full shadow-2xs">
                {approvedCount}/{totalCount} Onay
              </span>
            </div>
            <p className="text-[15px] font-bold text-[#1C1C1E] mt-1">
              {pendingOffer.cycleAmount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺ borç döngüsü bulundu!
            </p>
            <p className="text-[12px] text-[#86868B] mt-0.5">
              {pendingOffer.participants.map((p) => p.name.split(' ')[0]).join(' ➔ ')} (Sıfır banka transferi ile kapanabilir)
            </p>
          </div>
        </div>

        <button
          onClick={() => onOpenOffer(pendingOffer)}
          className="w-full sm:w-auto px-5 py-2.5 rounded-[14px] bg-[#00875A] text-white text-[13px] font-bold flex items-center justify-center gap-1.5 hover:bg-[#00744d] active:scale-[0.97] transition shadow-sm shadow-emerald-800/20 flex-shrink-0"
        >
          {isApprovedByMe ? (
            <>
              <CheckCircle2 className="w-4 h-4" />
              <span>Onaylandı (Detay)</span>
            </>
          ) : (
            <>
              <span>Döngüyü İncele</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </>
          )}
        </button>
      </div>
    </section>
  );
};
