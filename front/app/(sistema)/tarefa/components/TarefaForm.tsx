"use client"
import Link from "next/link";
import { Tarefa, TarefaFormProps } from "../../types/tarefa";
import { useRouter } from "next/navigation";
import { useState } from "react";
import axios from "axios";

export default function TarefaForm({ tarefaExistente }: TarefaFormProps) {
    const router = useRouter()
    //Valor Inicial
    const [tarefa, setTarefa] = useState<Tarefa>
        (tarefaExistente || new Tarefa(null, "", "", "", "ANDAMENTO"));
    //Atualiação Valor
    const handlerChange = (campo: 'titulo' | 'descricao' | 'dataPrazo', valor: string) => {
        setTarefa(valorAnterior =>
            new Tarefa(
                valorAnterior.id,
                campo === 'titulo' ? valor : valorAnterior.titulo,
                campo === 'descricao' ? valor : valorAnterior.descricao,
                campo === 'dataPrazo' ? valor : valorAnterior.dataPrazo,
                valorAnterior.status,
            )
        )
    };

    const handlerSalvar = async (formData: FormData) => {

        if (tarefaExistente) {

            var dadosRetorno = await axios.put<number>("http://localhost:8080/tarefa/" + tarefa.id, tarefa)
            if (dadosRetorno.status == 200) {
                alert("Tarefa foi salvo com sucesso!")

            } else {
                alert(dadosRetorno.data);
                return;
            }
            router.push("/tarefa")

        } else {

            var dadosRetorno = await axios.post<number>("http://localhost:8080/tarefa", tarefa)
            if (dadosRetorno.status == 200) {
                alert("Tarefa foi salvo com sucesso!")

            } else {
                alert(dadosRetorno.data);
                return;
            }
            router.push("/tarefa")
        }
    }
    return (
        <form action={handlerSalvar}>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">
                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label className="text-sm font-medium text-zinc-300">Título</label>
                    <input name="titulo" value={tarefa.titulo ?? ""} required onChange={(e) => handlerChange('titulo', e.target.value)} placeholder="Título da tarefa" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">Descrição</label>
                    <input name="descricao" value={tarefa.descricao ?? ""} required onChange={(e) => handlerChange('descricao', e.target.value)} placeholder="Descrição da tarefa" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">Data Prazo</label>
                    <input type="date" name="dataPrazo" value={(tarefa.dataPrazo ?? "").substring(0, 10)} required onChange={(e) => handlerChange('dataPrazo', e.target.value)} className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>

                <div className="sm:col-span-2 flex items-center justify-end gap-3 mt-4 pt-5 border-t border-white/10">
                    <Link href="/tarefa" className="px-4 py-2 rounded-lg text-zinc-400 hover:text-white text-sm font-medium transition-colors">Cancelar</Link>
                    <button type="submit" className="px-5 py-2 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm transition-colors">Salvar</button>
                </div>

            </div>
        </form>
    );
}