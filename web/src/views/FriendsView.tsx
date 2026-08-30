'use client';

import React, { useState, useMemo } from 'react';
import { UserPlus, Send, CreditCard, Check, Search, X, CheckCircle2, ChevronRight, Phone, Users as UsersIcon, ArrowDownLeft, ArrowUpRight } from 'lucide-react';
import { User, Expense, Settlement, Group } from '../types';
import { AddFriendModal } from '../components/AddFriendModal';

interface FriendsViewProps {
  currentUser: User;
  users: User[];
  expenses: Expense[];
  settlements: Settlement[];
  groups: Group[];
  isLocked: boolean;
  onSelectFriend: (friend: User) => void;
  onOpenSettleWithUser: (user: User, amount?: number) => void;
  onOpenNudgeWithUser: (user: User) => void;
  onAddFriend: (user: User) => void;
}

export const FriendsView: React.FC<FriendsViewProps> = ({
  currentUser,
  users,
  expenses,
  settlements,
  groups,
  isLocked,
  onSelectFriend,
  onOpenSettleWithUser,
  onOpenNudgeWithUser,
  onAddFriend
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [balanceFilter, setBalanceFilter] = useState<'all' | 'positive' | 'negative' | 'zero'>('all');
  const [showAddModal, setShowAddModal] = useState(false);

  const friends = useMemo(() => {
    return users.filter((u) => u.id !== currentUser.id);
  }, [users, currentUser.id]);

  const getBalanceWithFriend = (friendId: string) => {
    let bal = 0;

    expenses.forEach((exp) => {
      if (exp.paidBy === currentUser.id) {
        const split = exp.splits.find((s) => s.userId === friendId);
        if (split) bal += split.amountOwed;
      } else if (exp.paidBy === friendId) {
        const split = exp.splits.find((s) => s.userId === currentUser.id);
        if (split) bal -= split.amountOwed;
      }
    });

    settlements.forEach((set) => {
      if (set.payerId === currentUser.id && set.receiverId === friendId) {
        bal += set.amount;
      } else if (set.payerId === friendId && set.receiverId === currentUser.id) {
        bal -= set.amount;
      }
    });

    return bal;
  };

  const filteredFriends = useMemo(() => {
    return friends.filter((f) => {
      const matchesSearch =
        f.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (f.tag && f.tag.toLowerCase().includes(searchQuery.toLowerCase())) ||
        (f.phone && f.phone.includes(searchQuery));

      const bal = getBalanceWithFriend(f.id);
      let matchesFilter = true;
      if (balanceFilter === 'positive') matchesFilter = bal > 0.01;
      else if (balanceFilter === 'negative') matchesFilter = bal < -0.01;
      else if (balanceFilter === 'zero') matchesFilter = Math.abs(bal) <= 0.01;

      return matchesSearch && matchesFilter;
    });
  }, [friends, searchQuery, balanceFilter, expenses, settlements]);

  return (
    <div className="space-y-5 text-left animate-fadeIn">
      {/* Header */}
      <div className="flex items-center justify-between px-1">
        <div>
          <h2 className="text-[28px] font-extrabold text-[#0F172A] tracking-tight">Arkadaşlar</h2>
          <p className="text-[13px] text-[#64748B]">Bireysel borç ve alacak takibi, FAST transferleri</p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2 rounded-[12px] bg-[#00875A] text-white text-[13px] font-bold flex items-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-900/10"
        >
          <UserPlus className="w-4 h-4 stroke-[2.5]" />
          <span>Arkadaş Ekle</span>
        </button>
      </div>

      {/* Search & Filter Bar */}
      <div className="space-y-3">
        {/* Search */}
        <div className="relative">
          <Search className="w-4 h-4 text-[#94A3B8] absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Arkadaş ara (Ad, @tag veya telefon)..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full h-11 pl-10 pr-4 rounded-[14px] bg-white border border-slate-200 text-[13px] font-medium text-[#0F172A] focus:outline-none focus:border-[#00875A]"
          />
        </div>

        {/* Filter Pills with Icons */}
        <div className="flex items-center gap-2 overflow-x-auto pb-1 [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
          <button
            onClick={() => setBalanceFilter('all')}
            className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
              balanceFilter === 'all'
                ? 'bg-[#00875A] text-white shadow-2xs'
                : 'bg-white border border-slate-200 text-[#0F172A] hover:bg-slate-50'
            }`}
          >
            <UsersIcon className="w-3.5 h-3.5" />
            <span>Tümü ({friends.length})</span>
          </button>
          <button
            onClick={() => setBalanceFilter('positive')}
            className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
              balanceFilter === 'positive'
                ? 'bg-[#00875A] text-white shadow-2xs'
                : 'bg-white border border-slate-200 text-[#00875A] hover:bg-slate-50'
            }`}
          >
            <ArrowDownLeft className="w-3.5 h-3.5" />
            <span>Alacaklı Olduklarım (+₺)</span>
          </button>
          <button
            onClick={() => setBalanceFilter('negative')}
            className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
              balanceFilter === 'negative'
                ? 'bg-[#D32F2F] text-white shadow-2xs'
                : 'bg-white border border-slate-200 text-[#D32F2F] hover:bg-slate-50'
            }`}
          >
            <ArrowUpRight className="w-3.5 h-3.5" />
            <span>Borçlu Olduklarım (-₺)</span>
          </button>
          <button
            onClick={() => setBalanceFilter('zero')}
            className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
              balanceFilter === 'zero'
                ? 'bg-[#00875A] text-white shadow-2xs'
                : 'bg-white border border-slate-200 text-[#64748B] hover:bg-slate-50'
            }`}
          >
            <CheckCircle2 className="w-3.5 h-3.5" />
            <span>Fitleşilenler (0₺)</span>
          </button>
        </div>
      </div>

      {/* Friends List (Unified Grouped Stream - NO card-in-card!) */}
      {filteredFriends.length === 0 ? (
        <div className="bg-white rounded-[20px] border border-slate-200/80 p-10 text-center space-y-3 shadow-sm">
          <p className="text-[14px] text-[#64748B]">Aranan kritere uygun arkadaş bulunamadı.</p>
          <button
            onClick={() => setShowAddModal(true)}
            className="px-4 py-2 rounded-[12px] bg-[#00875A] text-white text-[13px] font-bold inline-flex items-center gap-1.5"
          >
            <UserPlus className="w-4 h-4" />
            <span>Yeni Arkadaş Ekle</span>
          </button>
        </div>
      ) : (
        <div className="bg-white rounded-[20px] border border-slate-200/80 divide-y divide-slate-100 overflow-hidden shadow-sm">
          {filteredFriends.map((friend) => {
            const balance = getBalanceWithFriend(friend.id);
            const isPositive = balance >= 0;
            const hasBalance = Math.abs(balance) > 0.01;

            return (
              <div
                key={friend.id}
                onClick={() => onSelectFriend(friend)}
                className="p-4 hover:bg-slate-50 active:bg-slate-100 cursor-pointer transition flex items-center justify-between"
              >
                <div className="flex items-center gap-3.5 min-w-0">
                  <div className="w-10 h-10 rounded-full bg-emerald-50 text-[#00875A] border border-emerald-200 flex items-center justify-center font-extrabold text-[14px] flex-shrink-0">
                    {friend.fullName.substring(0, 2).toUpperCase()}
                  </div>
                  <div className="min-w-0">
                    <h3 className="text-[14px] font-bold text-[#0F172A] truncate">{friend.fullName}</h3>
                    <p className="text-[12px] font-mono text-[#64748B] truncate">{friend.tag || '@' + friend.username}</p>
                  </div>
                </div>

                <div className="flex items-center gap-3 flex-shrink-0 ml-3">
                  <div className="text-right">
                    <span
                      className={`text-[10px] font-semibold block uppercase tracking-wider ${
                        isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                      }`}
                    >
                      {!hasBalance
                        ? 'fitleşildi'
                        : isPositive
                        ? 'sana borçlu'
                        : 'sen borçlusun'}
                    </span>
                    <span
                      className={`text-[15px] font-black font-tabular block ${
                        isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                      }`}
                    >
                      {isLocked
                        ? '•••• ₺'
                        : `${isPositive ? '+' : ''}${balance.toLocaleString('tr-TR', {
                            minimumFractionDigits: 2,
                            maximumFractionDigits: 2
                          })} ₺`}
                    </span>
                  </div>

                  <div className="flex items-center gap-1.5">
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        onOpenNudgeWithUser(friend);
                      }}
                      className="px-2.5 py-1.5 rounded-[10px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] text-[11px] font-bold flex items-center gap-1 active:scale-95 transition"
                    >
                      <Send className="w-3 h-3 text-[#64748B]" />
                      <span>Dürt</span>
                    </button>

                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        onOpenSettleWithUser(friend, Math.abs(balance));
                      }}
                      className="px-2.5 py-1.5 rounded-[10px] bg-[#00875A] text-white text-[11px] font-bold flex items-center gap-1 hover:bg-[#00744d] active:scale-95 transition"
                    >
                      <CreditCard className="w-3 h-3" />
                      <span>Fitleş</span>
                    </button>
                  </div>

                  <ChevronRight className="w-4 h-4 text-[#94A3B8]" />
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* 1:1 Android AddFriendModal */}
      <AddFriendModal
        isOpen={showAddModal}
        onClose={() => setShowAddModal(false)}
        existingFriends={users}
        onFriendAdded={(user) => {
          onAddFriend(user);
        }}
      />
    </div>
  );
};
