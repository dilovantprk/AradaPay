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
    <section className="bg-white rounded-[20px] sm:rounded-[24px] border border-slate-200/80 p-5 sm:p-6 space-y-3 shadow-sm">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <span className="text-[12px] font-bold text-[#64748B] tracking-[0.05em] uppercase">
          MASA DURUMU
        </span>

        <button
          onClick={onToggleLock}
          className="w-8 h-8 rounded-full bg-[#F1F5F9] hover:bg-slate-200 active:scale-[0.92] transition flex items-center justify-center text-[#64748B] hover:text-[#0F172A]"
          title={isLocked ? 'Bakiyeyi Göster' : 'Bakiyeyi Gizle'}
        >
          {isLocked ? (
            <EyeOff className="w-4 h-4 text-[#8E8E93]" />
          ) : (
            <Eye className="w-4 h-4 text-[#00875A]" />
          )}
        </button>
      </div>

      {/* Large Balance Display */}
      <div>
        <p
          className={`text-[36px] sm:text-[42px] font-black tracking-[-0.03em] leading-none font-tabular ${
            isLocked
              ? 'text-[#94A3B8] tracking-[0.2em]'
              : isPositive
              ? 'text-[#00875A]'
              : 'text-[#DC2626]'
          }`}
        >
          {isLocked ? `•••• ${currencySymbol}` : formattedBalance}
        </p>
      </div>

      {/* Clean, Flat Masada Kalan & Payıma Düşen Metric Rows */}
      {(totalReceivable > 0 || totalPayable > 0) && (
        <div className="grid grid-cols-2 gap-4 pt-3 border-t border-slate-100">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-full bg-emerald-50 text-[#00875A] flex items-center justify-center flex-shrink-0">
              <ArrowDownLeft className="w-4 h-4 stroke-[2.5]" />
            </div>
            <div className="min-w-0">
              <span className="text-[11px] font-bold text-[#64748B] uppercase tracking-wider block truncate">
                Masada Kalan Payım
              </span>
              <span className="text-[14px] font-extrabold text-[#00875A] font-tabular block truncate">
                {isLocked ? '•••• ₺' : `+${totalReceivable.toFixed(2)} ₺`}
              </span>
            </div>
          </div>

          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-full bg-rose-50 text-[#DC2626] flex items-center justify-center flex-shrink-0">
              <ArrowUpRight className="w-4 h-4 stroke-[2.5]" />
            </div>
            <div className="min-w-0">
              <span className="text-[11px] font-bold text-[#64748B] uppercase tracking-wider block truncate">
                Payıma Düşen
              </span>
              <span className="text-[14px] font-extrabold text-[#DC2626] font-tabular block truncate">
                {isLocked ? '•••• ₺' : `-${totalPayable.toFixed(2)} ₺`}
              </span>
            </div>
          </div>
        </div>
      )}
    </section>
  );
};

