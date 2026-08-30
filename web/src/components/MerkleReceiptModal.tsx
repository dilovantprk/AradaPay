import React, { useState, useEffect } from 'react';
import { X, ShieldCheck, Check, Copy, Printer, CheckCircle2, Lock } from 'lucide-react';
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

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-surfaceWhite w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-md rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* Header */}
        <div className="px-6 pt-[max(env(safe-area-inset-top),16px)] pb-4 border-b border-surfaceBorder flex items-center justify-between flex-shrink-0">
          <div className="flex items-center gap-2">
            <span className="w-8 h-8 rounded-full bg-primaryEmeraldContainer flex items-center justify-center text-primaryEmerald font-bold text-[12px]">
              AP
            </span>
            <div>
              <h2 className="text-[16px] font-bold text-textPrimary">AradaPay Dekontu</h2>
              <p className="text-[11px] text-textSecondary">Kriptografik Merkle Mührü</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-surfaceContainerLow flex items-center justify-center text-textSecondary hover:bg-slate-200 active:scale-95 transition"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Receipt Ticket Body */}
        <div className="p-6 overflow-y-auto flex-1 space-y-4">
          {/* Main Hero Amount in Receipt */}
          <div className="text-center py-4 px-3 bg-[#F8FAFC] rounded-[20px] border border-slate-200/80">
            <span className="text-[11px] font-bold text-textSecondary uppercase tracking-wider">
              İŞLEM TUTARI
            </span>
            <p className="text-[34px] font-extrabold text-primaryEmerald tracking-tight">
              {amount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} {currency}
            </p>
            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-primaryEmeraldContainer text-primaryEmerald text-[11px] font-bold mt-1">
              <CheckCircle2 className="w-3 h-3" />
              <span>İşlem Başarılı & Fitleşildi</span>
            </span>
          </div>

          {/* Ticket Info Rows */}
          <div className="space-y-2.5 text-[13px]">
            <div className="flex items-center justify-between py-1.5 border-b border-dashed border-slate-200">
              <span className="text-textSecondary">Gönderen (Borçlu)</span>
              <span className="font-bold text-textPrimary">{payerName}</span>
            </div>

            <div className="flex items-center justify-between py-1.5 border-b border-dashed border-slate-200">
              <span className="text-textSecondary">Alıcı (Alacaklı)</span>
              <span className="font-bold text-textPrimary">{receiverName}</span>
            </div>

            <div className="flex items-center justify-between py-1.5 border-b border-dashed border-slate-200">
              <span className="text-textSecondary">Tarih / Saat</span>
              <span className="font-medium text-textPrimary">
                {new Date(receiptData.timestamp).toLocaleString('tr-TR')}
              </span>
            </div>

            <div className="flex items-center justify-between py-1.5 border-b border-dashed border-slate-200">
              <span className="text-textSecondary">L2 Blok Numarası</span>
              <span className="font-mono font-bold text-textPrimary">#{receiptData.blockNumber}</span>
            </div>
          </div>

          {/* Cryptographic Merkle Hash Box */}
          <div className="p-3.5 rounded-[16px] bg-slate-900 text-white space-y-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-1.5">
                <Lock className="w-3.5 h-3.5 text-emerald-400" />
                <span className="text-[11px] font-bold text-emerald-400 uppercase tracking-wider">
                  HMAC-SHA256 & Merkle Root
                </span>
              </div>
              <button
                onClick={handleCopyHash}
                className="text-[11px] text-slate-300 hover:text-white flex items-center gap-1"
              >
                {copiedHash ? (
                  <>
                    <Check className="w-3 h-3 text-emerald-400" />
                    <span className="text-emerald-400">Kopyalandı</span>
                  </>
                ) : (
                  <>
                    <Copy className="w-3 h-3" />
                    <span>Kopyala</span>
                  </>
                )}
              </button>
            </div>

            <p className="text-[11px] font-mono text-slate-300 break-all bg-black/40 p-2 rounded-lg leading-relaxed select-all">
              {receiptData.merkleRoot}
            </p>

            <div className="flex items-center justify-between pt-1">
              <span className="text-[10px] text-slate-400">AradaPay Zero-Gas L2 Anchor</span>
              <button
                type="button"
                onClick={handleVerify}
                disabled={isVerifying}
                className="text-[11px] font-bold text-emerald-400 hover:underline active:scale-95 transition"
              >
                {isVerifying ? 'Doğrulanıyor...' : 'Matematiksel Doğrula ➜'}
              </button>
            </div>

            {verificationResult === true && (
              <div className="p-2 rounded bg-emerald-950/80 border border-emerald-500 text-emerald-300 text-[11px] font-medium flex items-center gap-1.5 mt-2">
                <ShieldCheck className="w-4 h-4 text-emerald-400 flex-shrink-0" />
                <span>Merkle Kök Kanıtı %100 Matematiksel Olarak Doğrulandı.</span>
              </div>
            )}
          </div>
        </div>

        {/* Footer Actions */}
        <div className="p-4 border-t border-surfaceBorder bg-surfaceWhite flex items-center gap-2.5">
          <button
            type="button"
            onClick={handlePrint}
            className="flex-1 h-[48px] rounded-[14px] bg-surfaceContainerLow text-textPrimary font-bold text-[13px] flex items-center justify-center gap-2 hover:bg-slate-200 active:scale-95 transition"
          >
            <Printer className="w-4 h-4 text-textSecondary" />
            <span>Yazdır / PDF Kaydet</span>
          </button>

          <button
            type="button"
            onClick={onClose}
            className="flex-1 h-[48px] rounded-[14px] bg-primaryEmerald text-white font-bold text-[13px] flex items-center justify-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-sm"
          >
            <span>Tamam</span>
          </button>
        </div>
      </div>
    </div>
  );
};
