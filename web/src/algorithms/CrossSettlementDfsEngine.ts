import { CrossSettlementOffer, CrossSettlementParticipant, CrossSettlementStep, User } from '../types';

/**
 * Scans for directed debt cycles using DFS algorithm (e.g. A -> B -> C -> A).
 * 1:1 Direct Port from Android Kotlin CrossSettlementDfsEngine.kt
 */
export const CrossSettlementDfsEngine = {
  detectCrossSettlementCycles(
    usersMap: Map<string, User>,
    pairwiseMatrix: Map<string, number> // key: `${from}_${to}` -> amount
  ): CrossSettlementOffer[] {
    const userIds = Array.from(usersMap.keys());
    const graph = new Map<string, Map<string, number>>();

    // Build directed adjacency list: graph[u][v] = amount (u owes v)
    pairwiseMatrix.forEach((amount, key) => {
      if (amount > 0.01) {
        const [from, to] = key.split('_');
        if (!graph.has(from)) {
          graph.set(from, new Map());
        }
        graph.get(from)!.set(to, amount);
      }
    });

    const visited = new Set<string>();
    const recursionStack: string[] = [];
    const detectedCycles: string[][] = [];

    function dfs(current: string) {
      visited.add(current);
      recursionStack.push(current);

      const neighbors = graph.get(current) || new Map<string, number>();
      for (const [nextUser] of neighbors.entries()) {
        if (recursionStack.includes(nextUser)) {
          // Cycle detected!
          const cycleStartIndex = recursionStack.indexOf(nextUser);
          const cycle = recursionStack.slice(cycleStartIndex);
          if (cycle.length >= 3 && !CrossSettlementDfsEngine.isDuplicateCycle(detectedCycles, cycle)) {
            detectedCycles.push(cycle);
          }
        } else if (!visited.has(nextUser)) {
          dfs(nextUser);
        }
      }

      recursionStack.pop();
    }

    for (const u of userIds) {
      if (!visited.has(u)) {
        dfs(u);
      }
    }

    // Convert raw cycle node lists into CrossSettlementOffer domain models
    const offers: CrossSettlementOffer[] = [];

    for (const cycle of detectedCycles) {
      const steps: CrossSettlementStep[] = [];
      let minCapacity = Number.MAX_VALUE;

      for (let i = 0; i < cycle.length; i++) {
        const fromId = cycle[i];
        const toId = cycle[(i + 1) % cycle.length];
        const owedAmount = graph.get(fromId)?.get(toId) || 0;

        if (owedAmount < minCapacity) {
          minCapacity = owedAmount;
        }

        const fromUser = usersMap.get(fromId);
        const toUser = usersMap.get(toId);

        steps.push({
          fromUserId: fromId,
          fromUserName: fromUser?.fullName || fromUser?.username || fromId,
          toUserId: toId,
          toUserName: toUser?.fullName || toUser?.username || toId,
          amount: 0 // Set after bottleneck minCapacity is finalized
        });
      }

      if (minCapacity > 0.01) {
        const finalizedSteps = steps.map((s) => ({ ...s, amount: minCapacity }));
        const participants: CrossSettlementParticipant[] = cycle
          .map((uid) => usersMap.get(uid))
          .filter((u): u is User => !!u)
          .map((u) => ({
            id: u.id,
            name: u.fullName,
            avatar: u.avatarUrl || '👤',
            username: u.username
          }));

        const initialApprovals: Record<string, boolean> = {};
        cycle.forEach((uid) => {
          initialApprovals[uid] = false;
        });

        offers.push({
          id: `offer_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`,
          cycleAmount: minCapacity,
          participants,
          steps: finalizedSteps,
          approvals: initialApprovals,
          status: 'PENDING',
          createdAt: Date.now().toString()
        });
      }
    }

    return offers;
  },

  isDuplicateCycle(existingCycles: string[][], newCycle: string[]): boolean {
    const newSet = new Set(newCycle);
    return existingCycles.some((existing) => {
      if (existing.length !== newCycle.length) return false;
      const existingSet = new Set(existing);
      return Array.from(newSet).every((item) => existingSet.has(item));
    });
  }
};
