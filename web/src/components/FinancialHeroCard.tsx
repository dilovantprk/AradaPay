import React from 'react';
import { Eye, EyeOff } from 'lucide-react';

interface FinancialHeroCardProps {
  netBalance: number;
  isLocked: boolean;
  onToggleLock: () => void;
  currencySymbol?: string;
}

export const FinancialHeroCard: React.FC<FinancialHeroCardProps> = ({
  netBalance,
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
    <section className="bg-surfaceWhite px-5 py-4 border-b border-surfaceBorder">
      <div className="max-w-2xl mx-auto">
        <div className="flex items-center justify-between">
          <span className="text-[11px] font-bold text-textSecondary tracking-[0.8px] uppercase">
            NET BAKİYE
          </span>

          <button
            onClick={onToggleLock}
            className="p-1.5 rounded-full text-textSecondary hover:bg-surfaceContainerLow active:scale-95 transition"
            title={isLocked ? 'Bakiyeyi Göster' : 'Bakiyeyi Gizle'}
          >
            {isLocked ? (
              <EyeOff className="w-4 h-4 text-textSecondary" />
            ) : (
              <Eye className="w-4 h-4 text-textSecondary" />
            )}
          </button>
        </div>

        <div className="mt-1">
          <p
            className={`text-[36px] md:text-[38px] font-extrabold tracking-[-1.0px] leading-tight ${
              isLocked
                ? 'text-textSecondary tracking-[2px]'
                : isPositive
                ? 'text-primaryEmerald'
                : 'text-accentRose'
            }`}
          >
            {isLocked ? `•••• ${currencySymbol}` : formattedBalance}
          </p>
        </div>
      </div>
    </section>
  );
};
