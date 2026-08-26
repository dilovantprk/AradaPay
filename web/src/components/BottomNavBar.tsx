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
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-surfaceWhite/95 backdrop-blur-md border-t border-surfaceBorder px-2 py-2">
      <div className="max-w-md mx-auto flex items-center justify-around">
        {tabs.map((tab) => {
          const isActive = currentTab === tab.key;
          return (
            <button
              key={tab.key}
              onClick={() => onTabChange(tab.key)}
              className="flex flex-col items-center justify-center flex-1 py-1 group active:scale-95 transition"
            >
              {/* M3 Active Indicator Pill */}
              <div
                className={`w-14 h-8 rounded-full flex items-center justify-center transition-all ${
                  isActive
                    ? 'bg-primaryEmeraldContainer text-primaryEmerald'
                    : 'text-textSecondary group-hover:text-textPrimary'
                }`}
              >
                {tab.icon}
              </div>
              <span
                className={`text-[11px] font-medium mt-0.5 transition-colors ${
                  isActive ? 'font-bold text-textPrimary' : 'text-textSecondary'
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
