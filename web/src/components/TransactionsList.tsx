'use client';

import React from 'react';
import {
  Utensils,
  ShoppingCart,
  Plane,
  Home,
  Film,
  Zap,
  Tag,
  CheckCircle2,
  Receipt,
  ChevronRight
} from 'lucide-react';
import { Expense, ExpenseCategory } from '../types';

interface TransactionsListProps {
  expenses: Expense[];
  currentUserId: string;
  isLocked: boolean;
  onSeeAllClick: () => void;
  onExpenseClick: (expense: Expense) => void;
}

const getCategoryIcon = (category: ExpenseCategory) => {
  switch (category) {
    case 'DINING':
      return <Utensils className="w-4 h-4 text-[#00875A]" />;
    case 'GROCERIES':
      return <ShoppingCart className="w-4 h-4 text-blue-600" />;
    case 'TRAVEL':
      return <Plane className="w-4 h-4 text-purple-600" />;
    case 'HOUSING':
      return <Home className="w-4 h-4 text-amber-600" />;
    case 'ENTERTAINMENT':
      return <Film className="w-4 h-4 text-pink-600" />;
    case 'UTILITIES':
      return <Zap className="w-4 h-4 text-indigo-600" />;
    default:
      return <Tag className="w-4 h-4 text-[#8E8E93]" />;
  }
};

export const TransactionsList: React.FC<TransactionsListProps> = ({
  expenses,
  currentUserId,
  isLocked,
  onSeeAllClick,
  onExpenseClick
}) => {
  return (
    <section className="px-4 sm:px-0 space-y-2">
      {/* Header */}
      <div className="flex items-center justify-between px-1">
        <span className="text-[12px] font-bold text-[#8E8E93] tracking-[0.05em] uppercase">
          SON HAREKETLER & MASRAFLAR
        </span>
        <button
          onClick={onSeeAllClick}
          className="text-[13px] font-semibold text-[#00875A] hover:underline"
        >
          Tümünü Gör
        </button>
      </div>

      {/* Grouped Inset Card */}
      <div className="apple-card overflow-hidden">
        {expenses.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 px-6 text-center">
            <CheckCircle2 className="w-10 h-10 text-[#00875A] mb-2 stroke-[2]" />
            <p className="text-[15px] font-bold text-[#1C1C1E]">
              Bekleyen Ödeme Yok
            </p>
            <p className="text-[13px] text-[#8E8E93] mt-0.5 max-w-xs">
              Tüm grup masrafları dengede ve fitleşildi.
            </p>
          </div>
        ) : (
          <div className="divide-y divide-black/[0.04]">
            {expenses.slice(0, 6).map((expense) => {
              const isPayer = expense.paidBy === currentUserId;
              const mySplit = expense.splits.find((s) => s.userId === currentUserId);
              const myAmount = isPayer
                ? expense.amount - (mySplit?.amountOwed || 0)
                : mySplit?.amountOwed || (expense.amount / Math.max(1, expense.splits.length));

              const isPositive = isPayer;

              return (
                <div
                  key={expense.id}
                  onClick={() => onExpenseClick(expense)}
                  className="flex items-center justify-between p-4 hover:bg-black/[0.02] active:bg-black/[0.04] cursor-pointer transition select-none"
                >
                  {/* Left: Category Icon & Details */}
                  <div className="flex items-center gap-3.5 min-w-0">
                    <div className="w-10 h-10 rounded-[14px] bg-[#F2F2F7] flex items-center justify-center flex-shrink-0">
                      {getCategoryIcon(expense.category)}
                    </div>

                    <div className="min-w-0">
                      <p className="text-[15px] font-semibold text-[#1C1C1E] truncate">
                        {expense.description}
                      </p>
                      <p className="text-[12px] text-[#8E8E93] truncate">
                        {expense.date || new Date(expense.createdAt).toLocaleDateString('tr-TR')} • {expense.splits.length} kişi
                      </p>
                    </div>
                  </div>

                  {/* Right: Amount & Status */}
                  <div className="flex items-center gap-2 flex-shrink-0 ml-3">
                    <div className="text-right">
                      <span
                        className={`text-[11px] font-semibold block ${
                          isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                        }`}
                      >
                        {isPositive ? 'alacaklısın' : 'borçlusun'}
                      </span>
                      <span
                        className={`text-[14px] font-black font-tabular block ${
                          isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                        }`}
                      >
                        {isLocked
                          ? '•••• ₺'
                          : `${isPositive ? '+' : '-'} ${myAmount.toLocaleString('tr-TR', {
                              minimumFractionDigits: 2,
                              maximumFractionDigits: 2
                            })} ₺`}
                      </span>
                    </div>

                    <ChevronRight className="w-4 h-4 text-[#C7C7CC]" />
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </section>
  );
};
