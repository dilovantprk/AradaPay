'use client';

import React from 'react';
import { Plus, CreditCard, Send } from 'lucide-react';

interface ActionButtonsRowProps {
  onAddExpenseClick: () => void;
  onSettleUpClick: () => void;
  onRequestMoneyClick?: () => void;
}

export const ActionButtonsRow: React.FC<ActionButtonsRowProps> = ({
  onAddExpenseClick,
  onSettleUpClick,
  onRequestMoneyClick
}) => {
  return (
    <section className="px-4 sm:px-0">
      <div className="flex flex-col sm:flex-row items-center gap-3">
        {/* Primary Action: Harcama Ekle */}
        <button
          onClick={onAddExpenseClick}
          className="w-full sm:flex-1 h-12 rounded-[16px] bg-[#00875A] text-white font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-[0.98] transition shadow-sm shadow-emerald-800/20"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Harcama Ekle</span>
        </button>

        {/* Secondary Action: Öde & Fitleş */}
        <button
          onClick={onSettleUpClick}
          className="w-full sm:flex-1 h-12 rounded-[16px] bg-white border border-black/[0.08] text-[#1C1C1E] font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-slate-50 active:scale-[0.98] transition shadow-apple-sm"
        >
          <CreditCard className="w-4 h-4 text-[#8E8E93]" />
          <span>Öde & Fitleş</span>
        </button>

        {/* Nudge / Para İste */}
        {onRequestMoneyClick && (
          <button
            onClick={onRequestMoneyClick}
            className="w-full sm:w-auto px-4 h-12 rounded-[16px] bg-black/5 hover:bg-black/10 text-[#1C1C1E] font-semibold text-[13px] flex items-center justify-center gap-1.5 active:scale-[0.98] transition"
            title="Dürt & Hatırlat"
          >
            <Send className="w-3.5 h-3.5 text-[#8E8E93]" />
            <span>Dürt</span>
          </button>
        )}
      </div>
    </section>
  );
};
