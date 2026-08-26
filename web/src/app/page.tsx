import React from 'react';
import { Navbar } from '@/components/Navbar';
import { HeroSection } from '@/components/HeroSection';
import { PhoneMockup } from '@/components/PhoneMockup';
import { DfsVisualizer } from '@/components/DfsVisualizer';
import { MerkleVerifier } from '@/components/MerkleVerifier';
import { FeaturesSection } from '@/components/FeaturesSection';
import { ArchitectureSection } from '@/components/ArchitectureSection';
import { InstallationGuide } from '@/components/InstallationGuide';
import { AuthorSection } from '@/components/AuthorSection';
import { Footer } from '@/components/Footer';
import { getApkMetadata } from '@/lib/apk-info';

export default function HomePage() {
  const apkInfo = getApkMetadata();

  return (
    <main className="min-h-screen bg-[#F8F9FA] text-[#0F172A] flex flex-col antialiased selection:bg-[#00875A]/20 selection:text-[#00875A]">
      {/* Top Fixed Header */}
      <Navbar />

      {/* Hero Section */}
      <HeroSection apkInfo={apkInfo} />

      {/* Interactive In-App Phone Simulator */}
      <PhoneMockup />

      {/* Interactive DFS Debt Simplification Visualizer */}
      <DfsVisualizer />

      {/* Cryptographic Merkle Tree Receipts & Blockchain Verifier */}
      <MerkleVerifier />

      {/* Feature Grid & Technological Breakdown */}
      <FeaturesSection />

      {/* Hedvig Clean Architecture & MVI Deep-dive */}
      <ArchitectureSection />

      {/* Step-by-Step Android APK Installation & ADB Guide */}
      <InstallationGuide apkInfo={apkInfo} />

      {/* Author & Engineering Bio */}
      <AuthorSection />

      {/* Footer */}
      <Footer />
    </main>
  );
}
