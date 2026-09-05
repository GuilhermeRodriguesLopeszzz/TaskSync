import Link from "next/link";


export default function Home() {
  return (

    <div className="bg-slate-950 text-slate-100 antialiased selection:bg-orange-500 selection:text-white min-h-screen">
            {/* Header / Navbar */}
            <header className="fixed top-0 left-0 right-0 z-50 bg-slate-950/80 backdrop-blur-md border-b border-slate-800">
                <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        {/* Logo: Gato minimalista em SVG */}
                        <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-orange-500 to-orange-700 flex items-center justify-center shadow-lg shadow-orange-500/20">
                            <svg className="w-6 h-6 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                <path d="M12 5c-3 0-5 2-5 5v1a5 5 0 0 0 10 0v-1c0-3-2-5-5-5z"></path>
                                <path d="M9 4L7 2"></path>
                                <path d="M15 4l2-2"></path>
                                <path d="M9 14v1a3 3 0 0 0 6 0v-1"></path>
                                <circle cx="10" cy="9" r="1" fill="currentColor"></circle>
                                <circle cx="14" cy="9" r="1" fill="currentColor"></circle>
                            </svg>
                        </div>
                        <span className="text-xl font-bold tracking-tight text-white">Task<span className="text-orange-500">Sync</span></span>
                    </div>
                    
                    <nav className="hidden md:flex items-center gap-8 text-sm font-medium text-slate-300">
                        <a href="#recursos" className="hover:text-orange-500 transition-colors">Recursos</a>
                        <a href="#sobre" className="hover:text-orange-500 transition-colors">Nossa História</a>
                        <a href="#beneficios" className="hover:text-orange-500 transition-colors">Vantagens</a>
                    </nav>

                    <div className="flex items-center gap-4">
                        
                        <Link href="/login" className="px-5 py-2.5 rounded-xl bg-orange-600 hover:bg-orange-500 text-white font-semibold text-sm transition-all shadow-lg shadow-orange-600/20 hover:shadow-orange-500/40">
                            Entrar </Link>
                    </div>
                </div>
            </header>

            {/* Hero Section */}
            <section className="relative pt-32 pb-20 md:pt-44 md:pb-32 overflow-hidden">
                <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[350px] bg-orange-600/15 blur-[140px] rounded-full pointer-events-none"></div>

                <div className="max-w-7xl mx-auto px-6 relative z-10 text-center">
                    <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-orange-500/10 border border-orange-500/20 text-orange-400 text-xs font-semibold mb-8">
                        <span className="w-2 h-2 rounded-full bg-orange-500 animate-pulse"></span>
                        Gestão ágil para equipes modernas
                    </div>
                    
                    <h1 className="text-4xl md:text-6xl lg:text-7xl font-extrabold tracking-tight text-white max-w-4xl mx-auto leading-[1.1]">
                        Sua equipe de desenvolvimento sincronizada e no <span className="text-transparent bg-clip-text bg-gradient-to-r from-orange-400 to-orange-600">controle</span>.
                    </h1>
                    
                    <p className="mt-6 text-lg md:text-xl text-slate-400 max-w-2xl mx-auto font-normal">
                        Gerentes alocam tarefas com precisão cirúrgica e desenvolvedores sabem exatamente o que entregar. Menos ruído, mais código em produção.
                    </p>

                    <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
                        <a href="#comecar" className="w-full sm:w-auto px-8 py-4 rounded-xl bg-orange-600 hover:bg-orange-500 text-white font-bold text-base transition-all shadow-xl shadow-orange-600/25 hover:shadow-orange-500/40 text-center">
                            Começar Gratuitamente
                        </a>
                        <a href="#sobre" className="w-full sm:w-auto px-8 py-4 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-300 border border-slate-800 font-semibold text-base transition-all text-center">
                            Conhecer a História
                        </a>
                    </div>

                    {/* Preview Card / Mockup Simulado */}
                    <div className="mt-16 relative max-w-5xl mx-auto rounded-2xl border border-slate-800 bg-slate-900/50 p-2 shadow-2xl backdrop-blur-sm">
                        <div className="rounded-xl bg-slate-950 p-6 text-left border border-slate-800/80">
                            <div className="flex items-center justify-between pb-4 border-b border-slate-800 mb-4">
                                <div className="flex items-center gap-2">
                                    <div className="w-3 h-3 rounded-full bg-red-500/80"></div>
                                    <div className="w-3 h-3 rounded-full bg-yellow-500/80"></div>
                                    <div className="w-3 h-3 rounded-full bg-green-500/80"></div>
                                </div>
                                <span className="text-xs text-slate-500 font-mono">tasksync-workspace.app</span>
                            </div>
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                <div className="bg-slate-900/80 p-4 rounded-lg border border-slate-800">
                                    <span className="text-xs font-semibold text-orange-400 uppercase tracking-wider">Backlog</span>
                                    <div className="mt-3 p-3 bg-slate-800/50 rounded border border-slate-700/50 text-sm font-medium">Refatorar autenticação OAuth</div>
                                </div>
                                <div className="bg-slate-900/80 p-4 rounded-lg border border-slate-800">
                                    <span className="text-xs font-semibold text-yellow-400 uppercase tracking-wider">Em Progresso (Dev)</span>
                                    <div className="mt-3 p-3 bg-slate-800/50 rounded border border-slate-700/50 text-sm font-medium">Criar endpoints de alocação</div>
                                </div>
                                <div className="bg-slate-900/80 p-4 rounded-lg border border-slate-800">
                                    <span className="text-xs font-semibold text-green-400 uppercase tracking-wider">Concluído</span>
                                    <div className="mt-3 p-3 bg-slate-800/50 rounded border border-slate-700/50 text-sm font-medium">Setup inicial Next.js</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Nossa História / Sobre */}
            <section id="sobre" className="py-24 bg-slate-900/40 border-y border-slate-800/60">
                <div className="max-w-5xl mx-auto px-6">
                    <div className="text-center max-w-3xl mx-auto">
                        <h2 className="text-3xl md:text-4xl font-bold tracking-tight text-white">Nossa História</h2>
                        <div className="w-12 h-1 bg-orange-500 mx-auto mt-4 rounded-full"></div>
                    </div>
                    
                    <div className="mt-12 text-slate-300 text-lg leading-relaxed space-y-6">
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
            <section id="recursos" className="py-24">
                <div className="max-w-7xl mx-auto px-6">
                    <div className="text-center max-w-3xl mx-auto mb-16">
                        <h2 className="text-3xl md:text-4xl font-bold tracking-tight text-white">Projetado para alta performance</h2>
                        <p className="mt-4 text-slate-400">Tudo o que sua equipe precisa para eliminar gargalos e focar no que importa: programar.</p>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                        {/* Card 1 */}
                        <div className="p-8 rounded-2xl bg-slate-900/60 border border-slate-800 hover:border-orange-500/50 transition-all group">
                            <div className="w-12 h-12 rounded-xl bg-orange-500/10 text-orange-500 flex items-center justify-center font-bold text-xl mb-6 group-hover:scale-110 transition-transform">
                                ⚡
                            </div>
                            <h3 className="text-xl font-bold text-white mb-3">Alocação Ágil</h3>
                            <p className="text-slate-400 leading-relaxed text-sm">
                                Gerentes distribuem tarefas com rastreabilidade total, evitando sobrecarga e garantindo clareza nas prioridades do sprint.
                            </p>
                        </div>

                        {/* Card 2 */}
                        <div className="p-8 rounded-2xl bg-slate-900/60 border border-slate-800 hover:border-orange-500/50 transition-all group">
                            <div className="w-12 h-12 rounded-xl bg-orange-500/10 text-orange-500 flex items-center justify-center font-bold text-xl mb-6 group-hover:scale-110 transition-transform">
                                🔒
                            </div>
                            <h3 className="text-xl font-bold text-white mb-3">Gestão de Acessos</h3>
                            <p className="text-slate-400 leading-relaxed text-sm">
                                Controle granular de permissões por cargo, garantindo segurança e que cada membro acesse apenas os módulos pertinentes do projeto.
                            </p>
                        </div>

                        {/* Card 3 */}
                        <div className="p-8 rounded-2xl bg-slate-900/60 border border-slate-800 hover:border-orange-500/50 transition-all group">
                            <div className="w-12 h-12 rounded-xl bg-orange-500/10 text-orange-500 flex items-center justify-center font-bold text-xl mb-6 group-hover:scale-110 transition-transform">
                                🎯
                            </div>
                            <h3 className="text-xl font-bold text-white mb-3">Foco do Desenvolvedor</h3>
                            <p className="text-slate-400 leading-relaxed text-sm">
                                Ambiente limpo e sem distrações para que o dev saiba o que puxar, com estimativas claras e histórico de alterações em tempo real.
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer className="border-t border-slate-800/80 bg-slate-950 py-12">
                <div className="max-w-7xl mx-auto px-6 flex flex-col md:flex-row items-center justify-between gap-6">
                    <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-lg bg-orange-600 flex items-center justify-center text-white font-bold text-sm">
                            🐱
                        </div>
                        <span className="text-base font-bold text-white">Task<span className="text-orange-500">Sync</span></span>
                    </div>
                    
                    <p className="text-sm text-slate-500 text-center">
                        &copy; 2026 TaskSync. Todos os direitos reservados. Feito com foco para equipes de desenvolvimento.
                    </p>
                    
                    <div className="flex items-center gap-6 text-sm text-slate-400">
                        <a href="#termos" className="hover:text-orange-500 transition-colors">Termos</a>
                        <a href="#privacidade" className="hover:text-orange-500 transition-colors">Privacidade</a>
                    </div>
                </div>
            </footer>
        </div>
  );
}
