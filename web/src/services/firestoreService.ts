import {
  collection,
  doc,
  getDoc,
  getDocs,
  setDoc,
  updateDoc,
  deleteDoc,
  onSnapshot,
  query,
  orderBy,
  where,
  limit
} from 'firebase/firestore';
import { db } from '../firebase/config';
import {
  User,
  Expense,
  Settlement,
  Group,
  GroupExpenseItem,
  CrossSettlementOffer,
  Nudge,
  ApprovalStatus
} from '../types';

export const FirestoreService = {
  // --- USERS ---
  async getUser(userId: string): Promise<User | null> {
    try {
      const docRef = doc(db, 'users', userId);
      const snapshot = await getDoc(docRef);
      if (snapshot.exists()) {
        return snapshot.data() as User;
      }
      return null;
    } catch (e) {
      console.warn('Firestore getUser error:', e);
      return null;
    }
  },

  async findUserByTag(tag: string): Promise<User | null> {
    try {
      const q = query(collection(db, 'users'), where('tag', '==', tag), limit(1));
      const snapshot = await getDocs(q);
      if (!snapshot.empty) {
        return snapshot.docs[0].data() as User;
      }
      return null;
    } catch (e) {
      console.warn('Firestore findUserByTag error:', e);
      return null;
    }
  },

  subscribeUser(userId: string, callback: (user: User | null) => void) {
    try {
      const docRef = doc(db, 'users', userId);
      return onSnapshot(docRef, (snapshot) => {
        if (snapshot.exists()) {
          callback(snapshot.data() as User);
        } else {
          callback(null);
        }
      }, (err) => {
        console.warn('User subscription fallback to local', err);
      });
    } catch (e) {
      console.warn('Firestore subscribeUser error:', e);
      return () => {};
    }
  },

  subscribeAllUsers(callback: (users: User[]) => void) {
    try {
      const colRef = collection(db, 'users');
      return onSnapshot(colRef, (snapshot) => {
        const users = snapshot.docs.map((d) => d.data() as User);
        callback(users);
      }, (err) => {
        console.warn('Users collection listener fallback', err);
      });
    } catch (e) {
      console.warn('Firestore subscribeAllUsers error:', e);
      return () => {};
    }
  },

  async saveUser(user: User): Promise<boolean> {
    try {
      await setDoc(doc(db, 'users', user.id), user);
      return true;
    } catch (e) {
      console.error('saveUser error:', e);
      return false;
    }
  },

  async deleteUser(userId: string): Promise<boolean> {
    try {
      await deleteDoc(doc(db, 'users', userId));
      return true;
    } catch (e) {
      console.error('deleteUser error:', e);
      return false;
    }
  },

  // --- EXPENSES ---
  subscribeExpenses(callback: (expenses: Expense[]) => void) {
    try {
      const q = query(collection(db, 'expenses'), orderBy('createdAt', 'desc'));
      return onSnapshot(q, (snapshot) => {
        const expenses = snapshot.docs.map((d) => d.data() as Expense);
        callback(expenses);
      }, (err) => {
        console.warn('Expenses listener fallback', err);
      });
    } catch (e) {
      console.warn('Firestore subscribeExpenses error:', e);
      return () => {};
    }
  },

  async addExpense(expense: Expense): Promise<boolean> {
    try {
      await setDoc(doc(db, 'expenses', expense.id), expense);
      return true;
    } catch (e) {
      console.error('addExpense error:', e);
      return false;
    }
  },

  async updateExpenseStatus(expenseId: string, status: ApprovalStatus): Promise<boolean> {
    try {
      await updateDoc(doc(db, 'expenses', expenseId), { status });
      return true;
    } catch (e) {
      console.error('updateExpenseStatus error:', e);
      return false;
    }
  },

  // --- SETTLEMENTS ---
  subscribeSettlements(callback: (settlements: Settlement[]) => void) {
    try {
      const q = query(collection(db, 'settlements'), orderBy('createdAt', 'desc'));
      return onSnapshot(q, (snapshot) => {
        const settlements = snapshot.docs.map((d) => d.data() as Settlement);
        callback(settlements);
      }, (err) => {
        console.warn('Settlements listener fallback', err);
      });
    } catch (e) {
      console.warn('Firestore subscribeSettlements error:', e);
      return () => {};
    }
  },

  async addSettlement(settlement: Settlement): Promise<boolean> {
    try {
      await setDoc(doc(db, 'settlements', settlement.id), settlement);
      return true;
    } catch (e) {
      console.error('addSettlement error:', e);
      return false;
    }
  },

  // --- GROUPS ---
  subscribeGroups(callback: (groups: Group[]) => void) {
    try {
      const q = query(collection(db, 'groups'), orderBy('createdAt', 'desc'));
      return onSnapshot(q, (snapshot) => {
        const groups = snapshot.docs.map((d) => d.data() as Group);
        callback(groups);
      }, (err) => {
        console.warn('Groups listener fallback', err);
      });
    } catch (e) {
      console.warn('Firestore subscribeGroups error:', e);
      return () => {};
    }
  },

  async saveGroup(group: Group): Promise<boolean> {
    try {
      await setDoc(doc(db, 'groups', group.id), group);
      return true;
    } catch (e) {
      console.error('saveGroup error:', e);
      return false;
    }
  },

  subscribeGroupExpenses(groupId: string, callback: (expenses: GroupExpenseItem[]) => void) {
    try {
      const q = query(collection(db, 'groups', groupId, 'expenses'), orderBy('date', 'desc'));
      return onSnapshot(q, (snapshot) => {
        const expenses = snapshot.docs.map((d) => d.data() as GroupExpenseItem);
        callback(expenses);
      }, (err) => {
        console.warn('Group expenses fallback', err);
      });
    } catch (e) {
      console.warn('subscribeGroupExpenses error:', e);
      return () => {};
    }
  },

  async addGroupExpense(groupId: string, expense: GroupExpenseItem): Promise<boolean> {
    try {
      await setDoc(doc(db, 'groups', groupId, 'expenses', expense.id), expense);
      return true;
    } catch (e) {
      console.error('addGroupExpense error:', e);
      return false;
    }
  },

  // --- CROSS SETTLEMENT OFFERS ---
  subscribeCrossOffers(callback: (offers: CrossSettlementOffer[]) => void) {
    try {
      const q = query(collection(db, 'cross_settlement_offers'), orderBy('createdAt', 'desc'));
      return onSnapshot(q, (snapshot) => {
        const offers = snapshot.docs.map((d) => d.data() as CrossSettlementOffer);
        callback(offers);
      }, (err) => {
        console.warn('Cross offers fallback', err);
      });
    } catch (e) {
      console.warn('Firestore subscribeCrossOffers error:', e);
      return () => {};
    }
  },

  async createCrossOffer(offer: CrossSettlementOffer): Promise<boolean> {
    try {
      await setDoc(doc(db, 'cross_settlement_offers', offer.id), offer);
      return true;
    } catch (e) {
      console.error('createCrossOffer error:', e);
      return false;
    }
  },

  async updateCrossApproval(offerId: string, userId: string, approved: boolean): Promise<boolean> {
    try {
      await updateDoc(doc(db, 'cross_settlement_offers', offerId), {
        [`approvals.${userId}`]: approved
      });
      return true;
    } catch (e) {
      console.error('updateCrossApproval error:', e);
      return false;
    }
  },

  // --- NUDGES ---
  subscribeNudges(userId: string, callback: (nudges: Nudge[]) => void) {
    try {
      const q = query(collection(db, 'nudges'), where('toUserId', '==', userId));
      return onSnapshot(q, (snapshot) => {
        const nudges = snapshot.docs.map((d) => d.data() as Nudge);
        callback(nudges);
      }, (err) => {
        console.warn('Nudges listener fallback', err);
      });
    } catch (e) {
      console.warn('Firestore subscribeNudges error:', e);
      return () => {};
    }
  },

  async sendNudge(nudge: Nudge): Promise<boolean> {
    try {
      await setDoc(doc(db, 'nudges', nudge.id), nudge);
      return true;
    } catch (e) {
      console.error('sendNudge error:', e);
      return false;
    }
  }
};
