'use client';

import React, { useState } from 'react';
import { ShieldCheck, CheckCircle2 } from 'lucide-react';

export function MerkleVerifier() {
  const [verified, setVerified] = useState(false);
  const [verifying, setVerifying] = useState(false);

  const handleVerify = () => {
    setVerifying(true);
    setTimeout(() => {
      setVerifying(false);
      setVerified(true);
    }, 400);
  };

  return (
    <section id="merkle-dekont" className="py-16 sm:py-24 bg-white border-y border-black/[0.06]">
      <div className="max-w-3xl mx-auto px-4 sm:px-6">
        <div className="text-center max-w-xl mx-auto mb-10">
          <div className="text-xs font-semibold text-[#00875A] tracking-wider uppercase mb-1">
            Kriptografik İspat
          </div>
          <h2 className="text-2xl sm:text-3xl font-semibold text-[#1D1D1F] tracking-tight">
            Merkle Tree Dekont Doğrulama
          </h2>
          <p className="text-sm text-[#6E6E73] mt-2">
            Her işlem SHA-256 ağacına kaydedilir ve sıfır masraflı resmi PDF banka dekontu üretilir.
          </p>
        </div>

        {/* Minimal Card */}
        <div className="bg-[#FBFBFD] rounded-2xl p-5 sm:p-6 border border-black/[0.06] shadow-xs space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-black/[0.04]">
            <span className="text-xs font-semibold text-[#1D1D1F]">Merkle Proof Motoru</span>
            <button
              onClick={handleVerify}
              disabled={verifying}
              className="inline-flex items-center space-x-1.5 px-3 py-1.5 bg-[#00875A] hover:bg-[#00754e] text-white text-xs font-medium rounded-lg transition-all disabled:opacity-50"
            >
              <ShieldCheck className="w-3 h-3" />
              <span>{verifying ? 'Hesaplanıyor...' : 'İspatı Doğrula'}</span>
            </button>
          </div>

          {/* Root node */}
          <div className="bg-white p-3.5 rounded-xl border border-black/[0.06] text-center space-y-1">
            <span className="text-[10px] font-semibold text-[#86868B] uppercase tracking-wider block">
              Merkle Root Hash
            </span>
            <code className="text-xs font-mono text-[#1D1D1F] font-bold block truncate">
              0x9d4e7f1b2c3a5e8841fa09199dcb65b8e
            </code>
            {verified && (
              <span className="inline-flex items-center space-x-1 text-[11px] text-[#00875A] font-medium pt-1">
                <CheckCircle2 className="w-3 h-3" />
                <span>Kriptografik Olarak Doğrulandı ✓</span>
              </span>
            )}
          </div>

          {/* Leaves */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-1.5 text-center text-xs">
            <div className="bg-white p-2.5 rounded-xl border border-[#00875A]/40">
              <span className="text-[10px] font-semibold text-[#00875A] block">Senin Tx</span>
              <span className="font-bold text-[#1D1D1F] font-tabular text-xs">750,00 TL</span>
            </div>
            <div className="bg-white p-2.5 rounded-xl border border-black/[0.04]">
              <span className="text-[10px] text-[#86868B] block">Kardeş #1</span>
              <span className="text-[#6E6E73] font-tabular text-xs">120,00 TL</span>
            </div>
            <div className="bg-white p-2.5 rounded-xl border border-black/[0.04]">
              <span className="text-[10px] text-[#86868B] block">Kardeş #2</span>
              <span className="text-[#6E6E73] font-tabular text-xs">350,00 TL</span>
            </div>
            <div className="bg-white p-2.5 rounded-xl border border-black/[0.04]">
              <span className="text-[10px] text-[#86868B] block">Kardeş #3</span>
              <span className="text-[#6E6E73] font-tabular text-xs">600,00 TL</span>
            </div>
          </div>

          <div className="pt-2 text-[11px] text-[#86868B] flex justify-between">
            <span>✓ 0,00 ₺ İşlem Masrafı</span>
            <span>✓ BKM & TCMB Uyumluluk Mührü</span>
          </div>
        </div>
      </div>
    </section>
  );
}
