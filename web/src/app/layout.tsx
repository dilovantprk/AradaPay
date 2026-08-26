import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'AradaPay (ArdaBank) — Yeni Nesil Finans ve Mahsuplaşma',
  description: 'Döngüsel borçları Graph-DFS algoritmasıyla çözen, Merkle Tree onaylı kriptografik dekont üreten ve temassız FAST/QR ödeme sunan FinTech platformu.',
  icons: {
    icon: '/favicon.svg'
  }
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="tr" className="scroll-smooth">
      <body className="bg-[#F8FAFC] text-[#0F172A] antialiased selection:bg-[#E8F5E9] selection:text-[#00875A]">
        {children}
      </body>
    </html>
  );
}
