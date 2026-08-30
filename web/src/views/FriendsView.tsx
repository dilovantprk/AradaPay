'use client';

import React, { useState } from 'react';
import { UserPlus, Send, CreditCard, Check, Search, X, CheckCircle2, ChevronRight } from 'lucide-react';
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
  const [showAddModal, setShowAddModal] = useState(false);
  const friends = users.filter((u) => u.id !== currentUser.id);

  // Compute bilateral balance with each friend
  const getBalanceWithFriend = (friendId: string) => {
    let balance = 0;
    expenses.forEach((exp) => {
      if (exp.paidBy === currentUser.id) {
        const split = exp.splits.find((s) => s.userId === friendId);
        if (split) balance += split.amountOwed;
      } else if (exp.paidBy === friendId) {
        const split = exp.splits.find((s) => s.userId === currentUser.id);
        if (split) balance -= split.amountOwed;
      }
    });

    settlements.forEach((set) => {
      if (set.payerId === currentUser.id && set.receiverId === friendId) {
        balance += set.amount;
      } else if (set.payerId === friendId && set.receiverId === currentUser.id) {
        balance -= set.amount;
      }
    });

    return balance;
  };

  return (
    <div className="space-y-4 text-left animate-fadeIn">
      {/* Header */}
      <div className="flex items-center justify-between px-1">
        <div>
          <h2 className="text-[26px] font-bold text-[#1C1C1E] tracking-tight">Arkadaşlar</h2>
          <p className="text-[13px] text-[#8E8E93]">Bireysel borç ve alacak takibi</p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2 rounded-full bg-[#00875A] text-white text-[13px] font-bold flex items-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-800/20"
        >
          <UserPlus className="w-4 h-4 stroke-[2.5]" />
          <span>Arkadaş Ekle</span>
        </button>
      </div>

      {/* Friends List */}
      <div className="space-y-3">
        {friends.length === 0 ? (
          <div className="apple-card p-8 text-center space-y-3">
            <p className="text-[14px] text-[#8E8E93]">Henüz eklenmiş arkadaşın yok.</p>
            <button
              onClick={() => setShowAddModal(true)}
              className="px-4 py-2 rounded-full bg-[#00875A] text-white text-[13px] font-bold inline-flex items-center gap-1.5"
            >
              <UserPlus className="w-4 h-4" />
              <span>İlk Arkadaşını Ekle</span>
            </button>
          </div>
        ) : (
          friends.map((friend) => {
            const balance = getBalanceWithFriend(friend.id);
            const isPositive = balance >= 0;
            const hasBalance = Math.abs(balance) > 0.01;

            return (
              <div
                key={friend.id}
                onClick={() => onSelectFriend(friend)}
                className="apple-card p-4 hover:border-black/[0.1] active:scale-[0.99] cursor-pointer transition space-y-3"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-full bg-emerald-100 text-[#00875A] border border-emerald-300 flex items-center justify-center font-extrabold text-[15px] shadow-2xs">
                      {friend.fullName.substring(0, 2).toUpperCase()}
                    </div>
                    <div>
                      <h3 className="text-[16px] font-bold text-[#1C1C1E]">{friend.fullName}</h3>
                      <p className="text-[12px] font-mono text-[#8E8E93]">{friend.tag || '@' + friend.username}</p>
                    </div>
                  </div>

                  <div className="text-right">
                    <span
                      className={`text-[11px] font-semibold block ${
                        isPositive ? 'text-[#00875A]' : 'text-[#D32F2F]'
                      }`}
                    >
                      {!hasBalance
                        ? 'fitleşildi (0 ₺)'
                        : isPositive
                        ? 'sana borçlu'
                        : 'sen borçlusun'}
                    </span>
                    <span
                      className={`text-[16px] font-black font-tabular block ${
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
                </div>

                {/* Action Buttons Row */}
                <div className="flex items-center gap-2 pt-2 border-t border-black/[0.04]">
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      onOpenNudgeWithUser(friend);
                    }}
                    className="flex-1 py-2 rounded-[12px] bg-black/5 hover:bg-black/10 text-[#1C1C1E] text-[12px] font-bold flex items-center justify-center gap-1.5 active:scale-95 transition"
                  >
                    <Send className="w-3.5 h-3.5 text-[#8E8E93]" />
                    <span>Dürt</span>
                  </button>

                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      onOpenSettleWithUser(friend, Math.abs(balance));
                    }}
                    className="flex-1 py-2 rounded-[12px] bg-[#00875A] text-white text-[12px] font-bold flex items-center justify-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-2xs"
                  >
                    <CreditCard className="w-3.5 h-3.5" />
                    <span>Fitleş</span>
                  </button>
                </div>
              </div>
            );
          })
        )}
      </div>

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
