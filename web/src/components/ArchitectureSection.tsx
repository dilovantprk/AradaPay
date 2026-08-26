import React from 'react';

export function ArchitectureSection() {
  return (
    <section className="py-16 sm:py-24 bg-white border-y border-black/[0.06]">
      <div className="max-w-4xl mx-auto px-4 sm:px-6">
        <div className="text-center max-w-xl mx-auto mb-10">
          <div className="text-xs font-semibold text-[#00875A] tracking-wider uppercase mb-1">
            Mimari
          </div>
          <h2 className="text-2xl sm:text-3xl font-semibold text-[#1D1D1F] tracking-tight">
            Hedvig Clean Architecture & MVI
          </h2>
          <p className="text-sm text-[#6E6E73] mt-2">
            Katı katman ayrımı, saf Kotlin domain mantığı ve reaktif Material 3 arayüzü.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
          {/* Domain Layer */}
          <div className="bg-[#FBFBFD] p-4 rounded-2xl border border-black/[0.06] shadow-xs space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-[10px] font-mono font-bold text-[#00875A] bg-[#E8F5E9] px-2 py-0.5 rounded">
                Domain
              </span>
              <span className="text-[10px] text-[#86868B]">Saf Kotlin</span>
            </div>
            <h3 className="text-xs font-bold text-[#1D1D1F]">İş Kuralları & Graf Motoru</h3>
            <p className="text-[11px] text-[#6E6E73] leading-relaxed">
              Android SDK bağımsız algoritmalar ve %100 birim test kapsamı.
            </p>
            <div className="text-[10px] text-[#1D1D1F] font-mono pt-1">
              • CrossSettlementDfsEngine<br />
              • DebtSimplifierEngine
            </div>
          </div>

          {/* Data Layer */}
          <div className="bg-[#FBFBFD] p-4 rounded-2xl border border-black/[0.06] shadow-xs space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-[10px] font-mono font-bold text-[#1D1D1F] bg-[#F2F2F7] px-2 py-0.5 rounded">
                Data
              </span>
              <span className="text-[10px] text-[#86868B]">Firebase & Kasa</span>
            </div>
            <h3 className="text-xs font-bold text-[#1D1D1F]">Kalıcılık & Şifreleme</h3>
            <p className="text-[11px] text-[#6E6E73] leading-relaxed">
              Cloud Firestore senkronizasyonu ve donanım şifreli yerel kasa.
            </p>
            <div className="text-[10px] text-[#1D1D1F] font-mono pt-1">
              • Firestore Realtime Sync<br />
              • EncryptedDataStore Vault
            </div>
          </div>

          {/* Presentation Layer */}
          <div className="bg-[#FBFBFD] p-4 rounded-2xl border border-black/[0.06] shadow-xs space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-[10px] font-mono font-bold text-[#1D1D1F] bg-[#F2F2F7] px-2 py-0.5 rounded">
                Presentation
              </span>
              <span className="text-[10px] text-[#86868B]">Jetpack Compose</span>
            </div>
            <h3 className="text-xs font-bold text-[#1D1D1F]">MVI & Reaktif Arayüz</h3>
            <p className="text-[11px] text-[#6E6E73] leading-relaxed">
              Unidirectional Data Flow ve Material You dinamik teması.
            </p>
            <div className="text-[10px] text-[#1D1D1F] font-mono pt-1">
              • Model-View-Intent (MVI)<br />
              • ML Kit CameraX Scanner
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
