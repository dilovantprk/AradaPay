import type { Metadata, Viewport } from 'next';
import './globals.css';

export const viewport: Viewport = {
  themeColor: '#00875A',
  width: 'device-width',
  initialScale: 1,
  maximumScale: 5,
};

export const metadata: Metadata = {
  title: 'AradaPay (ArdaBank) — Yeni Nesil FinTech & Grup Harcama Platformu',
  description: 'Dairesel borçları Graph-DFS algoritmasıyla çözen, Merkle Tree onaylı kriptografik dekont üreten ve temassız QR ödeme sunan Android FinTech platformu.',
  keywords: ['AradaPay', 'ArdaBank', 'Android FinTech', 'Grup Harcama', 'DFS Algoritması', 'Merkle Tree', 'QR Ödeme', 'Jetpack Compose', 'Material 3'],
  authors: [{ name: 'Mehmet Dilovan Toprak', url: 'https://dilovantprk.github.io' }],
  openGraph: {
    title: 'AradaPay — Grup Harcamalarında Borç Döngülerine Son',
    description: 'Graph-DFS algoritması ile borç sadeleştirme, Merkle Tree onaylı dekontlar ve temassız QR ödeme.',
    type: 'website',
    locale: 'tr_TR',
  },
  icons: {
    icon: '/favicon.ico',
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="tr" className="scroll-smooth">
      <body className="bg-[#F8F9FA] text-[#0F172A] antialiased selection:bg-[#00875A]/20 selection:text-[#00875A]">
        {children}
      </body>
    </html>
  );
}
