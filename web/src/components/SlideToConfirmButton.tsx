'use client';

import React, { useState, useRef, useEffect } from 'react';
import { ChevronRight, Check } from 'lucide-react';

interface SlideToConfirmButtonProps {
  onConfirm: () => void;
  label?: string;
  confirmedLabel?: string;
  disabled?: boolean;
}

export const SlideToConfirmButton: React.FC<SlideToConfirmButtonProps> = ({
  onConfirm,
  label = 'Onaylamak İçin Kaydırın',
  confirmedLabel = 'Onaylandı ✓',
  disabled = false
}) => {
  const [sliderPosition, setSliderPosition] = useState(0);
  const [isConfirmed, setIsConfirmed] = useState(false);
  const [isDragging, setIsDragging] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const handleStart = () => {
    if (disabled || isConfirmed) return;
    setIsDragging(true);
  };

  const handleMove = (clientX: number) => {
    if (!isDragging || !containerRef.current || isConfirmed) return;

    const containerRect = containerRef.current.getBoundingClientRect();
    const maxDrag = containerRect.width - 56; // 56px handle width
    const currentDrag = Math.max(0, Math.min(clientX - containerRect.left - 28, maxDrag));

    setSliderPosition(currentDrag);

    if (currentDrag >= maxDrag * 0.92) {
      setIsConfirmed(true);
      setIsDragging(false);
      setSliderPosition(maxDrag);
      onConfirm();
    }
  };

  const handleEnd = () => {
    if (!isDragging) return;
    setIsDragging(false);
    if (!isConfirmed) {
      setSliderPosition(0);
    }
  };

  useEffect(() => {
    const onMouseMove = (e: MouseEvent) => handleMove(e.clientX);
    const onMouseUp = () => handleEnd();
    const onTouchMove = (e: TouchEvent) => {
      if (e.touches[0]) handleMove(e.touches[0].clientX);
    };
    const onTouchEnd = () => handleEnd();

    if (isDragging) {
      window.addEventListener('mousemove', onMouseMove);
      window.addEventListener('mouseup', onMouseUp);
      window.addEventListener('touchmove', onTouchMove);
      window.addEventListener('touchend', onTouchEnd);
    }

    return () => {
      window.removeEventListener('mousemove', onMouseMove);
      window.removeEventListener('mouseup', onMouseUp);
      window.removeEventListener('touchmove', onTouchMove);
      window.removeEventListener('touchend', onTouchEnd);
    };
  }, [isDragging, isConfirmed]);

  return (
    <div
      ref={containerRef}
      className={`relative h-14 rounded-full overflow-hidden select-none transition-all ${
        disabled
          ? 'bg-slate-200 opacity-60 cursor-not-allowed'
          : isConfirmed
          ? 'bg-[#00875A] shadow-sm'
          : 'bg-[#F2F2F7] border border-black/[0.08]'
      }`}
    >
      {/* Background progress fill */}
      <div
        className="absolute top-0 left-0 bottom-0 bg-emerald-100 transition-all"
        style={{ width: `${sliderPosition + 56}px` }}
      />

      {/* Label Text */}
      <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
        <span
          className={`text-[13px] font-bold tracking-tight transition-all ${
            isConfirmed ? 'text-white' : 'text-[#8E8E93]'
          }`}
        >
          {isConfirmed ? confirmedLabel : label}
        </span>
      </div>

      {/* Draggable Slider Handle */}
      <div
        onMouseDown={handleStart}
        onTouchStart={handleStart}
        className={`absolute top-1 bottom-1 w-12 rounded-full flex items-center justify-center cursor-grab active:cursor-grabbing transition-transform ${
          isConfirmed
            ? 'bg-white text-[#00875A] shadow-md'
            : 'bg-[#00875A] text-white shadow-sm shadow-emerald-900/30'
        }`}
        style={{
          transform: `translateX(${sliderPosition + 4}px)`,
          transition: isDragging ? 'none' : 'transform 0.25s cubic-bezier(0.32, 0.72, 0, 1)'
        }}
      >
        {isConfirmed ? (
          <Check className="w-5 h-5 stroke-[2.5]" />
        ) : (
          <ChevronRight className="w-5 h-5 stroke-[2.5] animate-pulse" />
        )}
      </div>
    </div>
  );
};
