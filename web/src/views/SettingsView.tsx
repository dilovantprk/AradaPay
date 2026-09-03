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
  Edit3,
  Coins,
  Smartphone,
  Fingerprint,
  Building2
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
  const [currency, setCurrency] = useState<'TRY' | 'USD' | 'EUR'>('TRY');
  const [haptics, setHaptics] = useState(true);
  const [biometrics, setBiometrics] = useState(true);

  const handleCopyIban = () => {
    if (currentUser.iban) {
      navigator.clipboard.writeText(currentUser.iban.replace(/\s+/g, ''));
      setCopiedIban(true);
      setTimeout(() => setCopiedIban(false), 2000);
    }
  };

  return (
    <div className="space-y-4 text-left animate-fadeIn">
      {/* Desktop Header (Hidden on mobile because TopBar displays it) */}
      <div className="hidden md:block px-1">
        <h2 className="text-[28px] font-bold text-[#0F172A] tracking-tight">Ayarlar & Güvenlik</h2>
        <p className="text-[13px] text-[#64748B]">Hesap tercihleri, FAST tanımlamaları ve güvenlik kasası</p>
      </div>

      {/* 1. User Profile & FAST IBAN (Flat Unified Grouped Container) */}
      <div className="bg-white rounded-[20px] border border-slate-200/80 divide-y divide-slate-100 overflow-hidden shadow-sm">
        {/* Profile Row */}
        <div className="p-5 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-full bg-emerald-50 border border-emerald-200 flex items-center justify-center text-[#00875A] font-extrabold text-[20px] shadow-2xs">
              {currentUser.fullName.substring(0, 2).toUpperCase()}
            </div>
            <div>
              <h3 className="text-[16px] font-bold text-[#0F172A]">{currentUser.fullName}</h3>
              <p className="text-[13px] font-mono text-[#00875A] font-bold">
                {currentUser.tag || `@${currentUser.username}`}
              </p>
              <p className="text-[12px] text-[#64748B]">{currentUser.email}</p>
            </div>
          </div>

          <button
            onClick={() => setShowEditProfile(true)}
            className="px-3.5 py-2 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] text-[12px] font-bold flex items-center gap-1.5 active:scale-95 transition"
          >
            <Edit3 className="w-3.5 h-3.5" />
            <span>Profili Düzenle</span>
          </button>
        </div>

        {/* Clean Flat IBAN Row (No nested card!) */}
        {currentUser.iban && (
          <div className="p-4 px-5 flex items-center justify-between bg-slate-50/50">
            <div className="truncate mr-3">
              <span className="text-[10px] font-bold text-[#64748B] uppercase tracking-wider block">
                KAYITLI FAST / IBAN NUMARASI
              </span>
              <span className="text-[13px] font-mono font-bold text-[#0F172A] select-all truncate block">
                {currentUser.iban}
              </span>
            </div>

            <button
              onClick={handleCopyIban}
              className="px-3.5 py-1.5 rounded-[10px] bg-white border border-slate-200 text-[#0F172A] text-[12px] font-bold flex items-center gap-1.5 active:scale-95 transition shadow-2xs flex-shrink-0"
              title="IBAN Kopyala"
            >
              {copiedIban ? <Check className="w-3.5 h-3.5 text-[#00875A]" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copiedIban ? 'Kopyalandı' : 'Kopyala'}</span>
            </button>
          </div>
        )}
      </div>

      {/* 2. Currency Preferences Group */}
      <div className="bg-white rounded-[20px] border border-slate-200/80 p-5 space-y-3 shadow-sm">
        <h3 className="text-[11px] font-bold text-[#64748B] uppercase tracking-wider">
          VARSAYILAN PARA BİRİMİ
        </h3>

        <div className="grid grid-cols-3 gap-2">
          {(['TRY', 'USD', 'EUR'] as const).map((curr) => (
            <button
              key={curr}
              onClick={() => setCurrency(curr)}
              className={`py-2.5 rounded-[12px] text-[13px] font-bold transition flex items-center justify-center gap-1.5 active:scale-95 ${
                currency === curr
                  ? 'bg-[#00875A] text-white shadow-2xs'
                  : 'bg-[#F1F5F9] text-[#0F172A] hover:bg-slate-200'
              }`}
            >
              <Coins className="w-4 h-4" />
              <span>{curr === 'TRY' ? 'Türk Lirası (₺)' : curr === 'USD' ? 'Dolar ($)' : 'Euro (€)'}</span>
            </button>
          ))}
        </div>
      </div>

      {/* 3. Security & Privacy Group (Grouped Divided Rows) */}
      <div className="bg-white rounded-[20px] border border-slate-200/80 divide-y divide-slate-100 overflow-hidden shadow-sm">
        <div className="px-5 pt-4 pb-2">
          <h3 className="text-[11px] font-bold text-[#64748B] uppercase tracking-wider">
            GÜVENLİK VE GİZLİLİK
          </h3>
        </div>

        {/* Balance Privacy Toggle */}
        <div className="p-4 px-5 flex items-center justify-between">
          <div>
            <p className="text-[14px] font-semibold text-[#0F172A]">Bakiye Gizliliği (Maskeleme)</p>
            <p className="text-[12px] text-[#64748B]">Toplu alanlarda bakiyeleri '•••• ₺' olarak gizler</p>
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

        {/* Biometric FaceID / Fingerprint toggle */}
        <div className="p-4 px-5 flex items-center justify-between">
          <div>
            <p className="text-[14px] font-semibold text-[#0F172A]">Biyometrik Kasa (Touch ID / Face ID)</p>
            <p className="text-[12px] text-[#64748B]">Girişte biyometrik parmak izi onayı iste</p>
          </div>

          <button
            onClick={() => setBiometrics(!biometrics)}
            className={`w-12 h-7 rounded-full transition-colors relative p-1 ${
              biometrics ? 'bg-[#00875A]' : 'bg-slate-300'
            }`}
          >
            <div
              className={`w-5 h-5 rounded-full bg-white transition-transform ${
                biometrics ? 'translate-x-5' : 'translate-x-0'
              }`}
            />
          </button>
        </div>

        {/* Haptics toggle */}
        <div className="p-4 px-5 flex items-center justify-between">
          <div>
            <p className="text-[14px] font-semibold text-[#0F172A]">Haptik Titreşim & Dokunsal Geri Bildirim</p>
            <p className="text-[12px] text-[#64748B]">Buton ve kaydırma hareketlerinde Apple titreşimi</p>
          </div>

          <button
            onClick={() => setHaptics(!haptics)}
            className={`w-12 h-7 rounded-full transition-colors relative p-1 ${
              haptics ? 'bg-[#00875A]' : 'bg-slate-300'
            }`}
          >
            <div
              className={`w-5 h-5 rounded-full bg-white transition-transform ${
                haptics ? 'translate-x-5' : 'translate-x-0'
              }`}
            />
          </button>
        </div>
      </div>

      {/* 4. Data Wipe & Logout Group */}
      <div className="bg-white rounded-[20px] border border-slate-200/80 divide-y divide-slate-100 overflow-hidden shadow-sm">
        {/* Data wipe */}
        <div className="p-4 px-5 flex items-center justify-between">
          <div>
            <p className="text-[14px] font-bold text-[#D32F2F]">KVKK / Tüm Verilerimi Sıfırla</p>
            <p className="text-[12px] text-[#64748B]">Cihazdaki ve hesaptaki tüm hareketleri kalıcı siler</p>
          </div>

          <button
            onClick={() => setShowWipeConfirm(true)}
            className="px-3.5 py-2 rounded-[12px] bg-rose-50 hover:bg-rose-100 text-[#D32F2F] text-[12px] font-bold flex items-center gap-1.5 active:scale-95 transition"
          >
            <Trash2 className="w-3.5 h-3.5" />
            <span>Sıfırla</span>
          </button>
        </div>

        {/* Logout */}
        <div className="p-4 px-5 flex items-center justify-between">
          <div>
            <p className="text-[14px] font-bold text-[#0F172A]">Oturumu Kapat</p>
            <p className="text-[12px] text-[#64748B]">Hesaptan güvenli çıkış yap</p>
          </div>

          <button
            onClick={onLogout}
            className="px-3.5 py-2 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] text-[12px] font-bold flex items-center gap-1.5 active:scale-95 transition"
          >
            <LogOut className="w-3.5 h-3.5" />
            <span>Çıkış Yap</span>
          </button>
        </div>
      </div>

      {/* Profile Modal */}
      <EditProfileModal
        isOpen={showEditProfile}
        onClose={() => setShowEditProfile(false)}
        currentUser={currentUser}
        onSaveProfile={(updated) => {
          onSaveProfile(updated);
        }}
      />

      {/* Wipe Data Modal */}
      {showWipeConfirm && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
          <div className="bg-white max-w-sm w-full rounded-[24px] p-6 shadow-2xl space-y-4 border border-slate-200 animate-applePop">
            <div className="w-12 h-12 rounded-full bg-rose-50 text-[#D32F2F] flex items-center justify-center mx-auto">
              <Trash2 className="w-6 h-6" />
            </div>

            <div className="text-center space-y-1">
              <h3 className="text-[17px] font-bold text-[#0F172A]">Tüm Verileri Sıfırla?</h3>
              <p className="text-[13px] text-[#64748B]">
                Bu işlem geri alınamaz. Tüm harcamalarınız, bakiyeleriniz ve gruplarınız silinecektir.
              </p>
            </div>

            <div className="space-y-2 pt-2">
              <button
                onClick={() => {
                  onWipeData();
                  setShowWipeConfirm(false);
                }}
                className="w-full py-3 rounded-[14px] bg-[#D32F2F] hover:bg-[#b71c1c] text-white font-bold text-[14px] active:scale-95 transition"
              >
                Evet, Tüm Verilerimi Sil
              </button>

              <button
                onClick={() => setShowWipeConfirm(false)}
                className="w-full py-3 rounded-[14px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] font-bold text-[14px] active:scale-95 transition"
              >
                Vazgeç
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
