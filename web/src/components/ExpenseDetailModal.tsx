'use client';

import React from 'react';
import {
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
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* Top Bar (1:1 Android Style) */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3 bg-white border-b border-slate-100 flex items-center justify-between flex-shrink-0">
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center text-[#0F172A] hover:bg-slate-200 active:scale-95 transition"
            title="Geri"
          >
            <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
          </button>

          <h3 className="text-[17px] font-bold text-[#0F172A] tracking-tight">
            Harcama Detayı
          </h3>

          <button
            onClick={() => onOpenReceipt(expense)}
            className="w-10 h-10 rounded-[12px] bg-emerald-100 text-[#00875A] flex items-center justify-center hover:bg-emerald-200 active:scale-95 transition"
            title="Kriptografik Dekont"
          >
            <Receipt className="w-5 h-5 stroke-[2.2]" />
          </button>
        </div>

        {/* Body Content */}
        <div className="p-5 sm:p-6 overflow-y-auto flex-1 space-y-5 text-left">
          {/* Hero Amount & Description Card */}
          <div className="p-6 rounded-[24px] bg-[#F8FAFC] border border-slate-200 text-center space-y-2">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white border border-slate-200 text-[12px] font-bold text-[#0F172A] shadow-2xs">
              <span>{categoryInfo.icon}</span>
              <span>{categoryInfo.name}</span>
            </div>

            <div className="text-[36px] sm:text-[40px] font-extrabold text-[#00875A] font-tabular tracking-tight">
              {expense.amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
            </div>

            <h4 className="text-[18px] font-bold text-[#0F172A]">
              {expense.description}
            </h4>

            <div className="flex items-center justify-center gap-2 text-[12px] text-[#64748B] pt-1 font-medium">
              <Calendar className="w-3.5 h-3.5" />
              <span>{expense.date || 'Bugün'}</span>
              <span>•</span>
              <span>{isPayerMe ? 'Sen ödedin' : `${payer?.fullName || 'Arkadaş'} ödedi`}</span>
            </div>
          </div>

          {/* Bölüşüm Tablosu (Splits Breakdown) */}
          <div className="space-y-2">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block px-1">
              MASRAF BÖLÜŞÜMÜ ({expense.splits.length} KİŞİ)
            </span>

            <div className="space-y-2">
              {expense.splits.map((split) => {
                const isMe = split.userId === currentUser.id;
                const splitUser = isMe ? currentUser : users.find((u) => u.id === split.userId);
                const isThisPayer = split.userId === expense.paidBy;

                return (
                  <div
                    key={split.id}
                    className="p-3.5 rounded-[16px] bg-[#F8FAFC] border border-slate-200 flex items-center justify-between"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-9 h-9 rounded-[12px] bg-white border border-slate-200 text-[#0F172A] font-extrabold text-[12px] flex items-center justify-center">
                        {splitUser?.fullName.slice(0, 2).toUpperCase() || 'U'}
                      </div>
                      <div>
                        <div className="text-[14px] font-bold text-[#0F172A]">
                          {isMe ? 'Sen' : splitUser?.fullName || 'Katılımcı'}
                        </div>
                        <div className="text-[11px] text-[#64748B]">
                          {splitUser?.tag || `@${splitUser?.username || 'user'}`}
                        </div>
                      </div>
                    </div>

                    <div className="text-right">
                      <span className="text-[14px] font-extrabold text-[#00875A] font-tabular block">
                        {split.amountOwed.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
                      </span>
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
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block px-1">
              İŞLEM ZAMAN TÜNELİ
            </span>

            <div className="p-4 rounded-[18px] bg-[#F8FAFC] border border-slate-200 space-y-3">
              <div className="flex items-start gap-3 text-[13px]">
                <div className="w-5 h-5 rounded-full bg-emerald-100 text-[#00875A] flex items-center justify-center flex-shrink-0 mt-0.5">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                </div>
                <div>
                  <div className="font-bold text-[#0F172A]">Harcama Kaydedildi</div>
                  <div className="text-[11px] text-[#64748B]">
                    {payer?.fullName} tarafından AradaPay'e eklendi.
                  </div>
                </div>
              </div>

              <div className="flex items-start gap-3 text-[13px]">
                <div className="w-5 h-5 rounded-full bg-emerald-100 text-[#00875A] flex items-center justify-center flex-shrink-0 mt-0.5">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                </div>
                <div>
                  <div className="font-bold text-[#0F172A]">Bölüşüm Hesaplandı & Mühürlendi</div>
                  <div className="text-[11px] text-[#64748B]">
                    SHA-256 Merkle Ağacı kriptografik özeti oluşturuldu.
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-4 bg-white border-t border-slate-100 flex items-center gap-3 pb-[max(env(safe-area-inset-bottom),16px)]">
          {!isPayerMe ? (
            <button
              onClick={() => {
                onClose();
                onOpenSettleUp(expense);
              }}
              className="flex-1 h-12 rounded-[14px] bg-[#00875A] hover:bg-[#00744d] text-white font-bold text-[14px] flex items-center justify-center gap-2 active:scale-[0.98] transition shadow-sm shadow-emerald-800/20"
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
              className="flex-1 h-12 rounded-[14px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] font-bold text-[14px] flex items-center justify-center gap-2 active:scale-[0.98] transition"
            >
              <Send className="w-4 h-4 text-[#00875A]" />
              <span>Katılımcılara Dürtme Gönder</span>
            </button>
          )}

          <button
            onClick={() => onOpenReceipt(expense)}
            className="px-4 h-12 rounded-[14px] bg-white border border-slate-200 text-[#0F172A] font-bold text-[13px] flex items-center justify-center gap-1.5 hover:bg-slate-50 active:scale-[0.98] transition"
          >
            <Receipt className="w-4 h-4 text-[#00875A]" />
            <span className="hidden sm:inline">Dekont</span>
          </button>
        </div>
      </div>
    </div>
  );
};
