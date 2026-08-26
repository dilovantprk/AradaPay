import fs from 'fs';
import path from 'path';
import crypto from 'crypto';

export interface ApkMetadata {
  filename: string;
  version: string;
  sizeFormatted: string;
  sizeBytes: number;
  sha256: string;
  updatedAtFormatted: string;
}

export function getApkMetadata(): ApkMetadata {
  const apkPath = path.join(process.cwd(), 'public', 'AradaPay.apk');
  
  let sizeBytes = 44801160;
  let sha256 = '109c93a9e32837be4517b0cdccccb0fd5ee0bff46441355509da02f31c66eb7c';
  let updatedAt = new Date();

  if (fs.existsSync(apkPath)) {
    try {
      const stats = fs.statSync(apkPath);
      sizeBytes = stats.size;
      updatedAt = stats.mtime;

      const fileBuffer = fs.readFileSync(apkPath);
      sha256 = crypto.createHash('sha256').update(fileBuffer).digest('hex');
    } catch (err) {
      console.warn('Could not read dynamic APK stats, using fallback', err);
    }
  }

  // Read version from app/build.gradle.kts if accessible
  let version = '1.0.0';
  const gradlePath = path.join(process.cwd(), '..', 'app', 'build.gradle.kts');
  if (fs.existsSync(gradlePath)) {
    try {
      const gradleContent = fs.readFileSync(gradlePath, 'utf8');
      const match = gradleContent.match(/versionName\s*=\s*"([^"]+)"/);
      if (match && match[1]) {
        version = match[1];
      }
    } catch {
      // fallback
    }
  }

  const sizeMb = (sizeBytes / (1024 * 1024)).toFixed(1);

  return {
    filename: 'AradaPay.apk',
    version: `v${version}`,
    sizeFormatted: `${sizeMb} MB`,
    sizeBytes,
    sha256,
    updatedAtFormatted: updatedAt.toLocaleDateString('tr-TR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    }),
  };
}
