import React, { useState } from 'react';
import { X, Send, Search, CheckCircle2, ChevronDown, ChevronUp, Check } from 'lucide-react';
import { User, Nudge } from '../types';

interface RequestMoneyDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: User;
  users: User[];
  onSendNudge: (nudge: Nudge) => void;
}

export const RequestMoneyDrawer: React.FC<RequestMoneyDrawerProps> = ({
  isOpen,
  onClose,
  currentUser,
  users,
  onSendNudge
}) => {
  const otherUsers = users.filter((u) => u.id !== currentUser.id);
  const [selectedUser, setSelectedUser] = useState<User | null>(otherUsers[0] || null);
  const [searchQuery, setSearchQuery] = useState('');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [amount, setAmount] = useState('150.00');
  const [note, setNote] = useState('');
  const [sentSuccess, setSentSuccess] = useState(false);

  if (!isOpen) return null;

  const filteredUsers = otherUsers.filter(
    (u) =>
      u.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (u.tag && u.tag.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  const numAmount = parseFloat(amount.replace(',', '.')) || 0;
  const isValid = selectedUser && numAmount > 0;

  const handleSend = () => {
    if (!isValid || !selectedUser) return;

    const nudge: Nudge = {
      id: `nudge_${Date.now()}`,
      fromUserId: currentUser.id,
      toUserId: selectedUser.id,
      message: `${currentUser.fullName} senden ${numAmount.toFixed(2)} ₺ talep etti: "${note || 'AradaPay ödemesi'}"`,
      createdAt: new Date().toISOString(),
      isRead: false
    };

    onSendNudge(nudge);
    setSentSuccess(true);
    setTimeout(() => {
      setSentSuccess(false);
      onClose();
    }, 1500);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-fadeIn">
      <div className="bg-surfaceWhite w-full max-w-lg rounded-[24px] shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="px-6 py-4 border-b border-surfaceBorder flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Send className="w-5 h-5 text-primaryEmerald" />
            <h2 className="text-[18px] font-bold text-textPrimary">Para İste & Hatırlat</h2>
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
          {/* Recipient Selection Bar */}
          <div>
            <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-2">
              KİMDEN İSTENECEK?
            </label>

            <div className="flex items-center gap-2 p-2 rounded-[16px] bg-[#F8FAFC] border border-slate-200">
              {selectedUser ? (
                <div className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-primaryEmeraldContainer border border-primaryEmerald text-primaryEmerald text-[13px] font-bold">
                  <span className="w-5 h-5 rounded-full bg-primaryEmerald text-white text-[10px] flex items-center justify-center font-bold">
                    {selectedUser.fullName.substring(0, 2).toUpperCase()}
                  </span>
                  <span>{selectedUser.fullName.split(' ')[0]}</span>
                  <button
                    onClick={() => {
                      setSelectedUser(null);
                      setIsDropdownOpen(true);
                    }}
                    className="hover:text-primaryEmeraldDark"
                  >
                    <X className="w-3.5 h-3.5" />
                  </button>
                </div>
              ) : null}

              <input
                type="text"
                placeholder={selectedUser ? 'Değiştirmek için ara...' : 'Kişi ara veya seç...'}
                value={searchQuery}
                onFocus={() => setIsDropdownOpen(true)}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                  setIsDropdownOpen(true);
                }}
                className="flex-1 bg-transparent border-none outline-none text-[14px] text-textPrimary font-medium placeholder:text-slate-400 min-w-[120px]"
              />

              <button
                type="button"
                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                className="p-1.5 rounded-lg text-textSecondary hover:bg-slate-200"
              >
                {isDropdownOpen ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
              </button>
            </div>

            {/* Dropdown list */}
            {isDropdownOpen && (
              <div className="mt-2 border border-slate-200 rounded-[14px] bg-white divide-y divide-slate-100 max-h-48 overflow-y-auto shadow-sm">
                {filteredUsers.map((u) => {
                  const isChecked = selectedUser?.id === u.id;
                  return (
                    <div
                      key={u.id}
                      onClick={() => {
                        setSelectedUser(u);
                        setIsDropdownOpen(false);
                        setSearchQuery('');
                      }}
                      className={`p-3 flex items-center justify-between hover:bg-slate-50 cursor-pointer transition ${
                        isChecked ? 'bg-primaryEmeraldContainer/40' : ''
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-surfaceContainerLow flex items-center justify-center text-[12px] font-bold text-textPrimary">
                          {u.fullName.substring(0, 2).toUpperCase()}
                        </div>
                        <div>
                          <p className="text-[13px] font-semibold text-textPrimary">{u.fullName}</p>
                          <p className="text-[11px] text-textSecondary">{u.tag || '@' + u.username}</p>
                        </div>
                      </div>
                      {isChecked && <Check className="w-4 h-4 text-primaryEmerald" />}
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Amount Card */}
          <div className="flex flex-col items-center justify-center py-4 px-3 bg-surfaceContainerLow/50 rounded-[20px]">
            <span className="text-[11px] font-semibold text-textSecondary tracking-wider uppercase mb-1">
              TALEP EDİLECEK TUTAR
            </span>
            <div className="flex items-center justify-center gap-2">
              <span className="text-[32px] font-bold text-primaryEmerald">₺</span>
              <input
                type="text"
                placeholder="0,00"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-48 text-[38px] font-extrabold text-textPrimary bg-transparent border-none outline-none text-center focus:ring-0"
              />
            </div>

            {/* Quick Increment Chips */}
            <div className="flex items-center gap-2 mt-3 flex-wrap justify-center">
              {[50, 100, 250, 500].map((inc) => (
                <button
                  key={inc}
                  type="button"
                  onClick={() => {
                    const curr = parseFloat(amount.replace(',', '.')) || 0;
                    setAmount((curr + inc).toString());
                  }}
                  className="px-3 py-1 rounded-full bg-white border border-slate-200 text-textSecondary text-[12px] font-bold hover:border-primaryEmerald hover:text-primaryEmerald active:scale-95 transition shadow-2xs"
                >
                  +{inc} ₺
                </button>
              ))}
            </div>
          </div>

          {/* Note / Message */}
          <div>
            <label className="block text-[12px] font-bold text-textSecondary uppercase tracking-wider mb-1.5">
              HATIRLATMA NOTU
            </label>
            <input
              type="text"
              placeholder="Örn: Geçen haftaki akşam yemeği payı..."
              value={note}
              onChange={(e) => setNote(e.target.value)}
              className="w-full px-4 py-3 rounded-[14px] bg-white border border-slate-200 text-textPrimary text-[14px] font-medium outline-none focus:border-primaryEmerald focus:ring-2 focus:ring-primaryEmeraldContainer transition"
            />
          </div>
        </div>

        {/* Footer CTA */}
        <div className="p-4 border-t border-surfaceBorder bg-surfaceWhite">
          <button
            type="button"
            onClick={handleSend}
            disabled={!isValid || sentSuccess}
            className={`w-full h-[52px] rounded-[16px] font-bold text-[15px] flex items-center justify-center gap-2 transition shadow-sm ${
              sentSuccess
                ? 'bg-primaryEmerald text-white'
                : isValid
                ? 'bg-primaryEmerald text-white hover:bg-[#00744d] active:scale-[0.98]'
                : 'bg-slate-100 text-slate-400 cursor-not-allowed'
            }`}
          >
            {sentSuccess ? (
              <>
                <CheckCircle2 className="w-5 h-5" />
                <span>Hatırlatma (Dürtme) Gönderildi!</span>
              </>
            ) : (
              <>
                <Send className="w-5 h-5" />
                <span>Para Talebini & Dürtmeyi Gönder</span>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
