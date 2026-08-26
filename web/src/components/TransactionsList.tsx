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
  Receipt
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
      return <Utensils className="w-5 h-5 text-textPrimary" />;
    case 'GROCERIES':
      return <ShoppingCart className="w-5 h-5 text-textPrimary" />;
    case 'TRAVEL':
      return <Plane className="w-5 h-5 text-textPrimary" />;
    case 'HOUSING':
      return <Home className="w-5 h-5 text-textPrimary" />;
    case 'ENTERTAINMENT':
      return <Film className="w-5 h-5 text-textPrimary" />;
    case 'UTILITIES':
      return <Zap className="w-5 h-5 text-textPrimary" />;
    default:
      return <Tag className="w-5 h-5 text-textPrimary" />;
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
    <section className="bg-surfaceWhite py-3 border-b border-surfaceBorder">
      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between px-5 py-2">
          <span className="text-[11px] font-bold text-textSecondary tracking-[0.8px] uppercase">
            SON HAREKETLER
          </span>
          <button
            onClick={onSeeAllClick}
            className="text-[13px] font-semibold text-primaryEmerald hover:underline py-1"
          >
            Tümünü Gör
          </button>
        </div>

        <div className="border-t border-surfaceBorder" />

        {/* Empty State */}
        {expenses.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-10 px-5 text-center">
            <CheckCircle2 className="w-9 h-9 text-primaryEmerald mb-2 stroke-[2.2]" />
            <p className="text-[15px] font-bold text-textPrimary">
              Bekleyen Ödemeniz Yok
            </p>
            <p className="text-[12px] text-textSecondary mt-0.5 max-w-xs">
              Tüm hesaplar dengede, fitleşilecek borcunuz bulunmuyor.
            </p>
          </div>
        ) : (
          <div className="divide-y divide-surfaceBorder">
            {expenses.slice(0, 5).map((expense) => {
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
                  className="flex items-center justify-between px-5 py-3.5 hover:bg-[#F8FAFC] active:bg-[#F1F5F9] cursor-pointer transition"
                >
                  {/* Left: Icon & Title */}
                  <div className="flex items-center gap-3.5 min-w-0">
                    <div className="w-11 h-11 rounded-[14px] bg-surfaceContainerLow flex items-center justify-center flex-shrink-0">
                      {getCategoryIcon(expense.category)}
                    </div>

                    <div className="min-w-0">
                      <p className="text-[15px] font-semibold text-textPrimary truncate">
                        {expense.description}
                      </p>
                      <p className="text-[12px] text-textSecondary truncate">
                        {expense.date || new Date(expense.createdAt).toLocaleDateString('tr-TR')} • {expense.splits.length} kişi
                      </p>
                    </div>
                  </div>

                  {/* Right: Amount & Status */}
                  <div className="text-right flex-shrink-0 ml-3">
                    <span
                      className={`text-[11px] font-medium block ${
                        isPositive ? 'text-primaryEmerald' : 'text-accentRose'
                      }`}
                    >
                      {isPositive ? 'alacaklısın' : 'sen borçlusun'}
                    </span>
                    <span
                      className={`text-[14px] font-bold block ${
                        isPositive ? 'text-primaryEmerald' : 'text-accentRose'
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
                </div>
              );
            })}
          </div>
        )}
      </div>
    </section>
  );
};
