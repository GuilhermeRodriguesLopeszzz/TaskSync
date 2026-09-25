"use client"

import Link from "next/link";

import { useParams, useRouter } from "next/navigation";
import ProjetoForm from "../../components/ProjetoForm";
import { useEffect, useState } from "react";
import { Projeto } from "@/app/(sistema)/types/projeto";
import axios from "axios";

export default function EditarProjeto() {
    const parametro = useParams();

    const codigo = Number(parametro.codigo);

    const router = useRouter();

    const [projeto, setProjetos] = useState<Projeto | null>(null)

    useEffect(() => {

        buscarDados();
    }, []);

    const buscarDados = async () => {
        const valorProjetoBack = await axios.get<Projeto>("http://localhost:8080/projetos/" + codigo)

        if (valorProjetoBack.status == 200) {
            setProjetos(valorProjetoBack.data);
        } else {
            router.push("/projeto")
        }

    }

    if (!projeto) return (<div className="min-h-screen w-full flex items-center justify-center bg-[#0a0a0f] text-zinc-400 text-sm p-8">Carregando dados</div>);


    return (
        <div className="w-full px-6 py-10 md:px-10 md:py-14">
            <div className="max-w-4xl mx-auto">
                <div className="mb-10">
                    <Link href="/projeto" className="inline-flex items-center gap-1.5 text-sm text-zinc-500 hover:text-amber-400 transition-colors font-medium"> Voltar para Listagem</Link>
                    <div className="mt-5">
                        <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">Editar projeto {codigo}</h1>
                        <p className="mt-1.5 text-sm text-zinc-400">Preencha os dados para editar um novo projeto</p>
                    </div>
                </div>
                <div className="bg-white/[0.03] border border-white/10 rounded-2xl shadow-xl shadow-black/30 p-6 sm:p-8">
                    <ProjetoForm projetoExistente={projeto}/>
                </div>
            </div>
        </div>
    );
}