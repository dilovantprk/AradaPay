import { Currency } from '../types';

export interface SimplifiedTransaction {
  debtorId: string;   // Who needs to pay
  creditorId: string; // Who receives payment
  amount: number;
  currency: Currency;
}

/**
 * Minimizes total transaction count using Greedy Flow Algorithm.
 * 1:1 Direct Port from Android Kotlin DebtSimplifierEngine.kt
 */
export const DebtSimplifierEngine = {
  simplifyDebts(
    netBalances: Map<string, number>,
    currency: Currency = 'TRY'
  ): SimplifiedTransaction[] {
    // Debtor list (balance < -0.01) => sorted largest debt first
    const debtors: { userId: string; debt: number }[] = [];
    // Creditor list (balance > 0.01) => sorted largest credit first
    const creditors: { userId: string; credit: number }[] = [];

    netBalances.forEach((balance, userId) => {
      if (balance < -0.01) {
        debtors.push({ userId, debt: -balance });
      } else if (balance > 0.01) {
        creditors.push({ userId, credit: balance });
      }
    });

    debtors.sort((a, b) => b.debt - a.debt);
    creditors.sort((a, b) => b.credit - a.credit);

    const result: SimplifiedTransaction[] = [];

    let dIdx = 0;
    let cIdx = 0;

    while (dIdx < debtors.length && cIdx < creditors.length) {
      const debtor = debtors[dIdx];
      const creditor = creditors[cIdx];

      const minAmount = Math.min(debtor.debt, creditor.credit);

      result.push({
        debtorId: debtor.userId,
        creditorId: creditor.userId,
        amount: minAmount,
        currency
      });

      debtor.debt -= minAmount;
      creditor.credit -= minAmount;

      if (debtor.debt < 0.01) {
        dIdx++;
      }
      if (creditor.credit < 0.01) {
        cIdx++;
      }
    }

    return result;
  }
};
