'use client';

import React from 'react';
import {
  X,
  ArrowLeft,
  Calendar,
  MapPin,
  Users,
  ShieldCheck,
  Send,
  CreditCard,
  Receipt,
  CheckCircle2,
  Clock,
  Share2
} from 'lucide-react';
import { Expense, User, ExpenseCategory } from '../types';

interface ExpenseDetailModalProps {
  isOpen: boolean;
  onClose: () => void;
  expense: Expense | null;
  currentUser: User;
  users: User[];
  onOpenReceipt: (expense: Expense) => void;
  onOpenSettleUp: (expense: Expense) => void;
  onOpenNudge: (expense: Expense) => void;
}

const CATEGORY_ICONS: Record<ExpenseCategory, { icon: string; name: string }> = {
  DINING: { icon: '🍽️', name: 'Yemek & Kafe' },
  GROCERIES: { icon: '🛒', name: 'Market & Gıda' },
  TRAVEL: { icon: '✈️', name: 'Ulaşım & Seyahat' },
  HOUSING: { icon: '🏠', name: 'Ev & Kira' },
  ENTERTAINMENT: { icon: '🍿', name: 'Eğlence & Sinema' },
  UTILITIES: { icon: '⚡', name: 'Faturalar' },
  SHOPPING: { icon: '🛍️', name: 'Alışveriş' },
  OTHER: { icon: '📦', name: 'Diğer' }
};

