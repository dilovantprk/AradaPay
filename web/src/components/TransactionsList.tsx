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
        <span className="text-[12px] font-bold text-[#64748B] tracking-[0.05em] uppercase">
          SON HAREKETLER & MASRAFLAR
        </span>
        <button
          onClick={onSeeAllClick}
          className="text-[13px] font-semibold text-[#00875A] hover:underline"
        >
          Tümünü Gör
        </button>
      </div>

      {/* Grouped Inset Stream */}
      <div className="bg-white rounded-[20px] border border-slate-200/80 divide-y divide-slate-100 overflow-hidden shadow-sm">
        {expenses.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 px-6 text-center">
            <CheckCircle2 className="w-10 h-10 text-[#00875A] mb-2 stroke-[2]" />
            <p className="text-[15px] font-bold text-[#0F172A]">
              Bekleyen Ödeme Yok
            </p>
            <p className="text-[13px] text-[#64748B] mt-0.5 max-w-xs">
              Tüm grup masrafları dengede ve fitleşildi.
            </p>
          </div>
        ) : (
          expenses.slice(0, 6).map((expense) => {
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
                className="flex items-center justify-between p-4 hover:bg-slate-50 active:bg-slate-100 cursor-pointer transition select-none"
              >
                {/* Left: Category Icon & Details */}
                <div className="flex items-center gap-3.5 min-w-0">
                  <div className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center flex-shrink-0">
                    {getCategoryIcon(expense.category)}
                  </div>

                  <div className="min-w-0">
                    <p className="text-[14px] font-bold text-[#0F172A] truncate">
                      {expense.description}
                    </p>
                    <p className="text-[12px] text-[#64748B] truncate">
                      {expense.date || new Date(expense.createdAt).toLocaleDateString('tr-TR')} • {expense.splits.length} kişi
                    </p>
                  </div>
                </div>

                {/* Right: Amount & Status */}
                <div className="flex items-center gap-2 flex-shrink-0 ml-3">
                  <div className="text-right">
                    <span
                      className={`text-[10px] font-semibold block uppercase tracking-wider ${
                        isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                      }`}
                    >
                      {isPositive ? 'alacaklısın' : 'borçlusun'}
                    </span>
                    <span
                      className={`text-[15px] font-black font-tabular block ${
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

                  <ChevronRight className="w-4 h-4 text-[#94A3B8]" />
                </div>
              </div>
            );
          })
        )}
      </div>
    </section>
  );
};
