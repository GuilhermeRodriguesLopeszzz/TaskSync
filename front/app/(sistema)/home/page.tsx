export default function Home(){
    return(
        <div className="min-h-screen w-full flex items-center justify-center bg-[#0a0a0f] px-6 relative overflow-hidden">
            <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[500px] bg-gradient-to-r from-amber-500/15 via-orange-500/10 to-fuchsia-600/15 blur-[160px] rounded-full pointer-events-none"></div>
            <div className="absolute inset-0 bg-[linear-gradient(to_right,#ffffff08_1px,transparent_1px),linear-gradient(to_bottom,#ffffff08_1px,transparent_1px)] bg-[size:64px_64px] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_50%,#000_50%,transparent_100%)] pointer-events-none"></div>

            <div className="relative z-10 flex flex-col items-center text-center max-w-4xl">
                <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-amber-400 via-orange-500 to-fuchsia-600 flex items-center justify-center shadow-lg shadow-orange-500/30 mb-8">
                    <svg className="w-8 h-8 text-black" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M12 5c-3 0-5 2-5 5v1a5 5 0 0 0 10 0v-1c0-3-2-5-5-5z"></path>
                        <path d="M9 4L7 2"></path>
                        <path d="M15 4l2-2"></path>
                        <path d="M9 14v1a3 3 0 0 0 6 0v-1"></path>
                        <circle cx="10" cy="9" r="1" fill="currentColor"></circle>
                        <circle cx="14" cy="9" r="1" fill="currentColor"></circle>
                    </svg>
                </div>

                <span className="text-xs font-semibold tracking-widest text-amber-400 uppercase mb-4">TaskSync Platform</span>

                <h1 className="text-3xl sm:text-4xl md:text-5xl font-extrabold text-white leading-[1.1] tracking-tight">
                    Bem-vindo ao sistema de <span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-300 via-orange-400 to-fuchsia-500">Gerenciamento de Projetos</span>
                </h1>

                <div className="w-16 h-1 bg-gradient-to-r from-amber-400 to-fuchsia-500 rounded-full mt-8"></div>
            </div>
        </div>
    );
}