export default function Header() {
  return (
    <header className="relative bg-black border-b border-white/10">
      {/* mesmo glow sutil do hero, só que achatado no header */}
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top,rgba(217,70,239,0.08),transparent_60%)]" />

      <div className="relative max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-amber-400 via-orange-500 to-fuchsia-600 flex items-center justify-center shadow-lg shadow-orange-500/20">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="w-5 h-5 text-black"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </div>
          <span className="text-sm font-medium text-zinc-300">
            Usuario Guilherme
          </span>
        </div>

        <button
          className="px-4 py-2 rounded-lg text-sm font-semibold text-zinc-300
                     bg-white/5 border border-white/10
                     hover:text-white hover:border-fuchsia-500/40 hover:bg-fuchsia-500/10
                     transition-colors duration-200"
        >
          Sair
        </button>
      </div>

      {/* traço em gradiente igual ao do hero, só que esticado na base do header */}
      <div className="h-px bg-gradient-to-r from-transparent via-fuchsia-500/40 to-transparent" />
    </header>
  );
}