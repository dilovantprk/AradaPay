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
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
      <div className="bg-surfaceWhite w-full max-w-lg rounded-[24px] shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="px-6 py-4 border-b border-surfaceBorder flex items-center justify-between bg-gradient-to-r from-[#F0FDF4] to-[#ECFDF5]">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-xl bg-primaryEmerald text-white">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-[17px] font-bold text-textPrimary">
                DFS Akıllı Mahsuplaşma
              </h2>
              <span className="text-[11px] font-bold text-primaryEmerald">
                Sıfır Banka Transferi ile Borç İtfa
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-white/80 flex items-center justify-center text-textSecondary hover:bg-white active:scale-95 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto flex-1 space-y-5">
          {/* Amount Badge */}
          <div className="text-center py-4 px-3 bg-primaryEmeraldContainer/60 rounded-[20px] border border-[#BBF7D0]">
            <span className="text-[11px] font-bold text-onPrimaryContainer uppercase tracking-wider">
              OTOMATİK SIFIRLANACAK DÖNGÜSEL TUTAR
            </span>
            <p className="text-[36px] font-extrabold text-primaryEmerald tracking-tight mt-0.5">
              {offer.cycleAmount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
            </p>
            <p className="text-[12px] text-onPrimaryContainer font-medium mt-1">
              Hiç kimse para transferi yapmadan döngüsel borçlar karşılıklı düşürülür.
            </p>
          </div>

          {/* Directed Cycle Steps Flow */}
          <div>
            <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-2.5">
              TESPİT EDİLEN BORÇ DÖNGÜSÜ (DFS ZİNCİRİ)
            </label>

            <div className="space-y-2">
              {offer.steps.map((step, idx) => {
                return (
                  <div
                    key={idx}
                    className="p-3.5 rounded-[16px] bg-surfaceContainerLow/60 border border-slate-200 flex items-center justify-between"
                  >
                    <div className="flex items-center gap-2 min-w-0">
                      <span className="w-6 h-6 rounded-full bg-slate-200 flex items-center justify-center text-[11px] font-bold text-textPrimary">
                        {idx + 1}
                      </span>
                      <span className="text-[13px] font-bold text-textPrimary truncate">
                        {step.fromUserName.split(' ')[0]}
                      </span>
                      <ArrowRight className="w-4 h-4 text-primaryEmerald flex-shrink-0" />
                      <span className="text-[13px] font-bold text-textPrimary truncate">
                        {step.toUserName.split(' ')[0]}
                      </span>
                    </div>

                    <div className="text-right flex-shrink-0">
                      <span className="text-[13px] font-mono font-bold text-primaryEmerald">
                        {step.amount.toFixed(2)} ₺
                      </span>
                      <span className="text-[10px] text-textSecondary block">düşülecek</span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Participant Approvals */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <label className="text-[12px] font-bold text-textSecondary uppercase tracking-wider">
                KATILIMCI ONAYLARI
              </label>
              <span className="text-[12px] font-bold text-primaryEmerald">
                {approvedCount} / {totalCount} Onaylandı
              </span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
              {offer.participants.map((p) => {
                const approved = offer.approvals[p.id] === true;
                const isMe = p.id === currentUser.id;

                return (
                  <div
                    key={p.id}
                    className={`p-2.5 rounded-[12px] border flex items-center justify-between transition ${
                      approved
                        ? 'bg-primaryEmeraldContainer/40 border-primaryEmerald text-primaryEmerald'
                        : 'bg-white border-slate-200 text-textSecondary'
                    }`}
                  >
                    <div className="min-w-0">
                      <p className="text-[12px] font-bold text-textPrimary truncate">
                        {isMe ? 'Sen' : p.name.split(' ')[0]}
                      </p>
                      <p className="text-[10px] text-textSecondary truncate">@{p.username}</p>
                    </div>

                    {approved ? (
                      <CheckCircle2 className="w-4 h-4 text-primaryEmerald flex-shrink-0 ml-1" />
                    ) : (
                      <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-slate-100 text-slate-500">
                        Bekliyor
                      </span>
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        {/* Footer CTA */}
        <div className="p-4 border-t border-surfaceBorder bg-surfaceWhite">
          {isApprovedByMe ? (
            <div className="w-full h-[52px] rounded-[16px] bg-primaryEmeraldContainer border border-primaryEmerald text-primaryEmerald font-bold text-[14px] flex items-center justify-center gap-2">
              <Check className="w-5 h-5" />
              <span>
                {isFullyApproved
                  ? 'Tüm Katılımcılar Onayladı (Döngü Sıfırlandı)'
                  : 'Onayınız Kaydedildi (Diğerleri Bekleniyor)'}
              </span>
            </div>
          ) : (
            <button
              type="button"
              onClick={handleApprove}
              className="w-full h-[52px] rounded-[16px] bg-primaryEmerald text-white font-bold text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-[0.98] transition shadow-sm"
            >
              <Check className="w-5 h-5 stroke-[2.5]" />
              <span>Mahsuplaşmayı Onayla ({offer.cycleAmount.toFixed(2)} ₺ Silinsin)</span>
            </button>
          )}

          <p className="text-center text-[11px] text-textSecondary mt-2 flex items-center justify-center gap-1">
            <ShieldCheck className="w-3.5 h-3.5 text-primaryEmerald" />
            <span>Kriptografik Merkle Tree L2 defterine işlenir</span>
          </p>
        </div>
      </div>
    </div>
  );
};
