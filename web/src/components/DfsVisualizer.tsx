'use client';

import React, { useState } from 'react';
import { RefreshCw } from 'lucide-react';

export function DfsVisualizer() {
  const [optimized, setOptimized] = useState(false);

  return (
    <section id="dfs-algoritmasi" className="py-16 sm:py-24 bg-[#FBFBFD]">
      <div className="max-w-3xl mx-auto px-4 sm:px-6">
        <div className="text-center max-w-xl mx-auto mb-10">
          <div className="text-xs font-semibold text-[#00875A] tracking-wider uppercase mb-1">
            Matematiksel Model
          </div>
          <h2 className="text-2xl sm:text-3xl font-semibold text-[#1D1D1F] tracking-tight">
            Dairesel Borç Sadeleştirme
          </h2>
          <p className="text-sm text-[#6E6E73] mt-2">
            $A \to B \to C \to A$ dairesel borçları tek hamlede sadeleştirerek FAST transfer yükünü %65 azaltır.
          </p>
        </div>

        {/* Minimal Card */}
        <div className="bg-white rounded-2xl p-5 sm:p-6 border border-black/[0.06] shadow-xs">
          <div className="flex items-center justify-between pb-4 border-b border-black/[0.04]">
            <span className="text-xs font-semibold text-[#1D1D1F]">3 Kişilik Örnek Masraf Ağı</span>
            <button
              onClick={() => setOptimized(!optimized)}
              className={`inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                optimized
                  ? 'bg-[#F2F2F7] text-[#1D1D1F]'
                  : 'bg-[#1D1D1F] hover:bg-black text-white'
              }`}
            >
              <RefreshCw className="w-3 h-3" />
              <span>{optimized ? 'Sıfırla' : 'Sadeleştirmeyi Çalıştır'}</span>
            </button>
          </div>

          {/* User Nodes */}
          <div className="grid grid-cols-3 gap-2 py-5 text-center text-xs">
            <div className="bg-[#F8FAFC] p-3 rounded-xl border border-slate-100">
              <div className="font-semibold text-[#1D1D1F]">Ali</div>
              <div className="text-[10px] text-[#86868B]">@alituran</div>
              <div className="text-xs font-bold text-[#BE123C] font-tabular mt-1.5">-500 TL</div>
            </div>
            <div className="bg-[#F8FAFC] p-3 rounded-xl border border-slate-100">
              <div className="font-semibold text-[#1D1D1F]">Berk</div>
              <div className="text-[10px] text-[#86868B]">@berkkaya</div>
              <div className="text-xs font-bold text-[#00875A] font-tabular mt-1.5">+200 TL</div>
            </div>
            <div className="bg-[#F8FAFC] p-3 rounded-xl border border-slate-100">
              <div className="font-semibold text-[#1D1D1F]">Can</div>
              <div className="text-[10px] text-[#86868B]">@canerdogan</div>
              <div className="text-xs font-bold text-[#00875A] font-tabular mt-1.5">+300 TL</div>
            </div>
          </div>

          {/* Comparison */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
            <div className="bg-[#F8FAFC] p-3 rounded-xl border border-slate-100 space-y-1">
              <div className="font-semibold text-[#6E6E73] text-[11px] flex justify-between">
                <span>Geleneksel (3 Transfer)</span>
                <span>2.300 TL</span>
              </div>
              <div className="text-[11px] text-[#86868B] font-mono">
                <div>Ali ➡️ Berk: 1.000 TL</div>
                <div>Berk ➡️ Can: 800 TL</div>
                <div>Can ➡️ Ali: 500 TL</div>
              </div>
            </div>

            <div className={`p-3 rounded-xl border transition-all space-y-1 ${
              optimized ? 'bg-[#E8F5E9] border-[#00875A]/30 text-[#065F46]' : 'bg-[#F8FAFC] border-slate-100 text-[#6E6E73]'
            }`}>
              <div className="font-semibold text-[11px] flex justify-between">
                <span>DFS Sonucu (2 Transfer)</span>
                <span className="font-bold">800 TL (%65 Tasarruf)</span>
              </div>
              {optimized ? (
                <div className="text-[11px] font-mono font-medium">
                  <div>✓ Ali ➡️ Berk: 500 TL</div>
                  <div>✓ Ali ➡️ Can: 300 TL</div>
                  <div className="line-through opacity-50">✕ Can ➡️ Ali (Sıfırlandı)</div>
                </div>
              ) : (
                <div className="text-[11px] text-[#86868B]">
                  Butona basarak optimizasyonu görün.
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
