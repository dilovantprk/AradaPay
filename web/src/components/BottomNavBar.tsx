'use client';

import React from 'react';
import { LayoutDashboard, Users, UserCheck, Receipt, Settings } from 'lucide-react';

export type NavTab = 'dashboard' | 'groups' | 'friends' | 'activity' | 'settings';

interface BottomNavBarProps {
  currentTab: NavTab;
  onTabChange: (tab: NavTab) => void;
}

export const BottomNavBar: React.FC<BottomNavBarProps> = ({ currentTab, onTabChange }) => {
  const tabs: { key: NavTab; label: string; icon: React.ReactNode }[] = [
    { key: 'dashboard', label: 'Ana Sayfa', icon: <LayoutDashboard className="w-5 h-5" /> },
    { key: 'groups', label: 'Gruplar', icon: <Users className="w-5 h-5" /> },
    { key: 'friends', label: 'Arkadaşlar', icon: <UserCheck className="w-5 h-5" /> },
    { key: 'activity', label: 'Hareketler', icon: <Receipt className="w-5 h-5" /> },
    { key: 'settings', label: 'Ayarlar', icon: <Settings className="w-5 h-5" /> },
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 ios-tab-bar border-t border-black/[0.08] px-2 pt-2 md:hidden select-none pb-[max(env(safe-area-inset-bottom),10px)]">
      <div className="max-w-md mx-auto flex items-center justify-around">
        {tabs.map((tab) => {
          const isActive = currentTab === tab.key;
          return (
            <button
              key={tab.key}
              onClick={() => onTabChange(tab.key)}
              className="flex flex-col items-center justify-center flex-1 py-0.5 active:scale-[0.88] transition-transform duration-150"
            >
              <div
                className={`w-9 h-7 rounded-full flex items-center justify-center transition-all ${
                  isActive
                    ? 'text-[#00875A] scale-110'
                    : 'text-[#8E8E93]'
                }`}
              >
                {tab.icon}
              </div>
              <span
                className={`text-[10px] tracking-tight transition-colors ${
                  isActive ? 'font-bold text-[#00875A]' : 'font-medium text-[#8E8E93]'
                }`}
              >
                {tab.label}
              </span>
            </button>
          );
        })}
      </div>
    </nav>
  );
};
