'use client';

import React, { useState, useEffect } from 'react';
import { AradaPayBrandWordmark } from './AradaPayLogo';
import { Download, Menu, X, ArrowDownToLine } from 'lucide-react';
import { GithubIcon } from './Icons';

export function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 10);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-200 ${
        scrolled
          ? 'bg-white/90 backdrop-blur-xl border-b border-slate-200/80 shadow-xs py-2.5'
          : 'bg-[#F8F9FA]/80 backdrop-blur-md py-4'
      }`}
    >
      <div className="max-w-5xl mx-auto px-4 sm:px-6 flex items-center justify-between">
        {/* Logo */}
        <a href="#" className="flex items-center">
          <AradaPayBrandWordmark size={32} />
        </a>

        {/* Desktop Links */}
        <nav className="hidden md:flex items-center space-x-6 text-xs font-semibold text-[#64748B]">
          <a href="#ekranlar" className="hover:text-[#0F172A] transition-colors">
            Uygulama
          </a>
          <a href="#dfs-algoritmasi" className="hover:text-[#0F172A] transition-colors">
            DFS Optimizasyonu
          </a>
          <a href="#merkle-dekont" className="hover:text-[#0F172A] transition-colors">
            Merkle Dekont
          </a>
          <a href="#ozellikler" className="hover:text-[#0F172A] transition-colors">
            Özellikler
          </a>
          <a href="#kurulum" className="hover:text-[#0F172A] transition-colors">
            Kurulum
          </a>
        </nav>

        {/* CTA */}
        <div className="hidden sm:flex items-center space-x-2.5">
          <a
            href="https://github.com/dilovantprk/AradaPay"
            target="_blank"
            rel="noopener noreferrer"
            className="p-2 text-[#64748B] hover:text-[#0F172A] hover:bg-slate-100 rounded-full transition-colors"
            title="GitHub"
          >
            <GithubIcon className="w-4 h-4" />
          </a>

          <a
            href="/AradaPay.apk"
            download="AradaPay.apk"
            className="inline-flex items-center space-x-1.5 bg-[#00875A] hover:bg-[#00754e] text-white text-xs font-semibold px-4 py-2 rounded-full shadow-xs transition-all active:scale-[0.98]"
          >
            <ArrowDownToLine className="w-3.5 h-3.5" />
            <span>APK İndir</span>
          </a>
        </div>

        {/* Mobile Toggle */}
        <div className="flex md:hidden items-center space-x-2">
          <a
            href="/AradaPay.apk"
            download="AradaPay.apk"
            className="inline-flex items-center space-x-1 bg-[#00875A] text-white text-xs font-semibold px-3 py-1.5 rounded-full"
          >
            <Download className="w-3 h-3" />
            <span>APK</span>
          </a>
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-1.5 text-[#0F172A] hover:bg-slate-100 rounded-lg"
            aria-label="Menü"
          >
            {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-white/95 backdrop-blur-xl border-b border-slate-200 px-5 py-4 space-y-3 mt-2 shadow-md">
          <a
            href="#ekranlar"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-sm font-semibold text-[#0F172A]"
          >
            Uygulama İçi Simülatör
          </a>
          <a
            href="#dfs-algoritmasi"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-sm font-medium text-[#64748B]"
          >
            DFS Borç Sadeleştirme
          </a>
          <a
            href="#merkle-dekont"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-sm font-medium text-[#64748B]"
          >
            Merkle Blokzincir Dekont
          </a>
          <a
            href="#ozellikler"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-sm font-medium text-[#64748B]"
          >
            Özellikler
          </a>
          <a
            href="#kurulum"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-sm font-medium text-[#64748B]"
          >
            Android Kurulum
          </a>
        </div>
      )}
    </header>
  );
}
