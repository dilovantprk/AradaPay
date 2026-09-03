'use client';

import React, { useState, useMemo } from 'react';
import {
  PieChart,
  TrendingUp,
  Sparkles,
  CheckCircle2,
  ShieldCheck,
  Repeat,
  ArrowRight,
  Users,
  DollarSign,
  Calendar,
  ChevronRight
} from 'lucide-react';
import { Expense, Settlement, ExpenseCategory, CrossSettlementOffer, User } from '../types';
import { SmartSettlementReportModal } from '../components/SmartSettlementReportModal';

interface AnalyticsViewProps {
  expenses: Expense[];
  settlements: Settlement[];
  currentUserId: string;
  isLocked: boolean;
  crossOffers?: CrossSettlementOffer[];
  currentUser?: User;
  users?: User[];
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
  users = [],
  onOpenReceipt
}) => {
  const [selectedOfferForReport, setSelectedOfferForReport] = useState<CrossSettlementOffer | null>(null);

  const totalSpent = useMemo(() => {
    return expenses
      .filter((e) => e.paidBy === currentUserId)
      .reduce((sum, e) => sum + e.amount, 0);
  }, [expenses, currentUserId]);

  const totalSettled = useMemo(() => {
    return settlements.reduce((sum, s) => sum + s.amount, 0);
  }, [settlements]);

  // Category aggregations
  const categoryTotals = useMemo(() => {
    return expenses.reduce((acc, exp) => {
      acc[exp.category] = (acc[exp.category] || 0) + exp.amount;
      return acc;
    }, {} as Record<ExpenseCategory, number>);
  }, [expenses]);

  const grandTotal = useMemo(() => {
    return Object.values(categoryTotals).reduce((a, b) => a + b, 0) || 1;
  }, [categoryTotals]);

  // Top spending friends ranking (1:1 Android AnalyticsScreen.kt)
  const topFriends = useMemo(() => {
    const map = new Map<string, number>();
    expenses.forEach((exp) => {
      exp.splits.forEach((s) => {
        if (s.userId !== currentUserId) {
          map.set(s.userId, (map.get(s.userId) || 0) + s.amountOwed);
        }
      });
    });

    const list: { user: User; totalVolume: number }[] = [];
    map.forEach((volume, uId) => {
      const u = users.find((user) => user.id === uId);
      if (u) {
        list.push({ user: u, totalVolume: volume });
      }
    });

    return list.sort((a, b) => b.totalVolume - a.totalVolume);
  }, [expenses, users, currentUserId]);

  const activeOffer = crossOffers[0] || null;

  return (
    <div className="space-y-6 text-left animate-fadeIn">
      {/* Header */}
      <div className="px-1">
        <h2 className="text-[28px] font-extrabold text-[#1C1C1E] tracking-tight">Masa Analizi & Raporlar</h2>
        <p className="text-[13px] text-[#8E8E93]">Masadaki harcama alışkanlıkları, akıllı dengeleme tasarrufu ve adisyon dökümleri</p>
      </div>

      {/* Hero Stats (Apple HIG Style) */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="apple-card p-5 space-y-1">
          <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider block">
            MASAYA BIRAKILAN
          </span>
          <p className="text-[26px] font-black text-[#1C1C1E] font-tabular">
            {isLocked ? '•••• ₺' : `${totalSpent.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺`}
          </p>
          <span className="text-[11px] text-[#8E8E93]">{expenses.length} adet masaya bırakılan kayıt</span>
        </div>

        <div className="apple-card p-5 space-y-1">
          <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider block">
            ÖDEŞİLEN TUTAR
          </span>
          <p className="text-[26px] font-black text-[#00875A] font-tabular">
            {isLocked ? '•••• ₺' : `${totalSettled.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺`}
          </p>
          <span className="text-[11px] text-[#00875A] font-semibold">FAST İle Masadaki Hesap Kapatıldı</span>
        </div>

        <div className="apple-card p-5 space-y-1">
          <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider block">
            AKILLI DENGELEME TASARRUFU
          </span>
          <p className="text-[26px] font-black text-[#00875A] font-tabular">
            ~{(crossOffers.length * 45).toFixed(2)} ₺
          </p>
          <span className="text-[11px] text-[#8E8E93]">Kendiliğinden Ödeşme İle Sıfırlandı</span>
        </div>
      </div>

      {/* Smart Savings Card */}
      <div className="p-6 rounded-[24px] bg-gradient-to-br from-emerald-50 via-[#F0FDF4] to-emerald-100/50 border border-emerald-200/80 shadow-apple-sm space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-xl bg-[#00875A] text-white shadow-sm shadow-emerald-900/20">
              <Repeat className="w-5 h-5" />
            </div>
            <div>
              <span className="text-[12px] font-bold text-[#00875A] uppercase tracking-wider block">
                Akıllı Masa Dengeleme Tasarrufu
              </span>
              <p className="text-[18px] font-black text-[#1C1C1E]">
                %65 Daha Az Para Transferi
              </p>
            </div>
          </div>

          {activeOffer && (
            <button
              onClick={() => setSelectedOfferForReport(activeOffer)}
              className="px-4 py-2 rounded-full bg-white text-[#00875A] text-[12px] font-bold border border-emerald-300 hover:bg-emerald-50 transition shadow-2xs flex items-center gap-1.5"
            >
              <span>Raporu Aç</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          )}
        </div>

        <p className="text-[13px] text-[#8E8E93] leading-relaxed">
          AradaPay akıllı dengeleme algoritması, masadaki karşılıklı harcamaları otomatik eşitleyerek gereksiz para transferi adımlarını minimuma indirir.
        </p>
      </div>

      {/* 2-Column Desktop Grid for Category & Top Friends */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Category Breakdown */}
        <div className="lg:col-span-7 apple-card p-6 space-y-4">
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
                  <div className="h-2.5 rounded-full bg-[#F2F2F7] overflow-hidden">
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

        {/* Top Spending Friends (1:1 Android Parity) */}
        <div className="lg:col-span-5 apple-card p-6 space-y-4">
          <h3 className="text-[13px] font-bold text-[#8E8E93] uppercase tracking-wider">
            EN ÇOK HARCAMA YAPILAN ARKADAŞLAR
          </h3>

          {topFriends.length === 0 ? (
            <div className="p-6 text-center text-[#8E8E93] text-[13px]">
              Henüz harcama bölüşümü kaydı yok.
            </div>
          ) : (
            <div className="divide-y divide-black/[0.04]">
              {topFriends.slice(0, 5).map((item, idx) => (
                <div key={item.user.id} className="py-3 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-full bg-emerald-100 text-[#00875A] font-bold text-[12px] flex items-center justify-center">
                      {item.user.fullName.slice(0, 2).toUpperCase()}
                    </div>
                    <div>
                      <div className="text-[13px] font-bold text-[#1C1C1E]">{item.user.fullName}</div>
                      <div className="text-[11px] text-[#8E8E93] font-mono">{item.user.tag || `@${item.user.username}`}</div>
                    </div>
                  </div>

                  <span className="text-[14px] font-black text-[#1C1C1E] font-tabular">
                    {item.totalVolume.toFixed(2)} ₺
                  </span>
                </div>
              ))}
            </div>
          )}
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
