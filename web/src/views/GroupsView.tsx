'use client';

import React, { useState } from 'react';
import { Plus, Users, ArrowRight, ArrowLeft, UserPlus, ChevronRight, X, Check, Sparkles } from 'lucide-react';
import { Group, User, Expense } from '../types';
import { GroupDetailModal } from '../components/GroupDetailModal';

interface GroupsViewProps {
  groups: Group[];
  currentUser: User;
  users: User[];
  expenses: Expense[];
  isLocked: boolean;
  onAddExpenseClick: (group?: Group) => void;
  onSaveGroup: (group: Group) => void;
  onOpenSettleUp: (targetUser: User, amount?: number) => void;
  onViewExpenseDetail: (expense: Expense) => void;
}

export const GroupsView: React.FC<GroupsViewProps> = ({
  groups,
  currentUser,
  users,
  expenses,
  isLocked,
  onAddExpenseClick,
  onSaveGroup,
  onOpenSettleUp,
  onViewExpenseDetail
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

  const handleAddMemberToGroup = (groupId: string, newMember: User) => {
    const targetGroup = groups.find((g) => g.id === groupId);
    if (!targetGroup) return;

    const updatedGroup: Group = {
      ...targetGroup,
      members: [
        ...targetGroup.members,
        {
          id: newMember.id,
          name: newMember.fullName,
          avatar: newMember.fullName.slice(0, 2).toUpperCase(),
          tag: newMember.tag || '',
          balanceInGroup: 0
        }
      ]
    };
    onSaveGroup(updatedGroup);
    setSelectedGroup(updatedGroup);
  };

  return (
    <div className="space-y-4 text-left">
      {/* Header */}
      <div className="flex items-center justify-between px-1">
        <div>
          <h2 className="text-[26px] font-bold text-[#1C1C1E] tracking-tight">Gruplar</h2>
          <p className="text-[13px] text-[#8E8E93]">Ortak ev, tatil ve etkinlik harcamaları</p>
        </div>

        <button
          onClick={() => setShowCreateModal(true)}
          className="px-4 py-2 rounded-full bg-[#00875A] text-white text-[13px] font-bold flex items-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-800/20"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Grup Kur</span>
        </button>
      </div>

      {/* Groups Inset List */}
      <div className="space-y-3">
        {groups.map((group) => {
          const groupExp = expenses.filter((e) => e.groupId === group.id);
          const totalSpend = groupExp.reduce((sum, e) => sum + e.amount, 0);

          return (
            <div
              key={group.id}
              onClick={() => setSelectedGroup(group)}
              className="apple-card p-5 hover:border-black/[0.1] active:scale-[0.99] cursor-pointer transition flex items-center justify-between"
            >
              <div className="flex items-center gap-3.5">
                <div className="w-13 h-13 rounded-[18px] bg-[#F2F2F7] border border-black/[0.04] flex items-center justify-center text-[26px] shadow-2xs">
                  {group.emoji}
                </div>
                <div>
                  <h3 className="text-[16px] font-bold text-[#1C1C1E]">{group.name}</h3>
                  <p className="text-[12px] text-[#8E8E93]">
                    {group.category || 'Genel'} • {group.members.length} üye • {totalSpend.toFixed(2)} ₺ harcama
                  </p>
                </div>
              </div>

              <div className="flex items-center gap-2">
                <div className="text-right">
                  <span className="text-[11px] font-semibold text-[#8E8E93] block">Grup Detayı</span>
                  <span className="text-[13px] font-bold text-[#00875A]">İncele ➔</span>
                </div>
                <ChevronRight className="w-4 h-4 text-[#C7C7CC]" />
              </div>
            </div>
          );
        })}
      </div>

      {/* Create Group Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
          <div className="bg-white w-full max-w-lg rounded-t-[32px] sm:rounded-[28px] shadow-apple-modal border border-black/[0.08] overflow-hidden flex flex-col max-h-[92vh] animate-appleSheet">
            <div className="w-12 h-1.5 bg-black/15 rounded-full mx-auto mt-3 sm:hidden" />

            <div className="px-5 py-3.5 border-b border-black/[0.06] flex items-center justify-between bg-white/80 backdrop-blur-md">
              <button
                onClick={() => setShowCreateModal(false)}
                className="w-9 h-9 rounded-full bg-black/5 flex items-center justify-center text-[#1C1C1E]"
              >
                <ArrowLeft className="w-4 h-4" />
              </button>
              <h3 className="text-[17px] font-bold text-[#1C1C1E]">Yeni Grup Kur</h3>
              <div className="w-9" />
            </div>

            <form onSubmit={handleCreateGroup} className="p-5 sm:p-6 overflow-y-auto flex-1 space-y-4">
              {/* Emoji & Name */}
              <div className="space-y-1.5">
                <label className="text-[11px] font-bold text-[#8E8E93] uppercase">GRUP ADI VE İKON</label>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    maxLength={2}
                    value={newGroupEmoji}
                    onChange={(e) => setNewGroupEmoji(e.target.value)}
                    className="w-14 h-12 text-center text-[22px] rounded-[14px] bg-[#F2F2F7] border border-black/[0.06] focus:outline-none"
                  />
                  <input
                    type="text"
                    required
                    value={newGroupName}
                    onChange={(e) => setNewGroupName(e.target.value)}
                    placeholder="örn: Kadıköy Evi, Kaş Tatili 2026"
                    className="flex-1 h-12 px-4 rounded-[14px] bg-[#F2F2F7] border border-black/[0.06] text-[14px] font-bold text-[#1C1C1E] focus:outline-none focus:border-[#00875A]"
                  />
                </div>
              </div>

              {/* Members Selection */}
              <div className="space-y-1.5">
                <label className="text-[11px] font-bold text-[#8E8E93] uppercase">
                  GRUP ÜYELERİNİ SEÇ ({selectedMemberIds.length})
                </label>
                <div className="apple-card divide-y divide-black/[0.04] max-h-48 overflow-y-auto">
                  {users.map((u) => {
                    const isSelected = selectedMemberIds.includes(u.id);
                    return (
                      <div
                        key={u.id}
                        onClick={() => {
                          if (isSelected) {
                            if (selectedMemberIds.length > 1) {
                              setSelectedMemberIds(selectedMemberIds.filter((id) => id !== u.id));
                            }
                          } else {
                            setSelectedMemberIds([...selectedMemberIds, u.id]);
                          }
                        }}
                        className="p-3 flex items-center justify-between cursor-pointer hover:bg-black/[0.02]"
                      >
                        <span className="text-[13px] font-bold text-[#1C1C1E]">
                          {u.id === currentUser.id ? `Ben (${u.fullName})` : u.fullName}
                        </span>
                        <div
                          className={`w-5 h-5 rounded-full flex items-center justify-center text-[11px] text-white ${
                            isSelected ? 'bg-[#00875A]' : 'border border-slate-300'
                          }`}
                        >
                          {isSelected && '✓'}
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>

              <button
                type="submit"
                className="w-full h-12 rounded-[16px] bg-[#00875A] text-white font-bold text-[14px] hover:bg-[#00744d] active:scale-[0.98] transition mt-4 shadow-sm"
              >
                Grubu Oluştur
              </button>
            </form>
          </div>
        </div>
      )}

      {/* 1:1 Android GroupDetailModal */}
      <GroupDetailModal
        isOpen={selectedGroup !== null}
        onClose={() => setSelectedGroup(null)}
        group={selectedGroup}
        currentUser={currentUser}
        users={users}
        expenses={expenses}
        isLocked={isLocked}
        onOpenAddExpenseInGroup={(g) => {
          setSelectedGroup(null);
          onAddExpenseClick(g);
        }}
        onOpenSettleUp={(targetUser, amount) => {
          setSelectedGroup(null);
          onOpenSettleUp(targetUser, amount);
        }}
        onViewExpenseDetail={(exp) => {
          setSelectedGroup(null);
          onViewExpenseDetail(exp);
        }}
        onAddMemberToGroup={handleAddMemberToGroup}
      />
    </div>
  );
};
