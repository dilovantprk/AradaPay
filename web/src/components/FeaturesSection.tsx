import React from 'react';
import {
  Layers,
  FileText,
  Eye,
  QrCode,
  Coins,
  Cpu
} from 'lucide-react';

const features = [
  {
    icon: Layers,
    title: 'DFS Borç Sadeleştirme',
    desc: 'Dairesel borçları analiz ederek FAST transfer yükünü %65 azaltır.',
    badge: '%65 Tasarruf',
  },
  {
    icon: FileText,
    title: 'Merkle Tree Dekontları',
    desc: 'Sıfır gas ücretiyle değiştirilemez PDF banka dekontları üretir.',
    badge: 'BKM Mührü',
  },
  {
    icon: Eye,
    title: 'Gizlilik Modu & Biyometri',
    desc: 'MaskedFinancialText ile bakiyeleri "•••• ₺" olarak tek tıkla gizleyin.',
    badge: 'KVKK & GDPR',
  },
  {
    icon: QrCode,
    title: 'Temassız QR Mutabakat',
    desc: 'Google ML Kit Vision ve CameraX ile 100ms altında hızlı tarama.',
    badge: '<100ms ML Kit',
  },
  {
    icon: Coins,
    title: 'Çoklu Para Birimi',
    desc: 'Eşit, yüzdelik veya tutar bazlı bölüşüm. TRY, USD ve EUR desteği.',
    badge: 'TRY • USD • EUR',
  },
  {
    icon: Cpu,
    title: 'Clean Architecture & MVI',
    desc: '%100 saf Kotlin domain mantığı ve reaktif Jetpack Compose Material 3.',
    badge: 'Kotlin 100%',
  },
];

export function FeaturesSection() {
  return (
    <section id="ozellikler" className="py-16 sm:py-24 bg-[#FBFBFD]">
      <div className="max-w-4xl mx-auto px-4 sm:px-6">
        <div className="text-center max-w-xl mx-auto mb-10">
          <div className="text-xs font-semibold text-[#00875A] tracking-wider uppercase mb-1">
            Özellikler
          </div>
          <h2 className="text-2xl sm:text-3xl font-semibold text-[#1D1D1F] tracking-tight">
            Mühendislik Standartları
          </h2>
          <p className="text-sm text-[#6E6E73] mt-2">
            Matematik, kriptografi ve yerel Android performansı.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
          {features.map((item, idx) => {
            const Icon = item.icon;
            return (
              <div
                key={idx}
                className="bg-white p-4 rounded-2xl border border-black/[0.06] shadow-xs flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-2.5">
                    <div className="w-8 h-8 rounded-lg bg-[#F2F2F7] text-[#1D1D1F] flex items-center justify-center">
                      <Icon className="w-4 h-4" />
                    </div>
                    <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-[#F2F2F7] text-[#1D1D1F]">
                      {item.badge}
                    </span>
                  </div>
                  <h3 className="text-xs font-bold text-[#1D1D1F] mb-1">
                    {item.title}
                  </h3>
                  <p className="text-[11px] text-[#6E6E73] leading-relaxed">
                    {item.desc}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
