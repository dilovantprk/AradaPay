'use client';

import React, { useState } from 'react';
import { AradaPayLogo } from './AradaPayLogo';
import {
  Eye,
  EyeOff,
  CreditCard,
  QrCode,
  ShieldCheck,
  PieChart,
  RefreshCw
} from 'lucide-react';

type ScreenMode = 'dashboard' | 'request_sheet' | 'dfs' | 'receipt';

export function PhoneMockup() {
  const [activeScreen, setActiveScreen] = useState<ScreenMode>('dashboard');
  const [isLocked, setIsLocked] = useState(false);
  const [requestAmount, setRequestAmount] = useState('250');
  const [selectedFriend, setSelectedFriend] = useState('Ahmet Yılmaz');
  const [dfsResolved, setDfsResolved] = useState(false);

  return (
    <section id="ekranlar" className="py-16 sm:py-24 bg-white border-y border-black/[0.06]">
      <div className="max-w-4xl mx-auto px-4 sm:px-6">
        <div className="text-center max-w-xl mx-auto mb-10">
          <div className="text-xs font-semibold text-[#00875A] tracking-wider uppercase mb-1">
            Uygulama İçi
          </div>
          <h2 className="text-2xl sm:text-3xl font-semibold text-[#1D1D1F] tracking-tight">
            Yerel Android Arayüzü
          </h2>
          <p className="text-sm text-[#6E6E73] mt-2">
            Material 3 ve Apple HIG standartlarına uygun Jetpack Compose bileşenleri.
          </p>

          {/* Minimal Segmented Control */}
          <div className="inline-flex p-1 bg-[#F2F2F7] rounded-xl text-xs font-medium text-[#6E6E73] mt-6">
            <button
              onClick={() => setActiveScreen('dashboard')}
              className={`px-3 py-1.5 rounded-lg transition-all ${
                activeScreen === 'dashboard'
                  ? 'bg-white text-[#1D1D1F] font-semibold shadow-xs'
                  : 'hover:text-[#1D1D1F]'
              }`}
            >
              Ana Bakiye
            </button>
            <button
              onClick={() => setActiveScreen('request_sheet')}
              className={`px-3 py-1.5 rounded-lg transition-all ${
                activeScreen === 'request_sheet'
                  ? 'bg-white text-[#1D1D1F] font-semibold shadow-xs'
                  : 'hover:text-[#1D1D1F]'
              }`}
            >
              Ödeme Talebi
            </button>
            <button
              onClick={() => setActiveScreen('dfs')}
              className={`px-3 py-1.5 rounded-lg transition-all ${
                activeScreen === 'dfs'
                  ? 'bg-white text-[#1D1D1F] font-semibold shadow-xs'
                  : 'hover:text-[#1D1D1F]'
              }`}
            >
              DFS Sadeleştirme
            </button>
            <button
              onClick={() => setActiveScreen('receipt')}
              className={`px-3 py-1.5 rounded-lg transition-all ${
                activeScreen === 'receipt'
                  ? 'bg-white text-[#1D1D1F] font-semibold shadow-xs'
                  : 'hover:text-[#1D1D1F]'
              }`}
            >
              Banka Dekontu
            </button>
          </div>
        </div>

        {/* Device Frame */}
        <div className="max-w-[360px] mx-auto">
          <div className="bg-[#1C1C1E] p-2.5 rounded-[42px] shadow-xl border border-black/10">
            {/* Screen Inner */}
            <div className="bg-[#F2F2F7] rounded-[34px] overflow-hidden text-[#1D1D1F] min-h-[580px] flex flex-col justify-between select-none">
              {/* Minimal Status Bar */}
              <div className="pt-3 px-5 pb-1.5 flex items-center justify-between text-[11px] font-semibold text-[#1D1D1F]">
                <span>09:41</span>
                <div className="w-16 h-3.5 bg-black rounded-full" />
                <span>100%</span>
              </div>

              {/* Screen Body */}
              <div className="p-3.5 flex-1 overflow-y-auto space-y-3">
                {/* 1. DASHBOARD SCREEN */}
                {activeScreen === 'dashboard' && (
                  <div className="space-y-2.5 animate-fade-in">
                    {/* User Bar */}
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-2">
                        <div className="w-8 h-8 rounded-full bg-[#E8F5E9] text-[#00875A] font-bold text-xs flex items-center justify-center">
                          ⚡
                        </div>
                        <div>
                          <div className="text-xs font-bold leading-tight">Selam, Arda 👋</div>
                          <div className="text-[10px] text-[#6E6E73]">@ardaturk • AradaPay</div>
                        </div>
                      </div>
                      <span className="text-[10px] bg-white text-[#00875A] font-medium px-2 py-0.5 rounded-full border border-black/[0.04]">
                        BKM Onaylı
                      </span>
                    </div>

                    {/* Main Account Card */}
                    <div className="bg-white rounded-2xl p-3.5 border border-black/[0.04] shadow-xs space-y-2">
                      <div className="flex items-center justify-between">
                        <span className="text-[10px] font-medium text-[#6E6E73]">Net Bakiye & Alacaklar</span>
                        <button
                          onClick={() => setIsLocked(!isLocked)}
                          className="w-6 h-6 rounded-full bg-[#F2F2F7] text-[#6E6E73] flex items-center justify-center"
                        >
                          {isLocked ? <EyeOff className="w-3 h-3" /> : <Eye className="w-3 h-3" />}
                        </button>
                      </div>

                      <div className="text-2xl font-bold font-tabular text-[#1D1D1F]">
                        {isLocked ? '•••• ₺' : '850,00 TL'}
                      </div>

                      <div className="flex items-center justify-between text-[11px] pt-1">
                        <span className="font-semibold text-[#00875A]">
                          {isLocked ? 'Alacak: •••• ₺' : '+ 1.250,00 TL Alacak'}
                        </span>
                        <span className="font-semibold text-[#BE123C]">
                          {isLocked ? 'Borç: •••• ₺' : '- 400,00 TL Borç'}
                        </span>
                      </div>
                    </div>

                    {/* Quick Actions */}
                    <div className="grid grid-cols-4 gap-1.5 text-center text-[10px]">
                      <div className="bg-white p-2 rounded-xl border border-black/[0.04]">
                        <div className="w-6 h-6 mx-auto rounded-full bg-[#E8F5E9] text-[#00875A] font-bold flex items-center justify-center mb-1">
                          +
                        </div>
                        <span className="font-medium">Harcama</span>
                      </div>
                      <div className="bg-white p-2 rounded-xl border border-black/[0.04]">
                        <div className="w-6 h-6 mx-auto rounded-full bg-[#F2F2F7] text-[#1D1D1F] flex items-center justify-center mb-1">
                          <CreditCard className="w-3 h-3" />
                        </div>
                        <span className="font-medium">Öde</span>
                      </div>
                      <div
                        onClick={() => setActiveScreen('request_sheet')}
                        className="bg-white p-2 rounded-xl border border-black/[0.04] cursor-pointer"
                      >
                        <div className="w-6 h-6 mx-auto rounded-full bg-[#E8F5E9] text-[#00875A] font-bold flex items-center justify-center mb-1">
                          ₺
                        </div>
                        <span className="font-medium">İste</span>
                      </div>
                      <div
                        onClick={() => setActiveScreen('dfs')}
                        className="bg-white p-2 rounded-xl border border-black/[0.04] cursor-pointer"
                      >
                        <div className="w-6 h-6 mx-auto rounded-full bg-[#F2F2F7] text-[#1D1D1F] flex items-center justify-center mb-1">
                          <RefreshCw className="w-3 h-3" />
                        </div>
                        <span className="font-medium">DFS</span>
                      </div>
                    </div>

                    {/* Category Analytics */}
                    <div className="bg-white rounded-2xl p-3 border border-black/[0.04] shadow-xs space-y-1.5 text-xs">
                      <div className="flex items-center justify-between text-[11px] font-bold text-[#1D1D1F]">
                        <span>Ağustos Harcamaları</span>
                        <span className="text-[#00875A] font-medium">Detay ➔</span>
                      </div>

                      <div className="flex items-center justify-between pt-1 text-[11px]">
                        <span className="text-[#6E6E73]">🍔 Yeme & İçme (%45)</span>
                        <span className="font-bold font-tabular">520,00 TL</span>
                      </div>
                      <div className="flex items-center justify-between text-[11px]">
                        <span className="text-[#6E6E73]">🛒 Market (%30)</span>
                        <span className="font-bold font-tabular">340,00 TL</span>
                      </div>
                    </div>
                  </div>
                )}

                {/* 2. REQUEST MONEY SHEET */}
                {activeScreen === 'request_sheet' && (
                  <div className="bg-white rounded-2xl p-3.5 border border-black/[0.06] shadow-sm space-y-2.5 animate-fade-in text-xs">
                    <div className="w-8 h-1 bg-slate-200 rounded-full mx-auto" />
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-[#1D1D1F]">Ödeme Talebi</span>
                      <button onClick={() => setActiveScreen('dashboard')} className="text-[#86868B]">✕</button>
                    </div>

                    <div className="grid grid-cols-4 gap-1 text-center">
                      {['Ahmet', 'Zeynep', 'Mert', 'Elif'].map((name) => (
                        <div
                          key={name}
                          onClick={() => setSelectedFriend(name)}
                          className={`p-1.5 rounded-lg border text-[10px] cursor-pointer ${
                            selectedFriend.startsWith(name)
                              ? 'bg-[#E8F5E9] border-[#00875A] text-[#00875A] font-bold'
                              : 'bg-[#F8FAFC] border-slate-200'
                          }`}
                        >
                          {name}
                        </div>
                      ))}
                    </div>

                    <div className="bg-[#F8FAFC] p-2.5 rounded-xl border border-slate-200">
                      <span className="text-[10px] text-[#6E6E73] block">Tutar</span>
                      <div className="text-xl font-bold font-tabular my-0.5">{requestAmount} TL</div>
                      <div className="flex gap-1 pt-1">
                        {['50', '100', '250', '500'].map((val) => (
                          <button
                            key={val}
                            onClick={() => setRequestAmount(val)}
                            className="text-[9px] font-bold bg-white border border-slate-200 px-1.5 py-0.5 rounded"
                          >
                            +{val}
                          </button>
                        ))}
                      </div>
                    </div>

                    <button
                      onClick={() => setActiveScreen('dashboard')}
                      className="w-full py-2 bg-[#00875A] text-white text-[11px] font-bold rounded-lg"
                    >
                      Talebi Gönder ➔
                    </button>
                  </div>
                )}

                {/* 3. DFS RESOLVER */}
                {activeScreen === 'dfs' && (
                  <div className="bg-white rounded-2xl p-3.5 border border-black/[0.04] shadow-xs space-y-2.5 animate-fade-in text-xs">
                    <div className="flex items-center justify-between border-b border-slate-100 pb-1.5">
                      <span className="font-bold">DFS Döngü Sadeleştirici</span>
                      <span className="text-[10px] bg-[#E8F5E9] text-[#00875A] px-1.5 py-0.2 rounded font-bold">
                        %65 Tasarruf
                      </span>
                    </div>

                    <div className="space-y-1 text-[11px]">
                      <div className="flex justify-between bg-[#F8FAFC] p-1.5 rounded-lg">
                        <span>Ali ➡️ Berk:</span>
                        <span className="font-bold font-tabular">500 TL</span>
                      </div>
                      <div className="flex justify-between bg-[#F8FAFC] p-1.5 rounded-lg">
                        <span>Berk ➡️ Can:</span>
                        <span className="font-bold font-tabular">500 TL</span>
                      </div>
                      <div className="flex justify-between bg-[#F8FAFC] p-1.5 rounded-lg">
                        <span>Can ➡️ Ali:</span>
                        <span className="font-bold font-tabular">500 TL</span>
                      </div>
                    </div>

                    <div className="p-2 bg-[#E8F5E9] text-[#065F46] rounded-lg text-[10px]">
                      {dfsResolved
                        ? '✅ Dairesel 1.500 TL döngü sıfırlandı. 0 TL net transfer.'
                        : '500 TL dairesel transfer iptal edilebilir.'}
                    </div>

                    <button
                      onClick={() => setDfsResolved(!dfsResolved)}
                      className="w-full py-2 bg-[#00875A] text-white text-[11px] font-bold rounded-lg"
                    >
                      {dfsResolved ? 'Sıfırla' : 'Sadeleştirmeyi Onayla'}
                    </button>
                  </div>
                )}

                {/* 4. RECEIPT */}
                {activeScreen === 'receipt' && (
                  <div className="bg-white rounded-2xl p-3.5 border border-black/[0.04] shadow-xs space-y-2 animate-fade-in text-xs">
                    <div className="flex items-center justify-between pb-1.5 border-b border-slate-100">
                      <span className="font-bold">Banka Dekontu</span>
                      <span className="text-[9px] bg-[#E8F5E9] text-[#00875A] px-1.5 py-0.2 rounded font-bold">
                        Merkle Onaylı
                      </span>
                    </div>

                    <div className="space-y-1 text-[#6E6E73] text-[11px]">
                      <div className="flex justify-between">
                        <span>Ref:</span>
                        <span className="font-mono text-[#1D1D1F]">AP-2026-9481</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Tutar:</span>
                        <span className="font-bold text-[#00875A] font-tabular">1.250,00 TL</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Gas Masrafı:</span>
                        <span className="text-[#00875A]">0,00 TL</span>
                      </div>
                    </div>

                    <div className="pt-1 border-t border-slate-100">
                      <span className="text-[9px] text-[#86868B] block font-mono">Merkle Root:</span>
                      <span className="text-[9px] font-mono text-[#00875A] bg-slate-50 p-1 rounded block break-all mt-0.5">
                        0x9d4e7f1b2c3a5e8841fa09199dcb6
                      </span>
                    </div>
                  </div>
                )}
              </div>

              {/* Bottom Indicator */}
              <div className="py-2 flex justify-center">
                <div className="w-24 h-1 bg-black/20 rounded-full" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
