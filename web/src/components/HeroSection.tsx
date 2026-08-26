'use client';

import React, { useState, useEffect } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import {
  QrCode,
  ShieldCheck,
  Terminal,
  Copy,
  Check,
  ArrowDownToLine
} from 'lucide-react';
import { ApkMetadata } from '@/lib/apk-info';

interface HeroSectionProps {
  apkInfo: ApkMetadata;
}

export function HeroSection({ apkInfo }: HeroSectionProps) {
  const [copiedHash, setCopiedHash] = useState(false);
  const [copiedAdb, setCopiedAdb] = useState(false);
  const [showQrModal, setShowQrModal] = useState(false);
  const [downloadUrl, setDownloadUrl] = useState('/AradaPay.apk');

  useEffect(() => {
    if (typeof window !== 'undefined') {
      setDownloadUrl(`${window.location.origin}/AradaPay.apk`);
    }
  }, []);

  const copyToClipboard = (text: string, type: 'hash' | 'adb') => {
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
    <section className="pt-28 pb-12 sm:pt-36 sm:pb-16 text-center bg-[#F8F9FA]">
      <div className="max-w-3xl mx-auto px-4 sm:px-6">
        {/* Top Product Pill */}
        <div className="inline-flex items-center space-x-2 bg-[#E8F5E9] text-[#00875A] border border-[#00875A]/20 px-3.5 py-1 rounded-full text-xs font-semibold mb-6">
          <span className="w-1.5 h-1.5 rounded-full bg-[#00875A]" />
          <span>Android 14+ • Material 3 • Clean Architecture</span>
        </div>

        {/* Hero Title */}
        <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold tracking-tight text-[#0F172A] leading-[1.12] mb-4">
          Grup harcamalarında <br />
          <span className="text-[#00875A]">borç döngülerine son.</span>
        </h1>

        {/* Subtitle */}
        <p className="text-base sm:text-lg text-[#64748B] max-w-xl mx-auto font-normal leading-relaxed mb-8">
          Dairesel borçları <strong>Graph-DFS algoritması</strong> ile %65 sadeleştiren, 
          <strong> Merkle Tree</strong> ile resmi dekont üreten yerel Android uygulaması.
        </p>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row items-center justify-center gap-3 mb-8">
          {/* Primary Download Button */}
          <a
            href="/AradaPay.apk"
            download="AradaPay.apk"
            className="w-full sm:w-auto inline-flex items-center justify-center space-x-2.5 bg-[#00875A] hover:bg-[#00754e] text-white font-semibold text-sm px-6 py-3.5 rounded-2xl shadow-sm transition-all active:scale-[0.98]"
          >
            <ArrowDownToLine className="w-4 h-4" />
            <span>AradaPay APK İndir</span>
            <span className="text-xs bg-black/20 text-white px-2 py-0.5 rounded-md font-mono">
              {apkInfo.sizeFormatted}
            </span>
          </a>

          {/* QR Code Trigger */}
          <button
            onClick={() => setShowQrModal(!showQrModal)}
            className="w-full sm:w-auto inline-flex items-center justify-center space-x-2 bg-white hover:bg-slate-50 text-[#0F172A] font-semibold text-sm px-5 py-3.5 rounded-2xl border border-slate-200 shadow-xs transition-all"
          >
            <QrCode className="w-4 h-4 text-[#00875A]" />
            <span>Telefondan Tara</span>
          </button>
        </div>

        {/* Live QR Modal */}
        {showQrModal && (
          <div className="max-w-xs mx-auto mb-8 p-5 bg-white border border-slate-200 rounded-3xl shadow-sm animate-fade-in text-center">
            <div className="inline-block p-2 bg-[#F8FAFC] rounded-2xl border border-slate-100 mb-2">
              <QRCodeSVG value={downloadUrl} size={150} level="H" />
            </div>
            <div className="text-xs font-semibold text-[#0F172A]">Mobil Kameranızla Tarayın</div>
            <div className="text-[11px] text-[#64748B] mt-0.5 mb-2">
              APK dosyası doğrudan indirilir.
            </div>
          </div>
        )}

        {/* Technical Badges */}
        <div className="flex flex-wrap items-center justify-center gap-2 text-xs">
          <div className="inline-flex items-center space-x-2 bg-white border border-slate-200 px-3 py-1.5 rounded-xl shadow-2xs">
            <ShieldCheck className="w-3.5 h-3.5 text-[#00875A]" />
            <span className="font-mono text-[#64748B] text-[11px]">
              SHA-256: {apkInfo.sha256.slice(0, 10)}...{apkInfo.sha256.slice(-6)}
            </span>
            <button
              onClick={() => copyToClipboard(apkInfo.sha256, 'hash')}
              className="text-[#0F172A] hover:text-[#00875A] pl-1 font-semibold"
              title="Hash Kopyala"
            >
              {copiedHash ? <Check className="w-3.5 h-3.5 text-[#00875A]" /> : <Copy className="w-3.5 h-3.5" />}
            </button>
          </div>

          <div className="inline-flex items-center space-x-2 bg-white border border-slate-200 px-3 py-1.5 rounded-xl shadow-2xs">
            <Terminal className="w-3.5 h-3.5 text-[#64748B]" />
            <span className="font-mono text-[#0F172A] text-[11px]">adb install AradaPay.apk</span>
            <button
              onClick={() => copyToClipboard('adb install AradaPay.apk', 'adb')}
              className="text-[#0F172A] hover:text-[#00875A] pl-1 font-semibold"
              title="Kopyala"
            >
              {copiedAdb ? <Check className="w-3.5 h-3.5 text-[#00875A]" /> : <Copy className="w-3.5 h-3.5" />}
            </button>
          </div>
        </div>

        {/* Stats Strip */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 max-w-2xl mx-auto mt-10 pt-8 border-t border-black/[0.06]">
          <div>
            <div className="text-2xl font-bold text-[#0F172A] font-tabular">%65</div>
            <div className="text-xs text-[#64748B] mt-0.5">Sadeleştirme</div>
          </div>
          <div>
            <div className="text-2xl font-bold text-[#00875A] font-tabular">&lt;100ms</div>
            <div className="text-xs text-[#64748B] mt-0.5">ML Kit QR</div>
          </div>
          <div>
            <div className="text-2xl font-bold text-[#0F172A] font-tabular">0,00 ₺</div>
            <div className="text-xs text-[#64748B] mt-0.5">Dekont Masrafı</div>
          </div>
          <div>
            <div className="text-2xl font-bold text-[#0F172A]">100%</div>
            <div className="text-xs text-[#64748B] mt-0.5">Kotlin & MVI</div>
          </div>
        </div>
      </div>
    </section>
  );
}
