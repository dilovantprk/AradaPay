import { Expense, Settlement } from '../types';

export const NetBalanceCalculator = {
  /**
   * Calculates net balance for all users based on expenses and settlements.
   * Returns a map of userId -> netBalance (positive = owed money, negative = owes money).
   */
  calculateNetBalances(
    expenses: Expense[],
    settlements: Settlement[]
  ): Map<string, number> {
    const balanceMap = new Map<string, number>();

    // 1. Process Expenses
    expenses.forEach((expense) => {
      if (expense.status === 'REJECTED') return;

      const payerId = expense.paidBy;
      const totalAmount = expense.amount;

      // Payer initially credited for the full amount
      const currentPayerBalance = balanceMap.get(payerId) || 0;
      balanceMap.set(payerId, currentPayerBalance + totalAmount);

      // Deduct each participant's split share
      if (expense.splits && expense.splits.length > 0) {
        expense.splits.forEach((split) => {
          const participantId = split.userId;
          const currentPartBalance = balanceMap.get(participantId) || 0;
          balanceMap.set(participantId, currentPartBalance - split.amountOwed);
        });
      } else {
        // Fallback: payer gets charged their own full amount if no splits
        const currentPartBalance = balanceMap.get(payerId) || 0;
        balanceMap.set(payerId, currentPartBalance - totalAmount);
      }
    });

    // 2. Process Settlements
    settlements.forEach((settlement) => {
      if (settlement.status === 'REJECTED') return;

      const payerId = settlement.payerId; // paid out -> balance increases (less debt)
      const receiverId = settlement.receiverId; // received -> balance decreases (less credit)
      const amount = settlement.amount;

      balanceMap.set(payerId, (balanceMap.get(payerId) || 0) + amount);
      balanceMap.set(receiverId, (balanceMap.get(receiverId) || 0) - amount);
    });

    return balanceMap;
  }
};
