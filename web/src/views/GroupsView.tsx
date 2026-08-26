import React, { useState } from 'react';
import { Plus, Users, ArrowRight, UserPlus, ChevronRight, X, Check } from 'lucide-react';
import { Group, User, GroupExpenseItem } from '../types';

interface GroupsViewProps {
  groups: Group[];
  currentUser: User;
  users: User[];
  isLocked: boolean;
  onAddExpenseClick: () => void;
  onSaveGroup: (group: Group) => void;
}

export const GroupsView: React.FC<GroupsViewProps> = ({
  groups,
  currentUser,
  users,
  isLocked,
  onAddExpenseClick,
  onSaveGroup
}) => {
  const [selectedGroup, setSelectedGroup] = useState<Group | null>(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newGroupName, setNewGroupName] = useState('');
  const [newGroupEmoji, setNewGroupEmoji] = useState('🏠');
  const [newGroupCategory, setNewGroupCategory] = useState('Ev & Yaşam');
  const [selectedMemberIds, setSelectedMemberIds] = useState<string[]>([currentUser.id]);

  const handleCreateGroup = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newGroupName.trim()) return;

    const groupMembers = selectedMemberIds.map((id) => {
      const u = users.find((user) => user.id === id);
      return {
        id,
        name: u?.fullName || id,
        avatar: u?.fullName.substring(0, 2).toUpperCase() || 'AP',
        tag: u?.tag || '',
        balanceInGroup: 0.0
      };
    });

    const newGroup: Group = {
      id: `group_${Date.now()}`,
      name: newGroupName.trim(),
      emoji: newGroupEmoji,
      category: newGroupCategory,
      createdBy: currentUser.id,
      createdAt: new Date().toISOString(),
      userBalance: 0.0,
      totalExpenses: 0.0,
      members: groupMembers
    };

    onSaveGroup(newGroup);
    setShowCreateModal(false);
    setNewGroupName('');
    setSelectedMemberIds([currentUser.id]);
  };

  return (
    <div className="pb-24 max-w-2xl mx-auto px-5 py-4">
      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <div>
          <h2 className="text-[24px] font-bold text-textPrimary">Gruplar</h2>
          <p className="text-[12px] text-textSecondary">Ortak ev, tatil ve etkinlik harcamaları</p>
        </div>

        <button
          onClick={() => setShowCreateModal(true)}
          className="px-3.5 py-2 rounded-[12px] bg-primaryEmerald text-white text-[13px] font-bold flex items-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Grup Kur</span>
        </button>
      </div>

      {/* Groups List */}
      <div className="space-y-3">
        {groups.map((group) => {
          const isPositive = group.userBalance >= 0;
          return (
            <div
              key={group.id}
              onClick={() => setSelectedGroup(group)}
              className="p-4 rounded-[18px] bg-surfaceWhite border border-surfaceBorder hover:border-slate-300 active:scale-[0.99] cursor-pointer transition shadow-xs"
            >
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-[14px] bg-surfaceContainerLow flex items-center justify-center text-[22px]">
                    {group.emoji}
                  </div>
                  <div>
                    <h3 className="text-[16px] font-bold text-textPrimary">{group.name}</h3>
                    <p className="text-[12px] text-textSecondary">
                      {group.category} • {group.members.length} üye
                    </p>
                  </div>
                </div>

                <div className="text-right">
                  <span
                    className={`text-[11px] font-semibold block ${
                      isPositive ? 'text-primaryEmerald' : 'text-accentRose'
                    }`}
                  >
                    {isPositive ? 'grupta alacaklısın' : 'grupta borçlusun'}
                  </span>
                  <span
                    className={`text-[15px] font-bold block ${
                      isPositive ? 'text-primaryEmerald' : 'text-accentRose'
                    }`}
                  >
                    {isLocked
                      ? '•••• ₺'
                      : `${isPositive ? '+' : ''}${group.userBalance.toFixed(2)} ₺`}
                  </span>
                </div>
              </div>

              {/* Members Avatar Row */}
              <div className="flex items-center justify-between mt-3 pt-3 border-t border-surfaceBorder">
                <div className="flex items-center -space-x-2">
                  {group.members.slice(0, 5).map((m) => (
                    <div
                      key={m.id}
                      className="w-7 h-7 rounded-full bg-slate-200 border-2 border-white flex items-center justify-center text-[10px] font-bold text-textPrimary"
                      title={m.name}
                    >
                      {m.avatar}
                    </div>
                  ))}
                  {group.members.length > 5 && (
                    <div className="w-7 h-7 rounded-full bg-slate-100 border-2 border-white flex items-center justify-center text-[10px] font-bold text-slate-500">
                      +{group.members.length - 5}
                    </div>
                  )}
                </div>

                <span className="text-[12px] text-primaryEmerald font-semibold flex items-center gap-1">
                  <span>Detaylar</span>
                  <ChevronRight className="w-3.5 h-3.5" />
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Group Detail Modal */}
      {selectedGroup && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
          <div className="bg-surfaceWhite w-full max-w-lg rounded-[24px] shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
            <div className="px-6 py-4 border-b border-surfaceBorder flex items-center justify-between">
              <div className="flex items-center gap-3">
                <span className="text-[24px]">{selectedGroup.emoji}</span>
                <div>
                  <h3 className="text-[18px] font-bold text-textPrimary">{selectedGroup.name}</h3>
                  <p className="text-[12px] text-textSecondary">{selectedGroup.category}</p>
                </div>
              </div>
              <button
                onClick={() => setSelectedGroup(null)}
                className="w-8 h-8 rounded-full bg-surfaceContainerLow flex items-center justify-center text-textSecondary hover:bg-slate-200"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="p-6 overflow-y-auto flex-1 space-y-5">
              {/* Member Balances */}
              <div>
                <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-2">
                  ÜYE BAKİYE DURUMLARI
                </label>
                <div className="space-y-2">
                  {selectedGroup.members.map((m) => {
                    const isPos = m.balanceInGroup >= 0;
                    return (
                      <div
                        key={m.id}
                        className="p-3 rounded-[14px] bg-surfaceContainerLow/60 border border-slate-200 flex items-center justify-between"
                      >
                        <div className="flex items-center gap-3">
                          <span className="w-8 h-8 rounded-full bg-primaryEmeraldContainer text-primaryEmerald font-bold text-[12px] flex items-center justify-center">
                            {m.avatar}
                          </span>
                          <div>
                            <p className="text-[14px] font-semibold text-textPrimary">{m.name}</p>
                            <p className="text-[11px] text-textSecondary">{m.tag}</p>
                          </div>
                        </div>

                        <span
                          className={`text-[14px] font-bold ${
                            isPos ? 'text-primaryEmerald' : 'text-accentRose'
                          }`}
                        >
                          {isPos ? '+' : ''}
                          {m.balanceInGroup.toFixed(2)} ₺
                        </span>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>

            <div className="p-4 border-t border-surfaceBorder bg-surfaceWhite">
              <button
                onClick={() => {
                  setSelectedGroup(null);
                  onAddExpenseClick();
                }}
                className="w-full h-[50px] rounded-[16px] bg-primaryEmerald text-white font-bold text-[14px] flex items-center justify-center gap-2"
              >
                <Plus className="w-4 h-4" />
                <span>Bu Gruba Harcama Ekle</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Create Group Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
          <div className="bg-surfaceWhite w-full max-w-md rounded-[24px] shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
            <div className="px-6 py-4 border-b border-surfaceBorder flex items-center justify-between">
              <h3 className="text-[18px] font-bold text-textPrimary">Yeni Grup Oluştur</h3>
              <button
                onClick={() => setShowCreateModal(false)}
                className="w-8 h-8 rounded-full bg-surfaceContainerLow flex items-center justify-center text-textSecondary"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleCreateGroup} className="p-6 overflow-y-auto flex-1 space-y-4">
              <div>
                <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-1.5">
                  GRUP ADI
                </label>
                <input
                  type="text"
                  placeholder="Örn: Kadıköy Evi, Kaş Tatili..."
                  value={newGroupName}
                  onChange={(e) => setNewGroupName(e.target.value)}
                  className="w-full px-4 py-3 rounded-[14px] border border-slate-200 text-textPrimary text-[14px] outline-none focus:border-primaryEmerald"
                  autoFocus
                />
              </div>

              <div>
                <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-1.5">
                  EMOJI & KATEGORİ
                </label>
                <div className="flex items-center gap-2">
                  {['🏠', '🌊', '✈️', '🍽️', '🎮', '🚗'].map((em) => (
                    <button
                      key={em}
                      type="button"
                      onClick={() => setNewGroupEmoji(em)}
                      className={`w-10 h-10 rounded-xl text-[20px] flex items-center justify-center border transition ${
                        newGroupEmoji === em
                          ? 'border-primaryEmerald bg-primaryEmeraldContainer ring-1 ring-primaryEmerald'
                          : 'border-slate-200 hover:bg-slate-50'
                      }`}
                    >
                      {em}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-1.5">
                  ÜYELER ({selectedMemberIds.length} kişi)
                </label>
                <div className="space-y-1.5 max-h-40 overflow-y-auto">
                  {users.map((u) => {
                    const isSelected = selectedMemberIds.includes(u.id);
                    return (
                      <button
                        key={u.id}
                        type="button"
                        onClick={() => {
                          if (isSelected) {
                            if (selectedMemberIds.length > 1) {
                              setSelectedMemberIds(selectedMemberIds.filter((id) => id !== u.id));
                            }
                          } else {
                            setSelectedMemberIds([...selectedMemberIds, u.id]);
                          }
                        }}
                        className={`w-full p-2.5 rounded-xl border flex items-center justify-between text-left transition ${
                          isSelected ? 'bg-primaryEmeraldContainer/40 border-primaryEmerald' : 'border-slate-200'
                        }`}
                      >
                        <span className="text-[13px] font-semibold text-textPrimary">
                          {u.fullName} {u.id === currentUser.id ? '(Sen)' : ''}
                        </span>
                        {isSelected && <Check className="w-4 h-4 text-primaryEmerald" />}
                      </button>
                    );
                  })}
                </div>
              </div>

              <div className="pt-2">
                <button
                  type="submit"
                  disabled={!newGroupName.trim()}
                  className="w-full h-[50px] rounded-[16px] bg-primaryEmerald text-white font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-[#00744d] transition shadow-sm disabled:bg-slate-200 disabled:text-slate-400"
                >
                  <Plus className="w-4 h-4" />
                  <span>Grubu Oluştur</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
