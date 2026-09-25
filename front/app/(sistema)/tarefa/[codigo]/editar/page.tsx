"use client"

import Link from "next/link";
import TarefaForm from "../../components/TarefaForm";
import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { Tarefa } from "@/app/(sistema)/types/tarefa";
import axios from "axios";

export default function EditarTarefa(){
    const parametro = useParams();

    const codigo = Number (parametro.codigo);

    const router = useRouter();

    const [tarefa, setTarefa] = useState<Tarefa|null>(null)

    useEffect(() => {

        buscarDados();
}, []);

const buscarDados = async()=>{
    const valorTarefaBack = await axios.get<Tarefa>("http://localhost:8080/tarefa/"+codigo)

    if (valorTarefaBack.status==200){
        setTarefa(valorTarefaBack.data);
    }else{
        router.push("/tarefa")
    }
    
}

if (!tarefa) return (<div className="p-8">Carregando dados</div>);

    return(

        <div className="w-full px-6 py-10 md:px-10 md:py-14">
            <div className="max-w-4xl mx-auto">
                <div className="mb-10">
                    <Link href="/tarefa" className="inline-flex items-center gap-1.5 text-sm text-zinc-500 hover:text-amber-400 transition-colors font-medium"> Voltar para Listagem</Link>
                    <div className="mt-5">
                        <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">Editar tarefa {codigo}</h1>
                        <p className="mt-1.5 text-sm text-zinc-400">Preencha os dados para editar uma tarefa</p>
                    </div>
                </div>
                <div className="bg-white/[0.03] border border-white/10 rounded-2xl shadow-xl shadow-black/30 p-6 sm:p-8">
                    <TarefaForm tarefaExistente={tarefa}/>
                </div>
            </div>
        </div>
    );
}