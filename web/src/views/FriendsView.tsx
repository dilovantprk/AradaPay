'use client';

import React, { useState } from 'react';
import { UserPlus, Send, CreditCard, Check, Search, X, CheckCircle2 } from 'lucide-react';
import { User, Expense, Settlement } from '../types';
import { AddFriendModal } from '../components/AddFriendModal';

interface FriendsViewProps {
  currentUser: User;
  users: User[];
  expenses: Expense[];
  settlements: Settlement[];
  isLocked: boolean;
  onOpenSettleWithUser: (user: User) => void;
  onOpenNudgeWithUser: (user: User) => void;
  onAddFriend: (user: User) => void;
}

export const FriendsView: React.FC<FriendsViewProps> = ({
  currentUser,
  users,
  expenses,
  settlements,
  isLocked,
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
    <div className="pb-24 max-w-2xl mx-auto px-5 py-4">
      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <div>
          <h2 className="text-[24px] font-bold text-textPrimary">Arkadaşlar</h2>
          <p className="text-[12px] text-textSecondary">Bireysel borç & alacak takibi</p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="px-3.5 py-2 rounded-[12px] bg-primaryEmerald text-white text-[13px] font-bold flex items-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm"
        >
          <UserPlus className="w-4 h-4 stroke-[2.5]" />
          <span>Arkadaş Ekle</span>
        </button>
      </div>

      {/* Friends List */}
      <div className="space-y-3">
        {friends.length === 0 ? (
          <div className="p-8 text-center bg-white rounded-[22px] border border-slate-200 space-y-3">
            <p className="text-[14px] text-slate-500">Henüz eklenmiş arkadaşın yok.</p>
            <button
              onClick={() => setShowAddModal(true)}
              className="px-4 py-2 rounded-[12px] bg-primaryEmerald text-white text-[13px] font-bold inline-flex items-center gap-1.5"
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
                className="p-4 rounded-[18px] bg-white border border-surfaceBorder shadow-xs space-y-3"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-[14px] bg-surfaceContainerLow flex items-center justify-center font-bold text-[15px] text-textPrimary">
                      {friend.fullName.substring(0, 2).toUpperCase()}
                    </div>
                    <div>
                      <h3 className="text-[16px] font-bold text-textPrimary">{friend.fullName}</h3>
                      <p className="text-[12px] font-mono text-textSecondary">{friend.tag || '@' + friend.username}</p>
                    </div>
                  </div>

                  <div className="text-right">
                    <span
                      className={`text-[11px] font-semibold block ${
                        isPositive ? 'text-primaryEmerald' : 'text-accentRose'
                      }`}
                    >
                      {!hasBalance
                        ? 'fitleşildi (0 ₺)'
                        : isPositive
                        ? 'sana borçlu'
                        : 'sen borçlusun'}
                    </span>
                    <span
                      className={`text-[16px] font-bold block ${
                        isPositive ? 'text-primaryEmerald' : 'text-accentRose'
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
                <div className="flex items-center gap-2 pt-2 border-t border-surfaceBorder">
                  <button
                    onClick={() => onOpenNudgeWithUser(friend)}
                    className="flex-1 py-2 rounded-[12px] bg-surfaceContainerLow text-textPrimary text-[12px] font-bold flex items-center justify-center gap-1.5 hover:bg-slate-200 active:scale-95 transition"
                  >
                    <Send className="w-3.5 h-3.5 text-textSecondary" />
                    <span>Dürt / Hatırlat</span>
                  </button>

                  <button
                    onClick={() => onOpenSettleWithUser(friend)}
                    className="flex-1 py-2 rounded-[12px] bg-primaryEmerald text-white text-[12px] font-bold flex items-center justify-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-2xs"
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
