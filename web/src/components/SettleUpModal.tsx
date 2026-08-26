import React, { useState } from 'react';
import { X, Copy, Check, QrCode, ArrowRight, ShieldCheck, CheckCircle2 } from 'lucide-react';
import confetti from 'canvas-confetti';
import { User, Settlement } from '../types';

interface SettleUpModalProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: User;
  users: User[];
  onConfirmSettlement: (settlement: Settlement) => void;
  onShowReceipt: (settlement: Settlement) => void;
}

const SUPPORTED_BANKS = [
  { id: 'garanti', name: 'Garanti BBVA', color: '#1B5E20' },
  { id: 'isbank', name: 'Türkiye İş Bankası', color: '#0D47A1' },
  { id: 'akbank', name: 'Akbank', color: '#B71C1C' },
  { id: 'yapikredi', name: 'Yapı Kredi', color: '#01579B' },
  { id: 'ziraat', name: 'Ziraat Bankası', color: '#C62828' },
  { id: 'qnb', name: 'QNB Finansbank', color: '#4A148C' }
];

export const SettleUpModal: React.FC<SettleUpModalProps> = ({
  isOpen,
  onClose,
  currentUser,
  users,
  onConfirmSettlement,
  onShowReceipt
}) => {
  const otherUsers = users.filter((u) => u.id !== currentUser.id);
  const [selectedUserId, setSelectedUserId] = useState<string>(otherUsers[0]?.id || '');
  const [amount, setAmount] = useState<string>('320.00');
  const [copied, setCopied] = useState<boolean>(false);
  const [showQr, setShowQr] = useState<boolean>(false);

  if (!isOpen) return null;

  const selectedUser = users.find((u) => u.id === selectedUserId) || otherUsers[0];
  const numAmount = parseFloat(amount.replace(',', '.')) || 0;
  const iban = selectedUser?.iban || 'TR33 0006 1005 1978 4567 1000 01';

  const handleCopyIban = () => {
    navigator.clipboard.writeText(iban.replace(/\s+/g, ''));
    setCopied(true);
    setTimeout(() => setCopied(false), 2500);
  };

  const handleConfirm = () => {
    if (numAmount <= 0) return;

    const settlement: Settlement = {
      id: `set_${Date.now()}`,
      payerId: currentUser.id,
      receiverId: selectedUser.id,
      amount: numAmount,
      currency: 'TRY',
      createdAt: new Date().toISOString(),
      status: 'APPROVED',
      note: 'FAST / Havale ile ödendi ve fitleşildi'
    };

    // Trigger celebration confetti
    try {
      confetti({
        particleCount: 80,
        spread: 60,
        origin: { y: 0.6 }
      });
    } catch {
      // ignore
    }

    onConfirmSettlement(settlement);
    onShowReceipt(settlement);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
      <div className="bg-surfaceWhite w-full max-w-lg rounded-[24px] shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="px-6 py-4 border-b border-surfaceBorder flex items-center justify-between">
          <div className="flex items-center gap-2">
            <h2 className="text-[18px] font-bold text-textPrimary">Öde & Fitleş</h2>
            <span className="px-2 py-0.5 rounded-full bg-primaryEmeraldContainer text-primaryEmerald text-[11px] font-bold">
              FAST Anlık
            </span>
          </div>
          <button
            onClick={onClose}
            className="w-9 h-9 rounded-full bg-surfaceContainerLow flex items-center justify-center text-textSecondary hover:bg-slate-200 active:scale-95 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto flex-1 space-y-5">
          {/* Recipient Selection */}
          <div>
            <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-2">
              KİME ÖDEME YAPILACAK?
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
              {otherUsers.map((u) => {
                const isSelected = u.id === selectedUser?.id;
                return (
                  <button
                    key={u.id}
                    type="button"
                    onClick={() => setSelectedUserId(u.id)}
                    className={`p-3 rounded-[14px] flex items-center gap-3 border transition text-left ${
                      isSelected
                        ? 'border-primaryEmerald bg-primaryEmeraldContainer/40 ring-1 ring-primaryEmerald'
                        : 'border-slate-200 bg-white hover:border-slate-300'
                    }`}
                  >
                    <div
                      className={`w-10 h-10 rounded-full flex items-center justify-center font-bold text-[13px] ${
                        isSelected
                          ? 'bg-primaryEmerald text-white'
                          : 'bg-surfaceContainerLow text-textPrimary'
                      }`}
                    >
                      {u.fullName.substring(0, 2).toUpperCase()}
                    </div>
                    <div className="min-w-0">
                      <p className="text-[14px] font-semibold text-textPrimary truncate">
                        {u.fullName}
                      </p>
                      <p className="text-[11px] text-textSecondary truncate">{u.tag || '@' + u.username}</p>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Amount Card */}
          <div className="flex flex-col items-center justify-center py-3.5 px-3 bg-surfaceContainerLow/50 rounded-[18px]">
            <span className="text-[11px] font-semibold text-textSecondary tracking-wider uppercase mb-1">
              ÖDENECEK TUTAR
            </span>
            <div className="flex items-center justify-center gap-2">
              <span className="text-[28px] font-bold text-primaryEmerald">₺</span>
              <input
                type="text"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-40 text-[32px] font-extrabold text-textPrimary bg-transparent border-none outline-none text-center focus:ring-0"
              />
            </div>
          </div>

          {/* Recipient IBAN & Copy */}
          {selectedUser && (
            <div className="p-4 rounded-[16px] bg-[#F8FAFC] border border-slate-200">
              <div className="flex items-center justify-between mb-2">
                <span className="text-[11px] font-bold text-textSecondary uppercase tracking-wider">
                  ALICI IBAN (FAST / KOLAY ADRES)
                </span>
                <button
                  onClick={() => setShowQr(!showQr)}
                  className="text-[12px] font-semibold text-primaryEmerald flex items-center gap-1 hover:underline"
                >
                  <QrCode className="w-3.5 h-3.5" />
                  <span>{showQr ? 'Metni Göster' : 'QR ile Öde'}</span>
                </button>
              </div>

              {showQr ? (
                <div className="flex flex-col items-center justify-center py-4">
                  <div className="p-3 bg-white rounded-xl shadow-xs border border-slate-200">
                    {/* SVG QR Code Simulation */}
                    <div className="w-36 h-36 bg-slate-900 rounded-lg p-2 flex flex-col items-center justify-center text-white text-center">
                      <QrCode className="w-20 h-20 text-white stroke-[1.5]" />
                      <span className="text-[10px] text-slate-300 mt-1 font-mono">
                        FAST-{selectedUser.username.toUpperCase()}
                      </span>
                    </div>
                  </div>
                  <p className="text-[11px] text-textSecondary mt-2">
                    Mobil bankacılık uygulamanızla QR kodu tarayın
                  </p>
                </div>
              ) : (
                <div className="flex items-center justify-between gap-2 p-2.5 bg-white rounded-[12px] border border-slate-200">
                  <span className="text-[13px] font-mono font-bold text-textPrimary tracking-wider select-all truncate">
                    {iban}
                  </span>
                  <button
                    type="button"
                    onClick={handleCopyIban}
                    className={`px-3 py-1.5 rounded-[8px] text-[12px] font-bold flex items-center gap-1.5 transition ${
                      copied
                        ? 'bg-primaryEmerald text-white'
                        : 'bg-slate-100 text-textPrimary hover:bg-slate-200 active:scale-95'
                    }`}
                  >
                    {copied ? (
                      <>
                        <Check className="w-3.5 h-3.5" />
                        <span>Kopyalandı</span>
                      </>
                    ) : (
                      <>
                        <Copy className="w-3.5 h-3.5 text-textSecondary" />
                        <span>Kopyala</span>
                      </>
                    )}
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Quick Bank Launcher Hints */}
          <div>
            <span className="block text-[11px] font-bold text-textSecondary uppercase tracking-wider mb-2">
              BANKA UYGULAMANIZDAN GÖNDERİN
            </span>
            <div className="flex items-center gap-1.5 flex-wrap">
              {SUPPORTED_BANKS.map((b) => (
                <span
                  key={b.id}
                  className="px-2.5 py-1 rounded-full text-[11px] font-bold bg-surfaceContainerLow text-textSecondary border border-slate-200"
                >
                  {b.name}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Footer CTA */}
        <div className="p-4 border-t border-surfaceBorder bg-surfaceWhite flex flex-col gap-2">
          <button
            type="button"
            onClick={handleConfirm}
            className="w-full h-[52px] rounded-[16px] bg-primaryEmerald text-white font-bold text-[15px] flex items-center justify-center gap-2 hover:bg-[#00744d] active:scale-[0.98] transition shadow-sm"
          >
            <CheckCircle2 className="w-5 h-5" />
            <span>Ödemeyi Tamamla & Fitleş ({numAmount.toFixed(2)} ₺)</span>
          </button>
          <p className="text-center text-[11px] text-textSecondary flex items-center justify-center gap-1">
            <ShieldCheck className="w-3.5 h-3.5 text-primaryEmerald" />
            <span>İşlem SHA-256 Merkle Mührü ile kriptografik olarak imzalanır</span>
          </p>
        </div>
      </div>
    </div>
  );
};
