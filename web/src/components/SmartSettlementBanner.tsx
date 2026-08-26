import React from 'react';
import { Sparkles, ArrowRight, CheckCircle2 } from 'lucide-react';
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
    <section className="bg-surfaceWhite px-5 py-3 border-b border-surfaceBorder">
      <div className="max-w-2xl mx-auto">
        <div className="p-4 rounded-[16px] bg-gradient-to-r from-[#F0FDF4] to-[#ECFDF5] border border-[#BBF7D0] flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
          <div className="flex items-start gap-3">
            <div className="p-2 rounded-xl bg-primaryEmerald text-white mt-0.5">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-[13px] font-bold text-primaryEmerald uppercase tracking-wide">
                  ⚡ DFS Akıllı Mahsuplaşma
                </span>
                <span className="text-[11px] font-bold bg-primaryEmeraldContainer text-primaryEmerald px-2 py-0.5 rounded-full">
                  {approvedCount}/{totalCount} Onay
                </span>
              </div>
              <p className="text-[14px] font-bold text-textPrimary mt-0.5">
                {pendingOffer.cycleAmount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺ borç döngüsü bulundu!
              </p>
              <p className="text-[12px] text-textSecondary mt-0.5">
                {pendingOffer.participants.map((p) => p.name.split(' ')[0]).join(' ➔ ')} (Para transferi yapmadan sıfırlanabilir)
              </p>
            </div>
          </div>

          <button
            onClick={() => onOpenOffer(pendingOffer)}
            className="w-full sm:w-auto px-4 py-2.5 rounded-[12px] bg-primaryEmerald text-white text-[13px] font-bold flex items-center justify-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm self-stretch sm:self-center"
          >
            {isApprovedByMe ? (
              <>
                <CheckCircle2 className="w-4 h-4" />
                <span>Onaylandı (Detay)</span>
              </>
            ) : (
              <>
                <span>Döngüyü İncele & Onayla</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </div>
      </div>
    </section>
  );
};