export const ExpenseDetailModal: React.FC<ExpenseDetailModalProps> = ({
  isOpen,
  onClose,
  expense,
  currentUser,
  users,
  onOpenReceipt,
  onOpenSettleUp,
  onOpenNudge
}) => {
  if (!isOpen || !expense) return null;

  const payer = users.find((u) => u.id === expense.paidBy);
  const isPayerMe = expense.paidBy === currentUser.id;
  const mySplit = expense.splits.find((s) => s.userId === currentUser.id);
  const categoryInfo = CATEGORY_ICONS[expense.category] || { icon: '📦', name: 'Harcama' };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full max-w-lg rounded-t-[32px] sm:rounded-[28px] shadow-apple-modal border border-black/[0.08] overflow-hidden flex flex-col max-h-[92vh] animate-appleSheet">
        {/* Mobile iOS Drag Handle */}
        <div className="w-12 h-1.5 bg-black/15 rounded-full mx-auto mt-3 sm:hidden" />

        {/* Top App Bar (1:1 Android Style) */}
        <div className="px-5 py-3.5 border-b border-black/[0.06] flex items-center justify-between bg-white/80 backdrop-blur-md">
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-black/5 flex items-center justify-center text-[#1C1C1E] hover:bg-black/10 active:scale-95 transition"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>

          <h3 className="text-[17px] font-bold text-[#1C1C1E] tracking-tight">
            Harcama Detayı
          </h3>

          <button
            onClick={() => onOpenReceipt(expense)}
            className="w-9 h-9 rounded-full bg-emerald-50 text-[#00875A] flex items-center justify-center hover:bg-emerald-100 active:scale-95 transition"
            title="Kriptografik Dekont"
          >
            <Receipt className="w-4 h-4" />
          </button>
        </div>

        {/* Body Content */}
        <div className="p-5 sm:p-6 overflow-y-auto flex-1 space-y-5 text-left">
          {/* Hero Amount & Description Card */}
          <div className="p-6 rounded-[24px] bg-[#F2F2F7] border border-black/[0.04] text-center space-y-2">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white text-[12px] font-bold text-[#1C1C1E] shadow-2xs">
              <span>{categoryInfo.icon}</span>
              <span>{categoryInfo.name}</span>
            </div>

            <div className="text-[36px] sm:text-[40px] font-black text-[#1C1C1E] font-tabular tracking-tight">
              {expense.amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
            </div>

            <h4 className="text-[18px] font-bold text-[#1C1C1E]">
              {expense.description}
            </h4>

            <div className="flex items-center justify-center gap-4 text-[12px] text-[#8E8E93] pt-1">
              <span className="flex items-center gap-1">
                <Calendar className="w-3.5 h-3.5" />
                {expense.date || new Date(expense.createdAt).toLocaleDateString('tr-TR')}
              </span>
              <span>•</span>
              <span>Ödeyen: <strong>{isPayerMe ? 'Sen' : payer?.fullName || 'Bilinmiyor'}</strong></span>
            </div>
          </div>

          {/* Katılımcı Bölüşüm Listesi */}
          <div className="space-y-2.5">
            <div className="flex items-center justify-between px-1">
              <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider">
                BÖLÜŞÜM KATILIMCILARI ({expense.splits.length})
              </span>
              <span className="text-[11px] font-bold text-[#00875A]">
                {expense.splitMethod === 'EQUAL' ? 'Eşit Paylaşım' : 'Özel Pay'}
              </span>
            </div>

            <div className="apple-card divide-y divide-black/[0.04] overflow-hidden">
              {expense.splits.map((split) => {
                const user = users.find((u) => u.id === split.userId);
                const isThisPayer = split.userId === expense.paidBy;

                return (
                  <div
                    key={split.id}
                    className="p-3.5 flex items-center justify-between hover:bg-black/[0.02] transition"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-9 h-9 rounded-full bg-emerald-100 text-[#00875A] font-bold text-[13px] flex items-center justify-center">
                        {user?.fullName.slice(0, 2).toUpperCase() || 'AP'}
                      </div>
                      <div>
                        <div className="text-[14px] font-bold text-[#1C1C1E]">
                          {split.userId === currentUser.id ? 'Sen' : user?.fullName || 'Katılımcı'}
                        </div>
                        <div className="text-[11px] text-[#8E8E93] font-mono">
                          {user?.tag || `@${user?.username || 'user'}`}
                        </div>
                      </div>
                    </div>

                    <div className="text-right">
                      <div className="text-[14px] font-black text-[#1C1C1E] font-tabular">
                        {split.amountOwed.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                      </div>
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded-full inline-block mt-0.5 ${
                          isThisPayer
                            ? 'bg-emerald-100 text-[#00875A]'
                            : 'bg-amber-100 text-amber-800'
                        }`}
                      >
                        {isThisPayer ? 'Ödeyen' : 'Borçlu Payı'}
                      </span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* İşlem Zaman Tüneli (Timeline Events) */}
          <div className="space-y-2">
            <span className="text-[11px] font-bold text-[#8E8E93] uppercase tracking-wider block px-1">
              İŞLEM ZAMAN TÜNELİ
            </span>

            <div className="apple-card p-4 space-y-3">
              <div className="flex items-start gap-3 text-[13px]">
                <div className="w-5 h-5 rounded-full bg-emerald-100 text-[#00875A] flex items-center justify-center flex-shrink-0 mt-0.5">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                </div>
                <div>
                  <div className="font-bold text-[#1C1C1E]">Harcama Kaydedildi</div>
                  <div className="text-[11px] text-[#8E8E93]">
                    {payer?.fullName} tarafından AradaPay'e eklendi.
                  </div>
                </div>
              </div>

              <div className="flex items-start gap-3 text-[13px]">
                <div className="w-5 h-5 rounded-full bg-emerald-100 text-[#00875A] flex items-center justify-center flex-shrink-0 mt-0.5">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                </div>
                <div>
                  <div className="font-bold text-[#1C1C1E]">Bölüşüm Hesaplandı & Mühürlendi</div>
                  <div className="text-[11px] text-[#8E8E93]">
                    SHA-256 Merkle Ağacı kriptografik özeti oluşturuldu.
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-4 bg-white border-t border-black/[0.06] flex items-center gap-3">
          {!isPayerMe ? (
            <button
              onClick={() => {
                onClose();
                onOpenSettleUp(expense);
              }}
              className="flex-1 h-12 rounded-[16px] bg-[#00875A] hover:bg-[#00744d] text-white font-bold text-[14px] flex items-center justify-center gap-2 active:scale-[0.98] transition shadow-sm shadow-emerald-800/20"
            >
              <CreditCard className="w-4 h-4" />
              <span>Payımı FAST ile Öde ({mySplit?.amountOwed || 0} ₺)</span>
            </button>
          ) : (
            <button
              onClick={() => {
                onClose();
                onOpenNudge(expense);
              }}
              className="flex-1 h-12 rounded-[16px] bg-black/5 hover:bg-black/10 text-[#1C1C1E] font-bold text-[14px] flex items-center justify-center gap-2 active:scale-[0.98] transition"
            >
              <Send className="w-4 h-4 text-[#8E8E93]" />
              <span>Katılımcılara Dürtme Gönder</span>
            </button>
          )}

          <button
            onClick={() => onOpenReceipt(expense)}
            className="px-4 h-12 rounded-[16px] bg-white border border-black/[0.08] text-[#1C1C1E] font-bold text-[13px] flex items-center justify-center gap-1.5 hover:bg-slate-50 active:scale-[0.98] transition"
          >
            <Receipt className="w-4 h-4 text-[#8E8E93]" />
            <span className="hidden sm:inline">Dekont</span>
          </button>
        </div>
      </div>
    </div>
  );
};
