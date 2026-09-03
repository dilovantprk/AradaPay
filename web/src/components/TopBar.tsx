'use client';

import React from 'react';
import {
  Bell,
  Eye,
  EyeOff,
  ArrowLeft,
  Plus,
  UserPlus,
  CreditCard
} from 'lucide-react';
import { User, Group } from '../types';
import { NavTab } from './BottomNavBar';

interface TopBarProps {
  user: User;
  currentTab: NavTab;
  selectedGroup?: Group | null;
  selectedFriend?: User | null;
  isLocked: boolean;
  onToggleLock?: () => void;
  onProfileClick: () => void;
  onNotificationClick?: () => void;
  hasNudges?: boolean;
  onBack?: () => void;
  onOpenCreateGroup?: () => void;
  onOpenAddFriend?: () => void;
  onAddExpenseInGroup?: (group: Group) => void;
  onOpenSettleWithFriend?: (friend: User) => void;
}

export const TopBar: React.FC<TopBarProps> = ({
  user,
  currentTab,
  selectedGroup,
  selectedFriend,
  isLocked,
  onToggleLock,
  onProfileClick,
  onNotificationClick,
  hasNudges,
  onBack,
  onOpenCreateGroup,
  onOpenAddFriend,
  onAddExpenseInGroup,
  onOpenSettleWithFriend
}) => {
  const initials = user.fullName
    .split(' ')
    .filter(Boolean)
    .map((n) => n[0])
    .join('')
    .substring(0, 2)
    .toUpperCase();

  // 1. DETAIL VIEW: GROUP DETAIL
  if (selectedGroup) {
    return (
      <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-[#F1F5F9] px-4 sm:px-8 select-none transition-colors h-[64px] flex items-center">
        <div className="w-full max-w-5xl mx-auto flex items-center justify-between gap-3">
          {/* Left: Only Back Button */}
          <button
            onClick={onBack}
            className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-90 transition flex items-center justify-center text-[#0F172A] flex-shrink-0"
            title="Geri"
          >
            <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
          </button>

          {/* Right: Quick Action Buttons */}
          <div className="flex items-center gap-2 flex-shrink-0">
            {onToggleLock && (
              <button
                onClick={onToggleLock}
                className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-90 transition flex items-center justify-center text-[#64748B] hover:text-[#0F172A]"
                title={isLocked ? 'Bakiyeleri Göster' : 'Bakiyeleri Gizle'}
              >
                {isLocked ? <EyeOff className="w-4 h-4 text-[#8E8E93]" /> : <Eye className="w-4 h-4 text-[#00875A]" />}
              </button>
            )}
          </div>
        </div>
      </header>
    );
  }

  // 2. DETAIL VIEW: FRIEND DETAIL
  if (selectedFriend) {
    return (
      <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-[#F1F5F9] px-4 sm:px-8 select-none transition-colors h-[64px] flex items-center">
        <div className="w-full max-w-5xl mx-auto flex items-center justify-between gap-3">
          {/* Left: Only Back Button */}
          <button
            onClick={onBack}
            className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-90 transition flex items-center justify-center text-[#0F172A] flex-shrink-0"
            title="Geri"
          >
            <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
          </button>

          {/* Right: Quick Action Buttons */}
          <div className="flex items-center gap-2 flex-shrink-0">
            {onToggleLock && (
              <button
                onClick={onToggleLock}
                className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-90 transition flex items-center justify-center text-[#64748B] hover:text-[#0F172A]"
                title={isLocked ? 'Bakiyeleri Göster' : 'Bakiyeleri Gizle'}
              >
                {isLocked ? <EyeOff className="w-4 h-4 text-[#8E8E93]" /> : <Eye className="w-4 h-4 text-[#00875A]" />}
              </button>
            )}
          </div>
        </div>
      </header>
    );
  }

  // 3. PRIMARY TABS: DASHBOARD / GROUPS / FRIENDS / ACTIVITY / SETTINGS
  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-[#F1F5F9] px-4 sm:px-8 select-none transition-colors h-[64px] flex items-center">
      <div className="w-full max-w-5xl mx-auto flex items-center justify-between">
        {/* Left: Adaptive Title (Fixed Single Line) */}
        <div className="flex items-center">
          {currentTab === 'dashboard' && (
            <h1 className="text-[24px] sm:text-[26px] font-bold text-[#0F172A] tracking-[-0.5px] leading-none">
              Arada<span className="text-[#00875A]">Pay</span>
            </h1>
          )}

          {currentTab === 'groups' && (
            <h1 className="text-[24px] sm:text-[26px] font-bold text-[#0F172A] tracking-[-0.5px] leading-none">
              Gruplar
            </h1>
          )}

          {currentTab === 'friends' && (
            <h1 className="text-[24px] sm:text-[26px] font-bold text-[#0F172A] tracking-[-0.5px] leading-none">
              Arkadaşlar
            </h1>
          )}

          {currentTab === 'activity' && (
            <h1 className="text-[24px] sm:text-[26px] font-bold text-[#0F172A] tracking-[-0.5px] leading-none">
              Hareketler
            </h1>
          )}

          {currentTab === 'settings' && (
            <div className="flex items-center gap-3">
              <button
                onClick={onBack}
                className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-90 transition flex items-center justify-center text-[#0F172A] flex-shrink-0"
                title="Geri"
              >
                <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
              </button>
              <h1 className="text-[20px] sm:text-[22px] font-bold text-[#0F172A] tracking-[-0.5px] leading-none">
                Ayarlar & Güvenlik
              </h1>
            </div>
          )}
        </div>

        {/* Right: Action Buttons */}
        <div className="flex items-center gap-2">
          {currentTab === 'dashboard' && (
            <button
              onClick={onProfileClick}
              className="w-10 h-10 rounded-full bg-[#E6F4EA] border-[1.5px] border-[#00875A] flex items-center justify-center text-[#00875A] font-bold text-[14px] active:scale-90 transition shadow-2xs"
            >
              {user.avatarUrl ? (
                <img src={user.avatarUrl} alt={user.fullName} className="w-full h-full rounded-full object-cover" />
              ) : (
                <span>{initials}</span>
              )}
            </button>
          )}

          {currentTab === 'groups' && onOpenCreateGroup && (
            <button
              onClick={onOpenCreateGroup}
              className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-90 transition flex items-center justify-center text-[#0F172A]"
              title="Yeni Grup Kur"
            >
              <Plus className="w-5 h-5 stroke-[2.2]" />
            </button>
          )}

          {currentTab === 'friends' && onOpenAddFriend && (
            <button
              onClick={onOpenAddFriend}
              className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-90 transition flex items-center justify-center text-[#0F172A]"
              title="Arkadaş Ekle"
            >
              <UserPlus className="w-5 h-5 stroke-[2.2]" />
            </button>
          )}

          {currentTab === 'activity' && (
            <button
              onClick={onProfileClick}
              className="w-10 h-10 rounded-full bg-[#E6F4EA] border-[1.5px] border-[#00875A] flex items-center justify-center text-[#00875A] font-bold text-[14px] active:scale-90 transition"
            >
              <span>{initials}</span>
            </button>
          )}

          {currentTab === 'settings' && (
            <div className="w-10 h-10 rounded-full bg-[#E6F4EA] border-[1.5px] border-[#00875A] flex items-center justify-center text-[#00875A] font-bold text-[14px]">
              <span>{initials}</span>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
