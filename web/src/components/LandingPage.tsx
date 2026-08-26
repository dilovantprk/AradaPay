'use client';

import React, { useState } from 'react';
import {
  Download,
  Smartphone,
  Globe,
  Sparkles,
  ShieldCheck,
  Zap,
  ArrowRight,
  QrCode,
  CheckCircle2,
  Lock,
  Flame,
  Coffee,
  Home,
  Palmtree,
  Gamepad2,
  BellRing,
  EyeOff,
  Receipt,
  HeartHandshake,
  ChevronRight,
  Shield,
  Layers,
  Repeat
} from 'lucide-react';

interface LandingPageProps {
  onLaunchWebApp: () => void;
}

export const LandingPage: React.FC<LandingPageProps> = ({ onLaunchWebApp }) => {
  const [showQrModal, setShowQrModal] = useState(false);
  const [activeTabPreview, setActiveTabPreview] = useState<'equal' | 'cycle' | 'fast'>('cycle');

  const handleDirectDownload = () => {
    const link = document.createElement('a');
    link.href = '/AradaPay.apk';
    link.download = 'AradaPay.apk';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="min-h-screen bg-[#F5F5F7] text-[#1D1D1F] flex flex-col font-sans selection:bg-emerald-100 selection:text-emerald-900">
      {/* ========================================================================= */}
      {/* 1. APPLE HIG FROSTED HEADER */}
      {/* ========================================================================= */}
      <header className="sticky top-0 z-50 apple-glass border-b border-black/[0.06] px-4 sm:px-8 py-3.5 transition-all">
        <div className="max-w-6xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-[10px] bg-[#00875A] flex items-center justify-center text-white font-black text-[16px] shadow-sm shadow-emerald-900/20">
              AP
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-[17px] font-bold text-[#1D1D1F] tracking-tight leading-none">
                  Arada<span className="text-[#00875A]">Pay</span>
                </span>
                <span className="px-1.5 py-0.5 rounded-full bg-emerald-100 text-[#00875A] text-[10px] font-bold">
                  v1.0
                </span>
              </div>
              <span className="text-[11px] text-[#86868B] font-medium hidden sm:block">
                Sosyal Finans ve Harcama Paylaşımı
              </span>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={onLaunchWebApp}
              className="px-4 py-2 rounded-full bg-black/5 hover:bg-black/10 active:scale-[0.97] text-[#1D1D1F] text-[13px] font-semibold transition flex items-center gap-1.5"
            >
              <Globe className="w-3.5 h-3.5 text-[#86868B]" />
              <span>Web Sürümü</span>
            </button>

            <button
              onClick={handleDirectDownload}
              className="px-4 py-2 rounded-full bg-[#00875A] hover:bg-[#00744d] active:scale-[0.97] text-white text-[13px] font-semibold transition shadow-sm shadow-emerald-700/20 flex items-center gap-1.5"
            >
              <Download className="w-3.5 h-3.5 stroke-[2.5]" />
              <span>APK İndir</span>
            </button>
          </div>
        </div>
      </header>

      {/* ========================================================================= */}
      {/* 2. HERO SECTION (Apple Hardware/Software Launch Aesthetic) */}
      {/* ========================================================================= */}
      <section className="px-4 sm:px-8 pt-16 pb-20 md:pt-24 md:pb-32 relative overflow-hidden text-center">
        <div className="max-w-4xl mx-auto space-y-7 relative z-10">
          {/* Release Badge */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/80 border border-black/[0.06] shadow-apple-sm text-[12px] font-semibold text-[#1D1D1F] backdrop-blur-md">
            <span className="w-2 h-2 rounded-full bg-[#00875A] animate-pulse" />
            <span>Türkiye'nin İlk DFS Döngü Sıfırlamalı Finans Uygulaması</span>
          </div>

          {/* Master Headline */}
          <h1 className="text-4xl sm:text-6xl md:text-7xl font-extrabold tracking-[-0.03em] text-[#1D1D1F] leading-[1.08] max-w-3xl mx-auto">
            Ortamda harca. <br className="hidden sm:block" />
            <span className="bg-gradient-to-r from-[#00875A] via-[#059669] to-[#047857] bg-clip-text text-transparent">
              Saniyede fitleş.
            </span>
          </h1>

          {/* Subtitle */}
          <p className="text-[17px] sm:text-[20px] text-[#86868B] max-w-2xl mx-auto font-normal leading-relaxed">
            Arkadaş ortamında yapılan harcamaları tek tıkla bölüştürün, borç zincirlerini banka transferi yapmadan sıfırlayın ve FAST ile anında hesaplaşın.
          </p>

          {/* Primary Action Buttons */}
          <div className="flex flex-col sm:flex-row items-center justify-center gap-3.5 pt-3">
            <button
              onClick={handleDirectDownload}
              className="w-full sm:w-auto px-7 py-3.5 rounded-full bg-[#00875A] hover:bg-[#00744d] active:scale-[0.97] text-white text-[15px] font-semibold transition shadow-md shadow-emerald-700/25 flex items-center justify-center gap-2"
            >
              <Download className="w-4 h-4 stroke-[2.5]" />
              <span>Android APK İndir (v1.0.0)</span>
            </button>

            <button
              onClick={onLaunchWebApp}
              className="w-full sm:w-auto px-7 py-3.5 rounded-full bg-white hover:bg-slate-50 border border-black/[0.08] active:scale-[0.97] text-[#1D1D1F] text-[15px] font-semibold transition shadow-apple-sm flex items-center justify-center gap-2"
            >
              <span>Web'den Hemen Dene</span>
              <ArrowRight className="w-4 h-4 text-[#86868B]" />
            </button>

            <button
              onClick={() => setShowQrModal(true)}
              className="p-3.5 rounded-full bg-white hover:bg-slate-50 border border-black/[0.08] text-[#86868B] hover:text-[#1D1D1F] active:scale-[0.97] transition shadow-apple-sm hidden md:flex items-center justify-center"
              title="Karekod ile Telefona İndir"
            >
              <QrCode className="w-5 h-5" />
            </button>
          </div>

          <p className="text-[12px] text-[#86868B] pt-1">
            Android 8.0+ ve tüm modern web tarayıcılarıyla %100 uyumludur. Reklamsız & ücretsiz.
          </p>
        </div>

        {/* ========================================================================= */}
        {/* 3. INTERACTIVE HARDWARE DEVICE SHOWCASE */}
        {/* ========================================================================= */}
        <div className="max-w-4xl mx-auto mt-14 sm:mt-20">
          <div className="relative mx-auto rounded-[38px] sm:rounded-[48px] p-3 sm:p-4 bg-gradient-to-b from-[#E5E5EA] to-[#D1D1D6] shadow-[0_30px_90px_rgba(0,0,0,0.14)] border border-white/60">
            {/* Screen Bezel */}
            <div className="bg-[#F2F2F7] rounded-[30px] sm:rounded-[40px] overflow-hidden border border-black/[0.06] shadow-inner text-left">
              {/* Dynamic Island / Device Notch */}
              <div className="bg-white px-6 py-3 flex items-center justify-between border-b border-black/[0.04]">
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 rounded-full bg-[#FF5F56] border border-black/10" />
                  <div className="w-3 h-3 rounded-full bg-[#FFBD2E] border border-black/10" />
                  <div className="w-3 h-3 rounded-full bg-[#27C93F] border border-black/10" />
                </div>
                <div className="px-3 py-0.5 rounded-full bg-black/5 text-[11px] font-mono text-[#86868B]">
                  aradapay.app/live-preview
                </div>
                <div className="w-8" />
              </div>

              {/* Device Screen Body */}
              <div className="p-6 sm:p-8 space-y-6">
                {/* Interactive Demo Selector */}
                <div className="flex items-center justify-center gap-2 p-1 bg-black/5 rounded-full max-w-md mx-auto">
                  <button
                    onClick={() => setActiveTabPreview('cycle')}
                    className={`flex-1 py-1.5 rounded-full text-[12px] font-semibold transition ${
                      activeTabPreview === 'cycle'
                        ? 'bg-white text-[#1D1D1F] shadow-apple-sm'
                        : 'text-[#86868B]'
                    }`}
                  >
                    🔄 DFS Döngü Sıfırlama
                  </button>
                  <button
                    onClick={() => setActiveTabPreview('equal')}
                    className={`flex-1 py-1.5 rounded-full text-[12px] font-semibold transition ${
                      activeTabPreview === 'equal'
                        ? 'bg-white text-[#1D1D1F] shadow-apple-sm'
                        : 'text-[#86868B]'
                    }`}
                  >
                    🍰 Akıllı Masraf Bölüşümü
                  </button>
                  <button
                    onClick={() => setActiveTabPreview('fast')}
                    className={`flex-1 py-1.5 rounded-full text-[12px] font-semibold transition ${
                      activeTabPreview === 'fast'
                        ? 'bg-white text-[#1D1D1F] shadow-apple-sm'
                        : 'text-[#86868B]'
                    }`}
                  >
                    ⚡ FAST & TR-Karekod
                  </button>
                </div>

                {/* Preview Card 1: DFS Cycle */}
                {activeTabPreview === 'cycle' && (
                  <div className="bg-white rounded-[24px] p-6 border border-black/[0.04] shadow-apple-card space-y-4 animate-applePop">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <div className="p-2 rounded-xl bg-emerald-50 text-[#00875A]">
                          <Repeat className="w-5 h-5" />
                        </div>
                        <div>
                          <h4 className="text-[15px] font-bold text-[#1D1D1F]">
                            3 Kişilik Borç Döngüsü Tespit Edildi
                          </h4>
                          <span className="text-[12px] text-[#00875A] font-semibold">
                            Sıfır Banka Transferi ile 320 ₺ İtfa Edilebilir
                          </span>
                        </div>
                      </div>
                      <span className="px-2.5 py-1 rounded-full bg-emerald-100 text-[#00875A] text-[11px] font-bold">
                        %100 Doğrulanmış
                      </span>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 pt-2">
                      <div className="p-3.5 rounded-[16px] bg-[#F2F2F7] border border-black/[0.03]">
                        <span className="text-[11px] text-[#86868B] block font-medium">1. Adım</span>
                        <div className="text-[13px] font-bold text-[#1D1D1F] mt-1">
                          Arda ➔ Dilovan: <span className="text-[#00875A]">320 ₺</span>
                        </div>
                      </div>
                      <div className="p-3.5 rounded-[16px] bg-[#F2F2F7] border border-black/[0.03]">
                        <span className="text-[11px] text-[#86868B] block font-medium">2. Adım</span>
                        <div className="text-[13px] font-bold text-[#1D1D1F] mt-1">
                          Dilovan ➔ Caner: <span className="text-[#00875A]">320 ₺</span>
                        </div>
                      </div>
                      <div className="p-3.5 rounded-[16px] bg-[#F2F2F7] border border-black/[0.03]">
                        <span className="text-[11px] text-[#86868B] block font-medium">3. Adım</span>
                        <div className="text-[13px] font-bold text-[#1D1D1F] mt-1">
                          Caner ➔ Arda: <span className="text-[#00875A]">320 ₺</span>
                        </div>
                      </div>
                    </div>

                    <div className="p-3 rounded-[14px] bg-emerald-50/70 border border-emerald-200/60 flex items-center justify-between text-[13px] font-semibold text-[#00875A]">
                      <span>💡 Sonuç: Kimse kimseye para yollamadan tüm borçlar kapandı!</span>
                      <button
                        onClick={onLaunchWebApp}
                        className="px-3 py-1 rounded-full bg-[#00875A] text-white text-[11px] font-bold hover:bg-[#00744d] transition"
                      >
                        Dene
                      </button>
                    </div>
                  </div>
                )}

                {/* Preview Card 2: Equal Split */}
                {activeTabPreview === 'equal' && (
                  <div className="bg-white rounded-[24px] p-6 border border-black/[0.04] shadow-apple-card space-y-4 animate-applePop">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-2xl bg-amber-50 flex items-center justify-center text-xl">
                          🍽️
                        </div>
                        <div>
                          <h4 className="text-[15px] font-bold text-[#1D1D1F]">
                            Kadıköy Akşam Yemeği
                          </h4>
                          <span className="text-[12px] text-[#86868B]">
                            Toplam: 1.280,00 ₺ • 4 Kişi Eşit Pay
                          </span>
                        </div>
                      </div>
                      <div className="text-right">
                        <span className="text-[11px] text-[#86868B] block font-medium">Kişi Başı</span>
                        <span className="text-[16px] font-extrabold text-[#00875A] font-tabular">
                          320,00 ₺
                        </span>
                      </div>
                    </div>

                    <div className="flex items-center gap-2 pt-2">
                      {['Arda (Ödedi)', 'Dilovan (+320 ₺)', 'Caner (+320 ₺)', 'Selin (+320 ₺)'].map(
                        (name, idx) => (
                          <div
                            key={name}
                            className={`flex-1 p-2.5 rounded-[14px] text-center text-[12px] font-bold border ${
                              idx === 0
                                ? 'bg-emerald-50 border-[#00875A] text-[#00875A]'
                                : 'bg-[#F2F2F7] border-black/[0.03] text-[#1D1D1F]'
                            }`}
                          >
                            {name}
                          </div>
                        )
                      )}
                    </div>
                  </div>
                )}

                {/* Preview Card 3: FAST */}
                {activeTabPreview === 'fast' && (
                  <div className="bg-white rounded-[24px] p-6 border border-black/[0.04] shadow-apple-card space-y-4 animate-applePop">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-2xl bg-blue-50 text-blue-600 flex items-center justify-center">
                          <Zap className="w-5 h-5" />
                        </div>
                        <div>
                          <h4 className="text-[15px] font-bold text-[#1D1D1F]">
                            7/24 FAST & Havale Entegrasyonu
                          </h4>
                          <span className="text-[12px] text-[#86868B]">
                            Garanti, Akbank, İş Bankası, Yapı Kredi, Ziraat, Papara
                          </span>
                        </div>
                      </div>
                      <span className="px-2.5 py-1 rounded-full bg-blue-50 text-blue-700 text-[11px] font-bold">
                        BKM FAST Uyumlu
                      </span>
                    </div>

                    <div className="p-3.5 rounded-[16px] bg-[#1C1C1E] text-white flex items-center justify-between">
                      <div className="truncate mr-2">
                        <span className="text-[10px] text-[#86868B] block font-mono">ALICI IBAN</span>
                        <span className="text-[13px] font-mono font-bold text-emerald-400">
                          TR33 0006 1005 1978 4567 1000 01
                        </span>
                      </div>
                      <span className="px-3 py-1 rounded-full bg-[#00875A] text-white text-[11px] font-bold">
                        Kopyalandı ✓
                      </span>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ========================================================================= */}
      {/* 4. APPLE HIG BENTO GRID (4 Pillars) */}
      {/* ========================================================================= */}
      <section className="px-4 sm:px-8 py-20 bg-white border-t border-black/[0.06]">
        <div className="max-w-6xl mx-auto space-y-12">
          <div className="text-center max-w-2xl mx-auto space-y-3">
            <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-[#1D1D1F]">
              Mühendislik Harikası. <br />
              Finansal Basitlik.
            </h2>
            <p className="text-[16px] text-[#86868B]">
              Karmaşık borç tablolarını unutun. AradaPay arka planda çalışan matematiksel algoritmalarla hesaplaşmayı saniyelere indirir.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* Bento Card 1: DFS Cycle (Double width on large) */}
            <div className="lg:col-span-2 p-8 rounded-[28px] bg-[#F5F5F7] border border-black/[0.04] shadow-apple-sm space-y-4 relative overflow-hidden group hover:border-black/[0.1] transition-all">
              <div className="w-12 h-12 rounded-[18px] bg-[#00875A] text-white flex items-center justify-center shadow-md shadow-emerald-900/20">
                <Repeat className="w-6 h-6" />
              </div>
              <div className="space-y-2 max-w-lg">
                <h3 className="text-[22px] font-bold text-[#1D1D1F] tracking-tight">
                  DFS Graf Algoritması ile Borç Döngüsü İtfası
                </h3>
                <p className="text-[14px] text-[#86868B] leading-relaxed">
                  A kişisi B'ye, B kişisi C'ye, C kişisi de A'ya borçlu olduğunda sistem bu kapalı döngüyü anında tespit eder. Tek bir banka havalesi yapmadan ve işlem ücreti ödemeden karşılıklı borçları otomatik olarak sıfırlar.
                </p>
              </div>
            </div>

            {/* Bento Card 2: Privacy Mode */}
            <div className="p-8 rounded-[28px] bg-[#F5F5F7] border border-black/[0.04] shadow-apple-sm space-y-4 group hover:border-black/[0.1] transition-all">
              <div className="w-12 h-12 rounded-[18px] bg-[#1D1D1F] text-white flex items-center justify-center shadow-md">
                <EyeOff className="w-6 h-6" />
              </div>
              <div className="space-y-2">
                <h3 className="text-[20px] font-bold text-[#1D1D1F] tracking-tight">
                  Gizlilik Modu & PIN Kasası
                </h3>
                <p className="text-[14px] text-[#86868B] leading-relaxed">
                  Toplu alanlarda bakiyelerinizi tek tuşla <code>•••• ₺</code> olarak maskeleyin. 4 haneli finansal PIN kodunuz SHA-256 ile cihazınızda şifrelenir.
                </p>
              </div>
            </div>

            {/* Bento Card 3: Cryptographic Merkle Receipts */}
            <div className="p-8 rounded-[28px] bg-[#F5F5F7] border border-black/[0.04] shadow-apple-sm space-y-4 group hover:border-black/[0.1] transition-all">
              <div className="w-12 h-12 rounded-[18px] bg-blue-600 text-white flex items-center justify-center shadow-md">
                <ShieldCheck className="w-6 h-6" />
              </div>
              <div className="space-y-2">
                <h3 className="text-[20px] font-bold text-[#1D1D1F] tracking-tight">
                  Merkle Tree Kriptografik Dekont
                </h3>
                <p className="text-[14px] text-[#86868B] leading-relaxed">
                  Her fitleşme ve harcama işlemi SHA-256 Merkle Ağacı kök özeti ile mühürlenir. İşlem sonradan asla tahrif edilemez veya değiştirilemez.
                </p>
              </div>
            </div>

            {/* Bento Card 4: Nudge / Dürtme */}
            <div className="lg:col-span-2 p-8 rounded-[28px] bg-[#F5F5F7] border border-black/[0.04] shadow-apple-sm space-y-4 group hover:border-black/[0.1] transition-all">
              <div className="w-12 h-12 rounded-[18px] bg-amber-500 text-white flex items-center justify-center shadow-md">
                <BellRing className="w-6 h-6" />
              </div>
              <div className="space-y-2 max-w-lg">
                <h3 className="text-[22px] font-bold text-[#1D1D1F] tracking-tight">
                  Kibar Hatırlatma & "Dürtme" Motoru
                </h3>
                <p className="text-[14px] text-[#86868B] leading-relaxed">
                  "Borcunu ne zaman atıyorsun?" diye sormaya çekinmeyin. Tek tıkla eğlenceli hazır şablonlarla arkadaşınızı dürtün, bildirimle hesaplaşın.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ========================================================================= */}
      {/* 5. CALL TO ACTION FOOTER */}
      {/* ========================================================================= */}
      <section className="px-4 sm:px-8 py-20 bg-[#1D1D1F] text-white text-center">
        <div className="max-w-3xl mx-auto space-y-6">
          <h2 className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Arkadaş grubunuzda finansal huzur başlayın.
          </h2>
          <p className="text-[16px] text-[#86868B] max-w-xl mx-auto">
            AradaPay'i şimdi indirin veya tarayıcınızdan anında ücretsiz kullanmaya başlayın.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-3.5 pt-4">
            <button
              onClick={handleDirectDownload}
              className="w-full sm:w-auto px-8 py-4 rounded-full bg-[#00875A] hover:bg-[#00744d] active:scale-[0.97] text-white text-[15px] font-bold transition shadow-lg shadow-emerald-950 flex items-center justify-center gap-2"
            >
              <Download className="w-5 h-5 stroke-[2.5]" />
              <span>Android APK İndir (v1.0.0)</span>
            </button>

            <button
              onClick={onLaunchWebApp}
              className="w-full sm:w-auto px-8 py-4 rounded-full bg-white/10 hover:bg-white/20 active:scale-[0.97] text-white text-[15px] font-bold transition flex items-center justify-center gap-2"
            >
              <Globe className="w-5 h-5" />
              <span>Web Versiyonunu Aç</span>
            </button>
          </div>

          <div className="pt-12 text-[12px] text-[#86868B] border-t border-white/10 flex flex-col sm:flex-row items-center justify-between gap-4">
            <span>© 2026 AradaPay (ArdaBank FinTech). Tüm hakları saklıdır.</span>
            <span>KVKK m.11 ve 6698 Sayılı Kanun Uyumlu</span>
          </div>
        </div>
      </section>

      {/* ========================================================================= */}
      {/* 6. QR CODE DOWNLOAD MODAL (Apple Style Floating Sheet) */}
      {/* ========================================================================= */}
      {showQrModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
          <div className="bg-white w-full max-w-sm rounded-[28px] p-6 shadow-apple-modal border border-black/[0.08] text-center space-y-5 animate-applePop">
            <div className="flex items-center justify-between">
              <h3 className="text-[17px] font-bold text-[#1D1D1F]">Telefona İndir</h3>
              <button
                onClick={() => setShowQrModal(false)}
                className="w-8 h-8 rounded-full bg-black/5 flex items-center justify-center text-[#86868B] hover:text-[#1D1D1F]"
              >
                ✕
              </button>
            </div>

            <div className="p-4 bg-[#F2F2F7] rounded-[20px] inline-block border border-black/[0.04]">
              <div className="w-48 h-48 bg-[#1D1D1F] rounded-[14px] p-4 flex flex-col items-center justify-center text-white">
                <QrCode className="w-32 h-32 text-white" />
                <span className="text-[10px] font-mono text-emerald-400 mt-2">
                  aradapay.app/download
                </span>
              </div>
            </div>

            <p className="text-[13px] text-[#86868B] leading-relaxed">
              Kameranızla bu karekodu okutarak <strong>AradaPay.apk</strong> dosyasını doğrudan Android cihazınıza indirin.
            </p>

            <button
              onClick={handleDirectDownload}
              className="w-full py-3 rounded-full bg-[#00875A] text-white font-bold text-[14px] hover:bg-[#00744d] active:scale-[0.97] transition"
            >
              Doğrudan APK İndir
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
