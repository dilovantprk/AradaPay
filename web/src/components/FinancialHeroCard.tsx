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
      <div className="bg-white rounded-[24px] border border-slate-200/80 p-6 sm:p-7 space-y-4 shadow-sm">
        {/* Top Header */}
        <div className="flex items-center justify-between">
          <span className="text-[12px] font-bold text-[#64748B] tracking-[0.05em] uppercase">
            NET BAKİYE & HESAP ÖZETİ
          </span>

          <button
            onClick={onToggleLock}
            className="w-8 h-8 rounded-full bg-slate-100 hover:bg-slate-200 active:scale-[0.92] transition flex items-center justify-center text-[#64748B] hover:text-[#0F172A]"
            title={isLocked ? 'Bakiyeyi Göster' : 'Bakiyeyi Gizle'}
          >
            {isLocked ? (
              <EyeOff className="w-4 h-4" />
            ) : (
              <Eye className="w-4 h-4" />
            )}
          </button>
        </div>

        {/* Large Balance Display */}
        <div>
          <p
            className={`text-[40px] sm:text-[48px] font-extrabold tracking-[-0.03em] leading-none font-tabular ${
              isLocked
                ? 'text-[#94A3B8] tracking-[0.2em]'
                : isPositive
                ? 'text-[#00875A]'
                : 'text-[#D32F2F]'
            }`}
          >
            {isLocked ? `•••• ${currencySymbol}` : formattedBalance}
          </p>
        </div>

        {/* Clean, Flat Alacak & Borç Metric Rows (No nested card-in-card boxes!) */}
        {(totalReceivable > 0 || totalPayable > 0) && (
          <div className="grid grid-cols-2 gap-4 pt-4 border-t border-slate-100">
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-full bg-emerald-50 text-[#00875A] flex items-center justify-center flex-shrink-0">
                <ArrowDownLeft className="w-4 h-4 stroke-[2.5]" />
              </div>
              <div className="min-w-0">
                <span className="text-[11px] font-bold text-[#64748B] uppercase tracking-wider block truncate">
                  Alacaklarım
                </span>
                <span className="text-[15px] font-extrabold text-[#00875A] font-tabular block truncate">
                  {isLocked ? '•••• ₺' : `+${totalReceivable.toFixed(2)} ₺`}
                </span>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-full bg-rose-50 text-[#D32F2F] flex items-center justify-center flex-shrink-0">
                <ArrowUpRight className="w-4 h-4 stroke-[2.5]" />
              </div>
              <div className="min-w-0">
                <span className="text-[11px] font-bold text-[#64748B] uppercase tracking-wider block truncate">
                  Borçlarım
                </span>
                <span className="text-[15px] font-extrabold text-[#D32F2F] font-tabular block truncate">
                  {isLocked ? '•••• ₺' : `-${totalPayable.toFixed(2)} ₺`}
                </span>
              </div>
            </div>
          </div>
        )}
      </div>
    </section>
  );
};
