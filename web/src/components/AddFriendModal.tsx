'use client';

import React, { useState } from 'react';
import {
  ArrowLeft,
  Check,
  Search,
  X,
  UserPlus,
  Contact,
  RefreshCw,
  Phone,
  Sparkles,
  Share2,
  CheckCircle2,
  Users
} from 'lucide-react';
import { User } from '../types';

interface PhoneBookContact {
  name: string;
  phone: string;
  isAradaPayMember: boolean;
  tag?: string;
  iban?: string;
}

const MOCK_PHONEBOOK_CONTACTS: PhoneBookContact[] = [
  { name: 'Ahmet Yılmaz', phone: '+90 532 111 22 33', isAradaPayMember: true, tag: '@ahmet#1044', iban: 'TR64 0006 2000 0000 1122 3344 55' },
  { name: 'Zeynep Kaya', phone: '+90 533 222 33 44', isAradaPayMember: true, tag: '@zeynep#8812', iban: 'TR64 0006 2000 0000 2233 4455 66' },
  { name: 'Burak Demir', phone: '+90 542 333 44 55', isAradaPayMember: false },
  { name: 'Elif Şahin', phone: '+90 535 444 55 66', isAradaPayMember: true, tag: '@elif#3390', iban: 'TR64 0006 2000 0000 3344 5566 77' },
  { name: 'Mert Öztürk', phone: '+90 536 555 66 77', isAradaPayMember: false },
  { name: 'Gizem Aksoy', phone: '+90 537 666 77 88', isAradaPayMember: true, tag: '@gizem#7721', iban: 'TR64 0006 2000 0000 4455 6677 88' },
  { name: 'Onur Çetin', phone: '+90 538 777 88 99', isAradaPayMember: false }
];

const CANDIDATE_MEMBERS: User[] = [
  { id: 'cand_1', email: 'caner@aradapay.com', username: 'caner_e', fullName: 'Caner Erkin', iban: 'TR64 0006 2000 0000 7788 9900 11', tag: '@caner#1903' },
  { id: 'cand_2', email: 'selin@aradapay.com', username: 'selin_a', fullName: 'Selin Aydın', iban: 'TR64 0006 2000 0000 6677 8899 00', tag: '@selin#2839' },
  { id: 'cand_3', email: 'deniz@aradapay.com', username: 'deniz_c', fullName: 'Deniz Çelik', iban: 'TR64 0006 2000 0000 8899 0011 22', tag: '@deniz#5522' },
  { id: 'cand_4', email: 'emre@aradapay.com', username: 'emre_t', fullName: 'Emre Tok', iban: 'TR64 0006 2000 0000 9900 1122 33', tag: '@emre#6710' },
  { id: 'cand_5', email: 'melis@aradapay.com', username: 'melis_y', fullName: 'Melis Yıldız', iban: 'TR64 0006 2000 0000 1133 5577 99', tag: '@melis#8341' }
];

interface AddFriendModalProps {
  isOpen: boolean;
  onClose: () => void;
  existingFriends: User[];
  onFriendAdded: (user: User) => void;
}

