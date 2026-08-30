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
      <div className="grid grid-cols-3 gap-2.5">
        {/* Primary Action: Harcama Ekle */}
        <button
          onClick={onAddExpenseClick}
          className="h-12 rounded-[14px] bg-[#00875A] text-white font-bold text-[13px] flex items-center justify-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-900/10"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Harcama</span>
        </button>

        {/* Secondary Action: Öde & Fitleş */}
        <button
          onClick={onSettleUpClick}
          className="h-12 rounded-[14px] bg-white border border-slate-200 text-[#0F172A] font-bold text-[13px] flex items-center justify-center gap-1.5 hover:bg-slate-50 active:scale-95 transition shadow-2xs"
        >
          <CreditCard className="w-4 h-4 text-[#00875A]" />
          <span>Fitleş</span>
        </button>

        {/* Nudge / Para İste */}
        {onRequestMoneyClick && (
          <button
            onClick={onRequestMoneyClick}
            className="h-12 rounded-[14px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] font-bold text-[13px] flex items-center justify-center gap-1.5 active:scale-95 transition"
          >
            <Send className="w-3.5 h-3.5 text-[#64748B]" />
            <span>Dürt</span>
          </button>
        )}
      </div>
    </section>
  );
};
