/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Authentic Flat Fintech Design Tokens (1:1 Android Color.kt)
        primaryEmerald: "#00875A",          // Bank Primary Forest Green
        primaryEmeraldDark: "#006644",
        primaryEmeraldContainer: "#E8F5E9", // Soft Emerald 50 Tint
        onPrimaryContainer: "#065F46",      // Deep Forest Green

        accentRose: "#BE123C",              // Subdued Banker Rose/Crimson (Red 700)
        accentRoseContainer: "#FFF1F2",     // Soft Rose 50 Tint
        onAccentRoseContainer: "#9F1239",

        shinyCyan: "#334155",
        shinyCyanContainer: "#E5E5EA",
        accentAmber: "#475569",

        surfaceWhite: "#FFFFFF",
        surfaceBackground: "#FFFFFF",
        surfaceBorder: "#F1F5F9",           // 1px divider
        surfaceContainerLow: "#F1F5F9",     // Pill / avatar containers
        surfaceContainerHigh: "#E2E8F0",

        textPrimary: "#0F172A",             // Obsidian Slate 900
        textSecondary: "#64748B",           // Slate 500
        textTertiary: "#94A3B8",            // Slate 400
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', '"SF Pro Display"', '"SF Pro Text"', '"Roboto"', 'sans-serif'],
      },
      boxShadow: {
        'subtle': '0 1px 3px 0 rgba(0, 0, 0, 0.05), 0 1px 2px 0 rgba(0, 0, 0, 0.03)',
        'card': '0 4px 12px 0 rgba(0, 0, 0, 0.03)',
        'modal': '0 20px 40px -15px rgba(0, 0, 0, 0.15)',
      }
    },
  },
  plugins: [],
}
