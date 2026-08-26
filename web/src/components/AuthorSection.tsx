import React from 'react';
import { Globe } from 'lucide-react';
import { GithubIcon, TelegramIcon } from './Icons';

export function AuthorSection() {
  return (
    <section className="py-12 bg-white border-t border-black/[0.06] text-center">
      <div className="max-w-md mx-auto px-4">
        <h3 className="text-sm font-semibold text-[#1D1D1F]">Mehmet Dilovan Toprak</h3>
        <p className="text-xs text-[#86868B] mt-0.5">Android & FinTech Software Architect</p>

        <div className="flex items-center justify-center space-x-4 mt-3 text-xs text-[#6E6E73]">
          <a
            href="https://dilovantprk.github.io"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-[#1D1D1F] flex items-center space-x-1"
          >
            <Globe className="w-3.5 h-3.5" />
            <span>dilovantprk.github.io</span>
          </a>
          <a
            href="https://github.com/dilovantprk"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-[#1D1D1F] flex items-center space-x-1"
          >
            <GithubIcon className="w-3.5 h-3.5" />
            <span>@dilovantprk</span>
          </a>
          <a
            href="https://t.me/dilovaniac"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:text-[#1D1D1F] flex items-center space-x-1"
          >
            <TelegramIcon className="w-3.5 h-3.5" />
            <span>@dilovaniac</span>
          </a>
        </div>
      </div>
    </section>
  );
}
