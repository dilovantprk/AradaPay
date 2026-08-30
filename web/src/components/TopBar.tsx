'use client';

import React from 'react';
import { Bell, ShieldCheck } from 'lucide-react';
import { User } from '../types';

interface TopBarProps {
  user: User;
  onProfileClick: () => void;
  onNotificationClick?: () => void;
  hasNudges?: boolean;
}

export const TopBar: React.FC<TopBarProps> = ({
  user,
  onProfileClick,
  onNotificationClick,
  hasNudges
}) => {
  const firstName = user.fullName.trim().split(' ')[0] || user.username || 'Kullanıcı';
  const initials = user.fullName
    .split(' ')
    .map((n) => n[0])
    .join('')
    .substring(0, 2)
    .toUpperCase();

  return (
    <header className="sticky top-0 z-30 apple-glass border-b border-black/[0.06] px-5 sm:px-8 pt-[max(env(safe-area-inset-top),14px)] pb-3 transition-colors">
      <div className="max-w-5xl mx-auto flex items-center justify-between">
        <div>
          <p className="text-[11px] font-semibold text-[#8E8E93] uppercase tracking-wider">
            {new Date().toLocaleDateString('tr-TR', { weekday: 'long', day: 'numeric', month: 'short' }).toUpperCase()}
          </p>
          <h1 className="text-[26px] font-extrabold text-[#1C1C1E] tracking-tight leading-tight">
            Arada<span className="text-[#00875A]">Pay</span>
          </h1>
        </div>

        <div className="flex items-center gap-2.5">
          {/* Notification Button */}
          <button
            onClick={onNotificationClick}
            className="relative w-10 h-10 rounded-full bg-black/5 hover:bg-black/10 active:scale-[0.92] transition flex items-center justify-center text-[#1C1C1E]"
            title="Dürtmeler ve Bildirimler"
          >
            {hasNudges && (
              <span className="absolute top-2.5 right-2.5 w-2 h-2 bg-[#D32F2F] rounded-full ring-2 ring-white animate-pulse" />
            )}
            <Bell className="w-4 h-4 text-[#1C1C1E]" />
          </button>

          {/* Profile Button Capsule */}
          <button
            onClick={onProfileClick}
            className="w-10 h-10 rounded-full bg-emerald-100 border border-emerald-300 flex items-center justify-center text-[#00875A] font-extrabold text-[13px] active:scale-[0.92] transition shadow-2xs"
            title={`${user.fullName} (${user.tag || ''})`}
          >
            {user.avatarUrl ? (
              <img
                src={user.avatarUrl}
                alt={user.fullName}
                className="w-full h-full rounded-full object-cover"
              />
            ) : (
              <span>{initials}</span>
            )}
          </button>
        </div>
      </div>
    </header>
  );
};
