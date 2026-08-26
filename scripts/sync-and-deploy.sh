#!/usr/bin/env bash
set -e

# ==============================================================================
# AradaPay APK Sync & Vercel Auto-Deploy Script
# Bu script en son derlenen Android APK'sını web landing sitesine senkronize eder
# ve anında Vercel üzerinde canlıya alır.
# ==============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
WEB_DIR="$ROOT_DIR/web"

echo "🔍 AradaPay en güncel APK taranıyor..."

LATEST_APK=""

# 1. Check release build output
if [ -f "$ROOT_DIR/app/build/outputs/apk/release/app-release.apk" ]; then
    LATEST_APK="$ROOT_DIR/app/build/outputs/apk/release/app-release.apk"
# 2. Check debug build output
elif [ -f "$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk" ]; then
    LATEST_APK="$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk"
# 3. Check root APK
elif [ -f "$ROOT_DIR/AradaPay.apk" ]; then
    LATEST_APK="$ROOT_DIR/AradaPay.apk"
fi

if [ -z "$LATEST_APK" ]; then
    echo "❌ HATA: Hiçbir APK dosyası bulunamadı."
    exit 1
fi

echo "📦 Bulunan En Güncel APK: $LATEST_APK"
mkdir -p "$WEB_DIR/public"

# Sync to web/public and root
cp "$LATEST_APK" "$WEB_DIR/public/AradaPay.apk"
cp "$LATEST_APK" "$ROOT_DIR/AradaPay.apk"

APK_SIZE=$(ls -lh "$WEB_DIR/public/AradaPay.apk" | awk '{print $5}')
SHA256_HASH=$(shasum -a 256 "$WEB_DIR/public/AradaPay.apk" | awk '{print $1}')

echo "✅ APK Başarıyla Güncellendi!"
echo "   📊 Boyut: $APK_SIZE"
echo "   🔒 SHA-256: $SHA256_HASH"

# Build and deploy web to Vercel
echo "🚀 Vercel Production'a Deploy Ediliyor..."
cd "$WEB_DIR"
npm run build
npx vercel --prod --yes
npx vercel alias set https://web-mjh4i6493-toprakk025-8617s-projects.vercel.app aradapay.vercel.app 2>/dev/null || true

echo "🎉 Tebrikler! En güncel APK ve web sitesi yayında:"
echo "   👉 https://aradapay.vercel.app"
echo "   📲 https://aradapay.vercel.app/AradaPay.apk"
