'use client';

import React, { useState } from 'react';
import {
  User,
  Shield,
  Lock,
  Trash2,
  CheckCircle2,
  Copy,
  Check,
  LogOut,
  ArrowRight,
  UserCheck,
  Edit3
} from 'lucide-react';
import { User as UserType } from '../types';
import { EditProfileModal } from '../components/EditProfileModal';

interface SettingsViewProps {
  currentUser: UserType;
  isLocked: boolean;
  onToggleLock: () => void;
  onWipeData: () => void;
  onLogout: () => void;
  onSaveProfile: (updatedUser: UserType) => void;
}

export const SettingsView: React.FC<SettingsViewProps> = ({
  currentUser,
  isLocked,
  onToggleLock,
  onWipeData,
  onLogout,
  onSaveProfile
}) => {
  const [copiedIban, setCopiedIban] = useState(false);
  const [showWipeConfirm, setShowWipeConfirm] = useState(false);
  const [showEditProfile, setShowEditProfile] = useState(false);

  const handleCopyIban = () => {
    if (currentUser.iban) {
      navigator.clipboard.writeText(currentUser.iban.replace(/\s+/g, ''));
      setCopiedIban(true);
      setTimeout(() => setCopiedIban(false), 2000);
    }
  };

  return (
    <div className="space-y-4 text-left">
      {/* Header */}
      <div className="px-1">
        <h2 className="text-[26px] font-bold text-[#1C1C1E] tracking-tight">Ayarlar & Profil</h2>
        <p className="text-[13px] text-[#8E8E93]">Hesap, güvenlik ve gizlilik tercihleri</p>
      </div>

      {/* User Profile Card */}
      <div className="apple-card p-6 space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 rounded-full bg-emerald-100 border-2 border-[#00875A] flex items-center justify-center text-[#00875A] font-extrabold text-[22px] shadow-apple-sm">
              {currentUser.fullName.substring(0, 2).toUpperCase()}
            </div>
            <div>
              <h3 className="text-[18px] font-bold text-[#1C1C1E]">{currentUser.fullName}</h3>
              <p className="text-[13px] font-mono text-[#00875A] font-bold">
                {currentUser.tag || `@${currentUser.username}`}
              </p>
              <p className="text-[12px] text-[#8E8E93]">{currentUser.email}</p>
            </div>
          </div>

          <button
            onClick={() => setShowEditProfile(true)}
            className="px-3 py-1.5 rounded-full bg-black/5 hover:bg-black/10 text-[#1C1C1E] text-[12px] font-bold flex items-center gap-1.5 active:scale-95 transition"
          >
            <Edit3 className="w-3.5 h-3.5" />
            <span>Düzenle</span>
          </button>
        </div>

        {/* IBAN Card */}
        {currentUser.iban && (
          <div className="p-3.5 rounded-[16px] bg-[#F2F2F7] border border-black/[0.04] flex items-center justify-between">
            <div className="truncate mr-2">
              <span className="text-[10px] font-bold text-[#8E8E93] uppercase tracking-wider block">
                KAYITLI FAST / IBAN NUMARASI
              </span>
              <span className="text-[13px] font-mono font-bold text-[#1C1C1E] select-all truncate block">
                {currentUser.iban}
              </span>
            </div>

            <button
              onClick={handleCopyIban}
              className="p-2 rounded-xl bg-white border border-black/[0.06] text-[#8E8E93] hover:text-[#1C1C1E] flex-shrink-0"
              title="IBAN Kopyala"
            >
              {copiedIban ? <Check className="w-4 h-4 text-[#00875A]" /> : <Copy className="w-4 h-4" />}
            </button>
          </div>
        )}
      </div>

      {/* Security & Privacy */}
      <div className="apple-card p-6 space-y-3">
        <h3 className="text-[12px] font-bold text-[#8E8E93] uppercase tracking-wider">
          GÜVENLİK VE GİZLİLİK
        </h3>

        {/* Balance Privacy Toggle */}
        <div className="flex items-center justify-between py-2 border-b border-black/[0.04]">
          <div>
            <p className="text-[14px] font-semibold text-[#1C1C1E]">Bakiye Gizliliği (Maskeleme)</p>
            <p className="text-[12px] text-[#8E8E93]">Toplu alanlarda bakiyeleri '•••• ₺' olarak gizler</p>
          </div>

          <button
            onClick={onToggleLock}
            className={`w-12 h-7 rounded-full transition-colors relative p-1 ${
              isLocked ? 'bg-[#00875A]' : 'bg-slate-300'
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
        <div className="flex items-center gap-3 py-2 text-[13px] text-[#8E8E93]">
          <Shield className="w-5 h-5 text-[#00875A] flex-shrink-0" />
          <span>4 Haneli Finansal PIN Kodu ve SHA-256 Kasa aktif.</span>
        </div>
      </div>

      {/* Session Actions */}
      <div className="apple-card p-6 space-y-3">
        <h3 className="text-[12px] font-bold text-[#8E8E93] uppercase tracking-wider">
          OTURUM VE HESAP YÖNETİMİ
        </h3>

        <button
          onClick={onLogout}
          className="w-full py-3 rounded-[14px] bg-[#F2F2F7] text-[#1C1C1E] font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-[#E5E5EA] active:scale-95 transition"
        >
          <LogOut className="w-4 h-4 text-[#8E8E93]" />
          <span>Oturumu Kapat / Hesap Değiştir</span>
        </button>
      </div>

      {/* KVKK / GDPR Data Wipe */}
      <div className="apple-card p-6 space-y-3">
        <h3 className="text-[12px] font-bold text-[#D32F2F] uppercase tracking-wider">
          KVKK M.11 UNUTULMA HAKKI
        </h3>

        <p className="text-[12px] text-[#8E8E93] leading-relaxed">
          Tüm harcama geçmişinizi, grup üyeliklerinizi ve bakiye kayıtlarınızı cihazınızdan ve veritabanından kalıcı olarak silebilirsiniz.
        </p>

        {!showWipeConfirm ? (
          <button
            onClick={() => setShowWipeConfirm(true)}
            className="w-full py-2.5 rounded-[12px] border border-[#D32F2F]/30 text-[#D32F2F] text-[13px] font-bold hover:bg-rose-50 active:scale-95 transition flex items-center justify-center gap-1.5"
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
                className="flex-1 py-2 rounded-[10px] bg-[#D32F2F] text-white text-[12px] font-bold hover:bg-rose-700 transition"
              >
                Evet, Hepsini Sil
              </button>
              <button
                onClick={() => setShowWipeConfirm(false)}
                className="flex-1 py-2 rounded-[10px] bg-white border border-slate-300 text-[#1C1C1E] text-[12px] font-bold hover:bg-slate-100 transition"
              >
                Vazgeç
              </button>
            </div>
          </div>
        )}
      </div>

      {/* 1:1 Android EditProfileModal */}
      <EditProfileModal
        isOpen={showEditProfile}
        onClose={() => setShowEditProfile(false)}
        currentUser={currentUser}
        onSaveProfile={onSaveProfile}
      />
    </div>
  );
};
