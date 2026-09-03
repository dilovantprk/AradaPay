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
  UserPlus,
  Eye,
  EyeOff,
  AlertCircle,
  Loader2
} from 'lucide-react';
import { User } from '../types';
import { INITIAL_USERS } from '../services/mockData';
import { AuthService } from '../services/authService';

interface AuthScreenProps {
  onLoginSuccess: (user: User) => void;
  onBackToLanding: () => void;
}

type AuthMode = 'welcome' | 'login' | 'register' | 'forgot_password' | 'pin';

export const AuthScreen: React.FC<AuthScreenProps> = ({
  onLoginSuccess,
  onBackToLanding
}) => {
  const [mode, setMode] = useState<AuthMode>('welcome');

  // Login Form State
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  // Register Form State
  const [registerStep, setRegisterStep] = useState(1);
  const [fullName, setFullName] = useState('');
  const [registerEmail, setRegisterEmail] = useState('');
  const [registerPassword, setRegisterPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showRegisterPassword, setShowRegisterPassword] = useState(false);
  const [phone, setPhone] = useState('');
  const [otp, setOtp] = useState('');
  const [generatedOtp, setGeneratedOtp] = useState('');
  const [iban, setIban] = useState('TR64 0006 2000 0000 5566 7788 99');
  const [pin, setPin] = useState('');

  // Password Reset State
  const [resetEmail, setResetEmail] = useState('');
  const [resetSent, setResetSent] = useState(false);

  // UI Feedback States
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [pinError, setPinError] = useState('');
  const [selectedUserForPin, setSelectedUserForPin] = useState<User | null>(null);

  // Demo Fast Login Profiles
  const demoUsers: User[] = INITIAL_USERS.slice(0, 3);

  const clearMessages = () => {
    setErrorMessage('');
    setSuccessMessage('');
    setPinError('');
  };

  // 1. Google Sign-In Handler
  const handleGoogleSignIn = async () => {
    clearMessages();
    setIsLoading(true);
    try {
      const user = await AuthService.signInWithGoogle();
      onLoginSuccess(user);
    } catch (err: any) {
      console.error('Google Sign-In Error:', err);
      if (err.code === 'auth/popup-closed-by-user') {
        setErrorMessage('Giriş penceresi kapatıldı.');
      } else {
        setErrorMessage(err.message || 'Google ile giriş yapılırken bir hata oluştu.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  // 2. Email & Password Login Handler
  const handleEmailLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !password) return;

    clearMessages();
    setIsLoading(true);
    try {
      const user = await AuthService.signInWithEmail(email, password);
      onLoginSuccess(user);
    } catch (err: any) {
      console.error('Email Login Error:', err);
      if (err.code === 'auth/invalid-credential' || err.code === 'auth/wrong-password' || err.code === 'auth/user-not-found') {
        setErrorMessage('E-posta veya şifre hatalı. Lütfen tekrar deneyin.');
      } else if (err.code === 'auth/invalid-email') {
        setErrorMessage('Geçersiz bir e-posta adresi girdiniz.');
      } else if (err.code === 'auth/too-many-requests') {
        setErrorMessage('Çok fazla başarısız deneme. Lütfen bir süre sonra tekrar deneyin.');
      } else {
        setErrorMessage(err.message || 'Giriş yapılırken bir hata oluştu.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  // 3. Step-by-Step Register Handler
  const handleRegisterComplete = async () => {
    clearMessages();
    setIsLoading(true);
    try {
      const user = await AuthService.signUpWithEmail(
        registerEmail,
        registerPassword,
        fullName,
        phone || undefined,
        iban,
        pin
      );
      onLoginSuccess(user);
    } catch (err: any) {
      console.error('Registration Error:', err);
      if (err.code === 'auth/email-already-in-use') {
        setErrorMessage('Bu e-posta adresi zaten kullanımda. Giriş yapmayı deneyin.');
        setMode('login');
      } else if (err.code === 'auth/weak-password') {
        setErrorMessage('Şifreniz en az 6 karakter olmalıdır.');
      } else if (err.code === 'auth/invalid-email') {
        setErrorMessage('Geçerli bir e-posta adresi giriniz.');
      } else {
        setErrorMessage(err.message || 'Kayıt olurken bir hata oluştu.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  // 4. Password Reset Handler
  const handlePasswordReset = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!resetEmail.trim()) return;

    clearMessages();
    setIsLoading(true);
    try {
      await AuthService.sendPasswordReset(resetEmail);
      setResetSent(true);
      setSuccessMessage('Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.');
    } catch (err: any) {
      console.error('Password Reset Error:', err);
      setErrorMessage(err.message || 'Şifre sıfırlama isteği gönderilemedi.');
    } finally {
      setIsLoading(false);
    }
  };

  // 5. Demo Fast Account Selector
  const handleSelectDemoUser = (user: User) => {
    setSelectedUserForPin(user);
    setPin('');
    setPinError('');
    setMode('pin');
  };

  // 6. PIN Input Handler
  const handlePinInput = (digit: string) => {
    if (pin.length < 4) {
      const newPin = pin + digit;
      setPin(newPin);
      setPinError('');

      if (newPin.length === 4) {
        if (mode === 'register' && registerStep === 4) {
          // Finished PIN setup for registration
          setTimeout(() => {
            handleRegisterComplete();
          }, 200);
        } else if (selectedUserForPin) {
          // Demo / Saved user verification
          setTimeout(() => {
            onLoginSuccess(selectedUserForPin);
          }, 300);
        }
      }
    }
  };

  const handleDeletePin = () => {
    setPin((prev) => prev.slice(0, -1));
    setPinError('');
  };

  return (
    <div className="min-h-screen bg-white sm:bg-[#F8FAFC] flex flex-col justify-between font-sans selection:bg-emerald-100 selection:text-emerald-800">
      {/* Top Bar (Uniform 64px Header) */}
      <header className="px-5 h-[64px] flex items-center justify-between border-b border-slate-100 bg-white/95 backdrop-blur-md sticky top-0 z-20">
        <button
          onClick={() => {
            clearMessages();
            if (mode === 'register' && registerStep > 1) {
              setRegisterStep((prev) => prev - 1);
            } else if (mode !== 'welcome') {
              setMode('welcome');
            } else {
              onBackToLanding();
            }
          }}
          className="text-[13px] font-bold text-slate-600 hover:text-slate-900 flex items-center gap-1.5 transition"
        >
          ← {mode === 'welcome' ? 'Ana Sayfa' : 'Geri'}
        </button>

        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-[10px] bg-primaryEmerald flex items-center justify-center text-white font-black text-[14px] shadow-sm">
            AP
          </div>
          <span className="font-black text-[16px] text-textPrimary tracking-tight">
            Arada<span className="text-primaryEmerald">Pay</span>
          </span>
        </div>

        <div className="w-12 text-right">
          {mode === 'register' && (
            <span className="text-[12px] font-bold text-primaryEmerald bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">
              {registerStep}/4
            </span>
          )}
        </div>
      </header>

      {/* Main Container - Flat on mobile, framed on desktop */}
      <main className="flex-1 flex items-start sm:items-center justify-center p-0 sm:p-6">
        <div className="w-full max-w-md bg-white rounded-none sm:rounded-[24px] border-0 sm:border border-slate-200/90 shadow-none sm:shadow-lg p-5 sm:p-8 space-y-6">

          {/* Global Error Banner */}
          {errorMessage && (
            <div className="p-3.5 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-[13px] font-semibold flex items-center gap-2.5 animate-shake">
              <AlertCircle className="w-4 h-4 text-rose-600 shrink-0" />
              <span>{errorMessage}</span>
            </div>
          )}

          {/* Global Success Banner */}
          {successMessage && (
            <div className="p-3.5 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-[13px] font-semibold flex items-center gap-2.5">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>{successMessage}</span>
            </div>
          )}

          {/* ========================================================================= */}
          {/* MODE 1: WELCOME SCREEN */}
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
                  Sosyal harcamalar, grup bütçesi ve akıllı döngüsel mahsuplaşma platformu.
                </p>
              </div>

              {/* Google Sign-In Button */}
              <button
                type="button"
                onClick={handleGoogleSignIn}
                disabled={isLoading}
                className="w-full h-[52px] rounded-[16px] bg-white border border-slate-300 text-slate-700 font-bold text-[14px] flex items-center justify-center gap-3 hover:bg-slate-50 hover:border-slate-400 active:scale-[0.98] transition shadow-xs disabled:opacity-60"
              >
                {isLoading ? (
                  <Loader2 className="w-5 h-5 animate-spin text-primaryEmerald" />
                ) : (
                  <>
                    <svg className="w-5 h-5" viewBox="0 0 24 24">
                      <path
                        fill="#4285F4"
                        d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                      />
                      <path
                        fill="#34A853"
                        d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                      />
                      <path
                        fill="#FBBC05"
                        d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
                      />
                      <path
                        fill="#EA4335"
                        d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
                      />
                    </svg>
                    <span>Google ile Devam Et</span>
                  </>
                )}
              </button>

              <div className="relative flex py-1 items-center">
                <div className="flex-grow border-t border-slate-200"></div>
                <span className="flex-shrink mx-3 text-slate-400 text-[11px] font-bold uppercase">
                  veya
                </span>
                <div className="flex-grow border-t border-slate-200"></div>
              </div>

              {/* Action Buttons */}
              <div className="space-y-2.5">
                <button
                  onClick={() => {
                    clearMessages();
                    setMode('login');
                  }}
                  className="w-full h-[50px] rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm shadow-emerald-600/20"
                >
                  <LogIn className="w-4 h-4 stroke-[2.5]" />
                  <span>Giriş Yap</span>
                </button>

                <button
                  onClick={() => {
                    clearMessages();
                    setRegisterStep(1);
                    setMode('register');
                  }}
                  className="w-full h-[50px] rounded-[16px] bg-slate-100 text-textPrimary font-bold text-[14px] flex items-center justify-center gap-2 hover:bg-slate-200 active:scale-95 transition"
                >
                  <UserPlus className="w-4 h-4 text-textSecondary" />
                  <span>Kayıt Ol</span>
                </button>
              </div>

              {/* Fast Demo Account Selector */}
              <div className="space-y-2 pt-2 border-t border-slate-100">
                <span className="text-[11px] font-black text-slate-400 uppercase tracking-wider block">
                  HIZLI DEMO HESABI (ÖNİZLEME)
                </span>

                <div className="grid grid-cols-3 gap-2">
                  {demoUsers.map((user) => (
                    <button
                      key={user.id}
                      onClick={() => handleSelectDemoUser(user)}
                      className="py-2.5 px-3 rounded-[12px] bg-[#F1F5F9] hover:bg-emerald-50 active:scale-95 transition flex items-center justify-center gap-2 group"
                    >
                      <div className="w-6 h-6 rounded-full bg-[#0F172A] text-white font-bold text-[10px] flex items-center justify-center flex-shrink-0">
                        {user.fullName.slice(0, 2).toUpperCase()}
                      </div>
                      <span className="text-[12px] font-bold text-[#0F172A] group-hover:text-primaryEmerald truncate">
                        {user.fullName.split(' ')[0]}
                      </span>
                    </button>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* ========================================================================= */}
          {/* MODE 2: EMAIL & PASSWORD LOGIN */}
          {/* ========================================================================= */}
          {mode === 'login' && (
            <form onSubmit={handleEmailLogin} className="space-y-4 animate-fadeIn">
              <div className="text-center space-y-1.5">
                <h3 className="text-[22px] font-black text-textPrimary">Hesabına Giriş Yap</h3>
                <p className="text-[13px] text-textSecondary">
                  E-posta adresiniz ve şifreniz ile güvenli giriş yapın.
                </p>
              </div>

              <div className="space-y-3 pt-2">
                <div>
                  <label className="block text-[12px] font-bold text-textPrimary mb-1">
                    E-posta Adresi
                  </label>
                  <div className="relative">
                    <input
                      type="email"
                      required
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      placeholder="ornek@aradapay.com"
                      className="w-full h-[48px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] text-textPrimary focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                    />
                  </div>
                </div>

                <div>
                  <div className="flex items-center justify-between mb-1">
                    <label className="block text-[12px] font-bold text-textPrimary">
                      Şifre
                    </label>
                    <button
                      type="button"
                      onClick={() => {
                        clearMessages();
                        setResetEmail(email);
                        setMode('forgot_password');
                      }}
                      className="text-[11px] font-bold text-primaryEmerald hover:underline"
                    >
                      Şifremi Unuttum?
                    </button>
                  </div>
                  <div className="relative">
                    <input
                      type={showPassword ? 'text' : 'password'}
                      required
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      placeholder="••••••••"
                      className="w-full h-[48px] pl-4 pr-11 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] text-textPrimary focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                    >
                      {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                </div>
              </div>

              <button
                type="submit"
                disabled={isLoading}
                className="w-full h-[52px] rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm disabled:opacity-60 mt-4"
              >
                {isLoading ? (
                  <Loader2 className="w-5 h-5 animate-spin" />
                ) : (
                  <>
                    <span>Giriş Yap</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>

              <div className="text-center pt-2">
                <button
                  type="button"
                  onClick={() => {
                    clearMessages();
                    setRegisterStep(1);
                    setMode('register');
                  }}
                  className="text-[13px] font-semibold text-slate-600 hover:text-primaryEmerald"
                >
                  Hesabın yok mu? <b className="text-primaryEmerald">Kayıt Ol</b>
                </button>
              </div>
            </form>
          )}

          {/* ========================================================================= */}
          {/* MODE 3: FORGOT PASSWORD */}
          {/* ========================================================================= */}
          {mode === 'forgot_password' && (
            <form onSubmit={handlePasswordReset} className="space-y-4 animate-fadeIn">
              <div className="text-center space-y-1.5">
                <h3 className="text-[22px] font-black text-textPrimary">Şifremi Sıfırla</h3>
                <p className="text-[13px] text-textSecondary">
                  E-posta adresinize güvenli bir şifre sıfırlama bağlantısı göndereceğiz.
                </p>
              </div>

              <div className="space-y-3 pt-2">
                <div>
                  <label className="block text-[12px] font-bold text-textPrimary mb-1">
                    E-posta Adresi
                  </label>
                  <input
                    type="email"
                    required
                    value={resetEmail}
                    onChange={(e) => setResetEmail(e.target.value)}
                    placeholder="ornek@aradapay.com"
                    className="w-full h-[48px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] text-textPrimary focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={isLoading || resetSent}
                className="w-full h-[52px] rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm disabled:opacity-60 mt-4"
              >
                {isLoading ? (
                  <Loader2 className="w-5 h-5 animate-spin" />
                ) : (
                  <span>Sıfırlama Bağlantısı Gönder</span>
                )}
              </button>

              <div className="text-center pt-2">
                <button
                  type="button"
                  onClick={() => {
                    clearMessages();
                    setMode('login');
                  }}
                  className="text-[13px] font-bold text-slate-600 hover:text-slate-900"
                >
                  ← Giriş Ekranına Dön
                </button>
              </div>
            </form>
          )}

          {/* ========================================================================= */}
          {/* MODE 4: STEP-BY-STEP REGISTER (1..4) */}
          {/* ========================================================================= */}
          {mode === 'register' && (
            <div className="space-y-4 animate-fadeIn">
              {/* Step Progress Bar */}
              <div className="flex items-center gap-1.5">
                {[1, 2, 3, 4].map((s) => (
                  <div
                    key={s}
                    className={`h-1.5 rounded-full flex-1 transition-all ${
                      s === registerStep
                        ? 'bg-primaryEmerald'
                        : s < registerStep
                        ? 'bg-emerald-300'
                        : 'bg-slate-200'
                    }`}
                  />
                ))}
              </div>

              {/* STEP 1: Full Name, Email, Password */}
              {registerStep === 1 && (
                <form
                  onSubmit={(e) => {
                    e.preventDefault();
                    if (!fullName.trim() || !registerEmail.trim() || !registerPassword) return;
                    if (registerPassword !== confirmPassword) {
                      setErrorMessage('Şifreler birbiriyle eşleşmiyor.');
                      return;
                    }
                    if (registerPassword.length < 6) {
                      setErrorMessage('Şifre en az 6 karakter olmalıdır.');
                      return;
                    }
                    clearMessages();
                    setRegisterStep(2);
                  }}
                  className="space-y-3.5"
                >
                  <div className="text-center space-y-1">
                    <h3 className="text-[20px] font-black text-textPrimary">Kişisel Bilgilerin</h3>
                    <p className="text-[12px] text-textSecondary">
                      Hesabınızı oluşturmak için adınızı ve e-postanızı girin.
                    </p>
                  </div>

                  <div className="space-y-2.5">
                    <div>
                      <label className="block text-[12px] font-bold text-textPrimary mb-1">
                        Ad Soyad *
                      </label>
                      <input
                        type="text"
                        required
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                        placeholder="Mehmet Dilovan"
                        className="w-full h-[46px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                      />
                    </div>

                    <div>
                      <label className="block text-[12px] font-bold text-textPrimary mb-1">
                        E-posta Adresi *
                      </label>
                      <input
                        type="email"
                        required
                        value={registerEmail}
                        onChange={(e) => setRegisterEmail(e.target.value)}
                        placeholder="mehmet@ornek.com"
                        className="w-full h-[46px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                      />
                    </div>

                    <div>
                      <label className="block text-[12px] font-bold text-textPrimary mb-1">
                        Hesap Şifresi *
                      </label>
                      <div className="relative">
                        <input
                          type={showRegisterPassword ? 'text' : 'password'}
                          required
                          value={registerPassword}
                          onChange={(e) => setRegisterPassword(e.target.value)}
                          placeholder="En az 6 karakter"
                          className="w-full h-[46px] pl-4 pr-10 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                        />
                        <button
                          type="button"
                          onClick={() => setShowRegisterPassword(!showRegisterPassword)}
                          className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                        >
                          {showRegisterPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                        </button>
                      </div>
                    </div>

                    <div>
                      <label className="block text-[12px] font-bold text-textPrimary mb-1">
                        Şifre Tekrar *
                      </label>
                      <input
                        type="password"
                        required
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        placeholder="Şifrenizi tekrar girin"
                        className="w-full h-[46px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    className="w-full h-[50px] rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm mt-3"
                  >
                    <span>Devam Et →</span>
                  </button>
                </form>
              )}

              {/* STEP 2: Phone & OTP */}
              {registerStep === 2 && (
                <div className="space-y-4">
                  <div className="text-center space-y-1">
                    <h3 className="text-[20px] font-black text-textPrimary">Telefon Doğrulama</h3>
                    <p className="text-[12px] text-textSecondary">
                      Arkadaşlarınızla eşleşmek için telefon numaranızı ekleyin.
                    </p>
                  </div>

                  <div className="space-y-3">
                    <div>
                      <label className="block text-[12px] font-bold text-textPrimary mb-1">
                        Telefon Numarası
                      </label>
                      <div className="flex gap-2">
                        <span className="h-[46px] px-3 rounded-[14px] bg-slate-100 border border-slate-200 text-[14px] font-bold flex items-center text-slate-700">
                          +90
                        </span>
                        <input
                          type="tel"
                          value={phone}
                          onChange={(e) => setPhone(e.target.value.replace(/\D/g, '').slice(0, 10))}
                          placeholder="5XX XXX XX XX"
                          className="flex-1 h-[46px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
                        />
                      </div>
                    </div>

                    {!generatedOtp ? (
                      <button
                        type="button"
                        onClick={() => {
                          const code = Math.floor(100000 + Math.random() * 900000).toString();
                          setGeneratedOtp(code);
                        }}
                        className="w-full py-2.5 rounded-[12px] bg-slate-900 text-white font-bold text-[13px] hover:bg-slate-800 transition"
                      >
                        SMS Onay Kodu Gönder
                      </button>
                    ) : (
                      <div className="p-3 rounded-2xl bg-emerald-50 border border-emerald-200 space-y-2">
                        <div className="flex items-center justify-between text-[12px] font-bold text-emerald-800">
                          <span>📩 SMS Kodu: <b>{generatedOtp}</b></span>
                          <button
                            type="button"
                            onClick={() => setOtp(generatedOtp)}
                            className="px-2.5 py-1 rounded-lg bg-emerald-600 text-white text-[11px] font-black"
                          >
                            Otomatik Doldur
                          </button>
                        </div>
                        <input
                          type="text"
                          value={otp}
                          onChange={(e) => setOtp(e.target.value)}
                          placeholder="6 Haneli Kodu Girin"
                          className="w-full h-[40px] px-3 rounded-xl bg-white border border-emerald-300 text-[14px] text-center font-bold tracking-widest"
                        />
                      </div>
                    )}
                  </div>

                  <button
                    type="button"
                    onClick={() => setRegisterStep(3)}
                    className="w-full h-[50px] rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm mt-2"
                  >
                    <span>Doğrula ve Devam Et →</span>
                  </button>

                  <button
                    type="button"
                    onClick={() => setRegisterStep(3)}
                    className="w-full text-center text-[12px] text-slate-500 font-semibold hover:text-slate-800"
                  >
                    Bu adımı şimdilik atla (Opsiyonel)
                  </button>
                </div>
              )}

              {/* STEP 3: FAST IBAN */}
              {registerStep === 3 && (
                <div className="space-y-4">
                  <div className="text-center space-y-1">
                    <h3 className="text-[20px] font-black text-textPrimary">FAST IBAN Bilgin</h3>
                    <p className="text-[12px] text-textSecondary">
                      Ortak harcamalarda alacaklarınızı doğrudan almak için IBAN tanımlayın.
                    </p>
                  </div>

                  <div className="space-y-2">
                    <label className="block text-[12px] font-bold text-textPrimary">
                      Banka IBAN Numarası
                    </label>
                    <input
                      type="text"
                      value={iban}
                      onChange={(e) => setIban(e.target.value.toUpperCase())}
                      placeholder="TR64 0006 2000 ..."
                      className="w-full h-[46px] px-4 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] focus:outline-none focus:border-primaryEmerald focus:bg-white font-mono transition"
                    />
                    <div className="flex justify-end">
                      <button
                        type="button"
                        onClick={() => setIban('TR64 0006 2000 0000 5566 7788 99')}
                        className="text-[11px] font-bold text-primaryEmerald hover:underline"
                      >
                        + Varsayılan IBAN Kullan
                      </button>
                    </div>
                  </div>

                  <button
                    type="button"
                    onClick={() => {
                      setPin('');
                      setRegisterStep(4);
                    }}
                    className="w-full h-[50px] rounded-[16px] bg-primaryEmerald text-white font-black text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-95 transition shadow-sm mt-4"
                  >
                    <span>PIN Belirleme Adımına Geç →</span>
                  </button>
                </div>
              )}

              {/* STEP 4: 4-DIGIT PIN SETUP */}
              {/* STEP 4: 4-DIGIT PIN */}
              {registerStep === 4 && (
                <div className="space-y-6 text-center animate-fadeIn">
                  <div className="space-y-1.5">
                    <div className="w-12 h-12 rounded-2xl bg-emerald-100 text-primaryEmerald flex items-center justify-center mx-auto">
                      <KeyRound className="w-6 h-6 stroke-[2.2]" />
                    </div>
                    <h3 className="text-[20px] font-black text-textPrimary">
                      4 Haneli Güvenlik PIN Kodu Belirle
                    </h3>
                    <p className="text-[12px] text-textSecondary">
                      Finansal kilit açma ve onaylar için klavyenizle 4 haneli PIN girin.
                    </p>
                  </div>

                  {/* Keyboard-Friendly 4-Digit Native PIN Input */}
                  <div className="flex items-center justify-center gap-3 py-2">
                    {[0, 1, 2, 3].map((index) => (
                      <input
                        key={index}
                        id={`reg-pin-${index}`}
                        type="password"
                        inputMode="numeric"
                        pattern="[0-9]*"
                        maxLength={1}
                        autoFocus={index === 0}
                        value={pin[index] || ''}
                        disabled={isLoading}
                        onChange={(e) => {
                          const val = e.target.value.replace(/\D/g, '');
                          const currentPinArr = (pin || '').split('');
                          currentPinArr[index] = val.slice(-1);
                          const updatedPin = currentPinArr.join('').slice(0, 4);
                          setPin(updatedPin);
                          setPinError('');
                          if (val && index < 3) {
                            const nextInput = document.getElementById(`reg-pin-${index + 1}`);
                            nextInput?.focus();
                          }
                          if (updatedPin.length === 4) {
                            setTimeout(() => {
                              handleRegisterComplete();
                            }, 200);
                          }
                        }}
                        onKeyDown={(e) => {
                          if (e.key === 'Backspace' && !pin[index] && index > 0) {
                            const prevInput = document.getElementById(`reg-pin-${index - 1}`);
                            prevInput?.focus();
                          }
                        }}
                        onPaste={(e) => {
                          e.preventDefault();
                          const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 4);
                          if (pasted) {
                            setPin(pasted);
                            if (pasted.length === 4) {
                              setTimeout(() => handleRegisterComplete(), 200);
                            }
                          }
                        }}
                        className={`w-14 h-14 rounded-[16px] border-2 text-center text-[22px] font-extrabold transition-all ${
                          pin[index]
                            ? 'border-[#00875A] bg-emerald-50/50 text-[#00875A]'
                            : 'border-slate-200 bg-[#F8FAFC] text-[#0F172A] focus:border-[#00875A] focus:bg-white'
                        } focus:outline-none focus:ring-4 focus:ring-emerald-500/10`}
                      />
                    ))}
                  </div>

                  <button
                    type="button"
                    onClick={handleRegisterComplete}
                    disabled={pin.length < 4 || isLoading}
                    className="w-full h-12 rounded-[14px] bg-[#00875A] hover:bg-[#00744d] active:scale-98 text-white font-bold text-[14px] flex items-center justify-center gap-2 transition disabled:opacity-50 shadow-sm"
                  >
                    {isLoading ? (
                      <Loader2 className="w-5 h-5 animate-spin" />
                    ) : (
                      <>
                        <span>Hesabı Tamamla ve Başla</span>
                        <ArrowRight className="w-4 h-4" />
                      </>
                    )}
                  </button>
                </div>
              )}
            </div>
          )}

          {/* ========================================================================= */}
          {/* MODE 5: DEMO / PIN VERIFICATION */}
          {/* ========================================================================= */}
          {mode === 'pin' && selectedUserForPin && (
            <div className="space-y-6 text-center animate-fadeIn">
              <div className="space-y-1.5">
                <div className="w-12 h-12 rounded-2xl bg-emerald-100 text-primaryEmerald flex items-center justify-center mx-auto">
                  <KeyRound className="w-6 h-6 stroke-[2.2]" />
                </div>
                <h3 className="text-[20px] font-black text-textPrimary">
                  4 Haneli Güvenlik PIN Kodu
                </h3>
                <p className="text-[13px] text-textSecondary">
                  <b>{selectedUserForPin.fullName}</b> hesabını açmak için klavyenizden PIN girin (Örnek: 1234)
                </p>
              </div>

              {/* Keyboard-Friendly 4-Digit Native PIN Input */}
              <div className="flex items-center justify-center gap-3 py-2">
                {[0, 1, 2, 3].map((index) => (
                  <input
                    key={index}
                    id={`login-pin-${index}`}
                    type="password"
                    inputMode="numeric"
                    pattern="[0-9]*"
                    maxLength={1}
                    autoFocus={index === 0}
                    value={pin[index] || ''}
                    disabled={isLoading}
                    onChange={(e) => {
                      const val = e.target.value.replace(/\D/g, '');
                      const currentPinArr = (pin || '').split('');
                      currentPinArr[index] = val.slice(-1);
                      const updatedPin = currentPinArr.join('').slice(0, 4);
                      setPin(updatedPin);
                      setPinError('');
                      if (val && index < 3) {
                        const nextInput = document.getElementById(`login-pin-${index + 1}`);
                        nextInput?.focus();
                      }
                      if (updatedPin.length === 4) {
                        setTimeout(() => {
                          onLoginSuccess(selectedUserForPin);
                        }, 250);
                      }
                    }}
                    onKeyDown={(e) => {
                      if (e.key === 'Backspace' && !pin[index] && index > 0) {
                        const prevInput = document.getElementById(`login-pin-${index - 1}`);
                        prevInput?.focus();
                      }
                    }}
                    onPaste={(e) => {
                      e.preventDefault();
                      const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 4);
                      if (pasted) {
                        setPin(pasted);
                        if (pasted.length === 4) {
                          setTimeout(() => onLoginSuccess(selectedUserForPin), 250);
                        }
                      }
                    }}
                    className={`w-14 h-14 rounded-[16px] border-2 text-center text-[22px] font-extrabold transition-all ${
                      pin[index]
                        ? 'border-[#00875A] bg-emerald-50/50 text-[#00875A]'
                        : 'border-slate-200 bg-[#F8FAFC] text-[#0F172A] focus:border-[#00875A] focus:bg-white'
                    } focus:outline-none focus:ring-4 focus:ring-emerald-500/10`}
                  />
                ))}
              </div>

              {/* Action Buttons */}
              <div className="space-y-3 pt-2">
                <button
                  type="button"
                  onClick={() => {
                    setPin('1234');
                    setTimeout(() => onLoginSuccess(selectedUserForPin), 250);
                  }}
                  className="w-full h-11 rounded-[14px] bg-[#F1F5F9] hover:bg-slate-200 text-[#0F172A] font-bold text-[13px] transition flex items-center justify-center gap-1.5"
                >
                  <Zap className="w-4 h-4 text-[#00875A]" />
                  <span>Demo PIN'i Otomatik Doldur (1234)</span>
                </button>

                <button
                  type="button"
                  onClick={() => {
                    if (pin.length === 4) {
                      onLoginSuccess(selectedUserForPin);
                    }
                  }}
                  disabled={pin.length < 4}
                  className="w-full h-12 rounded-[14px] bg-[#00875A] hover:bg-[#00744d] active:scale-98 text-white font-bold text-[14px] flex items-center justify-center gap-2 transition disabled:opacity-40 shadow-sm"
                >
                  <span>Giriş Yap</span>
                  <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          )}
        </div>
      </main>

      {/* Footer */}
      <footer className="py-4 text-center text-[11px] text-textSecondary">
        <span>© 2026 AradaPay • Güvenli ve Gizli Kasa</span>
      </footer>
    </div>
  );
};
