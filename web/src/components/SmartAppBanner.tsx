'use client';

import React, { useState, useEffect } from 'react';
import { Download, X, Smartphone, Sparkles, Star } from 'lucide-react';

export const SmartAppBanner: React.FC = () => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    // Only show if not dismissed previously in this session
    const dismissed = sessionStorage.getItem('dismissed_app_banner');
    if (!dismissed) {
      setIsVisible(true);
    }
  }, []);

  const handleDismiss = () => {
    setIsVisible(false);
    sessionStorage.setItem('dismissed_app_banner', 'true');
  };

  if (!isVisible) return null;

  return (
    <aside aria-label="AradaPay Android Uygulamasını Yükleyin" className="px-4 pt-3 pb-1 md:hidden select-none animate-appleSheet">
      <div className="relative overflow-hidden rounded-[20px] bg-[#1C1C1E] text-white p-3.5 shadow-xl shadow-black/15 border border-white/[0.1] backdrop-blur-2xl flex items-center justify-between gap-3">
        {/* Ambient Gradient Glow */}
        <div className="absolute -top-12 -left-12 w-32 h-32 bg-[#00875A]/30 rounded-full blur-2xl pointer-events-none" />

        {/* Left: App Icon & Info */}
        <div className="flex items-center gap-3 min-w-0 z-10">
          <div className="relative flex-shrink-0">
            <div className="w-11 h-11 rounded-[12px] bg-gradient-to-tr from-[#00603e] to-[#00875A] flex items-center justify-center text-white font-black text-[18px] shadow-md border border-white/20">
              A
            </div>
            <div className="absolute -bottom-1 -right-1 w-4 h-4 rounded-full bg-[#34C759] border-2 border-[#1C1C1E] flex items-center justify-center">
              <Sparkles className="w-2 h-2 text-white" />
            </div>
          </div>

          <div className="truncate">
            <div className="flex items-center gap-1.5">
              <span className="text-[13px] font-extrabold text-white tracking-tight truncate">
                AradaPay Android
              </span>
              <span className="flex items-center text-[10px] font-bold text-amber-400 gap-0.5">
                <Star className="w-2.5 h-2.5 fill-amber-400" />
                <span>4.9</span>
              </span>
            </div>
            <p className="text-[11px] text-[#A1A1A6] truncate mt-0.5 font-medium">
              100ms Kamera QR & Parmak İzi Kasası
            </p>
          </div>
        </div>

        {/* Right: Actions */}
        <div className="flex items-center gap-2 flex-shrink-0 z-10">
          <a
            href="/AradaPay.apk"
            download="AradaPay.apk"
            className="px-4 py-1.5 rounded-full bg-[#00875A] hover:bg-[#00744d] active:scale-95 text-white font-extrabold text-[12px] transition flex items-center gap-1.5 shadow-sm shadow-emerald-900/40"
          >
            <Download className="w-3.5 h-3.5 stroke-[2.5]" />
            <span>YÜKLE</span>
          </a>

          <button
            onClick={handleDismiss}
            className="w-7 h-7 rounded-full bg-white/10 hover:bg-white/20 active:scale-90 text-[#8E8E93] hover:text-white flex items-center justify-center transition"
            title="Kapat"
          >
            <X className="w-3.5 h-3.5 stroke-[2.5]" />
          </button>
        </div>
      </div>
    </aside>
  );
};
