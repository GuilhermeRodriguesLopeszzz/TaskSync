import Link from "next/link";
import TarefaForm from "../components/TarefaForm";
export default function CadastroTarefa(){

    return(

        <div className="w-full px-6 py-10 md:px-10 md:py-14">
            <div className="max-w-4xl mx-auto">
                <div className="mb-10">
                    <Link href="/usuarios" className="inline-flex items-center gap-1.5 text-sm text-zinc-500 hover:text-amber-400 transition-colors font-medium"> Voltar para Listagem</Link>
                    <div className="mt-5">
                        <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">Nova tarefa</h1>
                        <p className="mt-1.5 text-sm text-zinc-400">Preencha os dados para registrar uma nova tarefa</p>
                    </div>
                </div>
                <div className="bg-white/[0.03] border border-white/10 rounded-2xl shadow-xl shadow-black/30 p-6 sm:p-8">
                    <TarefaForm/>
                </div>
            </div>
        </div>
    );
}