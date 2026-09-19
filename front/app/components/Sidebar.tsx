import Link from "next/link";

export default function Sidebar() {
  return (

    <aside className="w-64 shrink-0 h-screen sticky top-0 bg-white/[0.03] border-r border-white/10 backdrop-blur-xl flex flex-col">
        <div className="h-20 flex items-center px-6 text-xl font-bold text-white tracking-tight border-b border-white/5">
            Task<span className="text-transparent bg-clip-text bg-gradient-to-r from-amber-400 to-fuchsia-500">Sync</span>
        </div>
        <nav className="flex-1 flex flex-col gap-1 px-4 py-6">
            <Link href="/home" className="px-4 py-2.5 rounded-xl text-sm font-medium text-zinc-400 hover:text-white hover:bg-white/5 transition-colors">Home</Link>
            <Link href="/usuarios" className="px-4 py-2.5 rounded-xl text-sm font-medium text-zinc-400 hover:text-white hover:bg-white/5 transition-colors">Usuarios</Link>
            <Link href="/tarefa" className="px-4 py-2.5 rounded-xl text-sm font-medium text-zinc-400 hover:text-white hover:bg-white/5 transition-colors">Tarefas</Link>
            <Link href="/projeto" className="px-4 py-2.5 rounded-xl text-sm font-medium text-zinc-400 hover:text-white hover:bg-white/5 transition-colors">Projetos</Link>
            <Link href="/desenvolvedor" className="px-4 py-2.5 rounded-xl text-sm font-medium text-zinc-400 hover:text-white hover:bg-white/5 transition-colors">Desenvolvedores</Link>
        </nav>
    </aside>
      );
}