import {
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signInWithPopup,
  sendPasswordResetEmail,
  signOut as firebaseSignOut,
  onAuthStateChanged,
  User as FirebaseUser
} from 'firebase/auth';
import { auth, googleProvider } from '../firebase/config';
import { FirestoreService } from './firestoreService';
import { User } from '../types';

export const AuthService = {
  /**
   * Listen to Firebase Auth state changes
   */
  onAuthStateChange(callback: (user: FirebaseUser | null) => void) {
    return onAuthStateChanged(auth, callback);
  },

  /**
   * Get current Firebase Auth user
   */
  getCurrentFirebaseUser(): FirebaseUser | null {
    return auth.currentUser;
  },

  /**
   * Sign In with Email and Password
   */
  async signInWithEmail(email: string, pass: string): Promise<User> {
    const cred = await signInWithEmailAndPassword(auth, email.trim(), pass);
    const fbUser = cred.user;

    // Check if Firestore user document exists
    const existing = await FirestoreService.getUser(fbUser.uid);
    if (existing) {
      return existing;
    }

    // If not found in Firestore, create default profile
    const firstName = fbUser.displayName?.split(' ')[0] || email.split('@')[0];
    const tagSuffix = Math.floor(1000 + Math.random() * 9000);
    const newUser: User = {
      id: fbUser.uid,
      email: fbUser.email || email.trim(),
      username: firstName.toLowerCase(),
      fullName: fbUser.displayName || firstName,
      avatarUrl: fbUser.photoURL || '',
      phone: fbUser.phoneNumber || null,
      iban: 'TR64 0006 2000 0000 5566 7788 99',
      tag: `@${firstName.toLowerCase()}#${tagSuffix}`,
      defaultCurrency: 'TRY',
      createdAt: new Date().toISOString()
    };

    await FirestoreService.saveUser(newUser);
    return newUser;
  },

  /**
   * Sign Up with Email and Password + Full Profile Creation
   */
  async signUpWithEmail(
    email: string,
    pass: string,
    fullName: string,
    phone?: string,
    iban?: string,
    pin?: string
  ): Promise<User> {
    const cred = await createUserWithEmailAndPassword(auth, email.trim(), pass);
    const fbUser = cred.user;

    const trimmedName = fullName.trim();
    const firstName = trimmedName.split(' ')[0] || 'Kullanıcı';
    const tagSuffix = Math.floor(1000 + Math.random() * 9000);
    const userTag = `@${firstName.toLowerCase()}#${tagSuffix}`;

    const newUser: User = {
      id: fbUser.uid,
      email: email.trim(),
      username: firstName.toLowerCase(),
      fullName: trimmedName,
      avatarUrl: '',
      phone: phone || null,
      iban: iban || 'TR64 0006 2000 0000 5566 7788 99',
      tag: userTag,
      defaultCurrency: 'TRY',
      pin: pin || '',
      createdAt: new Date().toISOString()
    };

    await FirestoreService.saveUser(newUser);
    return newUser;
  },

  /**
   * Sign In with Google Popup
   */
  async signInWithGoogle(): Promise<User> {
    const cred = await signInWithPopup(auth, googleProvider);
    const fbUser = cred.user;

    const existing = await FirestoreService.getUser(fbUser.uid);
    if (existing) {
      return existing;
    }

    const name = fbUser.displayName || 'Google Kullanıcısı';
    const firstName = name.split(' ')[0] || name;
    const tagSuffix = Math.floor(1000 + Math.random() * 9000);

    const newUser: User = {
      id: fbUser.uid,
      email: fbUser.email || '',
      username: firstName.toLowerCase(),
      fullName: name,
      avatarUrl: fbUser.photoURL || '',
      phone: fbUser.phoneNumber || null,
      iban: 'TR64 0006 2000 0000 5566 7788 99',
      tag: `@${firstName.toLowerCase()}#${tagSuffix}`,
      defaultCurrency: 'TRY',
      createdAt: new Date().toISOString()
    };

    await FirestoreService.saveUser(newUser);
    return newUser;
  },

  /**
   * Send Password Reset Email
   */
  async sendPasswordReset(email: string): Promise<void> {
    await sendPasswordResetEmail(auth, email.trim());
  },

  /**
   * Sign Out
   */
  async signOut(): Promise<void> {
    await firebaseSignOut(auth);
  }
};
