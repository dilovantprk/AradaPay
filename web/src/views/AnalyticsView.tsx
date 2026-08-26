import React from 'react';
import { PieChart, TrendingUp, Sparkles, CheckCircle2, ShieldCheck } from 'lucide-react';
import { Expense, Settlement, ExpenseCategory } from '../types';

interface AnalyticsViewProps {
  expenses: Expense[];
  settlements: Settlement[];
  currentUserId: string;
  isLocked: boolean;
}

const CATEGORY_NAMES: Record<ExpenseCategory, { name: string; color: string; icon: string }> = {
  DINING: { name: 'Yemek & Kafe', color: '#00875A', icon: '🍽️' },
  GROCERIES: { name: 'Market & Gıda', color: '#0284C7', icon: '🛒' },
  TRAVEL: { name: 'Seyahat & Ulaşım', color: '#8B5CF6', icon: '✈️' },
  HOUSING: { name: 'Ev & Kira', color: '#F59E0B', icon: '🏠' },
  ENTERTAINMENT: { name: 'Eğlence & Sinema', color: '#EC4899', icon: '🍿' },
  UTILITIES: { name: 'Faturalar', color: '#6366F1', icon: '⚡' },
  SHOPPING: { name: 'Alışveriş', color: '#10B981', icon: '🛍️' },
  OTHER: { name: 'Diğer', color: '#64748B', icon: '📦' }
};

export const AnalyticsView: React.FC<AnalyticsViewProps> = ({
  expenses,
  settlements,
  currentUserId,
  isLocked
}) => {
  const totalSpent = expenses
    .filter((e) => e.paidBy === currentUserId)
    .reduce((sum, e) => sum + e.amount, 0);

  const totalSettled = settlements.reduce((sum, s) => sum + s.amount, 0);

  // Category aggregations
  const categoryTotals = expenses.reduce((acc, exp) => {
    acc[exp.category] = (acc[exp.category] || 0) + exp.amount;
    return acc;
  }, {} as Record<ExpenseCategory, number>);

  const grandTotal = Object.values(categoryTotals).reduce((a, b) => a + b, 0) || 1;

  return (
    <div className="pb-24 max-w-2xl mx-auto px-5 py-4 space-y-4">
      {/* Header */}
      <div>
        <h2 className="text-[24px] font-bold text-textPrimary">Finansal Analiz</h2>
        <p className="text-[12px] text-textSecondary">Harcama dağılımı ve tasarruf raporu</p>
      </div>

      {/* Hero Stats */}
      <div className="grid grid-cols-2 gap-3">
        <div className="p-4 rounded-[18px] bg-surfaceWhite border border-surfaceBorder shadow-xs">
          <span className="text-[11px] font-bold text-textSecondary uppercase tracking-wider block">
            TOPLAM ÖDENEN
          </span>
          <p className="text-[22px] font-bold text-textPrimary mt-1">
            {isLocked ? '•••• ₺' : `${totalSpent.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺`}
          </p>
          <span className="text-[11px] text-textSecondary">{expenses.length} adet harcama</span>
        </div>

        <div className="p-4 rounded-[18px] bg-surfaceWhite border border-surfaceBorder shadow-xs">
          <span className="text-[11px] font-bold text-textSecondary uppercase tracking-wider block">
            FİTLEŞİLEN TUTAR
          </span>
          <p className="text-[22px] font-bold text-primaryEmerald mt-1">
            {isLocked ? '•••• ₺' : `${totalSettled.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺`}
          </p>
          <span className="text-[11px] text-primaryEmerald font-medium">Banka EFT/FAST</span>
        </div>
      </div>

      {/* DFS Smart Savings Card */}
      <div className="p-4 rounded-[18px] bg-gradient-to-r from-[#F0FDF4] to-[#ECFDF5] border border-[#BBF7D0]">
        <div className="flex items-center gap-2 mb-1">
          <Sparkles className="w-4 h-4 text-primaryEmerald" />
          <span className="text-[13px] font-bold text-primaryEmerald uppercase">
            DFS Borç Optimizasyonu
          </span>
        </div>
        <p className="text-[18px] font-extrabold text-textPrimary">
          %65 Daha Az Banka Transferi
        </p>
        <p className="text-[12px] text-textSecondary mt-1 leading-relaxed">
          AradaPay DFS ve Greedy Borç Sadeleştirici motoru, döngüsel borçları otomatik mahsuplayarak işlem ücreti ve EFT trafiğini minimuma indirir.
        </p>
      </div>

      {/* Category Breakdown */}
      <div className="p-5 rounded-[18px] bg-surfaceWhite border border-surfaceBorder shadow-xs space-y-3">
        <h3 className="text-[14px] font-bold text-textPrimary uppercase tracking-wider">
          KATEGORİ BAZLI HARCAMALAR
        </h3>

        <div className="space-y-3">
          {(Object.keys(categoryTotals) as ExpenseCategory[]).map((catKey) => {
            const amount = categoryTotals[catKey] || 0;
            const percentage = Math.round((amount / grandTotal) * 100);
            const catInfo = CATEGORY_NAMES[catKey] || CATEGORY_NAMES.OTHER;

            return (
              <div key={catKey} className="space-y-1.5">
                <div className="flex items-center justify-between text-[13px]">
                  <span className="font-semibold text-textPrimary flex items-center gap-1.5">
                    <span>{catInfo.icon}</span>
                    <span>{catInfo.name}</span>
                  </span>
                  <span className="font-bold text-textPrimary">
                    {isLocked ? '•••• ₺' : `${amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺`}{' '}
                    <span className="text-[11px] font-normal text-textSecondary">({percentage}%)</span>
                  </span>
                </div>

                {/* Progress bar */}
                <div className="w-full h-2 rounded-full bg-slate-100 overflow-hidden">
                  <div
                    className="h-full rounded-full transition-all duration-500"
                    style={{
                      width: `${percentage}%`,
                      backgroundColor: catInfo.color
                    }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
