'use client';

import React, { useState, useEffect } from 'react';
import { ArrowLeft, ShieldCheck, Check, Copy, Printer, CheckCircle2, Lock } from 'lucide-react';
import { MerkleTreeBlockchainEngine, MerkleReceiptData } from '../algorithms/MerkleTreeBlockchainEngine';

interface MerkleReceiptModalProps {
  isOpen: boolean;
  onClose: () => void;
  txId?: string;
  payerName?: string;
  receiverName?: string;
  amount?: number;
  currency?: string;
}

export const MerkleReceiptModal: React.FC<MerkleReceiptModalProps> = ({
  isOpen,
  onClose,
  txId = 'tx_demo_882',
  payerName = 'Dilovan Toprak',
  receiverName = 'Kaan Demir',
  amount = 320.0,
  currency = '₺'
}) => {
  const [receiptData, setReceiptData] = useState<MerkleReceiptData | null>(null);
  const [copiedHash, setCopiedHash] = useState(false);
  const [verificationResult, setVerificationResult] = useState<boolean | null>(null);
  const [isVerifying, setIsVerifying] = useState(false);

  useEffect(() => {
    if (isOpen) {
      MerkleTreeBlockchainEngine.generateMerkleReceipt(
        txId,
        payerName,
        receiverName,
        amount,
        'TRY'
      ).then((data) => {
        setReceiptData(data);
        setVerificationResult(null);
      });
    }
  }, [isOpen, txId, payerName, receiverName, amount]);

  if (!isOpen || !receiptData) return null;

  const handleCopyHash = () => {
    navigator.clipboard.writeText(receiptData.txHash);
    setCopiedHash(true);
    setTimeout(() => setCopiedHash(false), 2000);
  };

  const handleVerify = async () => {
    setIsVerifying(true);
    const valid = await MerkleTreeBlockchainEngine.verifyMerkleProof(
      receiptData.txHash,
      receiptData.merkleRoot,
      receiptData.merkleProof
    );
    setTimeout(() => {
      setVerificationResult(valid);
      setIsVerifying(false);
    }, 400);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-md rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* Top Bar (1:1 Android Style) */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-3 bg-white border-b border-slate-100 flex items-center justify-between flex-shrink-0">
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-[12px] bg-[#F1F5F9] flex items-center justify-center text-[#0F172A] hover:bg-slate-200 active:scale-95 transition"
            title="Geri"
          >
            <ArrowLeft className="w-5 h-5 stroke-[2.2]" />
          </button>

          <h3 className="text-[17px] font-bold text-[#0F172A] tracking-tight">
            Dijital Ödeşme Dekontu
          </h3>

          <div className="w-10" />
        </div>

        {/* Receipt Body (Flat, Light & De-nested) */}
        <div className="flex-1 overflow-y-auto divide-y divide-slate-100 text-left">
          {/* 1. Hero Amount */}
          <div className="px-6 py-6 text-center space-y-1 bg-white">
            <span className="text-[11px] font-bold text-[#64748B] tracking-[0.05em] uppercase block">
              İŞLEM TUTARI
            </span>
            <p className="text-[40px] font-extrabold text-[#00875A] font-tabular tracking-tight">
              {amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} {currency}
            </p>
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-50 text-[#00875A] text-[11px] font-bold mt-1">
              <CheckCircle2 className="w-3.5 h-3.5" />
              <span>FAST ile Masadaki Hesap Tertemiz Oldu</span>
            </div>
          </div>

          {/* 2. Key Details List */}
          <div className="px-6 py-4 space-y-3 bg-white text-[13px]">
            <div className="flex items-center justify-between py-1">
              <span className="text-[#64748B]">Gönderen:</span>
              <span className="font-bold text-[#0F172A]">{payerName}</span>
            </div>
            <div className="flex items-center justify-between py-1">
              <span className="text-[#64748B]">Alıcı:</span>
              <span className="font-bold text-[#0F172A]">{receiverName}</span>
            </div>
            <div className="flex items-center justify-between py-1">
              <span className="text-[#64748B]">Tarih:</span>
              <span className="font-semibold text-[#0F172A]">
                {new Date(receiptData.timestamp).toLocaleDateString('tr-TR', {
                  day: 'numeric',
                  month: 'long',
                  year: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit'
                })}
              </span>
            </div>
            <div className="flex items-center justify-between py-1">
              <span className="text-[#64748B]">İşlem ID:</span>
              <span className="font-mono text-[11px] text-[#64748B]">{txId}</span>
            </div>
          </div>

          {/* 3. Security Signature */}
          <div className="px-6 py-4 space-y-3 bg-white">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-1.5">
                <Lock className="w-4 h-4 text-[#00875A]" />
                <span className="text-[12px] font-bold text-[#0F172A]">Dijital Güvenlik İmzası</span>
              </div>
              <button
                onClick={handleCopyHash}
                className="text-[11px] text-[#00875A] hover:underline flex items-center gap-1 font-bold"
              >
                {copiedHash ? <Check className="w-3 h-3" /> : <Copy className="w-3 h-3" />}
                <span>{copiedHash ? 'Kopyalandı' : 'Kopyala'}</span>
              </button>
            </div>

            <p className="font-mono text-[11px] text-[#475569] break-all bg-[#F8FAFC] p-3 rounded-[12px] leading-relaxed border border-slate-200">
              {receiptData.txHash}
            </p>

            <div>
              <button
                onClick={handleVerify}
                disabled={isVerifying}
                className="w-full py-2.5 rounded-[12px] bg-[#F1F5F9] hover:bg-slate-200 active:scale-98 text-[#0F172A] text-[12px] font-bold flex items-center justify-center gap-1.5 transition"
              >
                {isVerifying ? (
                  <span>Doğrulanıyor...</span>
                ) : verificationResult === true ? (
                  <span className="text-[#00875A] flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    İşlem Doğrulandı ✓
                  </span>
                ) : (
                  <span className="flex items-center gap-1">
                    <ShieldCheck className="w-3.5 h-3.5 text-[#00875A]" />
                    İşlem Doğruluğunu Kontrol Et
                  </span>
                )}
              </button>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 bg-white border-t border-slate-100 pb-[max(env(safe-area-inset-bottom),16px)]">
          <button
            onClick={onClose}
            className="w-full h-12 rounded-[14px] bg-[#00875A] hover:bg-[#00744d] text-white font-bold text-[14px] flex items-center justify-center gap-2 active:scale-[0.98] transition shadow-sm shadow-emerald-900/20"
          >
            Tamam
          </button>
        </div>
      </div>
    </div>
  );
};
