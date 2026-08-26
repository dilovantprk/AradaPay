'use client';

import React, { useState } from 'react';
import { Download, Terminal, ShieldCheck, Copy, ArrowDownToLine } from 'lucide-react';
import { ApkMetadata } from '@/lib/apk-info';

interface InstallationGuideProps {
  apkInfo: ApkMetadata;
}

export function InstallationGuide({ apkInfo }: InstallationGuideProps) {
  const [copiedHash, setCopiedHash] = useState(false);
  const [copiedAdb, setCopiedAdb] = useState(false);

  const copyText = (text: string, type: 'hash' | 'adb') => {
    navigator.clipboard.writeText(text);
    if (type === 'hash') {
      setCopiedHash(true);
      setTimeout(() => setCopiedHash(false), 2000);
    } else {
      setCopiedAdb(true);
      setTimeout(() => setCopiedAdb(false), 2000);
    }
  };

  return (
    <section id="kurulum" className="py-16 sm:py-24 bg-[#FBFBFD]">
      <div className="max-w-3xl mx-auto px-4 sm:px-6">
        <div className="text-center max-w-xl mx-auto mb-10">
          <div className="text-xs font-semibold text-[#00875A] tracking-wider uppercase mb-1">
            Kurulum
          </div>
          <h2 className="text-2xl sm:text-3xl font-semibold text-[#1D1D1F] tracking-tight">
            Android Kurulum & Doğrulama
          </h2>
          <p className="text-sm text-[#6E6E73] mt-2">
            En güncel sürümü doğrudan cihazınıza yükleyin.
          </p>
        </div>

        {/* 3 Step Row */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-2.5 mb-6 text-xs">
          <div className="bg-white p-4 rounded-2xl border border-black/[0.06] shadow-xs flex flex-col justify-between">
            <div>
              <div className="w-6 h-6 rounded-full bg-[#E8F5E9] text-[#00875A] font-bold text-[11px] flex items-center justify-center mb-2">
                1
              </div>
              <div className="font-bold text-[#1D1D1F] mb-1">APK'yı İndirin</div>
              <p className="text-[#6E6E73] text-[11px]">
                Web sitesinden paketi indirin ({apkInfo.sizeFormatted}).
              </p>
            </div>
            <a
              href="/AradaPay.apk"
              download="AradaPay.apk"
              className="mt-3 inline-flex items-center justify-center space-x-1 bg-[#1D1D1F] hover:bg-black text-white text-[11px] font-medium py-1.5 rounded-lg"
            >
              <ArrowDownToLine className="w-3 h-3" />
              <span>İndir</span>
            </a>
          </div>

          <div className="bg-white p-4 rounded-2xl border border-black/[0.06] shadow-xs">
            <div className="w-6 h-6 rounded-full bg-[#F2F2F7] text-[#1D1D1F] font-bold text-[11px] flex items-center justify-center mb-2">
              2
            </div>
            <div className="font-bold text-[#1D1D1F] mb-1">Yükleme İzni</div>
            <p className="text-[#6E6E73] text-[11px]">
              İndirilen dosyaya dokunup <em>"Bu kaynaktan izin ver"</em> seçeneğini onaylayın.
            </p>
          </div>

          <div className="bg-white p-4 rounded-2xl border border-black/[0.06] shadow-xs">
            <div className="w-6 h-6 rounded-full bg-[#F2F2F7] text-[#1D1D1F] font-bold text-[11px] flex items-center justify-center mb-2">
              3
            </div>
            <div className="font-bold text-[#1D1D1F] mb-1">Açın & Kullanın</div>
            <p className="text-[#6E6E73] text-[11px]">
              PIN kodunuzu belirleyip kullanmaya başlayın.
            </p>
          </div>
        </div>

        {/* Minimal Terminal & Hash Box */}
        <div className="bg-white rounded-2xl p-4 border border-black/[0.06] shadow-xs space-y-2 text-xs">
          <div className="flex items-center justify-between bg-[#F8FAFC] border border-slate-100 p-2.5 rounded-xl">
            <div>
              <span className="text-[10px] text-[#86868B] block font-mono">ADB Komutu</span>
              <code className="font-mono text-[#1D1D1F]">adb install AradaPay.apk</code>
            </div>
            <button
              onClick={() => copyText('adb install AradaPay.apk', 'adb')}
              className="text-[#1D1D1F] hover:underline text-[11px] font-medium"
            >
              {copiedAdb ? 'Kopyalandı ✓' : 'Kopyala'}
            </button>
          </div>

          <div className="flex items-center justify-between bg-[#F8FAFC] border border-slate-100 p-2.5 rounded-xl">
            <div className="truncate mr-2">
              <span className="text-[10px] text-[#86868B] block font-mono">
                SHA-256 Checksum ({apkInfo.updatedAtFormatted})
              </span>
              <code className="font-mono text-[#1D1D1F] truncate block text-[11px]">{apkInfo.sha256}</code>
            </div>
            <button
              onClick={() => copyText(apkInfo.sha256, 'hash')}
              className="text-[#1D1D1F] hover:underline text-[11px] font-medium flex-shrink-0"
            >
              {copiedHash ? 'Kopyalandı ✓' : 'Kopyala'}
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}