export const AddFriendModal: React.FC<AddFriendModalProps> = ({
  isOpen,
  onClose,
  existingFriends,
  onFriendAdded
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [isContactsSynced, setIsContactsSynced] = useState(false);
  const [syncingLoading, setSyncingLoading] = useState(false);
  const [successToast, setSuccessToast] = useState<string | null>(null);

  // Manual Add Form Tab
  const [showManualForm, setShowManualForm] = useState(false);
  const [manualName, setManualName] = useState('');
  const [manualTag, setManualTag] = useState('');
  const [manualIban, setManualIban] = useState('');

  if (!isOpen) return null;

  const existingIds = new Set(existingFriends.map((f) => f.id));
  const existingTags = new Set(existingFriends.map((f) => f.tag?.toLowerCase()));

  // Filter candidates
  const filteredCandidates = CANDIDATE_MEMBERS.filter((candidate) => {
    const notAlready = !existingIds.has(candidate.id) && !existingTags.has(candidate.tag?.toLowerCase());
    const matches =
      !searchQuery.trim() ||
      candidate.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      candidate.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (candidate.tag && candidate.tag.toLowerCase().includes(searchQuery.toLowerCase()));
    return notAlready && matches;
  });

  // Filter contacts
  const filteredContacts = MOCK_PHONEBOOK_CONTACTS.filter((contact) => {
    return (
      !searchQuery.trim() ||
      contact.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      contact.phone.includes(searchQuery) ||
      (contact.tag && contact.tag.toLowerCase().includes(searchQuery.toLowerCase()))
    );
  });

  const isSearchDirectTag =
    searchQuery.trim().startsWith('@') || searchQuery.trim().startsWith('#') || searchQuery.trim().length >= 3;

  const handleAddCandidate = (user: User) => {
    onFriendAdded(user);
    setSuccessToast(`${user.fullName} arkadaş listenize eklendi! ✨`);
    setTimeout(() => setSuccessToast(null), 2500);
  };

  const handleSyncContacts = () => {
    setSyncingLoading(true);
    setTimeout(() => {
      setSyncingLoading(false);
      setIsContactsSynced(true);
      setSuccessToast('Telefon rehberi başarıyla senkronize edildi! 📱');
      setTimeout(() => setSuccessToast(null), 2500);
    }, 600);
  };

  const handleAddDirectTag = () => {
    const clean = searchQuery.trim();
    const tag = clean.startsWith('@') ? clean : `@${clean}`;
    const directUser: User = {
      id: `direct_${Date.now()}`,
      email: `${clean.replace('@', '').replace('#', '_').toLowerCase()}@aradapay.com`,
      username: clean.replace('@', '').replace('#', '_').toLowerCase(),
      fullName: clean.replace('@', '').replace('#', ' ').replace(/\b\w/g, (c) => c.toUpperCase()),
      iban: 'TR64 0006 2000 0000 ' + Math.floor(1000 + Math.random() * 9000) + ' 22',
      tag: tag
    };
    onFriendAdded(directUser);
    setSearchQuery('');
    setSuccessToast(`${directUser.fullName} (${tag}) eklendi! 🚀`);
    setTimeout(() => setSuccessToast(null), 2500);
  };

  const handleManualSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!manualName.trim()) return;

    const tagFormatted = manualTag.trim().startsWith('@')
      ? manualTag.trim()
      : manualTag.trim()
      ? `@${manualTag.trim()}`
      : `@${manualName.toLowerCase().replace(/\s+/g, '')}#${Math.floor(1000 + Math.random() * 9000)}`;

    const newUser: User = {
      id: `manual_${Date.now()}`,
      email: `${manualName.toLowerCase().replace(/\s+/g, '')}@aradapay.com`,
      username: manualName.toLowerCase().replace(/\s+/g, '_'),
      fullName: manualName.trim(),
      iban: manualIban.trim() || 'TR64 0006 2000 0000 8888 99',
      tag: tagFormatted
    };

    onFriendAdded(newUser);
    setShowManualForm(false);
    setManualName('');
    setManualTag('');
    setManualIban('');
    setSuccessToast(`${newUser.fullName} başarıyla eklendi! ✅`);
    setTimeout(() => setSuccessToast(null), 2500);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-md animate-fadeIn">
      <div className="bg-white w-full h-[100dvh] sm:h-auto sm:max-h-[92vh] sm:max-w-lg rounded-none sm:rounded-[28px] shadow-2xl border-0 sm:border border-slate-200 overflow-hidden flex flex-col animate-appleSheet sm:animate-applePop">
        {/* ========================================================================= */}
        {/* 1. TOP APP BAR (1:1 Android Style) */}
        {/* ========================================================================= */}
        <div className="px-5 pt-[max(env(safe-area-inset-top),16px)] pb-4 bg-white border-b border-slate-200 flex items-center justify-between flex-shrink-0">
          <button
            onClick={onClose}
            className="w-10 h-10 rounded-[12px] bg-slate-100 flex items-center justify-center text-slate-800 hover:bg-slate-200 active:scale-95 transition"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>

          <h3 className="text-[18px] font-black text-textPrimary tracking-tight">
            Arkadaş Ekle
          </h3>

          <button
            onClick={onClose}
            className="w-10 h-10 rounded-[12px] bg-emerald-100 flex items-center justify-center text-primaryEmerald hover:bg-emerald-200 active:scale-95 transition"
          >
            <Check className="w-5 h-5 stroke-[2.5]" />
          </button>
        </div>

        {/* Toast Alert */}
        {successToast && (
          <div className="bg-primaryEmerald text-white text-[13px] font-bold py-2.5 px-4 text-center animate-fadeIn">
            {successToast}
          </div>
        )}

        {/* ========================================================================= */}
        {/* 2. INTRINSIC LIVE SEARCH BAR */}
        {/* ========================================================================= */}
        <div className="p-4 border-b border-slate-200 bg-white">
          <div className="relative flex items-center">
            <Search className="w-5 h-5 text-slate-400 absolute left-3.5 pointer-events-none" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Kullanıcı adı (@dilovan), #tag veya isim..."
              className="w-full h-11 pl-11 pr-10 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-[14px] text-textPrimary placeholder:text-slate-400 focus:outline-none focus:border-primaryEmerald focus:bg-white transition"
            />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery('')}
                className="absolute right-3 p-1 text-slate-400 hover:text-slate-600"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>
        </div>

        {/* Direct Add Prompt for Custom Tag */}
        {isSearchDirectTag &&
          !filteredCandidates.some((c) => c.tag?.toLowerCase() === searchQuery.trim().toLowerCase()) && (
            <div className="px-5 py-3 bg-emerald-50/70 border-b border-emerald-100 flex items-center justify-between animate-fadeIn">
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-emerald-100 text-primaryEmerald flex items-center justify-center">
                  <UserPlus className="w-4 h-4" />
                </div>
                <div>
                  <div className="text-[13px] font-bold text-textPrimary font-mono">
                    {searchQuery.trim().startsWith('@') ? searchQuery.trim() : `@${searchQuery.trim()}`}
                  </div>
                  <div className="text-[11px] text-slate-500 font-medium">Bu kullanıcıyı doğrudan ekle</div>
                </div>
              </div>

              <button
                onClick={handleAddDirectTag}
                className="px-3.5 py-1.5 rounded-[10px] bg-primaryEmerald text-white text-[12px] font-bold hover:bg-[#00744d] active:scale-95 transition shadow-2xs"
              >
                + Ekle
              </button>
            </div>
          )}

        {/* ========================================================================= */}
        {/* 3. SCROLLABLE LIST (Candidate members & Phone contacts) */}
        {/* ========================================================================= */}
        <div className="flex-1 overflow-y-auto p-5 space-y-6">
          {/* SECTION 1: ARADAPAY'DE BULUNAN KİŞİLER */}
          {filteredCandidates.length > 0 && (
            <div className="space-y-3">
              <div className="text-[11px] font-black text-slate-400 uppercase tracking-wider">
                ARADAPAY'DE BULUNAN KİŞİLER ({filteredCandidates.length})
              </div>

              <div className="space-y-2">
                {filteredCandidates.map((user) => {
                  const initials = user.fullName
                    .split(' ')
                    .map((n) => n[0])
                    .join('');
                  return (
                    <div
                      key={user.id}
                      className="p-3 rounded-[16px] bg-[#F8FAFC] border border-slate-200/80 flex items-center justify-between hover:border-emerald-300 transition"
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-xl bg-white border border-slate-200 text-slate-900 font-bold text-[14px] flex items-center justify-center shadow-2xs">
                          {initials}
                        </div>
                        <div>
                          <div className="text-[14px] font-bold text-textPrimary">{user.fullName}</div>
                          <div className="text-[12px] text-slate-500 font-medium">
                            {user.tag || `@${user.username}`}
                          </div>
                        </div>
                      </div>

                      <button
                        onClick={() => handleAddCandidate(user)}
                        className="w-9 h-9 rounded-xl bg-emerald-100 text-primaryEmerald hover:bg-emerald-200 flex items-center justify-center active:scale-95 transition"
                        title="Arkadaş Ekle"
                      >
                        <UserPlus className="w-4 h-4" />
                      </button>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* SECTION 2: TELEFON REHBERİ SENKRONİZASYONU */}
          <div className="space-y-3">
            <div className="text-[11px] font-black text-slate-400 uppercase tracking-wider">
              TELEFON REHBERİ SENKRONİZASYONU
            </div>

            {!isContactsSynced ? (
              <div className="p-4 rounded-[18px] bg-emerald-50/50 border border-emerald-200/80 flex items-center justify-between gap-3">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-emerald-100 text-primaryEmerald flex items-center justify-center">
                    <Contact className="w-5 h-5" />
                  </div>
                  <div>
                    <div className="text-[14px] font-bold text-textPrimary">Rehberdeki Arkadaşları Bul</div>
                    <div className="text-[12px] text-slate-500">AradaPay kullananları otomatik eşleştir</div>
                  </div>
                </div>

                <button
                  onClick={handleSyncContacts}
                  disabled={syncingLoading}
                  className="px-3.5 py-2 rounded-[12px] bg-primaryEmerald text-white text-[12px] font-bold flex items-center gap-1.5 hover:bg-[#00744d] active:scale-95 transition shadow-2xs flex-shrink-0"
                >
                  <RefreshCw className={`w-3.5 h-3.5 ${syncingLoading ? 'animate-spin' : ''}`} />
                  <span>{syncingLoading ? 'Taranıyor...' : 'Bağla'}</span>
                </button>
              </div>
            ) : (
              <div className="space-y-2">
                {filteredContacts.map((contact, idx) => (
                  <div
                    key={idx}
                    className="p-3 rounded-[16px] bg-white border border-slate-200 flex items-center justify-between"
                  >
                    <div className="flex items-center gap-3">
                      <div
                        className={`w-10 h-10 rounded-xl flex items-center justify-center font-bold text-[14px] ${
                          contact.isAradaPayMember
                            ? 'bg-emerald-100 text-primaryEmerald'
                            : 'bg-slate-100 text-slate-700'
                        }`}
                      >
                        {contact.name.slice(0, 2).toUpperCase()}
                      </div>
                      <div>
                        <div className="flex items-center gap-1.5">
                          <span className="text-[14px] font-bold text-textPrimary">{contact.name}</span>
                          {contact.isAradaPayMember && (
                            <span className="px-1.5 py-0.2 rounded-md bg-emerald-100 text-primaryEmerald text-[10px] font-extrabold">
                              AradaPay
                            </span>
                          )}
                        </div>
                        <div className="text-[12px] text-slate-500 font-medium">
                          {contact.tag || contact.phone}
                        </div>
                      </div>
                    </div>

                    {contact.isAradaPayMember ? (
                      <button
                        onClick={() =>
                          handleAddCandidate({
                            id: `contact_${idx}`,
                            email: `${contact.name.toLowerCase().replace(/\s+/g, '')}@aradapay.com`,
                            username: contact.name.toLowerCase().replace(/\s+/g, '_'),
                            fullName: contact.name,
                            iban: contact.iban || 'TR64 0006 2000 0000 1122 3344 55',
                            tag: contact.tag
                          })
                        }
                        className="px-3 py-1.5 rounded-[10px] bg-emerald-100 text-primaryEmerald text-[12px] font-bold hover:bg-emerald-200 active:scale-95 transition"
                      >
                        + Ekle
                      </button>
                    ) : (
                      <button
                        onClick={() => {
                          setSuccessToast(`${contact.name} kişisine SMS davet linki kopyalandı! 📲`);
                          setTimeout(() => setSuccessToast(null), 2500);
                        }}
                        className="px-3 py-1.5 rounded-[10px] bg-slate-100 text-slate-600 text-[12px] font-bold hover:bg-slate-200 active:scale-95 transition"
                      >
                        Davet Et
                      </button>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* SECTION 3: MANUEL KİŞİ EKLEME ACCORDION */}
          <div className="pt-2 border-t border-slate-200">
            {!showManualForm ? (
              <button
                onClick={() => setShowManualForm(true)}
                className="w-full py-2.5 rounded-[14px] bg-[#F8FAFC] border border-slate-200 text-slate-700 text-[13px] font-bold hover:bg-slate-100 transition"
              >
                + Manuel İsim / IBAN ile Kişi Ekle
              </button>
            ) : (
              <form onSubmit={handleManualSubmit} className="p-4 rounded-[18px] bg-[#F8FAFC] border border-slate-200 space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-[13px] font-bold text-textPrimary">Manuel Kişi Bilgisi</span>
                  <button
                    type="button"
                    onClick={() => setShowManualForm(false)}
                    className="text-slate-400 hover:text-slate-600 text-[12px]"
                  >
                    Vazgeç
                  </button>
                </div>

                <input
                  type="text"
                  required
                  value={manualName}
                  onChange={(e) => setManualName(e.target.value)}
                  placeholder="Ad Soyad (örn: Kerem Aktürkoğlu)"
                  className="w-full h-10 px-3.5 rounded-[12px] bg-white border border-slate-200 text-[13px] focus:outline-none focus:border-primaryEmerald"
                />

                <input
                  type="text"
                  value={manualTag}
                  onChange={(e) => setManualTag(e.target.value)}
                  placeholder="@tag veya kullanıcı adı (opsiyonel)"
                  className="w-full h-10 px-3.5 rounded-[12px] bg-white border border-slate-200 text-[13px] focus:outline-none focus:border-primaryEmerald font-mono"
                />

                <input
                  type="text"
                  value={manualIban}
                  onChange={(e) => setManualIban(e.target.value)}
                  placeholder="TR.. ile başlayan IBAN (opsiyonel)"
                  className="w-full h-10 px-3.5 rounded-[12px] bg-white border border-slate-200 text-[13px] focus:outline-none focus:border-primaryEmerald font-mono"
                />

                <button
                  type="submit"
                  className="w-full h-10 rounded-[12px] bg-primaryEmerald text-white text-[13px] font-bold hover:bg-[#00744d] transition shadow-2xs"
                >
                  Listeye Kaydet
                </button>
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
