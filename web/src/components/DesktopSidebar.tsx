'use client';

import React from 'react';
import {
  Home,
  Users,
  UserCheck,
  PieChart,
  Settings,
  Plus,
  CreditCard,
  Download,
  ShieldCheck,
  LogOut,
  QrCode
} from 'lucide-react';
import { User } from '../types';
import { NavTab } from './BottomNavBar';

interface DesktopSidebarProps {
  currentTab: NavTab;
  onTabChange: (tab: NavTab) => void;
  currentUser: User;
  onOpenAddExpense: () => void;
  onOpenSettleUp: () => void;
  onLogout: () => void;
}

export const DesktopSidebar: React.FC<DesktopSidebarProps> = ({
  currentTab,
  onTabChange,
  currentUser,
  onOpenAddExpense,
  onOpenSettleUp,
  onLogout
}) => {
  const navItems: { key: NavTab; label: string; icon: React.ReactNode }[] = [
    { key: 'dashboard', label: 'Ana Sayfa', icon: <Home className="w-5 h-5" /> },
    { key: 'groups', label: 'Gruplar', icon: <Users className="w-5 h-5" /> },
    { key: 'friends', label: 'Arkadaşlar', icon: <UserCheck className="w-5 h-5" /> },
    { key: 'analytics', label: 'Analiz & Raporlar', icon: <PieChart className="w-5 h-5" /> },
    { key: 'settings', label: 'Ayarlar & Gizlilik', icon: <Settings className="w-5 h-5" /> },
  ];

  return (
    <aside className="hidden lg:flex flex-col w-72 h-screen sticky top-0 bg-white/80 backdrop-blur-2xl border-r border-black/[0.06] p-5 justify-between select-none z-30">
      {/* 1. Header & Brand */}
      <div className="space-y-6">
        <div className="flex items-center gap-3 px-2">
          <div className="w-10 h-10 rounded-[12px] bg-[#00875A] flex items-center justify-center text-white font-black text-[18px] shadow-sm shadow-emerald-800/20">
            AP
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <span className="text-[19px] font-bold text-[#1D1D1F] tracking-tight">
                Arada<span className="text-[#00875A]">Pay</span>
              </span>
              <span className="px-1.5 py-0.5 rounded-full bg-emerald-100 text-[#00875A] text-[10px] font-bold">
                PRO
              </span>
            </div>
            <span className="text-[11px] text-[#86868B] font-medium">Sosyal Finans Sistemi</span>
          </div>
        </div>

        {/* 2. User Profile Capsule (macOS Style) */}
        <div
          onClick={() => onTabChange('settings')}
          className="p-3 rounded-[16px] bg-[#F2F2F7] border border-black/[0.03] flex items-center gap-3 cursor-pointer hover:bg-[#E5E5EA] transition active:scale-[0.98]"
        >
          <div className="w-10 h-10 rounded-full bg-emerald-100 text-[#00875A] font-bold text-[14px] flex items-center justify-center border border-emerald-300">
            {currentUser.fullName.slice(0, 2).toUpperCase()}
          </div>
          <div className="truncate flex-1">
            <div className="text-[13px] font-bold text-[#1D1D1F] truncate">
              {currentUser.fullName}
            </div>
            <div className="text-[11px] font-mono text-[#86868B] truncate">
              {currentUser.tag || `@${currentUser.username}`}
            </div>
          </div>
        </div>

        {/* 3. Action Buttons */}
        <div className="space-y-2 pt-1">
          <button
            onClick={onOpenAddExpense}
            className="w-full h-11 rounded-[14px] bg-[#00875A] text-white font-bold text-[13px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-[0.98] transition shadow-sm shadow-emerald-800/20"
          >
            <Plus className="w-4 h-4 stroke-[2.5]" />
            <span>Harcama Ekle</span>
          </button>

          <button
            onClick={onOpenSettleUp}
            className="w-full h-11 rounded-[14px] bg-white border border-black/[0.08] text-[#1D1D1F] font-bold text-[13px] flex items-center justify-center gap-2 hover:bg-[#F2F2F7] active:scale-[0.98] transition shadow-apple-sm"
          >
            <CreditCard className="w-4 h-4 text-[#86868B]" />
            <span>Öde & Fitleş</span>
          </button>
        </div>

        {/* 4. Navigation Links (Apple Sidebar HIG) */}
        <nav className="space-y-1 pt-2">
          {navItems.map((item) => {
            const isActive = currentTab === item.key;
            return (
              <button
                key={item.key}
                onClick={() => onTabChange(item.key)}
                className={`w-full h-10 px-3.5 rounded-[12px] flex items-center gap-3 text-[13px] font-semibold transition ${
                  isActive
                    ? 'bg-white text-[#1D1D1F] shadow-apple-sm font-bold border border-black/[0.04]'
                    : 'text-[#86868B] hover:text-[#1D1D1F] hover:bg-black/5'
                }`}
              >
                <span className={isActive ? 'text-[#00875A]' : 'text-[#86868B]'}>
                  {item.icon}
                </span>
                <span>{item.label}</span>
              </button>
            );
          })}
        </nav>
      </div>

      {/* 5. Footer (Android Download & Logout) */}
      <div className="space-y-3 pt-4 border-t border-black/[0.06]">
        {/* Android APK Download Card */}
        <a
          href="/AradaPay.apk"
          download="AradaPay.apk"
          className="p-3 rounded-[16px] bg-gradient-to-br from-emerald-50 to-emerald-100/60 border border-emerald-200/80 flex items-center justify-between group hover:border-emerald-300 transition"
        >
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-xl bg-[#00875A] text-white flex items-center justify-center">
              <Download className="w-4 h-4" />
            </div>
            <div>
              <div className="text-[12px] font-bold text-[#1D1D1F]">Android Uygulaması</div>
              <div className="text-[10px] text-[#00875A] font-semibold">APK v1.0.0 İndir</div>
            </div>
          </div>
          <QrCode className="w-4 h-4 text-[#00875A]" />
        </a>

        {/* Logout button */}
        <button
          onClick={onLogout}
          className="w-full py-2 rounded-[10px] text-[12px] font-semibold text-[#86868B] hover:text-red-600 hover:bg-red-50 flex items-center justify-center gap-1.5 transition"
        >
          <LogOut className="w-3.5 h-3.5" />
          <span>Oturumu Kapat</span>
        </button>
      </div>
    </aside>
  );
};
