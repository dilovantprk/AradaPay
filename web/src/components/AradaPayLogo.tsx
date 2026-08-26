import React from 'react';

interface AradaPayLogoProps {
  size?: number;
  className?: string;
}

export function AradaPayLogo({ size = 36, className = '' }: AradaPayLogoProps) {
  const outerRadius = Math.round(size * 0.28);
  const innerSize = Math.round(size * 0.48);
  const innerRadius = Math.round(size * 0.14);

  return (
    <div
      className={`relative flex items-center justify-center bg-[#0F172A] border border-black/10 shadow-xs ${className}`}
      style={{
        width: `${size}px`,
        height: `${size}px`,
        borderRadius: `${outerRadius}px`,
      }}
    >
      <div
        className="bg-[#00875A] transition-transform duration-200 group-hover:scale-105"
        style={{
          width: `${innerSize}px`,
          height: `${innerSize}px`,
          borderRadius: `${innerRadius}px`,
        }}
      />
    </div>
  );
}

export function AradaPayBrandWordmark({ size = 32 }: { size?: number }) {
  return (
    <div className="flex items-center space-x-2.5 cursor-pointer group select-none">
      <AradaPayLogo size={size} />
      <div className="flex flex-col">
        <span className="font-bold text-lg tracking-tight text-[#0F172A] leading-tight transition-colors">
          Arada<span className="text-[#00875A]">Pay</span>
        </span>
        <span className="text-[10px] text-[#64748B] tracking-wider font-medium uppercase leading-none">
          ArdaBank Platform
        </span>
      </div>
    </div>
  );
}
