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
  Layers,
  ChevronRight,
  Users
} from 'lucide-react';

interface LandingPageProps {
  onLaunchWebApp: () => void;
}

export const LandingPage: React.FC<LandingPageProps> = ({ onLaunchWebApp }) => {
  const [showQrModal, setShowQrModal] = useState(false);

  const handleDirectDownload = () => {
    const link = document.createElement('a');
    link.href = '/AradaPay.apk';
    link.download = 'AradaPay.apk';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="min-h-screen bg-white text-textPrimary flex flex-col font-sans selection:bg-primaryEmeraldContainer selection:text-primaryEmerald">
      {/* 1. Header */}
      <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-surfaceBorder px-5 py-3.5">
        <div className="max-w-5xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-10 h-10 rounded-[12px] bg-primaryEmerald flex items-center justify-center text-white font-bold text-[16px] shadow-sm">
              AP
            </div>
            <div>
              <h1 className="text-[20px] font-bold text-textPrimary tracking-tight leading-none">
                AradaPay
              </h1>
              <span className="text-[10px] font-semibold text-textSecondary uppercase tracking-wider">
                ArdaBank FinTech
              </span>
            </div>
          </div>

          <div className="flex items-center gap-2.5">
            {/* Web App CTA Button */}
            <button
              onClick={onLaunchWebApp}
              className="px-3.5 py-2 rounded-[12px] bg-surfaceContainerLow text-textPrimary text-[13px] font-semibold hover:bg-slate-200 active:scale-95 transition flex items-center gap-1.5"
            >
              <Globe className="w-4 h-4 text-textSecondary" />
              <span className="hidden sm:inline">Web Sürümünü Aç</span>
              <span className="sm:hidden">Web</span>
            </button>

            {/* Primary Android Download CTA Button */}
            <button
              onClick={handleDirectDownload}
              className="px-4 py-2 rounded-[12px] bg-primaryEmerald text-white text-[13px] font-bold hover:bg-[#00744d] active:scale-95 transition shadow-sm flex items-center gap-1.5"
            >
              <Download className="w-4 h-4 stroke-[2.5]" />
              <span>Uygulamayı İndir</span>
            </button>
          </div>
        </div>
      </header>

      {/* 2. Hero Section */}
      <section className="px-5 pt-12 pb-16 md:pt-20 md:pb-24 border-b border-surfaceBorder bg-gradient-to-b from-white via-[#F8FAFC] to-white">
        <div className="max-w-4xl mx-auto text-center space-y-6">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-primaryEmeraldContainer border border-primaryEmerald/30 text-primaryEmerald text-[12px] font-bold shadow-2xs">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Yeni Nesil FinTech & DFS Akıllı Mahsuplaşma</span>
          </div>

          {/* Main Title */}
          <h2 className="text-[34px] sm:text-[48px] md:text-[56px] font-extrabold text-textPrimary tracking-[-1.5px] leading-[1.1]">
            Grup Harcamalarını & Borç Döngülerini <br className="hidden sm:block" />
            <span className="text-primaryEmerald">Tek Tıkla Sıfırlayın.</span>
          </h2>

          {/* Subtitle */}
          <p className="text-[16px] sm:text-[18px] text-textSecondary max-w-2xl mx-auto font-normal leading-relaxed">
            AradaPay, çok taraflı karmaşık borç ağlarını graph DFS optimizasyonu ile çözer. Banka transferi yapmadan döngüleri siler, her işlemi kriptografik Merkle Tree makbuzu ile mühürler.
          </p>

          {/* Dual CTA Actions (Primary: Download App, Secondary: Open Web) */}
          <div className="pt-4 flex flex-col sm:flex-row items-center justify-center gap-3.5 max-w-md mx-auto">
            {/* Primary: Download Android App */}
            <button
              onClick={handleDirectDownload}
              className="w-full sm:flex-1 h-[56px] rounded-[18px] bg-primaryEmerald text-white font-bold text-[16px] flex items-center justify-center gap-2.5 hover:bg-[#00744d] active:scale-[0.98] transition shadow-md group"
            >
              <Smartphone className="w-5 h-5 group-hover:scale-110 transition-transform" />
              <span>Android APK İndir</span>
              <Download className="w-4 h-4 ml-0.5" />
            </button>

            {/* Secondary: Launch Web App */}
            <button
              onClick={onLaunchWebApp}
              className="w-full sm:flex-1 h-[56px] rounded-[18px] bg-white border-2 border-slate-200 text-textPrimary font-bold text-[15px] flex items-center justify-center gap-2 hover:border-textPrimary hover:bg-slate-50 active:scale-[0.98] transition"
            >
              <Globe className="w-4 h-4 text-textSecondary" />
              <span>Web'den Devam Et</span>
              <ArrowRight className="w-4 h-4 text-textSecondary" />
            </button>
          </div>

          {/* Download Trust Badges & QR Trigger */}
          <div className="pt-2 flex flex-wrap items-center justify-center gap-4 text-[12px] text-textSecondary font-medium">
            <span className="flex items-center gap-1.5">
              <CheckCircle2 className="w-4 h-4 text-primaryEmerald" />
              Android 14+ Uyumlu APK (v1.0)
            </span>
            <span className="flex items-center gap-1.5">
              <ShieldCheck className="w-4 h-4 text-primaryEmerald" />
              %100 Güvenli & Virüssüz
            </span>
            <button
              onClick={() => setShowQrModal(true)}
              className="text-primaryEmerald font-bold hover:underline flex items-center gap-1 cursor-pointer"
            >
              <QrCode className="w-3.5 h-3.5" />
              <span>Telefondan Tara & İndir</span>
            </button>
          </div>
        </div>
      </section>

      {/* 3. Key Feature Cards (Why download the native app vs web) */}
      <section className="px-5 py-16 max-w-5xl mx-auto w-full space-y-10">
        <div className="text-center space-y-2">
          <span className="text-[12px] font-bold text-primaryEmerald uppercase tracking-wider">
            NEDEN ARADAPAY?
          </span>
          <h3 className="text-[28px] sm:text-[36px] font-bold text-textPrimary tracking-tight">
            Klasik Masraf Bölüşümünün Ötesinde
          </h3>
          <p className="text-[14px] text-textSecondary max-w-lg mx-auto">
            Gelişmiş graph teorisi ve banka seviyesinde güvenlik algoritmalarıyla donatıldı.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          {/* Card 1: DFS Cycle Detection */}
          <div className="p-6 rounded-[24px] bg-white border border-surfaceBorder shadow-xs hover:shadow-md transition space-y-3">
            <div className="w-12 h-12 rounded-[16px] bg-primaryEmeraldContainer flex items-center justify-center text-primaryEmerald">
              <Sparkles className="w-6 h-6 stroke-[2.2]" />
            </div>
            <h4 className="text-[18px] font-bold text-textPrimary">
              DFS Döngüsel Borç İtfası
            </h4>
            <p className="text-[13px] text-textSecondary leading-relaxed">
              A ➔ B ➔ C ➔ A şeklindeki borç döngülerini otomatik yakalar. Tek kuruş banka transfer ücreti ödemeden borçları karşılıklı siler.
            </p>
          </div>

          {/* Card 2: Merkle Tree Receipts */}
          <div className="p-6 rounded-[24px] bg-white border border-surfaceBorder shadow-xs hover:shadow-md transition space-y-3">
            <div className="w-12 h-12 rounded-[16px] bg-slate-100 flex items-center justify-center text-textPrimary">
              <Lock className="w-6 h-6 stroke-[2.2]" />
            </div>
            <h4 className="text-[18px] font-bold text-textPrimary">
              HMAC-SHA256 Merkle Mührü
            </h4>
            <p className="text-[13px] text-textSecondary leading-relaxed">
              Tüm ödemeler sıfır-maliyetli L2 Merkle ağacına bağlanır. Matematiksel olarak değiştirilemez banka dekontları üretir.
            </p>
          </div>

          {/* Card 3: Contactless QR & Biometrics */}
          <div className="p-6 rounded-[24px] bg-white border border-surfaceBorder shadow-xs hover:shadow-md transition space-y-3">
            <div className="w-12 h-12 rounded-[16px] bg-[#FFF1F2] flex items-center justify-center text-accentRose">
              <Zap className="w-6 h-6 stroke-[2.2]" />
            </div>
            <h4 className="text-[18px] font-bold text-textPrimary">
              Temassız FAST & QR Fitleşme
            </h4>
            <p className="text-[13px] text-textSecondary leading-relaxed">
              Kamera donanım hızlandırmasıyla 100ms altında temassız QR ile fitleşin, tek tıkla FAST IBAN kopyalayarak bankanızdan gönderin.
            </p>
          </div>
        </div>
      </section>

      {/* 4. App vs Web Feature Comparison (Incentivizing native download) */}
      <section className="px-5 py-12 bg-surfaceContainerLow/50 border-y border-surfaceBorder">
        <div className="max-w-4xl mx-auto space-y-6">
          <div className="text-center space-y-1">
            <h3 className="text-[24px] font-bold text-textPrimary">
              Hangi Sürüm Size Uygun?
            </h3>
            <p className="text-[13px] text-textSecondary">
              Hem web hem mobil aynı veritabanını paylaşır, ancak native uygulama ekstra donanım gücü sunar.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Native Android Card (Recommended) */}
            <div className="p-6 rounded-[22px] bg-white border-2 border-primaryEmerald shadow-sm space-y-4 relative overflow-hidden">
              <div className="absolute top-0 right-0 bg-primaryEmerald text-white text-[10px] font-extrabold uppercase px-3 py-1 rounded-bl-[12px]">
                Önerilen Deneyim
              </div>

              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-primaryEmeraldContainer text-primaryEmerald flex items-center justify-center">
                  <Smartphone className="w-5 h-5" />
                </div>
                <div>
                  <h4 className="text-[16px] font-bold text-textPrimary">Android Mobil Uygulaması</h4>
                  <p className="text-[12px] text-primaryEmerald font-semibold">Tam Donanım & Anlık Bildirim</p>
                </div>
              </div>

              <ul className="space-y-2 text-[13px] text-textSecondary">
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-primaryEmerald flex-shrink-0" />
                  <span>ML Kit CameraX ile 100ms Anlık QR Tarayıcı</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-primaryEmerald flex-shrink-0" />
                  <span>Parmak İzi / Yüz Tanıma (Biometric Vault)</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-primaryEmerald flex-shrink-0" />
                  <span>Anlık Push Bildirimleri (Dürtmeler & Onaylar)</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-primaryEmerald flex-shrink-0" />
                  <span>Çevrimdışı (Offline) Yerel Şifreli Kasa</span>
                </li>
              </ul>

              <button
                onClick={handleDirectDownload}
                className="w-full py-3 rounded-[14px] bg-primaryEmerald text-white text-[14px] font-bold flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-2xs"
              >
                <Download className="w-4 h-4" />
                <span>Hemen APK İndir (Ücretsiz)</span>
              </button>
            </div>

            {/* Web Version Card */}
            <div className="p-6 rounded-[22px] bg-white border border-slate-200 shadow-xs space-y-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-slate-100 text-textPrimary flex items-center justify-center">
                  <Globe className="w-5 h-5" />
                </div>
                <div>
                  <h4 className="text-[16px] font-bold text-textPrimary">AradaPay Web Sürümü</h4>
                  <p className="text-[12px] text-textSecondary">Tarayıcıdan Anında Erişim</p>
                </div>
              </div>

              <ul className="space-y-2 text-[13px] text-textSecondary">
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-slate-400 flex-shrink-0" />
                  <span>Kurulum gerektirmez, doğrudan tarayıcıda çalışır</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-slate-400 flex-shrink-0" />
                  <span>Aynı Firebase veritabanıyla canlı senkron</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-slate-400 flex-shrink-0" />
                  <span>Masaüstü ve tabletler için optimize edilmiş arayüz</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-slate-400 flex-shrink-0" />
                  <span>Harcama ekleme, grup yönetimi ve fitleşme</span>
                </li>
              </ul>

              <button
                onClick={onLaunchWebApp}
                className="w-full py-3 rounded-[14px] bg-surfaceContainerLow text-textPrimary text-[14px] font-bold flex items-center justify-center gap-2 hover:bg-slate-200 active:scale-95 transition"
              >
                <Globe className="w-4 h-4" />
                <span>Web Uygulamasını Başlat</span>
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* 5. Footer */}
      <footer className="px-5 py-8 border-t border-surfaceBorder bg-white mt-auto">
        <div className="max-w-5xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4 text-[12px] text-textSecondary">
          <div className="flex items-center gap-2">
            <span className="font-bold text-textPrimary">AradaPay</span>
            <span>•</span>
            <span>© 2026 ArdaBank Ekosistemi. Tüm hakları saklıdır.</span>
          </div>

          <div className="flex items-center gap-4 font-medium">
            <button onClick={handleDirectDownload} className="text-primaryEmerald font-bold hover:underline">
              APK İndir
            </button>
            <button onClick={onLaunchWebApp} className="hover:text-textPrimary">
              Web Uygulaması
            </button>
            <span className="text-slate-300">|</span>
            <span>KVKK m.11 Uyumlu</span>
          </div>
        </div>
      </footer>

      {/* QR Code Modal for Desktop Users */}
      {showQrModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
          <div className="bg-white w-full max-w-sm rounded-[24px] shadow-2xl p-6 text-center space-y-4">
            <h3 className="text-[18px] font-bold text-textPrimary">Telefondan İndirin</h3>
            <p className="text-[12px] text-textSecondary">
              Telefonunuzun kamerasını aşağıdaki QR koda tutarak doğrudan AradaPay APK dosyasını indirin.
            </p>

            <div className="p-4 bg-[#F8FAFC] rounded-[18px] border border-slate-200 inline-block">
              <div className="w-44 h-44 bg-slate-900 rounded-xl p-3 flex flex-col items-center justify-center text-white">
                <QrCode className="w-28 h-28 text-white stroke-[1.5]" />
                <span className="text-[10px] font-mono text-emerald-400 mt-1">DOWNLOAD-ARADAPAY-APK</span>
              </div>
            </div>

            <div className="pt-2">
              <button
                onClick={() => setShowQrModal(false)}
                className="w-full py-2.5 rounded-[12px] bg-slate-100 text-textPrimary text-[13px] font-bold hover:bg-slate-200 transition"
              >
                Kapat
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
