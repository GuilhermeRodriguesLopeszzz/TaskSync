"use client"
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Projeto, ProjetoFormProps } from "../../types/projeto";
import axios from "axios";

export default function ProjetoForm({ projetoExistente }: ProjetoFormProps) {

    const router = useRouter()
    //Valor Inicial
    const [projeto, setProjetos] = useState<Projeto>

        (projetoExistente || new Projeto(null, "", "", "", "", "BAIXA"));
    //Atualiação Valor
    const handlerChange = (campo: 'nome' | 'descricao' | 'dataInicio' | 'dataPrevistaFim', valor: string) => {
        setProjetos(valorAnterior =>
            new Projeto(
                valorAnterior.id,
                campo === 'nome' ? valor : valorAnterior.nome,
                campo === 'descricao' ? valor : valorAnterior.descricao,
                campo === 'dataInicio' ? valor : valorAnterior.dataInicio,
                campo === 'dataPrevistaFim' ? valor : valorAnterior.dataPrevistaFim,
                valorAnterior.status,
            )

        )
    };

    const handlerSalvar = async (formData: FormData) => {

        if (projetoExistente) {

            var dadosRetorno = await axios.put<number>("http://localhost:8080/projetos/" + projeto.id, projeto)
            if (dadosRetorno.status == 200) {
                alert("Projeto foi salvo com sucesso!")

            } else {
                alert(dadosRetorno.data);
                return;
            }
            router.push("/projeto")

        } else {

            var dadosRetorno = await axios.post<number>("http://localhost:8080/projetos", projeto)
            if (dadosRetorno.status == 200) {
                alert("Projeto foi salvo com sucesso!")

            } else {
                alert(dadosRetorno.data);
                return;
            }
            router.push("/projeto")
        }
    }

    return (
        <form action={handlerSalvar}>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">
                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label className="text-sm font-medium text-zinc-300">Nome do projeto</label>
                    <input name="nome" value={projeto.nome} required onChange={(e) => handlerChange('nome', e.target.value)} className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all" />
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">Descrição do Projeto</label>
                    <input name="descricao" value={projeto.descricao} required onChange={(e) => handlerChange('descricao', e.target.value)} className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all" />
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">Data Inicio</label>
                    <input type="date" name="dataInicio" value={(projeto.dataInicio ?? "").substring(0, 10)} required onChange={(e) => handlerChange('dataInicio', e.target.value)} className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all" />
                </div>
                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label className="text-sm font-medium text-zinc-300">Data Prevista Fim</label>
                    {/* Protege contra null (??) antes de chamar .substring(), 
                     que exige o formato "YYYY-MM-DD" pedido pelo input type="date".
                     Título e descrição não precisam disso porque só são exibidos direto 
                     no value, sem chamar nenhum método sobre a string. */}
                    <input type="date" name="dataPrevistaFim" value={(projeto.dataPrevistaFim ?? "").substring(0, 10)} required onChange={(e) => handlerChange('dataPrevistaFim', e.target.value)} className="w-full sm:w-1/2 px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all" />
                </div>

                <div className="sm:col-span-2 flex items-center justify-end gap-3 mt-4 pt-5 border-t border-white/10">
                    <Link href="/projeto" className="px-4 py-2 rounded-lg text-zinc-400 hover:text-white text-sm font-medium transition-colors">Cancelar</Link>
                    <button type="submit" className="px-5 py-2 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm transition-colors">Salvar</button>
                </div>

            </div>
        </form>
    );
}