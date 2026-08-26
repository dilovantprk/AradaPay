'use client';

import React, { useState } from 'react';
import { PieChart, TrendingUp, Sparkles, CheckCircle2, ShieldCheck, Repeat, ArrowRight } from 'lucide-react';
import { Expense, Settlement, ExpenseCategory, CrossSettlementOffer, User } from '../types';
import { SmartSettlementReportModal } from '../components/SmartSettlementReportModal';

interface AnalyticsViewProps {
  expenses: Expense[];
  settlements: Settlement[];
  currentUserId: string;
  isLocked: boolean;
  crossOffers?: CrossSettlementOffer[];
  currentUser?: User;
  onOpenReceipt?: (txId: string) => void;
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
  isLocked,
  crossOffers = [],
  currentUser,
  onOpenReceipt
}) => {
  const [selectedOfferForReport, setSelectedOfferForReport] = useState<CrossSettlementOffer | null>(null);

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

  const activeOffer = crossOffers[0] || null;

  return (
    <div className="space-y-4 text-left">
      {/* Header */}
      <div className="px-1">
        <h2 className="text-[26px] font-bold text-[#1C1C1E] tracking-tight">Finansal Analiz</h2>
        <p className="text-[13px] text-[#8E8E93]">Harcama dağılımı ve tasarruf raporu</p>
      </div>

      {/* Hero Stats (Apple HIG Style) */}
      <div className="grid grid-cols-2 gap-3">
        <div className="apple-card p-5 space-y-1">
          <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider block">
            TOPLAM ÖDENEN
          </span>
          <p className="text-[24px] font-black text-[#1C1C1E] font-tabular">
            {isLocked ? '•••• ₺' : `${totalSpent.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺`}
          </p>
          <span className="text-[11px] text-[#8E8E93]">{expenses.length} adet harcama kaydı</span>
        </div>

        <div className="apple-card p-5 space-y-1">
          <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider block">
            FİTLEŞİLEN TUTAR
          </span>
          <p className="text-[24px] font-black text-[#00875A] font-tabular">
            {isLocked ? '•••• ₺' : `${totalSettled.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺`}
          </p>
          <span className="text-[11px] text-[#00875A] font-semibold">FAST / Havale Tamamlandı</span>
        </div>
      </div>

      {/* DFS Smart Savings Card */}
      <div className="p-6 rounded-[24px] bg-gradient-to-br from-emerald-50 via-[#F0FDF4] to-emerald-100/50 border border-emerald-200/80 shadow-apple-sm space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="p-2 rounded-xl bg-[#00875A] text-white shadow-sm shadow-emerald-900/20">
              <Repeat className="w-4 h-4" />
            </div>
            <span className="text-[12px] font-bold text-[#00875A] uppercase tracking-wider">
              DFS Akıllı Mahsuplaşma Tasarrufu
            </span>
          </div>

          {activeOffer && (
            <button
              onClick={() => setSelectedOfferForReport(activeOffer)}
              className="px-3 py-1 rounded-full bg-white text-[#00875A] text-[11px] font-bold border border-emerald-300 hover:bg-emerald-50 transition shadow-2xs flex items-center gap-1"
            >
              <span>Raporu Aç</span>
              <ArrowRight className="w-3 h-3" />
            </button>
          )}
        </div>

        <p className="text-[20px] font-black text-[#1C1C1E]">
          %65 Daha Az Banka Transferi
        </p>

        <p className="text-[13px] text-[#8E8E93] leading-relaxed">
          AradaPay DFS ve Greedy Borç Sadeleştirici motoru, döngüsel borçları otomatik mahsuplayarak işlem ücreti ve EFT trafiğini minimuma indirir.
        </p>
      </div>

      {/* Category Breakdown */}
      <div className="apple-card p-6 space-y-4">
        <h3 className="text-[13px] font-bold text-[#8E8E93] uppercase tracking-wider">
          KATEGORİ BAZLI HARCAMALAR
        </h3>

        <div className="space-y-4">
          {(Object.keys(categoryTotals) as ExpenseCategory[]).map((catKey) => {
            const amount = categoryTotals[catKey] || 0;
            const percentage = Math.round((amount / grandTotal) * 100);
            const catInfo = CATEGORY_NAMES[catKey] || CATEGORY_NAMES.OTHER;

            return (
              <div key={catKey} className="space-y-1.5">
                <div className="flex items-center justify-between text-[13px]">
                  <div className="flex items-center gap-2">
                    <span>{catInfo.icon}</span>
                    <span className="font-bold text-[#1C1C1E]">{catInfo.name}</span>
                  </div>
                  <span className="font-black text-[#1C1C1E] font-tabular">
                    {isLocked ? '•••• ₺' : `${amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺`} ({percentage}%)
                  </span>
                </div>

                {/* Progress Bar */}
                <div className="h-2 rounded-full bg-[#F2F2F7] overflow-hidden">
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

      {/* 1:1 Android SmartSettlementReportModal */}
      {selectedOfferForReport && currentUser && (
        <SmartSettlementReportModal
          isOpen={selectedOfferForReport !== null}
          onClose={() => setSelectedOfferForReport(null)}
          offer={selectedOfferForReport}
          currentUser={currentUser}
          onOpenReceipt={(txId) => {
            setSelectedOfferForReport(null);
            if (onOpenReceipt) onOpenReceipt(txId);
          }}
        />
      )}
    </div>
  );
};
