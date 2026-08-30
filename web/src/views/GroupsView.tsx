'use client';

import React, { useState, useMemo } from 'react';
import { Plus, Users, ArrowRight, ArrowLeft, UserPlus, ChevronRight, X, Check, Sparkles, Search, LayoutGrid, Home, Plane, Utensils, Film, Folder } from 'lucide-react';
import { Group, User, Expense } from '../types';

interface GroupsViewProps {
  groups: Group[];
  currentUser: User;
  users: User[];
  expenses: Expense[];
  isLocked: boolean;
  onSelectGroup: (group: Group) => void;
  onAddExpenseClick: (group?: Group) => void;
  onSaveGroup: (group: Group) => void;
}

const GROUP_CATEGORIES = ['Tümü', 'Ev & Yaşam', 'Tatil & Seyahat', 'Yemek & Kafe', 'Etkinlik & Festival', 'Genel'];

export const GroupsView: React.FC<GroupsViewProps> = ({
  groups,
  currentUser,
  users,
  expenses,
  isLocked,
  onSelectGroup,
  onAddExpenseClick,
  onSaveGroup
}) => {
  const [selectedCategory, setSelectedCategory] = useState('Tümü');
  const [searchQuery, setSearchQuery] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);

  // Form State
  const [newGroupName, setNewGroupName] = useState('');
  const [newGroupEmoji, setNewGroupEmoji] = useState('🏠');
  const [newGroupCategory, setNewGroupCategory] = useState('Ev & Yaşam');
  const [selectedMemberIds, setSelectedMemberIds] = useState<string[]>([currentUser.id]);

  const filteredGroups = useMemo(() => {
    return groups.filter((g) => {
      const matchesCat = selectedCategory === 'Tümü' || g.category === selectedCategory;
      const matchesSearch = g.name.toLowerCase().includes(searchQuery.toLowerCase());
      return matchesCat && matchesSearch;
    });
  }, [groups, selectedCategory, searchQuery]);

  const handleCreateGroup = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newGroupName.trim()) return;

    const newGroupMembers = selectedMemberIds.map((id) => {
      const u = users.find((user) => user.id === id) || currentUser;
      return {
        id: u.id,
        name: u.fullName,
        avatar: u.avatarUrl || '',
        tag: u.tag || '@' + u.username,
        balanceInGroup: 0
      };
    });

    const newGroup: Group = {
      id: `group_${Date.now()}`,
      name: newGroupName.trim(),
      emoji: newGroupEmoji || '👥',
      category: newGroupCategory,
      members: newGroupMembers,
      createdBy: currentUser.id,
      createdAt: new Date().toISOString(),
      userBalance: 0,
      totalExpenses: 0
    };

    onSaveGroup(newGroup);
    setShowCreateModal(false);
    setNewGroupName('');
    setNewGroupEmoji('🏠');
    setSelectedMemberIds([currentUser.id]);
  };

  return (
    <div className="space-y-5 text-left animate-fadeIn">
      {/* Header */}
      <div className="flex items-center justify-between px-1">
        <div>
          <h2 className="text-[28px] font-extrabold text-[#0F172A] tracking-tight">Gruplar</h2>
          <p className="text-[13px] text-[#64748B]">Ortak ev, tatil, yemek ve arkadaş harcamaları</p>
        </div>

        <button
          onClick={() => setShowCreateModal(true)}
          className="px-4 py-2 rounded-[12px] bg-[#00875A] text-white text-[13px] font-bold flex items-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-900/10"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Grup Kur</span>
        </button>
      </div>

      {/* Search & Category Filter Bar */}
      <div className="space-y-3">
        {/* Search */}
        <div className="relative">
          <Search className="w-4 h-4 text-[#94A3B8] absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Grup ara (örn: Kadıköy Evi)..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full h-11 pl-10 pr-4 rounded-[14px] bg-white border border-slate-200 text-[13px] font-medium text-[#0F172A] focus:outline-none focus:border-[#00875A]"
          />
        </div>

        {/* Category Pills with Icons */}
        <div className="flex items-center gap-2 overflow-x-auto pb-1 [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
          {GROUP_CATEGORIES.map((cat) => {
            const isSelected = selectedCategory === cat;
            const getIcon = () => {
              switch (cat) {
                case 'Ev & Yaşam':
                  return <Home className="w-3.5 h-3.5" />;
                case 'Tatil & Seyahat':
                  return <Plane className="w-3.5 h-3.5" />;
                case 'Yemek & Kafe':
                  return <Utensils className="w-3.5 h-3.5" />;
                case 'Etkinlik & Festival':
                  return <Film className="w-3.5 h-3.5" />;
                case 'Genel':
                  return <Folder className="w-3.5 h-3.5" />;
                default:
                  return <LayoutGrid className="w-3.5 h-3.5" />;
              }
            };

            return (
              <button
                key={cat}
                onClick={() => setSelectedCategory(cat)}
                className={`inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-[12px] font-bold flex-shrink-0 transition active:scale-95 ${
                  isSelected
                    ? 'bg-[#00875A] text-white shadow-2xs'
                    : 'bg-white border border-slate-200 text-[#0F172A] hover:bg-slate-50'
                }`}
              >
                {getIcon()}
                <span>{cat}</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Groups List (Unified Grouped Stream - NO card-in-card!) */}
      {filteredGroups.length === 0 ? (
        <div className="bg-white rounded-[20px] border border-slate-200/80 p-10 text-center space-y-3 shadow-sm">
          <p className="text-[14px] text-[#64748B]">Aranan kriterlere uygun grup bulunamadı.</p>
          <button
            onClick={() => setShowCreateModal(true)}
            className="px-4 py-2 rounded-[12px] bg-[#00875A] text-white text-[13px] font-bold inline-flex items-center gap-1.5"
          >
            <Plus className="w-4 h-4" />
            <span>Yeni Grup Kur</span>
          </button>
        </div>
      ) : (
        <div className="bg-white rounded-[20px] border border-slate-200/80 divide-y divide-slate-100 overflow-hidden shadow-sm">
          {filteredGroups.map((group) => {
            const groupExp = expenses.filter((e) => e.groupId === group.id);
            const totalSpend = groupExp.reduce((sum, e) => sum + e.amount, 0);

            // Compute user balance inside this group
            let myBal = 0;
            groupExp.forEach((e) => {
              if (e.paidBy === currentUser.id) {
                const mySp = e.splits.find((s) => s.userId === currentUser.id);
                myBal += e.amount - (mySp?.amountOwed || 0);
              } else {
                const mySp = e.splits.find((s) => s.userId === currentUser.id);
                if (mySp) myBal -= mySp.amountOwed;
              }
            });

            const isPos = myBal >= 0;

            return (
              <div
                key={group.id}
                onClick={() => onSelectGroup(group)}
                className="p-4 hover:bg-slate-50 active:bg-slate-100 cursor-pointer transition flex items-center justify-between"
              >
                <div className="flex items-center gap-3.5 min-w-0">
                  <div className="w-11 h-11 rounded-[14px] bg-slate-100 border border-slate-200/60 flex items-center justify-center text-[22px] shadow-2xs flex-shrink-0">
                    {group.emoji}
                  </div>
                  <div className="min-w-0">
                    <h3 className="text-[14px] font-bold text-[#0F172A] truncate">{group.name}</h3>
                    <p className="text-[12px] text-[#64748B] mt-0.5 truncate">
                      {group.category || 'Genel'} • {group.members.length} üye • {totalSpend.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺ harcama
                    </p>
                  </div>
                </div>

                <div className="text-right flex items-center gap-3 flex-shrink-0 ml-3">
                  <div>
                    <span
                      className={`text-[15px] font-black font-tabular block ${
                        isPos ? 'text-[#00875A]' : 'text-[#D32F2F]'
                      }`}
                    >
                      {isLocked
                        ? '•••• ₺'
                        : `${isPos ? '+' : ''}${myBal.toLocaleString('tr-TR', {
                            minimumFractionDigits: 2,
                            maximumFractionDigits: 2
                          })} ₺`}
                    </span>
                    <span className="text-[10px] text-[#64748B] block">
                      {Math.abs(myBal) < 0.01 ? 'Denk' : isPos ? 'Alacaklısın' : 'Borçlusun'}
                    </span>
                  </div>
                  <ChevronRight className="w-4 h-4 text-[#94A3B8]" />
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Create Group Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
          <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
            <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3 bg-white border-b border-slate-100 flex items-center justify-between flex-shrink-0">
              <button
                onClick={() => setShowCreateModal(false)}
                className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center text-[#0F172A] hover:bg-slate-200 active:scale-95 transition"
                title="Geri"
              >
                <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
              </button>
              <h3 className="text-[17px] font-bold text-[#0F172A] tracking-tight">Yeni Grup Kur</h3>
              <div className="w-10" />
            </div>

            <form onSubmit={handleCreateGroup} className="p-5 sm:p-6 overflow-y-auto flex-1 space-y-4 text-left">
              {/* Emoji & Name */}
              <div className="space-y-1.5">
                <label className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">GRUP ADI VE İKON</label>
                <div className="flex items-center gap-2">
                  <input
                    type="text"
                    maxLength={2}
                    value={newGroupEmoji}
                    onChange={(e) => setNewGroupEmoji(e.target.value)}
                    className="w-14 h-12 text-center text-[22px] rounded-[14px] bg-[#F8FAFC] border border-slate-200 focus:outline-none focus:border-[#00875A]"
                  />
                  <input
                    type="text"
                    required
                    value={newGroupName}
                    onChange={(e) => setNewGroupName(e.target.value)}
                    placeholder="örn: Kadıköy Evi, Kaş Tatili 2026"
                    className="flex-1 h-12 px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] font-bold text-[#0F172A] focus:outline-none focus:border-[#00875A]"
                  />
                </div>
              </div>

              {/* Category selector */}
              <div className="space-y-1.5">
                <label className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">KATEGORİ</label>
                <select
                  value={newGroupCategory}
                  onChange={(e) => setNewGroupCategory(e.target.value)}
                  className="w-full h-12 px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] font-bold text-[#0F172A] focus:outline-none focus:border-[#00875A]"
                >
                  <option value="Ev & Yaşam">Ev & Yaşam</option>
                  <option value="Tatil & Seyahat">Tatil & Seyahat</option>
                  <option value="Yemek & Kafe">Yemek & Kafe</option>
                  <option value="Etkinlik & Festival">Etkinlik & Festival</option>
                  <option value="Genel">Genel</option>
                </select>
              </div>

              {/* Members Selection */}
              <div className="space-y-1.5">
                <label className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
                  GRUP ÜYELERİNİ SEÇ ({selectedMemberIds.length})
                </label>
                <div className="rounded-[18px] bg-[#F8FAFC] border border-slate-200 divide-y divide-slate-100 max-h-48 overflow-y-auto">
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
                        className="p-3 flex items-center justify-between cursor-pointer hover:bg-slate-100/50"
                      >
                        <div className="flex items-center gap-2.5">
                          <div className="w-8 h-8 rounded-full bg-emerald-100 text-[#00875A] text-[11px] font-extrabold flex items-center justify-center">
                            {u.fullName.slice(0, 2).toUpperCase()}
                          </div>
                          <span className="text-[13px] font-bold text-[#0F172A]">
                            {u.id === currentUser.id ? `Ben (${u.fullName})` : u.fullName}
                          </span>
                        </div>
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

              <div className="pt-2 pb-[max(env(safe-area-inset-bottom),8px)]">
                <button
                  type="submit"
                  className="w-full h-12 rounded-[14px] bg-[#00875A] text-white font-bold text-[14px] hover:bg-[#00744d] active:scale-[0.98] transition shadow-sm shadow-emerald-900/20"
                >
                  Grubu Oluştur
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
