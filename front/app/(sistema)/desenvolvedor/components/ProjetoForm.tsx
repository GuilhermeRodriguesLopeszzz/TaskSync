import Link from "next/link";

export default function DesenvolvedorForm() {
    return (
        <form action="">

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">
                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label className="text-sm font-medium text-zinc-300">Nome completo</label>
                    <input name="nome" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">CPF</label>
                    <input name="CPF" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">E-mail</label>
                    <input name="email" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>
                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label className="text-sm font-medium text-zinc-300">Senha</label>
                    <input name="senha" className="w-full sm:w-1/2 px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>

                <div className="sm:col-span-2 flex items-center justify-end gap-3 mt-4 pt-5 border-t border-white/10">
                    <Link href="/desenvolvedor" className="px-4 py-2 rounded-lg text-zinc-400 hover:text-white text-sm font-medium transition-colors">Cancelar</Link>
                    <button type="submit" className="px-5 py-2 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm transition-colors">Salvar</button>
                </div>

            </div>
        </form>
    );
}