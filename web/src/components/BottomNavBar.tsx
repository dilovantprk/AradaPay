'use client';

import React from 'react';
import { Home, Users, User, ReceiptText, ShieldCheck } from 'lucide-react';

export type NavTab = 'dashboard' | 'groups' | 'friends' | 'activity' | 'settings';

interface BottomNavBarProps {
  currentTab: NavTab;
  onTabChange: (tab: NavTab) => void;
}

export const BottomNavBar: React.FC<BottomNavBarProps> = ({ currentTab, onTabChange }) => {
  const tabs: { key: NavTab; label: string; icon: React.ReactNode }[] = [
    { key: 'dashboard', label: 'Ana Sayfa', icon: <Home className="w-[22px] h-[22px]" /> },
    { key: 'groups', label: 'Gruplar', icon: <Users className="w-[22px] h-[22px]" /> },
    { key: 'friends', label: 'Kişiler', icon: <User className="w-[22px] h-[22px]" /> },
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-white border-t border-[#EEF0F1] px-2 md:hidden select-none pb-[max(env(safe-area-inset-bottom),10px)] pt-2 transition-colors">
      <div className="max-w-md mx-auto flex items-center justify-around">
        {tabs.map((tab) => {
          const isActive = currentTab === tab.key;
          return (
            <button
              key={tab.key}
              onClick={() => onTabChange(tab.key)}
              className="flex flex-col items-center justify-center flex-1 py-1 active:scale-[0.88] transition-transform duration-150"
            >
              <div
                className={`transition-colors duration-200 ${
                  isActive ? 'text-[#00875A]' : 'text-[#6B7480]'
                }`}
              >
                {tab.icon}
              </div>
              <span
                className={`text-[11px] mt-1 tracking-tight transition-colors duration-200 ${
                  isActive ? 'font-bold text-[#00875A]' : 'font-medium text-[#6B7480]'
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

