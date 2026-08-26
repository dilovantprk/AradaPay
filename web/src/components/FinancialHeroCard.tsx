'use client';

import React from 'react';
import { Eye, EyeOff, ArrowUpRight, ArrowDownLeft } from 'lucide-react';

interface FinancialHeroCardProps {
  netBalance: number;
  totalReceivable?: number;
  totalPayable?: number;
  isLocked: boolean;
  onToggleLock: () => void;
  currencySymbol?: string;
}

export const FinancialHeroCard: React.FC<FinancialHeroCardProps> = ({
  netBalance,
  totalReceivable = 0,
  totalPayable = 0,
  isLocked,
  onToggleLock,
  currencySymbol = '₺'
}) => {
  const isPositive = netBalance >= 0;
  const formattedBalance = `${isPositive ? '+' : ''}${netBalance.toLocaleString('tr-TR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })} ${currencySymbol}`;

  return (
    <section className="px-4 sm:px-0 pt-4">
      <div className="apple-card p-6 sm:p-7 space-y-4">
        {/* Top Header */}
        <div className="flex items-center justify-between">
          <span className="text-[12px] font-bold text-[#8E8E93] tracking-[0.05em] uppercase">
            NET BAKİYE & HESAP ÖZETİ
          </span>

          <button
            onClick={onToggleLock}
            className="w-8 h-8 rounded-full bg-black/5 hover:bg-black/10 active:scale-[0.92] transition flex items-center justify-center text-[#8E8E93] hover:text-[#1C1C1E]"
            title={isLocked ? 'Bakiyeyi Göster' : 'Bakiyeyi Gizle'}
          >
            {isLocked ? (
              <EyeOff className="w-4 h-4 text-[#8E8E93]" />
            ) : (
              <Eye className="w-4 h-4 text-[#8E8E93]" />
            )}
          </button>
        </div>

        {/* Large Balance Display */}
        <div>
          <p
            className={`text-[40px] sm:text-[48px] font-extrabold tracking-[-0.03em] leading-none font-tabular ${
              isLocked
                ? 'text-[#8E8E93] tracking-[0.2em]'
                : isPositive
                ? 'text-[#00875A]'
                : 'text-[#D32F2F]'
            }`}
          >
            {isLocked ? `•••• ${currencySymbol}` : formattedBalance}
          </p>
        </div>

        {/* Alacak vs Borç Status Chips (Apple HIG Style) */}
        {(totalReceivable > 0 || totalPayable > 0) && (
          <div className="flex items-center gap-3 pt-3 border-t border-black/[0.04]">
            <div className="flex-1 p-3 rounded-[14px] bg-[#E8F5E9] border border-[#C8E6C9] flex items-center justify-between">
              <div className="flex items-center gap-1.5 text-[12px] font-bold text-[#00875A]">
                <ArrowDownLeft className="w-3.5 h-3.5" />
                <span>Alacaklısın</span>
              </div>
              <span className="text-[13px] font-black text-[#00875A] font-tabular">
                {isLocked ? '•••• ₺' : `+${totalReceivable.toFixed(2)} ₺`}
              </span>
            </div>

            <div className="flex-1 p-3 rounded-[14px] bg-[#FFEBEE] border border-[#FFCDD2] flex items-center justify-between">
              <div className="flex items-center gap-1.5 text-[12px] font-bold text-[#D32F2F]">
                <ArrowUpRight className="w-3.5 h-3.5" />
                <span>Borçlusun</span>
              </div>
              <span className="text-[13px] font-black text-[#D32F2F] font-tabular">
                {isLocked ? '•••• ₺' : `-${totalPayable.toFixed(2)} ₺`}
              </span>
            </div>
          </div>
        )}
      </div>
    </section>
  );
};
