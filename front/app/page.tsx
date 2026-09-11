import Link from "next/link";


export default function Home() {
  return (

    <div className="bg-[#0a0a0f] text-zinc-100 antialiased selection:bg-amber-400 selection:text-black min-h-screen">
            {/* Header / Navbar */}
            <header className="fixed top-0 left-0 right-0 z-50 bg-[#0a0a0f]/70 backdrop-blur-xl border-b border-white/5">
                <div className="max-w-7xl mx-auto px-6 lg:px-10 h-20 flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        {/* Logo: Gato minimalista em SVG */}
                        <div className="w-10 h-10 rounded-2xl bg-gradient-to-br from-amber-400 via-orange-500 to-fuchsia-600 flex items-center justify-center shadow-lg shadow-orange-500/30">
                            <svg className="w-6 h-6 text-black" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M12 5c-3 0-5 2-5 5v1a5 5 0 0 0 10 0v-1c0-3-2-5-5-5z"></path>
                                <path d="M9 4L7 2"></path>
                                <path d="M15 4l2-2"></path>
                                <path d="M9 14v1a3 3 0 0 0 6 0v-1"></path>
                                <circle cx="10" cy="9" r="1" fill="currentColor"></circle>
                                <circle cx="14" cy="9" r="1" fill="currentColor"></circle>
                            </svg>
                        </div>
                        <span className="text-xl font-bold tracking-tight text-white">Task<span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 to-fuchsia-500">Sync</span></span>
                    </div>
                    
                    <nav className="hidden md:flex items-center gap-9 text-sm font-medium text-zinc-400">
                        <a href="#recursos" className="hover:text-white transition-colors duration-200">Recursos</a>
                        <a href="#sobre" className="hover:text-white transition-colors duration-200">Nossa História</a>
                        <a href="#beneficios" className="hover:text-white transition-colors duration-200">Vantagens</a>
                    </nav>

                    <div className="flex items-center gap-4">
                        
                        <Link href="/login" className="px-5 py-2.5 rounded-full bg-white text-black hover:bg-amber-300 font-semibold text-sm transition-all duration-200 shadow-lg shadow-black/30 hover:-translate-y-0.5">
                            Entrar </Link>
                    </div>
                </div>
            </header>

            {/* Hero Section */}
            <section className="relative pt-40 pb-24 md:pt-52 md:pb-36 overflow-hidden">
                <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[900px] h-[500px] bg-gradient-to-r from-amber-500/20 via-orange-500/10 to-fuchsia-600/20 blur-[160px] rounded-full pointer-events-none"></div>
                <div className="absolute inset-0 bg-[linear-gradient(to_right,#ffffff08_1px,transparent_1px),linear-gradient(to_bottom,#ffffff08_1px,transparent_1px)] bg-[size:64px_64px] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_60%,transparent_100%)] pointer-events-none"></div>

                <div className="max-w-7xl mx-auto px-6 relative z-10 text-center">
                    <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-white/5 border border-white/10 text-zinc-300 text-xs font-medium mb-8 backdrop-blur-sm">
                        <span className="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse"></span>
                        Gestão ágil para equipes modernas
                    </div>
                    
                    <h1 className="text-4xl md:text-6xl lg:text-7xl font-extrabold tracking-tight text-white max-w-4xl mx-auto leading-[1.05]">
                        Sua equipe de desenvolvimento sincronizada e no <span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-300 via-orange-400 to-fuchsia-500">controle</span>.
                    </h1>
                    
                    <p className="mt-7 text-lg md:text-xl text-zinc-400 max-w-2xl mx-auto font-normal leading-relaxed">
                        Gerentes alocam tarefas com precisão cirúrgica e desenvolvedores sabem exatamente o que entregar. Menos ruído, mais código em produção.
                    </p>

                    <div className="mt-11 flex flex-col sm:flex-row items-center justify-center gap-4">
                        <a href="#comecar" className="w-full sm:w-auto px-8 py-4 rounded-full bg-gradient-to-r from-amber-400 to-orange-500 hover:from-amber-300 hover:to-orange-400 text-black font-bold text-base transition-all duration-200 shadow-xl shadow-orange-500/25 hover:shadow-orange-400/40 hover:-translate-y-0.5 text-center">
                            Começar Gratuitamente
                        </a>
                        <a href="#sobre" className="w-full sm:w-auto px-8 py-4 rounded-full bg-white/5 hover:bg-white/10 text-zinc-200 border border-white/10 font-semibold text-base transition-all duration-200 backdrop-blur-sm text-center">
                            Conhecer a História
                        </a>
                    </div>

                    {/* Preview Card / Mockup Simulado */}
                    <div className="mt-20 relative max-w-5xl mx-auto rounded-3xl border border-white/10 bg-white/[0.03] p-2 shadow-2xl shadow-black/60 backdrop-blur-xl">
                        <div className="rounded-[1.25rem] bg-[#0a0a0f] p-6 md:p-8 text-left border border-white/5">
                            <div className="flex items-center justify-between pb-4 border-b border-white/5 mb-5">
                                <div className="flex items-center gap-2">
                                    <div className="w-3 h-3 rounded-full bg-red-500/70"></div>
                                    <div className="w-3 h-3 rounded-full bg-amber-400/70"></div>
                                    <div className="w-3 h-3 rounded-full bg-emerald-400/70"></div>
                                </div>
                                <span className="text-xs text-zinc-500 font-mono">tasksync-workspace.app</span>
                            </div>
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
                                <div className="bg-white/[0.03] p-4 rounded-xl border border-white/5 hover:border-white/10 transition-colors">
                                    <span className="text-xs font-semibold text-zinc-400 uppercase tracking-wider">Backlog</span>
                                    <div className="mt-3 p-3 bg-white/[0.04] rounded-lg border border-white/5 text-sm font-medium text-zinc-200">Refatorar autenticação OAuth</div>
                                </div>
                                <div className="bg-white/[0.03] p-4 rounded-xl border border-amber-400/20 hover:border-amber-400/40 transition-colors">
                                    <span className="text-xs font-semibold text-amber-400 uppercase tracking-wider">Em Progresso (Dev)</span>
                                    <div className="mt-3 p-3 bg-white/[0.04] rounded-lg border border-white/5 text-sm font-medium text-zinc-200">Criar endpoints de alocação</div>
                                </div>
                                <div className="bg-white/[0.03] p-4 rounded-xl border border-fuchsia-400/20 hover:border-fuchsia-400/40 transition-colors">
                                    <span className="text-xs font-semibold text-fuchsia-400 uppercase tracking-wider">Concluído</span>
                                    <div className="mt-3 p-3 bg-white/[0.04] rounded-lg border border-white/5 text-sm font-medium text-zinc-200">Setup inicial Next.js</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Nossa História / Sobre */}
            <section id="sobre" className="py-28 bg-gradient-to-b from-white/[0.02] to-transparent border-y border-white/5">
                <div className="max-w-5xl mx-auto px-6 lg:px-10">
                    <div className="text-center max-w-3xl mx-auto">
                        <span className="text-xs font-semibold tracking-widest text-amber-400 uppercase">Quem somos</span>
                        <h2 className="mt-3 text-3xl md:text-4xl font-bold tracking-tight text-white">Nossa História</h2>
                    </div>
                    
                    <div className="mt-14 text-zinc-400 text-lg leading-relaxed space-y-7 max-w-3xl mx-auto">
                        <p>
                            O <strong className="text-white font-semibold">TaskSync</strong> nasceu da dor real de quem vive o dia a dia de engenharia de software. Vimos gerentes de projetos perdidos em planilhas desconexas e desenvolvedores recebendo demandas sem contexto, gerando atrito e atrasos em entregas críticas.
                        </p>
                        <p>
                            Criamos uma plataforma focada em resolver exatamente isso: capacitar gestores a alocarem tarefas de forma cirúrgica nos desenvolvedores certos, unificada a uma <strong className="text-white font-semibold">gestão de acessos rigorosa e descomplicada</strong>. Cada um vê o que precisa ver, mantendo o fluxo ágil, transparente e ágil como um gato predador focado na presa.
                        </p>
                    </div>
                </div>
            </section>

            {/* Recursos / Benefícios */}
            <section id="recursos" className="py-28">
                <div className="max-w-7xl mx-auto px-6 lg:px-10">
                    <div className="text-center max-w-3xl mx-auto mb-20">
                        <span className="text-xs font-semibold tracking-widest text-amber-400 uppercase">Por que TaskSync</span>
                        <h2 className="mt-3 text-3xl md:text-4xl font-bold tracking-tight text-white">Projetado para alta performance</h2>
                        <p className="mt-4 text-zinc-400 leading-relaxed">Tudo o que sua equipe precisa para eliminar gargalos e focar no que importa: programar.</p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        {/* Card 1 */}
                        <div className="p-8 md:pt-10 rounded-3xl bg-white/[0.03] border border-white/5 hover:border-amber-400/30 hover:bg-white/[0.05] transition-all duration-300 group">
                            <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-amber-400/20 to-orange-500/10 text-amber-400 flex items-center justify-center font-bold text-xl mb-6 group-hover:scale-110 transition-transform duration-300">
                                ⚡
                            </div>
                            <h3 className="text-xl font-bold text-white mb-3">Alocação Ágil</h3>
                            <p className="text-zinc-400 leading-relaxed text-sm">
                                Gerentes distribuem tarefas com rastreabilidade total, evitando sobrecarga e garantindo clareza nas prioridades do sprint.
                            </p>
                        </div>

                        {/* Card 2 */}
                        <div className="p-8 md:pt-14 rounded-3xl bg-white/[0.03] border border-white/5 hover:border-fuchsia-400/30 hover:bg-white/[0.05] transition-all duration-300 group">
                            <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-fuchsia-400/20 to-fuchsia-600/10 text-fuchsia-400 flex items-center justify-center font-bold text-xl mb-6 group-hover:scale-110 transition-transform duration-300">
                                🔒
                            </div>
                            <h3 className="text-xl font-bold text-white mb-3">Gestão de Acessos</h3>
                            <p className="text-zinc-400 leading-relaxed text-sm">
                                Controle granular de permissões por cargo, garantindo segurança e que cada membro acesse apenas os módulos pertinentes do projeto.
                            </p>
                        </div>

                        {/* Card 3 */}
                        <div className="p-8 md:pt-10 rounded-3xl bg-white/[0.03] border border-white/5 hover:border-emerald-400/30 hover:bg-white/[0.05] transition-all duration-300 group">
                            <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-emerald-400/20 to-emerald-600/10 text-emerald-400 flex items-center justify-center font-bold text-xl mb-6 group-hover:scale-110 transition-transform duration-300">
                                🎯
                            </div>
                            <h3 className="text-xl font-bold text-white mb-3">Foco do Desenvolvedor</h3>
                            <p className="text-zinc-400 leading-relaxed text-sm">
                                Ambiente limpo e sem distrações para que o dev saiba o que puxar, com estimativas claras e histórico de alterações em tempo real.
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer className="border-t border-white/5 bg-[#0a0a0f] py-14">
                <div className="max-w-7xl mx-auto px-6 lg:px-10 flex flex-col md:flex-row items-center justify-between gap-6">
                    <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-amber-400 to-fuchsia-600 flex items-center justify-center text-black font-bold text-sm">
                            🐱
                        </div>
                        <span className="text-base font-bold text-white">Task<span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 to-fuchsia-500">Sync</span></span>
                    </div>
                    
                    <p className="text-sm text-zinc-500 text-center">
                        &copy; 2026 TaskSync. Todos os direitos reservados. Feito com foco para equipes de desenvolvimento.
                    </p>
                    
                    <div className="flex items-center gap-6 text-sm text-zinc-400">
                        <a href="#termos" className="hover:text-white transition-colors duration-200">Termos</a>
                        <a href="#privacidade" className="hover:text-white transition-colors duration-200">Privacidade</a>
                    </div>
                </div>
            </footer>
        </div>
  );
}
