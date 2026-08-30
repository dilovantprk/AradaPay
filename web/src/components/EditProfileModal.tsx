'use client';

import React, { useState } from 'react';
import {
  X,
  ArrowLeft,
  User,
  ShieldCheck,
  Check,
  Building2,
  Lock,
  Sparkles
} from 'lucide-react';
import { User as UserType } from '../types';

interface EditProfileModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: UserType;
  onSaveProfile: (updatedUser: UserType) => void;
}

export const EditProfileModal: React.FC<EditProfileModalProps> = ({
  isOpen,
  onClose,
  currentUser,
  onSaveProfile
}) => {
  const [fullName, setFullName] = useState(currentUser.fullName);
  const [tag, setTag] = useState(currentUser.tag || `@${currentUser.username}`);
  const [iban, setIban] = useState(currentUser.iban || '');
  const [pin, setPin] = useState('1903');
  const [showPinInput, setShowPinInput] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  if (!isOpen) return null;

  const detectBank = (ibanStr: string) => {
    const clean = ibanStr.replace(/\s+/g, '');
    if (clean.length < 9) return null;
    const code = clean.substring(4, 9);
    switch (code) {
      case '00062':
        return 'Garanti BBVA';
      case '00064':
        return 'Türkiye İş Bankası';
      case '00046':
        return 'Akbank';
      case '00067':
        return 'Yapı Kredi';
      case '00010':
        return 'Ziraat Bankası';
      case '00111':
        return 'QNB Finansbank';
      default:
        return 'BKM / FAST Uyumlu Banka';
    }
  };

  const detectedBank = detectBank(iban);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const updated: UserType = {
      ...currentUser,
      fullName: fullName.trim(),
      tag: tag.trim(),
      iban: iban.trim()
    };
    onSaveProfile(updated);
    setSavedSuccess(true);
    setTimeout(() => {
      setSavedSuccess(false);
      onClose();
    }, 1000);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-apple-modal border-0 sm:border border-black/[0.08] overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* Header */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3.5 border-b border-black/[0.06] flex items-center justify-between bg-white/80 backdrop-blur-md flex-shrink-0">
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-black/5 flex items-center justify-center text-[#1C1C1E] hover:bg-black/10 active:scale-95 transition"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>

          <h3 className="text-[17px] font-bold text-[#1C1C1E] tracking-tight">
            Profili Düzenle
          </h3>

          <div className="w-9" />
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="p-5 sm:p-6 overflow-y-auto flex-1 space-y-4 text-left">
          {/* Avatar Preview */}
          <div className="text-center space-y-2 pb-2">
            <div className="w-20 h-20 rounded-full bg-emerald-100 text-[#00875A] border-2 border-emerald-300 flex items-center justify-center font-extrabold text-[24px] mx-auto shadow-apple-sm">
              {fullName.slice(0, 2).toUpperCase() || 'AP'}
            </div>
            <p className="text-[12px] text-[#8E8E93]">Profil Fotoğrafı & Baş Harfler</p>
          </div>

          {/* Full Name */}
          <div className="space-y-1.5">
            <label className="text-[11px] font-bold text-[#8E8E93] uppercase">
              AD SOYAD
            </label>
            <input
              type="text"
              required
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              className="w-full h-12 px-4 rounded-[14px] bg-[#F2F2F7] border border-black/[0.06] text-[14px] font-bold text-[#1C1C1E] focus:outline-none focus:border-[#00875A]"
            />
          </div>

          {/* Tag */}
          <div className="space-y-1.5">
            <label className="text-[11px] font-bold text-[#8E8E93] uppercase">
              ARADAPAY ETİKETİ (@TAG)
            </label>
            <input
              type="text"
              required
              value={tag}
              onChange={(e) => setTag(e.target.value)}
              className="w-full h-12 px-4 rounded-[14px] bg-[#F2F2F7] border border-black/[0.06] text-[14px] font-mono font-bold text-[#00875A] focus:outline-none focus:border-[#00875A]"
            />
          </div>

          {/* IBAN with Bank Detection */}
          <div className="space-y-1.5">
            <div className="flex items-center justify-between">
              <label className="text-[11px] font-bold text-[#8E8E93] uppercase">
                FAST / IBAN NUMARASI
              </label>
              {detectedBank && (
                <span className="text-[10px] font-bold text-[#00875A] flex items-center gap-1">
                  <Building2 className="w-3 h-3" />
                  <span>{detectedBank}</span>
                </span>
              )}
            </div>
            <input
              type="text"
              value={iban}
              onChange={(e) => setIban(e.target.value)}
              placeholder="TR00 0000 0000 0000 0000 0000 00"
              className="w-full h-12 px-4 rounded-[14px] bg-[#F2F2F7] border border-black/[0.06] text-[14px] font-mono font-bold text-[#1C1C1E] focus:outline-none focus:border-[#00875A]"
            />
          </div>

          {/* Security PIN Change */}
          <div className="p-4 rounded-[18px] bg-[#F2F2F7] border border-black/[0.04] space-y-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Lock className="w-4 h-4 text-[#00875A]" />
                <span className="text-[13px] font-bold text-[#1C1C1E]">
                  4 Haneli Finansal PIN
                </span>
              </div>
              <button
                type="button"
                onClick={() => setShowPinInput(!showPinInput)}
                className="text-[12px] font-bold text-[#00875A] hover:underline"
              >
                {showPinInput ? 'Gizle' : 'Değiştir'}
              </button>
            </div>

            {showPinInput && (
              <div className="pt-2 animate-fadeIn space-y-1">
                <input
                  type="password"
                  maxLength={4}
                  value={pin}
                  onChange={(e) => setPin(e.target.value)}
                  placeholder="••••"
                  className="w-32 h-10 px-3 text-center rounded-[10px] bg-white border border-black/[0.08] text-[18px] font-mono font-bold tracking-widest"
                />
                <p className="text-[10px] text-[#8E8E93]">SHA-256 ile cihazda şifrelenir.</p>
              </div>
            )}
          </div>
        </form>

        {/* Footer */}
        <div className="p-4 bg-white border-t border-black/[0.06]">
          <button
            onClick={handleSubmit}
            className="w-full h-12 rounded-[16px] bg-[#00875A] hover:bg-[#00744d] text-white font-bold text-[14px] flex items-center justify-center gap-2 active:scale-[0.98] transition shadow-sm"
          >
            {savedSuccess ? (
              <>
                <Check className="w-4 h-4" />
                <span>Kaydedildi ✓</span>
              </>
            ) : (
              <span>Değişiklikleri Kaydet</span>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
