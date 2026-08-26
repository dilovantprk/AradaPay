'use client';

import React, { useState } from 'react';
import { User, Shield, Lock, Trash2, CheckCircle2, Copy, Check, LogOut, ArrowRight, UserCheck } from 'lucide-react';
import { User as UserType } from '../types';

interface SettingsViewProps {
  currentUser: UserType;
  isLocked: boolean;
  onToggleLock: () => void;
  onWipeData: () => void;
  onLogout: () => void;
}

export const SettingsView: React.FC<SettingsViewProps> = ({
  currentUser,
  isLocked,
  onToggleLock,
  onWipeData,
  onLogout
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
            <p className="text-[13px] font-mono text-primaryEmerald font-bold">{currentUser.tag || `@${currentUser.username}`}</p>
            <p className="text-[12px] text-textSecondary">{currentUser.email}</p>
          </div>
        </div>

        {/* IBAN Card */}
        {currentUser.iban && (
          <div className="p-3 rounded-[14px] bg-[#F8FAFC] border border-slate-200 flex items-center justify-between">
            <div className="truncate mr-2">
              <span className="text-[10px] font-bold text-textSecondary uppercase tracking-wider block">
                KAYITLI FAST / IBAN NUMARASI
              </span>
              <span className="text-[13px] font-mono font-bold text-textPrimary select-all truncate block">
                {currentUser.iban}
              </span>
            </div>

            <button
              onClick={handleCopyIban}
              className="p-2 rounded-lg bg-white border border-slate-200 text-textSecondary hover:text-textPrimary flex-shrink-0"
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
        <div className="flex items-center gap-3 py-2 text-[13px] text-textSecondary">
          <Shield className="w-5 h-5 text-primaryEmerald flex-shrink-0" />
          <span>4 Haneli Finansal PIN Kodu ve SHA-256 Kasa aktif.</span>
        </div>
      </div>

      {/* Session Actions */}
      <div className="p-5 rounded-[20px] bg-surfaceWhite border border-surfaceBorder shadow-xs space-y-3">
        <h3 className="text-[13px] font-bold text-textSecondary uppercase tracking-wider">
          OTURUM VE HESAP YÖNETİMİ
        </h3>

        <button
          onClick={onLogout}
          className="w-full py-3 rounded-[14px] bg-slate-100 text-slate-800 font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-slate-200 active:scale-95 transition"
        >
          <LogOut className="w-4 h-4 text-slate-600" />
          <span>Oturumu Kapat / Hesap Değiştir</span>
        </button>
      </div>

      {/* KVKK / GDPR Data Wipe */}
      <div className="p-5 rounded-[20px] bg-surfaceWhite border border-surfaceBorder shadow-xs space-y-3">
        <h3 className="text-[13px] font-bold text-accentRose uppercase tracking-wider">
          KVKK M.11 UNUTULMA HAKKI
        </h3>

        <p className="text-[12px] text-textSecondary leading-relaxed">
          Tüm harcama geçmişinizi, grup üyeliklerinizi ve bakiye kayıtlarınızı cihazınızdan ve veritabanından kalıcı olarak silebilirsiniz.
        </p>

        {!showWipeConfirm ? (
          <button
            onClick={() => setShowWipeConfirm(true)}
            className="w-full py-2.5 rounded-[12px] border border-accentRose/30 text-accentRose text-[13px] font-bold hover:bg-rose-50 active:scale-95 transition flex items-center justify-center gap-1.5"
          >
            <Trash2 className="w-4 h-4" />
            <span>Tüm Verilerimi Kalıcı Olarak Temizle</span>
          </button>
        ) : (
          <div className="p-4 rounded-[14px] bg-rose-50 border border-rose-200 space-y-3">
            <p className="text-[12px] font-bold text-rose-800">
              Emin misiniz? Bu işlem geri alınamaz ve tüm yerel & bulut verileriniz silinir.
            </p>
            <div className="flex items-center gap-2">
              <button
                onClick={() => {
                  onWipeData();
                  setShowWipeConfirm(false);
                }}
                className="flex-1 py-2 rounded-[10px] bg-accentRose text-white text-[12px] font-bold hover:bg-rose-700 transition"
              >
                Evet, Hepsini Sil
              </button>
              <button
                onClick={() => setShowWipeConfirm(false)}
                className="flex-1 py-2 rounded-[10px] bg-white border border-slate-300 text-textPrimary text-[12px] font-bold hover:bg-slate-100 transition"
              >
                Vazgeç
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
