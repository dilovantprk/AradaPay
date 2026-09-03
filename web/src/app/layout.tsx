import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'AradaPay — Masadaki Ortak Hesabı Kolayca Bölüşün',
  description: 'Arkadaş arasındaki ortak hesabı tabu olmaktan çıkaran, masadaki payları tek tıkla bölüştüren, döngüleri akıllı dengeleyen ve anında FAST ile ödeştiren sosyal finans platformu.',
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
