export type Currency = 'TRY' | 'USD' | 'EUR';

export type SplitMethod = 'EQUAL' | 'EXACT' | 'PERCENTAGE';

export type ExpenseCategory =
  | 'DINING'
  | 'GROCERIES'
  | 'TRAVEL'
  | 'HOUSING'
  | 'ENTERTAINMENT'
  | 'UTILITIES'
  | 'SHOPPING'
  | 'OTHER';

export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface User {
  id: string;
  email: string;
  username: string;
  fullName: string;
  avatarUrl?: string | null;
  phone?: string | null;
  iban?: string | null;
  tag?: string | null; // e.g. 'Kaan#5674'
  defaultCurrency?: Currency;
  createdAt?: string;
}

export interface ExpenseSplit {
  id: string;
  expenseId: string;
  userId: string;
  amountOwed: number;
  percentage?: number | null;
  status: ApprovalStatus;
  approvedAt?: string | null;
}

export interface Expense {
  id: string;
  groupId?: string | null;
  paidBy: string; // User ID who paid
  amount: number;
  currency: Currency;
  description: string;
  category: ExpenseCategory;
  splitMethod: SplitMethod;
  dueDate?: string | null;
  status: ApprovalStatus;
  createdAt: string;
  date?: string | null;
  splits: ExpenseSplit[];
}

export interface GroupMember {
  id: string;
  name: string;
  avatar: string;
  tag: string;
  balanceInGroup: number; // +: alacaklı, -: borçlu
}

export interface GroupExpenseItem {
  id: string;
  groupId: string;
  title: string;
  totalAmount: number;
  payerId: string;
  payerName: string;
  yourShare: number;
  date: string;
  category: ExpenseCategory;
  isSettled: boolean;
}

export interface Group {
  id: string;
  name: string;
  emoji: string;
  category: string;
  members: GroupMember[];
  createdBy: string;
  createdAt: string;
  userBalance: number; // +: grupta alacaklı, -: grupta borçlu
  totalExpenses: number;
  isArchived?: boolean;
}

export interface Settlement {
  id: string;
  payerId: string;
  receiverId: string;
  amount: number;
  currency: Currency;
  createdAt: string;
  status: ApprovalStatus;
  note?: string | null;
}

export interface CrossSettlementStep {
  fromUserId: string;
  fromUserName: string;
  toUserId: string;
  toUserName: string;
  amount: number;
}

export interface CrossSettlementParticipant {
  id: string;
  name: string;
  avatar: string;
  username: string;
}

export interface CrossSettlementOffer {
  id: string;
  cycleAmount: number; // Sıfırlanacak borç tutarı
  participants: CrossSettlementParticipant[];
  steps: CrossSettlementStep[];
  approvals: Record<string, boolean>; // userId -> approved
  status: ApprovalStatus;
  createdAt: string;
}

export interface Nudge {
  id: string;
  fromUserId: string;
  toUserId: string;
  expenseId?: string | null;
  message: string;
  createdAt: string;
  isRead: boolean;
}

export interface SupportedBank {
  id: string;
  name: string;
  color: string;
  code: string;
}
