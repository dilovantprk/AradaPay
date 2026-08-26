'use client';

import React, { useState } from 'react';
import {
  Smartphone,
  Lock,
  Mail,
  ArrowRight,
  Sparkles,
  ShieldCheck,
  Zap,
  CheckCircle2,
  Users,
  KeyRound,
  Delete,
  LogIn,
  UserPlus
} from 'lucide-react';
import { User } from '../types';
import { INITIAL_USERS } from '../services/mockData';

interface AuthScreenProps {
  onLoginSuccess: (user: User) => void;
  onBackToLanding: () => void;
}

type AuthMode = 'welcome' | 'login' | 'register' | 'pin';

export const AuthScreen: React.FC<AuthScreenProps> = ({
  onLoginSuccess,
  onBackToLanding
}) => {
  const [mode, setMode] = useState<AuthMode>('welcome');
  const [emailOrPhone, setEmailOrPhone] = useState('');
  const [fullName, setFullName] = useState('');
  const [tag, setTag] = useState('');
  const [pin, setPin] = useState('');
  const [pinError, setPinError] = useState('');
  const [selectedUserForPin, setSelectedUserForPin] = useState<User | null>(null);

  // Demo Fast Login Profiles
  const demoUsers: User[] = INITIAL_USERS.slice(0, 3); // Arda, Dilovan, Caner

  const handleSelectDemoUser = (user: User) => {
    setSelectedUserForPin(user);
    setPin('');
    setPinError('');
    setMode('pin');
  };

  const handlePinInput = (digit: string) => {
    if (pin.length < 4) {
      const newPin = pin + digit;
      setPin(newPin);
      setPinError('');

      if (newPin.length === 4) {
        // Auto-validate PIN (default demo PIN: '1234' or any 4 digit in demo mode)
        setTimeout(() => {
          if (selectedUserForPin) {
            onLoginSuccess(selectedUserForPin);
          } else {
            // New user login
            const newUser: User = {
              id: `user_${Date.now()}`,
              email: emailOrPhone.includes('@') ? emailOrPhone : `${tag.replace('@', '').toLowerCase()}@aradapay.com`,
              username: tag ? tag.replace('@', '').replace('#', '_').toLowerCase() : 'kullanici',
              fullName: fullName.trim() || 'AradaPay Üyesi',
              iban: 'TR64 0006 2000 0000 ' + Math.floor(1000 + Math.random() * 9000) + ' 11',
              tag: tag.startsWith('@') ? tag : `@${tag || 'kullanici'}`
            };
            onLoginSuccess(newUser);
          }
        }, 300);
      }
    }
  };

  const handleDeletePin = () => {
    setPin((prev) => prev.slice(0, -1));
    setPinError('');
  };

  const handleCustomLoginSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!emailOrPhone.trim()) return;

    // Check if matches existing user
    const existing = INITIAL_USERS.find(
      (u) =>
        u.email.toLowerCase() === emailOrPhone.toLowerCase() ||
        u.tag?.toLowerCase() === emailOrPhone.toLowerCase() ||
        u.username.toLowerCase() === emailOrPhone.toLowerCase()
    );

    setSelectedUserForPin(
      existing || {
        id: `user_${Date.now()}`,
        email: emailOrPhone,
        username: emailOrPhone.split('@')[0],
        fullName: emailOrPhone.split('@')[0].toUpperCase(),
        iban: 'TR64 0006 2000 0000 9999 11',
        tag: `@${emailOrPhone.split('@')[0]}#1001`
      }
    );
    setPin('');
    setMode('pin');
  };

  return (
    <div className="min-h-screen bg-[#F8FAFC] flex flex-col justify-between font-sans selection:bg-emerald-100 selection:text-emerald-800">
      {/* Top Bar */}
      <header className="px-5 py-4 flex items-center justify-between border-b border-slate-200 bg-white">
        <button
          onClick={mode === 'welcome' ? onBackToLanding : () => setMode('welcome')}
          className="text-[13px] font-bold text-slate-600 hover:text-slate-900 flex items-center gap-1.5"
        >
          ← {mode === 'welcome' ? 'Ana Sayfa' : 'Geri'}
        </button>

        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-[10px] bg-primaryEmerald flex items-center justify-center text-white font-black text-[14px]">
            AP
          </div>
          <span className="font-black text-[16px] text-textPrimary tracking-tight">
            Arada<span className="text-primaryEmerald">Pay</span>
          </span>
        </div>

        <div className="w-12" />
      </header>

      {/* Main Container */}
      <main className="flex-1 flex items-center justify-center p-4 sm:p-6">
        <div className="w-full max-w-md bg-white rounded-[28px] border border-slate-200/90 shadow-xl p-6 sm:p-8 space-y-6">
          {/* ========================================================================= */}
          {/* MODE 1: WELCOME SCREEN (Onboarding Cards + Quick Profiles) */}
          {/* ========================================================================= */}
          {mode === 'welcome' && (
            <div className="space-y-6 animate-fadeIn">
              <div className="text-center space-y-2">
                <div className="w-14 h-14 rounded-2xl bg-emerald-100 text-primaryEmerald flex items-center justify-center mx-auto shadow-sm">
                  <Sparkles className="w-7 h-7 stroke-[2.2]" />
                </div>
                <h2 className="text-[24px] font-black text-textPrimary tracking-tight">
                  AradaPay'e Hoş Geldin
                </h2>
                <p className="text-[13px] text-textSecondary leading-relaxed">
                  Sosyal finans ve akıllı mahsuplaşma dünyasına adım at.
                </p>
              </div>

              {/* Fast Demo Account Selector */}
              <div className="space-y-2.5">
                <span className="text-[11px] font-black text-slate-500 uppercase tracking-wider block">
                  HIZLI DEMO HESABI SEÇ
                </span>

                <div className="space-y-2">
                  {demoUsers.map((user) => (
                    <button
                      key={user.id}
                      onClick={() => handleSelectDemoUser(user)}
                      className="w-full p-3 rounded-[16px] bg-[#F8FAFC] border border-slate-200/80 hover:border-emerald-500 hover:bg-emerald-50/50 flex items-center justify-between group transition active:scale-[0.99]"
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-xl bg-slate-900 text-white font-bold text-[14px] flex items-center justify-center">
                          {user.fullName
                            .split(' ')
                            .map((n) => n[0])
                            .join('')}
                        </div>
                        <div className="text-left">
                          <div className="text-[14px] font-bold text-textPrimary group-hover:text-primaryEmerald transition">
                            {user.fullName}
                          </div>
                          <div className="text-[12px] text-textSecondary font-medium">
                            {user.tag || `@${user.username}`}
                          </div>
                        </div>
                      </div>

                      <div className="flex items-center gap-1.5 text-primaryEmerald text-[12px] font-extrabold">
                        <span>Giriş Yap</span>
                        <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform" />
                      </div>
                    </button>
                  ))}
                </div>
              </div>

              <div className="relative flex py-1 items-center">
                <div className="flex-grow border-t border-slate-200"></div>
                <span className="flex-shrink mx-3 text-slate-400 text-[11px] font-bold uppercase">
                  veya
                </span>
                <div className="flex-grow border-t border-slate-200"></div>
              </div>

              {/* Custom Login / Register Buttons */}
              <div className="space-y-2.5">
                <button
                  onClick={() => setMode('login')}
                  className="w-full h-[50px] rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-600/20"
                >
                  <LogIn className="w-4 h-4 stroke-[2.5]" />
                  <span>E-posta / Telefon ile Giriş</span>
                </button>

                <button
                  onClick={() => setMode('register')}
                  className="w-full h-[50px] rounded-[16px] bg-surfaceContainerLow text-textPrimary font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-slate-200 active:scale-95 transition"
                >
                  <UserPlus className="w-4 h-4 text-textSecondary" />
                  <span>Yeni Hesap Oluştur</span>
                </button>
              </div>
            </div>
          )}

          {/* ========================================================================= */}
          {/* MODE 2: LOGIN FORM */}
          {/* ========================================================================= */}
          {mode === 'login' && (
            <form onSubmit={handleCustomLoginSubmit} className="space-y-5 animate-fadeIn">
              <div className="text-center space-y-1.5">
                <h3 className="text-[22px] font-black text-textPrimary">Hesabına Giriş Yap</h3>
                <p className="text-[13px] text-textSecondary">
                  E-posta adresin, telefonun veya @tag etiketin ile devam et.
                </p>
              </div>

              <div className="space-y-3">
                <label className="block text-[12px] font-bold text-textPrimary">
                  Giriş Bilgisi
                </label>
                <div className="relative">
                  <input
                    type="text"
                    required
                    value={emailOrPhone}
                    onChange={(e) => setEmailOrPhone(e.target.value)}
                    placeholder="ornek@aradapay.com veya @kaan#5674"
                    className="w-full h-[50px] px-4 rounded-[16px] bg-[#F8FAFC] border border-slate-200 text-[14px] text-textPrimary placeholder:text-slate-400 focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                  />
                </div>
              </div>

              <button
                type="submit"
                className="w-full h-[52px] rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm"
              >
                <span>Devam Et (PIN Girişi)</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </form>
          )}

          {/* ========================================================================= */}
          {/* MODE 3: REGISTER FORM */}
          {/* ========================================================================= */}
          {mode === 'register' && (
            <form
              onSubmit={(e) => {
                e.preventDefault();
                setMode('pin');
              }}
              className="space-y-4 animate-fadeIn"
            >
              <div className="text-center space-y-1.5">
                <h3 className="text-[22px] font-black text-textPrimary">Yeni Hesap Aç</h3>
                <p className="text-[13px] text-textSecondary">
                  Saniyeler içinde AradaPay hesabını oluştur.
                </p>
              </div>

              <div className="space-y-3">
                <div>
                  <label className="block text-[12px] font-bold text-textPrimary mb-1">
                    Ad Soyad
                  </label>
                  <input
                    type="text"
                    required
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    placeholder="Ahmet Yılmaz"
                    className="w-full h-[46px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                  />
                </div>

                <div>
                  <label className="block text-[12px] font-bold text-textPrimary mb-1">
                    Kişisel @Tag Etiketin
                  </label>
                  <input
                    type="text"
                    required
                    value={tag}
                    onChange={(e) => setTag(e.target.value)}
                    placeholder="@ahmet#2026"
                    className="w-full h-[46px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] focus:outline-none focus:border-primaryEmerald focus:bg-white transition font-mono"
                  />
                </div>

                <div>
                  <label className="block text-[12px] font-bold text-textPrimary mb-1">
                    E-posta veya Telefon
                  </label>
                  <input
                    type="text"
                    required
                    value={emailOrPhone}
                    onChange={(e) => setEmailOrPhone(e.target.value)}
                    placeholder="ahmet@gmail.com"
                    className="w-full h-[46px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                  />
                </div>
              </div>

              <button
                type="submit"
                className="w-full h-[50px] rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm mt-2"
              >
                <span>PIN Belirle & Kaydol</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </form>
          )}

          {/* ========================================================================= */}
          {/* MODE 4: 4-DIGIT PIN PAD (1:1 Android PinPadDialog) */}
          {/* ========================================================================= */}
          {mode === 'pin' && (
            <div className="space-y-6 text-center animate-fadeIn">
              <div className="space-y-1.5">
                <div className="w-12 h-12 rounded-2xl bg-emerald-100 text-primaryEmerald flex items-center justify-center mx-auto">
                  <KeyRound className="w-6 h-6 stroke-[2.2]" />
                </div>
                <h3 className="text-[20px] font-black text-textPrimary">
                  4 Haneli Güvenlik PIN Kodu
                </h3>
                <p className="text-[12px] text-textSecondary">
                  {selectedUserForPin
                    ? `${selectedUserForPin.fullName} hesabını doğrulamak için PIN girin (Demo: 1234)`
                    : 'Hesabınız için 4 haneli PIN kodunuzu girin'}
                </p>
              </div>

              {/* PIN Bubbles (4 Circles) */}
              <div className="flex items-center justify-center gap-4 py-2">
                {[0, 1, 2, 3].map((index) => {
                  const filled = pin.length > index;
                  return (
                    <div
                      key={index}
                      className={`w-4 h-4 rounded-full transition-all duration-200 ${
                        filled
                          ? 'bg-primaryEmerald scale-110 shadow-sm shadow-emerald-500/50'
                          : 'bg-slate-200 border-2 border-slate-300'
                      }`}
                    />
                  );
                })}
              </div>

              {pinError && (
                <div className="text-[12px] font-bold text-accentRose animate-shake">
                  {pinError}
                </div>
              )}

              {/* Numeric Keypad (1:1 Material 3 PIN Pad) */}
              <div className="grid grid-cols-3 gap-2.5 max-w-[260px] mx-auto pt-2">
                {['1', '2', '3', '4', '5', '6', '7', '8', '9'].map((digit) => (
                  <button
                    key={digit}
                    type="button"
                    onClick={() => handlePinInput(digit)}
                    className="h-14 rounded-2xl bg-[#F8FAFC] border border-slate-200 text-slate-800 font-bold text-[20px] hover:bg-emerald-50 hover:border-emerald-400 active:scale-90 transition shadow-2xs"
                  >
                    {digit}
                  </button>
                ))}

                <button
                  type="button"
                  onClick={() => setPin('1234')}
                  className="h-14 rounded-2xl bg-slate-100 text-slate-600 font-bold text-[11px] hover:bg-slate-200 active:scale-90 transition"
                >
                  Demo PIN
                </button>

                <button
                  type="button"
                  onClick={() => handlePinInput('0')}
                  className="h-14 rounded-2xl bg-[#F8FAFC] border border-slate-200 text-slate-800 font-bold text-[20px] hover:bg-emerald-50 hover:border-emerald-400 active:scale-90 transition shadow-2xs"
                >
                  0
                </button>

                <button
                  type="button"
                  onClick={handleDeletePin}
                  className="h-14 rounded-2xl bg-rose-50 border border-rose-100 text-rose-600 flex items-center justify-center hover:bg-rose-100 active:scale-90 transition"
                >
                  <Delete className="w-5 h-5" />
                </button>
              </div>
            </div>
          )}
        </div>
      </main>

      {/* Footer */}
      <footer className="py-4 text-center text-[11px] text-textSecondary">
        <span>© 2026 AradaPay • 256-bit SHA-256 Şifreli Finansal Kasa</span>
      </footer>
    </div>
  );
};
