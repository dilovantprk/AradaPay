'use client';

import React from 'react';
import { Home, Users, UserCheck, PieChart, Settings } from 'lucide-react';

export type NavTab = 'dashboard' | 'groups' | 'friends' | 'analytics' | 'settings';

interface BottomNavBarProps {
  currentTab: NavTab;
  onTabChange: (tab: NavTab) => void;
}

export const BottomNavBar: React.FC<BottomNavBarProps> = ({ currentTab, onTabChange }) => {
  const tabs: { key: NavTab; label: string; icon: React.ReactNode }[] = [
    { key: 'dashboard', label: 'Ana Sayfa', icon: <Home className="w-5 h-5" /> },
    { key: 'groups', label: 'Gruplar', icon: <Users className="w-5 h-5" /> },
    { key: 'friends', label: 'Arkadaşlar', icon: <UserCheck className="w-5 h-5" /> },
    { key: 'analytics', label: 'Analiz', icon: <PieChart className="w-5 h-5" /> },
    { key: 'settings', label: 'Ayarlar', icon: <Settings className="w-5 h-5" /> },
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 apple-glass border-t border-black/[0.06] px-2 py-1.5 lg:hidden select-none pb-[env(safe-area-inset-bottom,8px)]">
      <div className="max-w-md mx-auto flex items-center justify-around">
        {tabs.map((tab) => {
          const isActive = currentTab === tab.key;
          return (
            <button
              key={tab.key}
              onClick={() => onTabChange(tab.key)}
              className="flex flex-col items-center justify-center flex-1 py-1 active:scale-[0.92] transition-transform duration-150"
            >
              <div
                className={`w-10 h-7 rounded-full flex items-center justify-center transition-all ${
                  isActive
                    ? 'text-[#00875A]'
                    : 'text-[#8E8E93] hover:text-[#1C1C1E]'
                }`}
              >
                {tab.icon}
              </div>
              <span
                className={`text-[10px] font-medium transition-colors ${
                  isActive ? 'font-bold text-[#00875A]' : 'text-[#8E8E93]'
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
