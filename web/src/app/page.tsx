'use client';

import dynamic from 'next/dynamic';
import React from 'react';

// Dynamically import App to prevent SSR hydration issues with Web APIs & Firebase
const App = dynamic(() => import('../App'), {
  ssr: false,
  loading: () => (
    <div className="min-h-screen bg-[#F8FAFC] flex flex-col items-center justify-center">
      <div className="w-12 h-12 rounded-[16px] bg-[#00875A] flex items-center justify-center text-white font-bold text-[18px] animate-pulse shadow-md">
        AP
      </div>
      <p className="text-[13px] font-bold text-[#0F172A] mt-3">AradaPay Yükleniyor...</p>
    </div>
  )
});

export default function HomePage() {
  return <App />;
}
