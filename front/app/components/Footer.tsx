export default function Footer() {
    const anoAtual = new Date().getFullYear();
  return (
    <footer className="border-t border-white/5 bg-[#0a0a0f]">
        <div className="max-w-7xl mx-auto px-6 lg:px-10 py-6">
            <div className="flex justify-center">
                <p className="text-sm text-zinc-500 text-center">&copy; {anoAtual}
                    <span className="text-white font-semibold mx-1">TaskSync</span>
                    Todos os direitos reservados.
                </p>
            </div>
        </div>
    </footer>
  );
}