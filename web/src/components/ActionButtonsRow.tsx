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
    <section className="bg-surfaceWhite px-5 py-3.5 border-b border-surfaceBorder">
      <div className="max-w-2xl mx-auto flex flex-col sm:flex-row items-center gap-3">
        {/* Harcama Ekle Butonu */}
        <button
          onClick={onAddExpenseClick}
          className="w-full sm:flex-1 h-[52px] rounded-[16px] bg-primaryEmerald text-white font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-[0.98] transition shadow-sm"
        >
          <Plus className="w-[18px] h-[18px] stroke-[2.5]" />
          <span>Harcama Ekle</span>
        </button>

        {/* Öde & Fitleş Butonu */}
        <button
          onClick={onSettleUpClick}
          className="w-full sm:flex-1 h-[52px] rounded-[16px] bg-textPrimary text-white font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-[#1e293b] active:scale-[0.98] transition shadow-sm"
        >
          <CreditCard className="w-[18px] h-[18px] stroke-[2.2]" />
          <span>Öde & Fitleş</span>
        </button>

        {/* Para İste & Dürt Butonu (Varsa) */}
        {onRequestMoneyClick && (
          <button
            onClick={onRequestMoneyClick}
            className="w-full sm:w-auto px-4 h-[52px] rounded-[16px] bg-surfaceContainerLow text-textPrimary font-semibold text-[13px] flex items-center justify-center gap-1.5 hover:bg-slate-200 active:scale-[0.98] transition"
            title="Para İste & Hatırlat"
          >
            <Send className="w-4 h-4 text-textSecondary" />
            <span className="hidden sm:inline">Para İste</span>
          </button>
        )}
      </div>
    </section>
  );
};
