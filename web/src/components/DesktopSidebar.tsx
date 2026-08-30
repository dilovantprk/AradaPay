'use client';

import React from 'react';
import {
  LayoutDashboard,
  Users,
  UserCheck,
  PieChart,
  Settings,
  Plus,
  CreditCard,
  Download,
  LogOut,
  QrCode,
  ShieldCheck,
  Sparkles,
  Command,
  Search,
  Receipt
} from 'lucide-react';
import { NavTab } from './BottomNavBar';
import { User } from '../types';

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
  const navItems = [
    {
      section: 'GENEL BAKIŞ',
      items: [
        { id: 'dashboard' as NavTab, label: 'Ana Panel', icon: LayoutDashboard, badge: null }
      ]
    },
    {
      section: 'SOSYAL FİNANS',
      items: [
        { id: 'groups' as NavTab, label: 'Gruplarım', icon: Users, badge: null },
        { id: 'friends' as NavTab, label: 'Arkadaşlar', icon: UserCheck, badge: null }
      ]
    },
    {
      section: 'ANALİZ & GÜVENLİK',
      items: [
        { id: 'analytics' as NavTab, label: 'Finansal Analiz', icon: PieChart, badge: 'DFS' },
        { id: 'settings' as NavTab, label: 'Ayarlar & Kasa', icon: Settings, badge: null }
      ]
    }
  ];

  return (
    <aside className="hidden lg:flex w-72 h-screen sticky top-0 flex-col bg-[#EEEEF0]/80 backdrop-blur-3xl border-r border-black/[0.08] select-none z-40">
      {/* 1. macOS Window Traffic Lights & App Brand Header */}
      <div className="p-5 pb-3">
        <div className="flex items-center gap-2 mb-4">
          <div className="w-3 h-3 rounded-full bg-[#FF5F56] border border-black/10 shadow-2xs hover:opacity-80 transition cursor-pointer" />
          <div className="w-3 h-3 rounded-full bg-[#FFBD2E] border border-black/10 shadow-2xs hover:opacity-80 transition cursor-pointer" />
          <div className="w-3 h-3 rounded-full bg-[#27C93F] border border-black/10 shadow-2xs hover:opacity-80 transition cursor-pointer" />
          <span className="text-[10px] font-bold text-[#8E8E93] ml-2 uppercase tracking-wider">AradaPay macOS</span>
        </div>

        {/* Brand Logo Title */}
        <div className="flex items-center gap-2.5 px-1 py-1">
          <div className="w-8 h-8 rounded-[10px] bg-[#00875A] flex items-center justify-center text-white font-extrabold text-[16px] shadow-sm shadow-emerald-900/20">
            A
          </div>
          <div>
            <span className="text-[16px] font-extrabold text-[#1C1C1E] tracking-tight block">
              AradaPay
            </span>
            <span className="text-[10px] text-[#8E8E93] font-medium block -mt-0.5">
              Sosyal Borç & Fitleşme
            </span>
          </div>
        </div>

        {/* macOS Search Input */}
        <div className="mt-4 relative">
          <Search className="w-3.5 h-3.5 text-[#8E8E93] absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            readOnly
            placeholder="Hızlı Arama..."
            className="w-full h-8 pl-8 pr-12 rounded-[8px] bg-black/[0.04] border border-black/[0.06] text-[12px] text-[#1C1C1E] placeholder:text-[#8E8E93] focus:outline-none cursor-pointer"
          />
          <div className="absolute right-2 top-1/2 -translate-y-1/2 flex items-center gap-0.5 px-1.5 py-0.5 rounded bg-white/80 border border-black/[0.08] text-[9px] font-mono text-[#8E8E93]">
            <span>⌘K</span>
          </div>
        </div>
      </div>

      {/* 2. macOS Sidebar Navigation Groups */}
      <div className="flex-1 px-3 py-2 space-y-5 overflow-y-auto">
        {navItems.map((sec, secIdx) => (
          <div key={secIdx} className="space-y-1">
            <span className="text-[10px] font-bold text-[#8E8E93] uppercase tracking-wider px-3 block mb-1">
              {sec.section}
            </span>

            {sec.items.map((item) => {
              const Icon = item.icon;
              const isActive = currentTab === item.id;

              return (
                <button
                  key={item.id}
                  onClick={() => onTabChange(item.id)}
                  className={`w-full flex items-center justify-between px-3 py-2 rounded-[8px] text-[13px] font-semibold transition-all ${
                    isActive
                      ? 'bg-[#00875A] text-white shadow-2xs font-bold'
                      : 'text-[#1C1C1E] hover:bg-black/[0.04] active:bg-black/[0.07]'
                  }`}
                >
                  <div className="flex items-center gap-2.5">
                    <Icon
                      className={`w-4 h-4 transition-colors ${
                        isActive ? 'text-white' : 'text-[#8E8E93]'
                      }`}
                    />
                    <span>{item.label}</span>
                  </div>

                  {item.badge && (
                    <span
                      className={`text-[9px] font-bold px-1.5 py-0.5 rounded-full ${
                        isActive
                          ? 'bg-white/20 text-white'
                          : 'bg-emerald-100 text-[#00875A]'
                      }`}
                    >
                      {item.badge}
                    </span>
                  )}
                </button>
              );
            })}
          </div>
        ))}

        {/* macOS Quick Action Buttons */}
        <div className="pt-2 px-1 space-y-2">
          <button
            onClick={onOpenAddExpense}
            className="w-full h-10 rounded-[10px] bg-[#00875A] hover:bg-[#00744d] text-white font-bold text-[13px] flex items-center justify-center gap-2 active:scale-[0.98] transition shadow-sm shadow-emerald-900/20"
          >
            <Plus className="w-4 h-4 stroke-[2.5]" />
            <span>+ Harcama Ekle</span>
          </button>

          <button
            onClick={onOpenSettleUp}
            className="w-full h-10 rounded-[10px] bg-white border border-black/[0.08] hover:bg-slate-50 text-[#1C1C1E] font-bold text-[13px] flex items-center justify-center gap-2 active:scale-[0.98] transition shadow-2xs"
          >
            <CreditCard className="w-4 h-4 text-[#00875A]" />
            <span>Öde & Fitleş</span>
          </button>
        </div>
      </div>

      {/* 3. macOS Sidebar Footer User Profile */}
      <div className="p-3 border-t border-black/[0.08] bg-white/40 space-y-2">
        <div className="flex items-center justify-between p-2 rounded-[10px] hover:bg-black/[0.03] transition">
          <div className="flex items-center gap-2.5 truncate">
            <div className="relative">
              <div className="w-8 h-8 rounded-full bg-emerald-100 text-[#00875A] font-extrabold text-[12px] flex items-center justify-center">
                {currentUser.fullName.slice(0, 2).toUpperCase()}
              </div>
              <span className="w-2.5 h-2.5 rounded-full bg-[#34C759] border-2 border-white absolute -bottom-0.5 -right-0.5" />
            </div>

            <div className="truncate text-left">
              <div className="text-[13px] font-bold text-[#1C1C1E] truncate">
                {currentUser.fullName}
              </div>
              <div className="text-[10px] text-[#8E8E93] font-mono truncate">
                {currentUser.tag || `@${currentUser.username}`}
              </div>
            </div>
          </div>

          <button
            onClick={onLogout}
            className="p-1.5 rounded-lg hover:bg-black/5 text-[#8E8E93] hover:text-[#D32F2F] transition"
            title="Oturumu Kapat"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </aside>
  );
};
