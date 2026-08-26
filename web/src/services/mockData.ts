import { User, Expense, Group, Settlement, CrossSettlementOffer, Nudge } from '../types';

export const CURRENT_USER: User = {
  id: 'user_dilovan',
  email: 'dilovan@ardabank.com',
  username: 'dilovan',
  fullName: 'Dilovan Toprak',
  avatarUrl: '',
  phone: '+90 555 123 4567',
  iban: 'TR33 0006 1005 1978 4567 1000 01',
  tag: 'Dilovan#1453',
  defaultCurrency: 'TRY',
  createdAt: '2026-01-01T00:00:00Z'
};

export const INITIAL_USERS: User[] = [
  CURRENT_USER,
  {
    id: 'user_kaan',
    email: 'kaan@ardabank.com',
    username: 'kaan',
    fullName: 'Kaan Demir',
    avatarUrl: '',
    phone: '+90 532 987 6543',
    iban: 'TR62 0001 5001 2345 6789 0001 23',
    tag: 'Kaan#5674',
    defaultCurrency: 'TRY',
    createdAt: '2026-01-02T00:00:00Z'
  },
  {
    id: 'user_zeynep',
    email: 'zeynep@ardabank.com',
    username: 'zeynep',
    fullName: 'Zeynep Yılmaz',
    avatarUrl: '',
    phone: '+90 541 222 3344',
    iban: 'TR44 0006 2000 1122 3344 5566 77',
    tag: 'Zeynep#8899',
    defaultCurrency: 'TRY',
    createdAt: '2026-01-03T00:00:00Z'
  },
  {
    id: 'user_arda',
    email: 'arda@ardabank.com',
    username: 'arda',
    fullName: 'Arda Çelik',
    avatarUrl: '',
    phone: '+90 505 444 8899',
    iban: 'TR11 0006 4000 9988 7766 5544 33',
    tag: 'Arda#2026',
    defaultCurrency: 'TRY',
    createdAt: '2026-01-04T00:00:00Z'
  }
];

export const INITIAL_GROUPS: Group[] = [
  {
    id: 'group_ev',
    name: 'Kadıköy Evi',
    emoji: '🏠',
    category: 'Ev & Yaşam',
    createdBy: 'user_dilovan',
    createdAt: '2026-02-01T10:00:00Z',
    userBalance: 850.0,
    totalExpenses: 4600.0,
    members: [
      { id: 'user_dilovan', name: 'Dilovan Toprak', avatar: 'DT', tag: 'Dilovan#1453', balanceInGroup: 850.0 },
      { id: 'user_kaan', name: 'Kaan Demir', avatar: 'KD', tag: 'Kaan#5674', balanceInGroup: -450.0 },
      { id: 'user_zeynep', name: 'Zeynep Yılmaz', avatar: 'ZY', tag: 'Zeynep#8899', balanceInGroup: -400.0 }
    ]
  },
  {
    id: 'group_tatil',
    name: 'Kaş Dalış Tatili',
    emoji: '🌊',
    category: 'Seyahat',
    createdBy: 'user_kaan',
    createdAt: '2026-02-15T14:00:00Z',
    userBalance: -320.0,
    totalExpenses: 12400.0,
    members: [
      { id: 'user_dilovan', name: 'Dilovan Toprak', avatar: 'DT', tag: 'Dilovan#1453', balanceInGroup: -320.0 },
      { id: 'user_kaan', name: 'Kaan Demir', avatar: 'KD', tag: 'Kaan#5674', balanceInGroup: 1100.0 },
      { id: 'user_arda', name: 'Arda Çelik', avatar: 'AÇ', tag: 'Arda#2026', balanceInGroup: -780.0 }
    ]
  }
];

export const INITIAL_EXPENSES: Expense[] = [
  {
    id: 'exp_1',
    groupId: 'group_ev',
    paidBy: 'user_dilovan',
    amount: 1200.0,
    currency: 'TRY',
    description: 'Şubat Market Alışverişi',
    category: 'GROCERIES',
    splitMethod: 'EQUAL',
    status: 'APPROVED',
    createdAt: '2026-02-24T12:30:00Z',
    date: '24 Şubat 2026',
    splits: [
      { id: 'sp_1', expenseId: 'exp_1', userId: 'user_dilovan', amountOwed: 400.0, status: 'APPROVED' },
      { id: 'sp_2', expenseId: 'exp_1', userId: 'user_kaan', amountOwed: 400.0, status: 'APPROVED' },
      { id: 'sp_3', expenseId: 'exp_1', userId: 'user_zeynep', amountOwed: 400.0, status: 'APPROVED' }
    ]
  },
  {
    id: 'exp_2',
    groupId: 'group_tatil',
    paidBy: 'user_kaan',
    amount: 960.0,
    currency: 'TRY',
    description: 'Akşam Yemeği & Balık',
    category: 'DINING',
    splitMethod: 'EQUAL',
    status: 'APPROVED',
    createdAt: '2026-02-23T20:15:00Z',
    date: '23 Şubat 2026',
    splits: [
      { id: 'sp_4', expenseId: 'exp_2', userId: 'user_dilovan', amountOwed: 320.0, status: 'APPROVED' },
      { id: 'sp_5', expenseId: 'exp_2', userId: 'user_kaan', amountOwed: 320.0, status: 'APPROVED' },
      { id: 'sp_6', expenseId: 'exp_2', userId: 'user_arda', amountOwed: 320.0, status: 'APPROVED' }
    ]
  },
  {
    id: 'exp_3',
    paidBy: 'user_dilovan',
    amount: 450.0,
    currency: 'TRY',
    description: 'Sinema & Kahve',
    category: 'ENTERTAINMENT',
    splitMethod: 'EQUAL',
    status: 'APPROVED',
    createdAt: '2026-02-21T18:00:00Z',
    date: '21 Şubat 2026',
    splits: [
      { id: 'sp_7', expenseId: 'exp_3', userId: 'user_dilovan', amountOwed: 225.0, status: 'APPROVED' },
      { id: 'sp_8', expenseId: 'exp_3', userId: 'user_kaan', amountOwed: 225.0, status: 'APPROVED' }
    ]
  }
];

export const INITIAL_CROSS_OFFERS: CrossSettlementOffer[] = [
  {
    id: 'cross_offer_1',
    cycleAmount: 320.0,
    status: 'PENDING',
    createdAt: '2026-02-25T11:00:00Z',
    participants: [
      { id: 'user_dilovan', name: 'Dilovan Toprak', avatar: 'DT', username: 'dilovan' },
      { id: 'user_kaan', name: 'Kaan Demir', avatar: 'KD', username: 'kaan' },
      { id: 'user_arda', name: 'Arda Çelik', avatar: 'AÇ', username: 'arda' }
    ],
    steps: [
      { fromUserId: 'user_dilovan', fromUserName: 'Dilovan Toprak', toUserId: 'user_kaan', toUserName: 'Kaan Demir', amount: 320.0 },
      { fromUserId: 'user_kaan', fromUserName: 'Kaan Demir', toUserId: 'user_arda', toUserName: 'Arda Çelik', amount: 320.0 },
      { fromUserId: 'user_arda', fromUserName: 'Arda Çelik', toUserId: 'user_dilovan', toUserName: 'Dilovan Toprak', amount: 320.0 }
    ],
    approvals: {
      user_dilovan: true,
      user_kaan: false,
      user_arda: false
    }
  }
];
