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
    <section className="grid grid-cols-2 gap-3">
      {/* Primary Action 1: Harcama Ekle */}
      <button
        onClick={onAddExpenseClick}
        className="h-[52px] rounded-[16px] bg-[#00875A] text-white font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-[0.96] transition shadow-sm shadow-emerald-900/15"
      >
        <Plus className="w-5 h-5 stroke-[2.5]" />
        <span>Harcama Ekle</span>
      </button>

      {/* Primary Action 2: Fitleş */}
      <button
        onClick={onSettleUpClick}
        className="h-[52px] rounded-[16px] bg-[#0F172A] text-white font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-[#1e293b] active:scale-[0.96] transition shadow-sm shadow-slate-900/15"
      >
        <CreditCard className="w-5 h-5 text-white" />
        <span>Fitleş</span>
      </button>
    </section>
  );
};

