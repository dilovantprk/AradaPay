import { initializeApp, getApps } from 'firebase/app';
import { getFirestore } from 'firebase/firestore';
import { getAuth } from 'firebase/auth';
import { getStorage } from 'firebase/storage';

const firebaseConfig = {
  apiKey: "AIzaSyDdOvn-gmjkih9ZlAMFBEEemLa_rQlMTec",
  authDomain: "ardabank-app-2026.firebaseapp.com",
  projectId: "ardabank-app-2026",
  storageBucket: "ardabank-app-2026.firebasestorage.app",
  messagingSenderId: "908604335031",
  appId: "1:908604335031:web:98e3f0c1729cd6dcfebe32"
};

const app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApps()[0];
export const db = getFirestore(app);
export const auth = getAuth(app);
export const storage = getStorage(app);
export default app;
