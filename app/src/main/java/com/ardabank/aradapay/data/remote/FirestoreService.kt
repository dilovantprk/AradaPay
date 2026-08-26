package com.ardabank.aradapay.data.remote

import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.CrossSettlementOffer
import com.ardabank.aradapay.domain.model.Expense
import com.ardabank.aradapay.domain.model.Nudge
import com.ardabank.aradapay.domain.model.Settlement
import com.ardabank.aradapay.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val db: FirebaseFirestore
) {
    // --- USER MANAGEMENT ---
    fun getUserFlow(userId: String): Flow<User?> = callbackFlow {
        val docRef = db.collection("users").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(User::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getAllUsersFlow(): Flow<List<User>> = callbackFlow {
        val listener = db.collection("users").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val users = snapshot.toObjects(User::class.java)
                trySend(users)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    fun saveUser(user: User, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(user.id)
            .set(user)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun findUserByTag(tag: String, onResult: (User?) -> Unit) {
        db.collection("users")
            .whereEqualTo("tag", tag)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val user = snapshot.documents.firstOrNull()?.toObject(User::class.java)
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun syncFriendWithFirestore(currentUserId: String, friendUser: User, onComplete: (Boolean) -> Unit) {
        // Save friend record under user friends collection and save the friend user record in global directory
        db.collection("users").document(friendUser.id)
            .set(friendUser)
            .addOnSuccessListener {
                db.collection("users").document(currentUserId)
                    .collection("friends").document(friendUser.id)
                    .set(friendUser)
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteUser(userId: String, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // --- EXPENSES ---
    fun getExpensesFlow(): Flow<List<Expense>> = callbackFlow {
        val listener = db.collection("expenses")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Expense::class.java))
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun addExpense(expense: Expense, onComplete: (Boolean) -> Unit) {
        db.collection("expenses").document(expense.id)
            .set(expense)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateExpenseStatus(expenseId: String, status: ApprovalStatus, onComplete: (Boolean) -> Unit) {
        db.collection("expenses").document(expenseId)
            .update("status", status)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // --- SETTLEMENTS ---
    fun getSettlementsFlow(): Flow<List<Settlement>> = callbackFlow {
        val listener = db.collection("settlements")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Settlement::class.java))
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun addSettlement(settlement: Settlement, onComplete: (Boolean) -> Unit) {
        db.collection("settlements").document(settlement.id)
            .set(settlement)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateSettlementStatus(settlementId: String, status: ApprovalStatus, onComplete: (Boolean) -> Unit) {
        db.collection("settlements").document(settlementId)
            .update("status", status)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // --- CROSS SETTLEMENT OFFERS ---
    fun getCrossSettlementOffersFlow(): Flow<List<CrossSettlementOffer>> = callbackFlow {
        val listener = db.collection("cross_settlement_offers")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(CrossSettlementOffer::class.java))
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun createCrossSettlementOffer(offer: CrossSettlementOffer, onComplete: (Boolean) -> Unit) {
        db.collection("cross_settlement_offers").document(offer.id)
            .set(offer)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateCrossSettlementApproval(offerId: String, userId: String, approved: Boolean, onComplete: (Boolean) -> Unit) {
        db.collection("cross_settlement_offers").document(offerId)
            .update("approvals.$userId", approved)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // --- NUDGES ---
    fun getNudgesFlow(userId: String): Flow<List<Nudge>> = callbackFlow {
        val listener = db.collection("nudges")
            .whereEqualTo("toUserId", userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Nudge::class.java))
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun sendNudge(nudge: Nudge, onComplete: (Boolean) -> Unit) {
        db.collection("nudges").document(nudge.id)
            .set(nudge)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // --- GROUPS ---
    fun getGroupsFlow(): Flow<List<com.ardabank.aradapay.domain.model.Group>> = callbackFlow {
        val listener = db.collection("groups")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(com.ardabank.aradapay.domain.model.Group::class.java))
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun saveGroup(group: com.ardabank.aradapay.domain.model.Group, onComplete: (Boolean) -> Unit) {
        db.collection("groups").document(group.id)
            .set(group)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getGroupExpensesFlow(groupId: String): Flow<List<com.ardabank.aradapay.domain.model.GroupExpenseItem>> = callbackFlow {
        val listener = db.collection("groups").document(groupId)
            .collection("expenses")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.toObjects(com.ardabank.aradapay.domain.model.GroupExpenseItem::class.java))
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }

    fun addGroupExpense(groupId: String, expense: com.ardabank.aradapay.domain.model.GroupExpenseItem, onComplete: (Boolean) -> Unit) {
        db.collection("groups").document(groupId)
            .collection("expenses").document(expense.id)
            .set(expense)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
