import React from 'react';
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
    <header className="sticky top-0 z-30 bg-surfaceWhite/95 backdrop-blur-md border-b border-surfaceBorder px-5 py-3 transition-colors">
      <div className="max-w-2xl mx-auto flex items-center justify-between">
        <div>
          <p className="text-[12px] font-medium text-textSecondary tracking-normal">
            Merhaba, {firstName}
          </p>
          <h1 className="text-[26px] font-bold text-textPrimary tracking-[-0.5px] leading-tight">
            AradaPay
          </h1>
        </div>

        <div className="flex items-center gap-3">
          {hasNudges && (
            <button
              onClick={onNotificationClick}
              className="relative p-2.5 rounded-full bg-primaryEmeraldContainer text-primaryEmerald hover:opacity-80 active:scale-95 transition"
              title="Bildirimler"
            >
              <span className="absolute top-1.5 right-1.5 w-2.5 h-2.5 bg-accentRose rounded-full ring-2 ring-white animate-pulse" />
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
              </svg>
            </button>
          )}

          <button
            onClick={onProfileClick}
            className="w-11 h-11 rounded-full bg-primaryEmeraldContainer border-[1.5px] border-primaryEmerald flex items-center justify-center text-primaryEmerald font-bold text-[14px] active:scale-95 transition shadow-sm"
            title={`${user.fullName} (${user.tag || ''})`}
          >
            {user.avatarUrl ? (
              <img
                src={user.avatarUrl}
                alt={user.fullName}
                className="w-full h-full rounded-full object-cover"
              />
            ) : (
              initials
            )}
          </button>
        </div>
      </div>
    </header>
  );
};
