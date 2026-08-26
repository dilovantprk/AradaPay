import React from 'react';
import { AradaPayBrandWordmark } from './AradaPayLogo';
import { GithubIcon } from './Icons';

export function Footer() {
  return (
    <footer className="bg-[#FBFBFD] border-t border-black/[0.06] py-6 text-xs text-[#86868B]">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 flex flex-col sm:flex-row items-center justify-between gap-3">
        <div className="flex items-center space-x-2">
          <AradaPayBrandWordmark size={24} />
          <span>• Android 14+ FinTech Platform</span>
        </div>

        <div className="flex items-center space-x-4">
          <a
            href="/AradaPay.apk"
            download="AradaPay.apk"
            className="text-[#00875A] font-medium hover:underline"
          >
            APK İndir
          </a>
          <a
            href="https://github.com/dilovantprk/AradaPay"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-[#1D1D1F] flex items-center space-x-1"
          >
            <GithubIcon className="w-3.5 h-3.5" />
            <span>GitHub</span>
          </a>
          <span>MIT Lisansı</span>
        </div>
      </div>
    </footer>
  );
}
