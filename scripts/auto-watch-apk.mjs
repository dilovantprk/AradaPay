import fs from 'fs';
import path from 'path';
import crypto from 'crypto';
import { execSync, spawn } from 'child_process';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const rootDir = path.resolve(__dirname, '..');
const webDir = path.join(rootDir, 'web');
const webPublicApk = path.join(webDir, 'public', 'AradaPay.apk');
const rootApk = path.join(rootDir, 'AradaPay.apk');

console.log('👀 AradaPay Otomatik APK İzleyici Başlatıldı...');
console.log(`📁 İzlenen dizin: ${rootDir}`);

let isDeploying = false;
let lastHash = '';

function getHash(filePath) {
  if (!fs.existsSync(filePath)) return '';
  const buffer = fs.readFileSync(filePath);
  return crypto.createHash('sha256').update(buffer).digest('hex');
}

function handleApkChange(sourcePath) {
  if (isDeploying) return;
  
  try {
    const currentHash = getHash(sourcePath);
    if (!currentHash || currentHash === lastHash) return;

    isDeploying = true;
    lastHash = currentHash;

    console.log(`\n🔔 Yeni APK Değişikliği Tespit Edildi: ${path.basename(sourcePath)}`);
    console.log(`🔒 Yeni SHA-256: ${currentHash}`);

    // Sync to web/public and root
    fs.mkdirSync(path.dirname(webPublicApk), { recursive: true });
    fs.copyFileSync(sourcePath, webPublicApk);
    if (sourcePath !== rootApk) {
      fs.copyFileSync(sourcePath, rootApk);
    }

    const stats = fs.statSync(webPublicApk);
    const sizeMb = (stats.size / (1024 * 1024)).toFixed(1);
    console.log(`📦 APK Senkronize Edildi (${sizeMb} MB)`);

    console.log('🚀 Vercel Production Dağıtımı Başlatılıyor...');
    execSync('npm run build', { cwd: webDir, stdio: 'inherit' });
    execSync('npx vercel --prod --yes', { cwd: webDir, stdio: 'inherit' });
    try {
      execSync('npx vercel alias set $(npx vercel ls web --scope toprakk025-8617s-projects | grep production | head -n 1 | awk \'{print $2}\') aradapay.vercel.app', { cwd: webDir, stdio: 'ignore' });
    } catch {}

    console.log('✅ Tebrikler! Yeni APK ve Web Sitesi Otomatik Olarak Canlıya Alındı:');
    console.log('👉 https://aradapay.vercel.app');
    console.log('📲 https://aradapay.vercel.app/AradaPay.apk\n');
  } catch (err) {
    console.error('❌ Dağıtım sırasında hata oluştu:', err.message);
  } finally {
    setTimeout(() => {
      isDeploying = false;
    }, 5000);
  }
}

// Initial check
if (fs.existsSync(rootApk)) {
  lastHash = getHash(rootApk);
}

// Watch root APK
if (fs.existsSync(rootApk)) {
  fs.watch(rootApk, (eventType) => {
    if (eventType === 'change' || eventType === 'rename') {
      setTimeout(() => handleApkChange(rootApk), 1000);
    }
  });
}

// Watch app/build/outputs/apk if exists
const apkOutputDir = path.join(rootDir, 'app', 'build', 'outputs', 'apk');
if (fs.existsSync(apkOutputDir)) {
  fs.watch(apkOutputDir, { recursive: true }, (eventType, filename) => {
    if (filename && filename.endsWith('.apk')) {
      const fullPath = path.join(apkOutputDir, filename);
      if (fs.existsSync(fullPath)) {
        setTimeout(() => handleApkChange(fullPath), 1000);
      }
    }
  });
}

console.log('⚡ Android Studio derlemeleri veya APK değişiklikleri otomatik canlıya alınacaktır.');
