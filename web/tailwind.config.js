/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Apple HIG / Fintech Emerald Accents
        primaryEmerald: "#00875A",          // Refined Forest Green
        primaryEmeraldDark: "#006644",
        primaryEmeraldContainer: "#E8F5E9",
        onPrimaryContainer: "#065F46",

        accentRose: "#D32F2F",              // iOS System Red
        accentRoseContainer: "#FFEBEE",
        onAccentRoseContainer: "#B71C1C",

        // Apple iOS / macOS Neutral Palette
        appleBg: "#F2F2F7",                 // iOS Grouped Background
        appleCard: "#FFFFFF",               // Secondary System Grouped Background
        appleBorder: "rgba(0, 0, 0, 0.06)",  // Hairline 1px separator
        appleSeparator: "rgba(60, 60, 67, 0.12)",

        textPrimary: "#1C1C1E",             // Apple Label Primary
        textSecondary: "#8E8E93",           // Apple Label Secondary
        textTertiary: "#C7C7CC",            // Apple Label Tertiary
        textQuaternary: "#E5E5EA",
      },
      fontFamily: {
        sans: [
          "-apple-system",
          "BlinkMacSystemFont",
          '"SF Pro Display"',
          '"SF Pro Text"',
          '"SF Pro"',
          "Inter",
          "-apple-system-subheadline",
          "system-ui",
          "sans-serif"
        ],
        mono: ['"SF Mono"', "Menlo", "Monaco", "Consolas", "monospace"],
      },
      boxShadow: {
        'apple-sm': '0 1px 2px 0 rgba(0, 0, 0, 0.04)',
        'apple-card': '0 2px 8px 0 rgba(0, 0, 0, 0.04), 0 1px 2px 0 rgba(0, 0, 0, 0.02)',
        'apple-floating': '0 8px 24px -4px rgba(0, 0, 0, 0.08), 0 2px 6px -1px rgba(0, 0, 0, 0.04)',
        'apple-modal': '0 24px 60px -12px rgba(0, 0, 0, 0.2), 0 12px 24px -8px rgba(0, 0, 0, 0.08)',
      },
      borderRadius: {
        'apple-sm': '10px',
        'apple': '14px',
        'apple-lg': '20px',
        'apple-xl': '28px',
        'apple-2xl': '36px',
      }
    },
  },
  plugins: [],
}
