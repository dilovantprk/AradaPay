import React, { useState } from 'react';
import { User, Shield, Lock, Trash2, CheckCircle2, Copy, Check } from 'lucide-react';
import { User as UserType } from '../types';

interface SettingsViewProps {
  currentUser: UserType;
  isLocked: boolean;
  onToggleLock: () => void;
  onWipeData: () => void;
}

export const SettingsView: React.FC<SettingsViewProps> = ({
  currentUser,
  isLocked,
  onToggleLock,
  onWipeData
}) => {
  const [copiedIban, setCopiedIban] = useState(false);
  const [showWipeConfirm, setShowWipeConfirm] = useState(false);

  const handleCopyIban = () => {
    if (currentUser.iban) {
      navigator.clipboard.writeText(currentUser.iban.replace(/\s+/g, ''));
      setCopiedIban(true);
      setTimeout(() => setCopiedIban(false), 2000);
    }
  };

  return (
    <div className="pb-24 max-w-2xl mx-auto px-5 py-4 space-y-4">
      {/* Header */}
      <div>
        <h2 className="text-[24px] font-bold text-textPrimary">Ayarlar & Profil</h2>
        <p className="text-[12px] text-textSecondary">Hesap ve güvenlik tercihleri</p>
      </div>

      {/* User Profile Card */}
      <div className="p-5 rounded-[20px] bg-surfaceWhite border border-surfaceBorder shadow-xs space-y-4">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-full bg-primaryEmeraldContainer border-2 border-primaryEmerald flex items-center justify-center text-primaryEmerald font-bold text-[22px]">
            {currentUser.fullName.substring(0, 2).toUpperCase()}
          </div>
          <div>
            <h3 className="text-[18px] font-bold text-textPrimary">{currentUser.fullName}</h3>
            <p className="text-[13px] font-mono text-primaryEmerald font-bold">{currentUser.tag}</p>
            <p className="text-[12px] text-textSecondary">{currentUser.email}</p>
          </div>
        </div>

        {/* IBAN Card */}
        {currentUser.iban && (
          <div className="p-3 rounded-[14px] bg-[#F8FAFC] border border-slate-200 flex items-center justify-between">
            <div>
              <span className="text-[10px] font-bold text-textSecondary uppercase tracking-wider block">
                KAYITLI FAST / IBAN NUMARASI
              </span>
              <span className="text-[13px] font-mono font-bold text-textPrimary select-all">
                {currentUser.iban}
              </span>
            </div>

            <button
              onClick={handleCopyIban}
              className="p-2 rounded-lg bg-white border border-slate-200 text-textSecondary hover:text-textPrimary"
              title="IBAN Kopyala"
            >
              {copiedIban ? <Check className="w-4 h-4 text-primaryEmerald" /> : <Copy className="w-4 h-4" />}
            </button>
          </div>
        )}
      </div>

      {/* Security & Privacy */}
      <div className="p-5 rounded-[20px] bg-surfaceWhite border border-surfaceBorder shadow-xs space-y-3">
        <h3 className="text-[13px] font-bold text-textSecondary uppercase tracking-wider">
          GÜVENLİK VE GİZLİLİK
        </h3>

        {/* Balance Privacy Toggle */}
        <div className="flex items-center justify-between py-2 border-b border-surfaceBorder">
          <div>
            <p className="text-[14px] font-semibold text-textPrimary">Bakiye Gizliliği (Maskeleme)</p>
            <p className="text-[12px] text-textSecondary">Toplu alanlarda bakiyeleri '•••• ₺' olarak gizler</p>
          </div>

          <button
            onClick={onToggleLock}
            className={`w-12 h-7 rounded-full transition-colors relative p-1 ${
              isLocked ? 'bg-primaryEmerald' : 'bg-slate-300'
            }`}
          >
            <div
              className={`w-5 h-5 rounded-full bg-white transition-transform ${
                isLocked ? 'translate-x-5' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        {/* SHA-256 PIN Vault Info */}
        <div className="flex items-center justify-between py-2">
          <div className="flex items-center gap-3">
            <Shield className="w-5 h-5 text-primaryEmerald" />
            <div>
              <p className="text-[14px] font-semibold text-textPrimary">SHA-256 Finansal Kasa</p>
              <p className="text-[12px] text-textSecondary">Tüm işlemler Merkle Tree zincirine bağlıdır</p>
            </div>
          </div>
          <span className="text-[11px] font-bold text-primaryEmerald bg-primaryEmeraldContainer px-2 py-1 rounded-full">
            Aktif
          </span>
        </div>
      </div>

      {/* KVKK / GDPR Data Erasure */}
      <div className="p-5 rounded-[20px] bg-surfaceWhite border border-surfaceBorder shadow-xs space-y-3">
        <h3 className="text-[13px] font-bold text-accentRose uppercase tracking-wider">
          KVKK MADDE 11 UNUTULMA HAKKI
        </h3>
        <p className="text-[12px] text-textSecondary leading-relaxed">
          KVKK m.11 ve GDPR standartları uyarınca, dilediğiniz an tek tıkla hesabınıza ait tüm harcama, grup ve bakiye geçmişini kalıcı olarak silebilirsiniz.
        </p>

        {showWipeConfirm ? (
          <div className="p-3 rounded-xl bg-accentRoseContainer border border-accentRose/20 space-y-2">
            <p className="text-[12px] font-bold text-accentRose">
              Tüm verileriniz silinsin mi? Bu işlem geri alınamaz!
            </p>
            <div className="flex items-center gap-2">
              <button
                onClick={onWipeData}
                className="px-3 py-1.5 rounded-lg bg-accentRose text-white text-[12px] font-bold"
              >
                Evet, Kalıcı Olarak Sil
              </button>
              <button
                onClick={() => setShowWipeConfirm(false)}
                className="px-3 py-1.5 rounded-lg bg-white text-textSecondary text-[12px] font-semibold"
              >
                İptal
              </button>
            </div>
          </div>
        ) : (
          <button
            onClick={() => setShowWipeConfirm(true)}
            className="px-4 py-2.5 rounded-[12px] bg-accentRoseContainer text-accentRose text-[13px] font-bold flex items-center gap-2 hover:bg-rose-100 active:scale-95 transition"
          >
            <Trash2 className="w-4 h-4" />
            <span>Tüm Verileri Sıfırla & Hesabı Temizle</span>
          </button>
        )}
      </div>

      {/* App Version Info */}
      <div className="text-center py-3 text-[11px] text-textSecondary">
        <p className="font-semibold text-textPrimary">AradaPay Web Platformu v1.0</p>
        <p>ArdaBank FinTech Ecosystem • Next-Gen Material 3</p>
      </div>
    </div>
  );
};
