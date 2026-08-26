import React, { useState } from 'react';
import { UserPlus, Send, CreditCard, Check, Search, X, CheckCircle2 } from 'lucide-react';
import { User, Expense, Settlement } from '../types';

interface FriendsViewProps {
  currentUser: User;
  users: User[];
  expenses: Expense[];
  settlements: Settlement[];
  isLocked: boolean;
  onOpenSettleWithUser: (user: User) => void;
  onOpenNudgeWithUser: (user: User) => void;
  onAddFriend: (tag: string) => void;
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
  const [searchTag, setSearchTag] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);
  const [addedSuccess, setAddedSuccess] = useState(false);

  const friends = users.filter((u) => u.id !== currentUser.id);

  const handleAddFriendSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchTag.trim()) return;
    onAddFriend(searchTag.trim());
    setAddedSuccess(true);
    setTimeout(() => {
      setAddedSuccess(false);
      setShowAddModal(false);
      setSearchTag('');
    }, 1200);
  };

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
        {friends.map((friend) => {
          const balance = getBalanceWithFriend(friend.id);
          const isPositive = balance >= 0;
          const hasBalance = Math.abs(balance) > 0.01;

          return (
            <div
              key={friend.id}
              className="p-4 rounded-[18px] bg-surfaceWhite border border-surfaceBorder shadow-xs space-y-3"
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
        })}
      </div>

      {/* Add Friend Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
          <div className="bg-surfaceWhite w-full max-w-md rounded-[24px] shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
            <div className="px-6 py-4 border-b border-surfaceBorder flex items-center justify-between">
              <h3 className="text-[18px] font-bold text-textPrimary">Arkadaş Ekle</h3>
              <button
                onClick={() => setShowAddModal(false)}
                className="w-8 h-8 rounded-full bg-surfaceContainerLow flex items-center justify-center text-textSecondary"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleAddFriendSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-1.5">
                  ARADAPAY ETİKETİ VEYA KULLANICI ADI
                </label>
                <div className="relative">
                  <input
                    type="text"
                    placeholder="Örn: Kaan#5674 veya zeynep"
                    value={searchTag}
                    onChange={(e) => setSearchTag(e.target.value)}
                    className="w-full px-4 py-3 rounded-[14px] border border-slate-200 text-textPrimary text-[14px] outline-none focus:border-primaryEmerald pl-10"
                    autoFocus
                  />
                  <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
                </div>
                <p className="text-[11px] text-textSecondary mt-1">
                  Arkadaşınızın profilindeki etiket kodunu girerek anında ekleyin.
                </p>
              </div>

              <div className="pt-2">
                <button
                  type="submit"
                  disabled={!searchTag.trim() || addedSuccess}
                  className={`w-full h-[50px] rounded-[16px] font-bold text-[14px] flex items-center justify-center gap-2 transition ${
                    addedSuccess
                      ? 'bg-primaryEmerald text-white'
                      : searchTag.trim()
                      ? 'bg-primaryEmerald text-white hover:bg-[#00744d]'
                      : 'bg-slate-200 text-slate-400 cursor-not-allowed'
                  }`}
                >
                  {addedSuccess ? (
                    <>
                      <CheckCircle2 className="w-5 h-5" />
                      <span>Arkadaş Eklendi!</span>
                    </>
                  ) : (
                    <>
                      <UserPlus className="w-4 h-4" />
                      <span>Arkadaş Olarak Ekle</span>
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
