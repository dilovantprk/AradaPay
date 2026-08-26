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
  HeartHandshake
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
    <div className="min-h-screen bg-[#F8FAFC] text-textPrimary flex flex-col font-sans selection:bg-primaryEmeraldContainer selection:text-primaryEmerald">
      {/* 1. Top Navigation Bar */}
      <header className="sticky top-0 z-40 bg-white/90 backdrop-blur-md border-b border-surfaceBorder px-4 sm:px-6 py-3.5">
        <div className="max-w-6xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-10 h-10 rounded-[14px] bg-primaryEmerald flex items-center justify-center text-white font-black text-[18px] shadow-sm shadow-emerald-500/20">
              AP
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <h1 className="text-[19px] font-black text-textPrimary tracking-tight leading-none">
                  Arada<span className="text-primaryEmerald">Pay</span>
                </h1>
                <span className="px-1.5 py-0.5 rounded-full bg-emerald-100 text-primaryEmerald text-[10px] font-extrabold">
                  v1.0
                </span>
              </div>
              <span className="text-[11px] font-semibold text-textSecondary">
                Gençliğin Sosyal Finans Ekosistemi
              </span>
            </div>
          </div>

          <div className="flex items-center gap-2 sm:gap-3">
            {/* Web App CTA Button */}
            <button
              onClick={onLaunchWebApp}
              className="px-3.5 py-2 rounded-[12px] bg-surfaceContainerLow text-textPrimary text-[13px] font-bold hover:bg-slate-200 active:scale-95 transition flex items-center gap-1.5"
            >
              <Globe className="w-4 h-4 text-textSecondary" />
              <span className="hidden sm:inline">Web'den Devam Et</span>
              <span className="sm:hidden">Web</span>
            </button>

            {/* Primary Android Download CTA Button */}
            <button
              onClick={handleDirectDownload}
              className="px-4 py-2 rounded-[12px] bg-primaryEmerald text-white text-[13px] font-black hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-600/25 flex items-center gap-1.5"
            >
              <Download className="w-4 h-4 stroke-[2.5]" />
              <span>APK İndir</span>
            </button>
          </div>
        </div>
      </header>

      {/* 2. Hero Section - Gen Z High-Energy Social FinTech */}
      <section className="px-4 sm:px-6 pt-12 pb-16 md:pt-20 md:pb-24 bg-gradient-to-b from-white via-[#F8FAFC] to-[#F1F5F9] border-b border-surfaceBorder relative overflow-hidden">
        {/* Subtle Background Glows */}
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 w-[500px] h-[300px] bg-emerald-400/10 rounded-full blur-3xl pointer-events-none" />

        <div className="max-w-4xl mx-auto text-center space-y-6 relative z-10">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-emerald-50 border border-emerald-200 text-primaryEmerald text-[12px] font-extrabold shadow-2xs">
            <Flame className="w-3.5 h-3.5 fill-emerald-500 text-emerald-500" />
            <span>"Kanka sonra atarımcılar" tarihe karışıyor</span>
          </div>

          {/* Main Title */}
          <h2 className="text-[34px] sm:text-[50px] md:text-[62px] font-black text-textPrimary tracking-[-1.5px] leading-[1.08]">
            Ortamda Harca, <br />
            <span className="text-primaryEmerald bg-gradient-to-r from-emerald-600 to-teal-500 bg-clip-text text-transparent">
              Saniyede Fitleş.
            </span>
          </h2>

          {/* Subtitle */}
          <p className="text-[15px] sm:text-[18px] text-textSecondary max-w-2xl mx-auto font-normal leading-relaxed">
            Kahve, festival, ev kirası, halı saha veya cuma akşamı buluşması... Kim kime ne kadar borçlu kavgasına son. 
            AradaPay borç döngülerini otomatik sıfırlar, arkadaş grubunu anında eşitler.
          </p>

          {/* Dual Action Buttons */}
          <div className="pt-2 flex flex-col sm:flex-row items-center justify-center gap-3 max-w-md mx-auto">
            {/* Primary: Download Android APK */}
            <button
              onClick={handleDirectDownload}
              className="w-full sm:flex-1 h-[56px] rounded-[18px] bg-primaryEmerald text-white font-black text-[16px] flex items-center justify-center gap-2.5 hover:bg-[#00744d] active:scale-[0.98] transition shadow-lg shadow-emerald-600/25 group"
            >
              <Smartphone className="w-5 h-5 group-hover:scale-110 transition-transform" />
              <span>Android APK İndir</span>
              <Download className="w-4 h-4 ml-0.5" />
            </button>

            {/* Secondary: Launch Web App */}
            <button
              onClick={onLaunchWebApp}
              className="w-full sm:flex-1 h-[56px] rounded-[18px] bg-white border-2 border-slate-200 text-textPrimary font-bold text-[15px] flex items-center justify-center gap-2 hover:border-textPrimary hover:bg-slate-50 active:scale-[0.98] transition shadow-xs"
            >
              <Globe className="w-4 h-4 text-textSecondary" />
              <span>Web'den Hemen Dene</span>
              <ArrowRight className="w-4 h-4 text-textSecondary" />
            </button>
          </div>

          {/* Social Proof & Trust Badges */}
          <div className="pt-4 flex flex-wrap items-center justify-center gap-4 text-[12px] text-textSecondary font-semibold">
            <span className="flex items-center gap-1.5 bg-white px-3 py-1.5 rounded-full border border-slate-200 shadow-2xs">
              <CheckCircle2 className="w-4 h-4 text-primaryEmerald" />
              Android 14+ Uyumlu APK (v1.0)
            </span>
            <span className="flex items-center gap-1.5 bg-white px-3 py-1.5 rounded-full border border-slate-200 shadow-2xs">
              <ShieldCheck className="w-4 h-4 text-primaryEmerald" />
              %100 Güvenli & Virüssüz
            </span>
            <button
              onClick={() => setShowQrModal(true)}
              className="flex items-center gap-1.5 bg-emerald-50 text-primaryEmerald px-3 py-1.5 rounded-full border border-emerald-200 font-bold hover:bg-emerald-100 transition cursor-pointer"
            >
              <QrCode className="w-3.5 h-3.5" />
              <span>Telefondan Tara & Yükle</span>
            </button>
          </div>

          {/* Quick Stats Grid */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 max-w-2xl mx-auto pt-6">
            <div className="p-3.5 rounded-[18px] bg-white border border-slate-200/80 shadow-2xs">
              <div className="text-[22px] font-black text-textPrimary">%65</div>
              <div className="text-[11px] font-semibold text-textSecondary">Daha Az FAST Transferi</div>
            </div>
            <div className="p-3.5 rounded-[18px] bg-white border border-slate-200/80 shadow-2xs">
              <div className="text-[22px] font-black text-primaryEmerald">&lt;100ms</div>
              <div className="text-[11px] font-semibold text-textSecondary">Kamera QR Okuma</div>
            </div>
            <div className="p-3.5 rounded-[18px] bg-white border border-slate-200/80 shadow-2xs">
              <div className="text-[22px] font-black text-textPrimary">0,00 ₺</div>
              <div className="text-[11px] font-semibold text-textSecondary">Sıfır Komisyon</div>
            </div>
            <div className="p-3.5 rounded-[18px] bg-white border border-slate-200/80 shadow-2xs">
              <div className="text-[22px] font-black text-emerald-600">Tek Tık</div>
              <div className="text-[11px] font-semibold text-textSecondary">Borç Sıfırlama (DFS)</div>
            </div>
          </div>
        </div>
      </section>

      {/* 3. Gen Z Lifestyle Scenarios (Vibes & Hangouts) */}
      <section className="px-4 sm:px-6 py-16 max-w-6xl mx-auto w-full space-y-10">
        <div className="text-center space-y-2">
          <span className="text-[12px] font-extrabold text-primaryEmerald uppercase tracking-wider">
            HER ORTAMDA YANINDA
          </span>
          <h3 className="text-[28px] sm:text-[36px] font-black text-textPrimary tracking-tight">
            Nerede Harcadıysan Orada Fitleş
          </h3>
          <p className="text-[14px] text-textSecondary max-w-lg mx-auto">
            Grup sohbetlerinde "kim ne ödedi" tartışması bitti. AradaPay hayatın tam merkezinde.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Card 1: Kahve & Kafe */}
          <div className="p-5 rounded-[22px] bg-white border border-slate-200 shadow-xs hover:border-emerald-300 hover:shadow-md transition space-y-3">
            <div className="w-12 h-12 rounded-[16px] bg-amber-50 text-amber-600 flex items-center justify-center">
              <Coffee className="w-6 h-6 stroke-[2.2]" />
            </div>
            <h4 className="text-[17px] font-bold text-textPrimary">
              3. Dalga Kahve & Buluşmalar
            </h4>
            <p className="text-[13px] text-textSecondary leading-relaxed">
              Masada 4 latte, 2 cheesecake söylendi. Kim ödediyse harcamayı ekler, herkesin payı saniyede bölünür.
            </p>
          </div>

          {/* Card 2: Ev Arkadaşları */}
          <div className="p-5 rounded-[22px] bg-white border border-slate-200 shadow-xs hover:border-emerald-300 hover:shadow-md transition space-y-3">
            <div className="w-12 h-12 rounded-[16px] bg-blue-50 text-blue-600 flex items-center justify-center">
              <Home className="w-6 h-6 stroke-[2.2]" />
            </div>
            <h4 className="text-[17px] font-bold text-textPrimary">
              Öğrenci & Ev Arkadaşları
            </h4>
            <p className="text-[13px] text-textSecondary leading-relaxed">
              Kira, fiber internet, market ve deterjan masrafları. Ay sonu Excel tablosu açmadan bakiye kendiliğinden dengelenir.
            </p>
          </div>

          {/* Card 3: Festival & Tatil */}
          <div className="p-5 rounded-[22px] bg-white border border-slate-200 shadow-xs hover:border-emerald-300 hover:shadow-md transition space-y-3">
            <div className="w-12 h-12 rounded-[16px] bg-emerald-50 text-primaryEmerald flex items-center justify-center">
              <Palmtree className="w-6 h-6 stroke-[2.2]" />
            </div>
            <h4 className="text-[17px] font-bold text-textPrimary">
              Festival, Kaş & Airbnb
            </h4>
            <p className="text-[13px] text-textSecondary leading-relaxed">
              "Sen benzini aldın, ben villayı tuttum, o da mangalı ödedi." AradaPay tüm zinciri tek bir net hesaba indirger.
            </p>
          </div>

          {/* Card 4: Halı Saha & Gaming */}
          <div className="p-5 rounded-[22px] bg-white border border-slate-200 shadow-xs hover:border-emerald-300 hover:shadow-md transition space-y-3">
            <div className="w-12 h-12 rounded-[16px] bg-purple-50 text-purple-600 flex items-center justify-center">
              <Gamepad2 className="w-6 h-6 stroke-[2.2]" />
            </div>
            <h4 className="text-[17px] font-bold text-textPrimary">
              Halı Saha & PS Turnuvaları
            </h4>
            <p className="text-[13px] text-textSecondary leading-relaxed">
              Kişi başı 150₺'yi toplamak için 14 kişiye IBAN atmaya son. Grubu aç, herkes tek tıkla QR okutarak borcunu kapasın.
            </p>
          </div>
        </div>
      </section>

      {/* 4. Gen Z Killer Features Section */}
      <section className="px-4 sm:px-6 py-16 bg-white border-y border-surfaceBorder">
        <div className="max-w-5xl mx-auto space-y-12">
          <div className="text-center space-y-2">
            <span className="text-[12px] font-extrabold text-primaryEmerald uppercase tracking-wider">
              NEDEN BAŞKA HİÇBİR ŞEYE BENZEMİYOR?
            </span>
            <h3 className="text-[28px] sm:text-[36px] font-black text-textPrimary tracking-tight">
              Gençlerin Finansal Süper Güçleri
            </h3>
            <p className="text-[14px] text-textSecondary max-w-lg mx-auto">
              Klasik bankacılık hantallığını unutturan modern özellikler.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Feature 1: DFS Cycle Detection */}
            <div className="p-6 rounded-[24px] bg-[#F8FAFC] border border-slate-200/90 space-y-3.5 hover:shadow-md transition">
              <div className="w-12 h-12 rounded-[16px] bg-emerald-100 text-primaryEmerald flex items-center justify-center">
                <Sparkles className="w-6 h-6 stroke-[2.2]" />
              </div>
              <h4 className="text-[18px] font-bold text-textPrimary">
                Sihirli Borç Döngüsü (DFS)
              </h4>
              <p className="text-[13px] text-textSecondary leading-relaxed">
                Ali sana 200₺, sen Berk'e 200₺, Berk de Ali'ye 200₺ borçluysa; sistem döngüyü yakalar ve 3 ayrı transfer yapmak yerine borçları <strong>0 TL masrafla otomatik sıfırlar</strong>.
              </p>
            </div>

            {/* Feature 2: Nudge / Tatlı Dürtme */}
            <div className="p-6 rounded-[24px] bg-[#F8FAFC] border border-slate-200/90 space-y-3.5 hover:shadow-md transition">
              <div className="w-12 h-12 rounded-[16px] bg-rose-100 text-rose-600 flex items-center justify-center">
                <BellRing className="w-6 h-6 stroke-[2.2]" />
              </div>
              <h4 className="text-[18px] font-bold text-textPrimary">
                Tatlı Dürtme (Nudge)
              </h4>
              <p className="text-[13px] text-textSecondary leading-relaxed">
                "Kanka benim parayı ne zaman atacan?" diye sormaya çekinmene gerek yok. Tek tıkla arkadaşına esprili ve samimi bir dürtme bildirimi yolla.
              </p>
            </div>

            {/* Feature 3: Privacy Mode */}
            <div className="p-6 rounded-[24px] bg-[#F8FAFC] border border-slate-200/90 space-y-3.5 hover:shadow-md transition">
              <div className="w-12 h-12 rounded-[16px] bg-slate-200 text-slate-800 flex items-center justify-center">
                <EyeOff className="w-6 h-6 stroke-[2.2]" />
              </div>
              <h4 className="text-[18px] font-bold text-textPrimary">
                Meraklı Gözler Koruması
              </h4>
              <p className="text-[13px] text-textSecondary leading-relaxed">
                Metrobüste veya kafede arkandaki meraklı gözler bakiyeni görmesin. Tek tıkla tüm parasal tutarları <code className="bg-slate-300/60 px-1.5 py-0.5 rounded text-[12px] font-mono">•••• ₺</code> şeklinde anında maskele.
              </p>
            </div>

            {/* Feature 4: 100ms QR Fast */}
            <div className="p-6 rounded-[24px] bg-[#F8FAFC] border border-slate-200/90 space-y-3.5 hover:shadow-md transition">
              <div className="w-12 h-12 rounded-[16px] bg-amber-100 text-amber-700 flex items-center justify-center">
                <Zap className="w-6 h-6 stroke-[2.2]" />
              </div>
              <h4 className="text-[18px] font-bold text-textPrimary">
                100ms QR ile Temassız Fitleş
              </h4>
              <p className="text-[13px] text-textSecondary leading-relaxed">
                26 haneli IBAN numarası kopyalamakla uğraşma. Arkadaşının telefonundaki QR'ı kameraya tut, 100 milisaniyede banka uygulamana aktarılıp fitleş.
              </p>
            </div>

            {/* Feature 5: Cryptographic Receipt */}
            <div className="p-6 rounded-[24px] bg-[#F8FAFC] border border-slate-200/90 space-y-3.5 hover:shadow-md transition">
              <div className="w-12 h-12 rounded-[16px] bg-blue-100 text-blue-600 flex items-center justify-center">
                <Receipt className="w-6 h-6 stroke-[2.2]" />
              </div>
              <h4 className="text-[18px] font-bold text-textPrimary">
                Merkle Mühürlü Havalı Dekont
              </h4>
              <p className="text-[13px] text-textSecondary leading-relaxed">
                Bakkal defteri gibi değil; SHA-256 Merkle ağacıyla mühürlenen, resmi ve havalı PDF bilet dekontu üret. Kimse "ben bunu ödemiştim" diyemesin.
              </p>
            </div>

            {/* Feature 6: Tag-based Friends */}
            <div className="p-6 rounded-[24px] bg-[#F8FAFC] border border-slate-200/90 space-y-3.5 hover:shadow-md transition">
              <div className="w-12 h-12 rounded-[16px] bg-emerald-100 text-emerald-700 flex items-center justify-center">
                <HeartHandshake className="w-6 h-6 stroke-[2.2]" />
              </div>
              <h4 className="text-[18px] font-bold text-textPrimary">
                Discord Tarzı @Tag Etiketleri
              </h4>
              <p className="text-[13px] text-textSecondary leading-relaxed">
                Telefon numarası paylaşmana gerek yok! <span className="font-mono font-bold text-primaryEmerald">@kaan#5674</span> gibi kişisel oyuncu etiketinle arkadaş ekle, hızlıca masrafa dahil et.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* 5. Mobile App vs Web Experience (Highlighting Native APK benefits) */}
      <section className="px-4 sm:px-6 py-16 bg-[#F1F5F9]/70 border-b border-surfaceBorder">
        <div className="max-w-4xl mx-auto space-y-8">
          <div className="text-center space-y-1.5">
            <h3 className="text-[26px] sm:text-[32px] font-black text-textPrimary tracking-tight">
              En İyi Deneyim İçin: Android APK
            </h3>
            <p className="text-[14px] text-textSecondary">
              İsteyen tarayıcıdan da devam edebilir ama native uygulama bambaşka bir seviye.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Native Android Card */}
            <div className="p-6 sm:p-7 rounded-[24px] bg-white border-2 border-primaryEmerald shadow-sm space-y-4 relative overflow-hidden">
              <div className="absolute top-0 right-0 bg-primaryEmerald text-white text-[10px] font-black uppercase px-3 py-1 rounded-bl-[14px] tracking-wider">
                TAM DONANIM GÜCÜ
              </div>

              <div className="flex items-center gap-3">
                <div className="w-11 h-11 rounded-2xl bg-emerald-100 text-primaryEmerald flex items-center justify-center">
                  <Smartphone className="w-6 h-6" />
                </div>
                <div>
                  <h4 className="text-[17px] font-black text-textPrimary">Android Mobil Uygulaması</h4>
                  <p className="text-[12px] text-primaryEmerald font-bold">100ms KameraX QR + Parmak İzi</p>
                </div>
              </div>

              <ul className="space-y-2.5 text-[13px] text-textSecondary font-medium">
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-primaryEmerald flex-shrink-0" />
                  <span>Kamera donanımıyla anında QR tarama (&lt;100ms)</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-primaryEmerald flex-shrink-0" />
                  <span>Parmak izi / FaceID ile finansal kasa kilidi</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-primaryEmerald flex-shrink-0" />
                  <span>Anlık dürtmeler ve harcama push bildirimleri</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-primaryEmerald flex-shrink-0" />
                  <span>İnternetsiz (Offline) şifreli yerel harcama kaydı</span>
                </li>
              </ul>

              <button
                onClick={handleDirectDownload}
                className="w-full py-3.5 rounded-[16px] bg-primaryEmerald text-white text-[14px] font-black flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-md shadow-emerald-600/20"
              >
                <Download className="w-4 h-4 stroke-[2.5]" />
                <span>Hemen APK İndir (42.7 MB)</span>
              </button>
            </div>

            {/* Web Experience Card */}
            <div className="p-6 sm:p-7 rounded-[24px] bg-white border border-slate-200 shadow-xs space-y-4">
              <div className="flex items-center gap-3">
                <div className="w-11 h-11 rounded-2xl bg-slate-100 text-textPrimary flex items-center justify-center">
                  <Globe className="w-6 h-6" />
                </div>
                <div>
                  <h4 className="text-[17px] font-bold text-textPrimary">AradaPay Web Sürümü</h4>
                  <p className="text-[12px] text-textSecondary font-medium">Kurulumsuz Doğrudan Tarayıcı</p>
                </div>
              </div>

              <ul className="space-y-2.5 text-[13px] text-textSecondary font-medium">
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-slate-400 flex-shrink-0" />
                  <span>İndirme gerekmez, tek tıkla doğrudan başlar</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-slate-400 flex-shrink-0" />
                  <span>Aynı Firebase veritabanıyla 1:1 canlı senkron</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-slate-400 flex-shrink-0" />
                  <span>Döngüsel borç çözümü ve Merkle dekont doğrulaması</span>
                </li>
                <li className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-slate-400 flex-shrink-0" />
                  <span>Masaüstü, Mac/PC ve iPad için optimize görünüm</span>
                </li>
              </ul>

              <button
                onClick={onLaunchWebApp}
                className="w-full py-3.5 rounded-[16px] bg-slate-100 text-textPrimary text-[14px] font-bold flex items-center justify-center gap-2 hover:bg-slate-200 active:scale-95 transition"
              >
                <Globe className="w-4 h-4 text-textSecondary" />
                <span>Web Uygulamasını Aç</span>
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* 6. Footer */}
      <footer className="px-4 sm:px-6 py-8 border-t border-surfaceBorder bg-white mt-auto">
        <div className="max-w-6xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4 text-[12px] text-textSecondary">
          <div className="flex items-center gap-2">
            <span className="font-bold text-textPrimary">AradaPay</span>
            <span>•</span>
            <span>Yeni Nesil Sosyal Finans Platformu</span>
            <span>•</span>
            <span>© 2026 ArdaBank Ekosistemi</span>
          </div>

          <div className="flex items-center gap-4 font-semibold">
            <button onClick={handleDirectDownload} className="text-primaryEmerald font-extrabold hover:underline">
              APK İndir
            </button>
            <button onClick={onLaunchWebApp} className="hover:text-textPrimary">
              Web Sürümü
            </button>
            <span className="text-slate-300">|</span>
            <span>KVKK m.11 Uyumlu</span>
          </div>
        </div>
      </footer>

      {/* QR Code Modal for Desktop Users */}
      {showQrModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
          <div className="bg-white w-full max-w-sm rounded-[26px] shadow-2xl p-6 text-center space-y-4 border border-slate-100">
            <div className="w-10 h-10 rounded-full bg-emerald-100 text-primaryEmerald flex items-center justify-center mx-auto">
              <QrCode className="w-5 h-5 stroke-[2.5]" />
            </div>
            <h3 className="text-[19px] font-black text-textPrimary">Telefondan Tara & Yükle</h3>
            <p className="text-[12px] text-textSecondary">
              Telefonunun kamerasını aşağıdaki QR koda tutarak doğrudan <strong>AradaPay.apk</strong> dosyasını anında telefonuna indir.
            </p>

            <div className="p-4 bg-[#F8FAFC] rounded-[20px] border border-slate-200 inline-block">
              <div className="w-44 h-44 bg-slate-900 rounded-xl p-3 flex flex-col items-center justify-center text-white">
                <QrCode className="w-28 h-28 text-white stroke-[1.5]" />
                <span className="text-[10px] font-mono text-emerald-400 mt-1">DOWNLOAD-ARADAPAY-APK</span>
              </div>
            </div>

            <div className="pt-2">
              <button
                onClick={() => setShowQrModal(false)}
                className="w-full py-2.5 rounded-[14px] bg-slate-100 text-textPrimary text-[13px] font-bold hover:bg-slate-200 transition"
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
