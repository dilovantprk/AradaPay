'use client';

import React from 'react';
import { ArrowRight, Download, CreditCard, Users, Zap } from 'lucide-react';

interface LandingPageProps {
  onLaunchWebApp: () => void;
}

export const LandingPage: React.FC<LandingPageProps> = ({ onLaunchWebApp }) => {
  const handleDirectDownload = () => {
    const link = document.createElement('a');
    link.href = '/AradaPay.apk';
    link.download = 'AradaPay.apk';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="min-h-screen bg-[#FDFDFD] text-[#0F172A] flex flex-col font-sans selection:bg-emerald-100 selection:text-emerald-900">
      {/* 1. MINIMAL HEADER */}
      <header className="sticky top-0 z-40 bg-[#FDFDFD]/90 backdrop-blur-md border-b border-slate-100 px-6 sm:px-12 h-[64px] flex items-center">
        <div className="max-w-5xl w-full mx-auto flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-[9px] bg-[#00875A] flex items-center justify-center text-white font-extrabold text-[14px] shadow-sm">
              AP
            </div>
            <span className="text-[17px] font-bold tracking-tight text-[#0F172A]">
              Arada<span className="text-[#00875A]">Pay</span>
            </span>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={handleDirectDownload}
              className="px-3.5 py-1.5 rounded-[10px] text-[13px] font-medium text-[#64748B] hover:text-[#0F172A] hover:bg-slate-100 transition hidden sm:inline-flex items-center gap-1.5"
            >
              <Download className="w-3.5 h-3.5" />
              <span>APK İndir</span>
            </button>

            <button
              onClick={onLaunchWebApp}
              className="px-4 py-2 rounded-[12px] bg-[#0F172A] hover:bg-[#1E293B] active:scale-95 text-white text-[13px] font-bold transition flex items-center gap-1.5 shadow-sm"
            >
              <span>Uygulamayı Aç</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </header>

      {/* 2. MINIMAL HERO */}
      <main className="flex-1 flex flex-col items-center justify-center px-6 sm:px-12 pt-16 pb-20 max-w-4xl mx-auto text-center space-y-10">
        <div className="space-y-4 max-w-2xl">
          <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-[#0F172A] leading-[1.1]">
            Ortamda harca. <br />
            <span className="text-[#00875A]">Saniyede fitleş.</span>
          </h1>

          <p className="text-[16px] sm:text-[18px] text-[#64748B] font-normal leading-relaxed pt-2">
            Arkadaş gruplarındaki ortak masrafları takip edin, borçları tek bir transfere indirin ve FAST ile anında kapatın.
          </p>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row items-center gap-3 pt-2">
          <button
            onClick={onLaunchWebApp}
            className="w-full sm:w-auto h-[50px] px-8 rounded-[14px] bg-[#00875A] hover:bg-[#00744d] active:scale-95 text-white font-bold text-[15px] transition flex items-center justify-center gap-2 shadow-sm shadow-emerald-900/15"
          >
            <span>Web Uygulamasını Başlat</span>
            <ArrowRight className="w-4 h-4 stroke-[2.5]" />
          </button>

          <button
            onClick={handleDirectDownload}
            className="w-full sm:w-auto h-[50px] px-6 rounded-[14px] bg-white border border-slate-200 hover:bg-slate-50 active:scale-95 text-[#0F172A] font-semibold text-[14px] transition flex items-center justify-center gap-2"
          >
            <Download className="w-4 h-4 text-[#64748B]" />
            <span>Android APK (v1.0)</span>
          </button>
        </div>

        {/* 3. CLEAN PRODUCT PREVIEW CARD */}
        <div className="w-full max-w-xl mx-auto pt-6 text-left">
          <div className="bg-white rounded-[24px] border border-slate-200 shadow-xl shadow-slate-100 p-6 space-y-5">
            {/* Header */}
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center text-[18px]">
                  🏠
                </div>
                <div>
                  <h3 className="text-[15px] font-bold text-[#0F172A]">Kadıköy Evi</h3>
                  <p className="text-[12px] text-[#64748B]">3 Kişi • Ev & Yaşam</p>
                </div>
              </div>

              <div className="text-right">
                <span className="text-[14px] font-extrabold text-[#00875A] block">+450 ₺</span>
                <span className="text-[11px] text-[#64748B]">Alacaklısın</span>
              </div>
            </div>

            {/* Expense Rows */}
            <div className="space-y-2.5 text-[13px]">
              <div className="flex items-center justify-between p-3 rounded-[12px] bg-[#F8FAFC]">
                <div>
                  <span className="font-semibold text-[#0F172A] block">Akşam Yemeği</span>
                  <span className="text-[11px] text-[#64748B]">Sen ödedin (3 kişi eşit)</span>
                </div>
                <span className="font-bold text-[#0F172A]">800 ₺</span>
              </div>

              <div className="flex items-center justify-between p-3 rounded-[12px] bg-[#F8FAFC]">
                <div>
                  <span className="font-semibold text-[#0F172A] block">Moda Kahveleri</span>
                  <span className="text-[11px] text-[#64748B]">Mert ödedi</span>
                </div>
                <span className="font-bold text-[#0F172A]">180 ₺</span>
              </div>
            </div>

            {/* Smart Debt Simplification Strip */}
            <div className="p-3.5 rounded-[14px] bg-emerald-50 border border-emerald-200/80 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Zap className="w-4 h-4 text-[#00875A] flex-shrink-0" />
                <span className="text-[12px] font-bold text-[#00875A]">
                  Mert ➔ Sen: 250 ₺ (FAST ile Kapat)
                </span>
              </div>
              <span className="text-[11px] font-bold text-[#00875A] bg-white px-2.5 py-0.5 rounded-full border border-emerald-200">
                Fitleş
              </span>
            </div>
          </div>
        </div>

        {/* 4. 3 CLEAN PILLARS */}
        <div className="w-full grid grid-cols-1 sm:grid-cols-3 gap-5 pt-6 text-left">
          <div className="space-y-2 p-5 rounded-[18px] bg-white border border-slate-100 shadow-xs">
            <div className="w-8 h-8 rounded-[10px] bg-emerald-50 text-[#00875A] flex items-center justify-center">
              <Users className="w-4 h-4" />
            </div>
            <h4 className="text-[14px] font-bold text-[#0F172A]">Adil Bölüşüm</h4>
            <p className="text-[12px] text-[#64748B] leading-relaxed">
              Eşit, yüzdelik veya kişiye özel harcamaları tek ekranda paylaştırın.
            </p>
          </div>

          <div className="space-y-2 p-5 rounded-[18px] bg-white border border-slate-100 shadow-xs">
            <div className="w-8 h-8 rounded-[10px] bg-blue-50 text-blue-600 flex items-center justify-center">
              <Zap className="w-4 h-4" />
            </div>
            <h4 className="text-[14px] font-bold text-[#0F172A]">Akıllı Sadeleştirme</h4>
            <p className="text-[12px] text-[#64748B] leading-relaxed">
              Gruplar arası karşılıklı borçları minimum sayıda doğrudan transfere indirir.
            </p>
          </div>

          <div className="space-y-2 p-5 rounded-[18px] bg-white border border-slate-100 shadow-xs">
            <div className="w-8 h-8 rounded-[10px] bg-purple-50 text-purple-600 flex items-center justify-center">
              <CreditCard className="w-4 h-4" />
            </div>
            <h4 className="text-[14px] font-bold text-[#0F172A]">FAST & IBAN</h4>
            <p className="text-[12px] text-[#64748B] leading-relaxed">
              Tek tıkla panoya kopyalanan IBAN ile kendi bankanızdan saniyede ödeşin.
            </p>
          </div>
        </div>
      </main>

      {/* 5. MINIMAL FOOTER */}
      <footer className="px-6 sm:px-12 py-8 border-t border-slate-100 text-center text-[12px] text-[#94A3B8]">
        <div className="max-w-5xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-3">
          <span>AradaPay © 2026 • Masadaki Ortak Finans</span>
          <span>KVKK m.11 Uyumlu • Cihaz İçi Veri Gizliliği</span>
        </div>
      </footer>
    </div>
  );
};

