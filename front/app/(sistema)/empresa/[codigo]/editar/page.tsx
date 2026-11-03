"use client"

import Link from "next/link";

import { useParams, useRouter } from "next/navigation";
import EmpresaForm from "../../components/EmpresaForm";
import { useEffect, useState } from "react";
import { Empresa } from "@/app/(sistema)/types/empresa";
import { consultaEmpresaPorId } from "@/app/services/empresaService";


export default function EditarEmpresa(){
    const parametro = useParams();

    const codigo = Number (parametro.codigo);

    const router = useRouter();

    const [empresa, setEmpresa] = useState<Empresa|null>(null)

    useEffect(() => {

        buscarDados();
}, []);


const buscarDados = async()=>{

    const valorEmpresaBack = await consultaEmpresaPorId(codigo);

    if (valorEmpresaBack){
        setEmpresa(valorEmpresaBack);
    }else{
        router.push("/empresa")
    }
    
}

if (!empresa) return (<div className="p-8">Carregando dados</div>);


    return(
        <div className="w-full px-6 py-10 md:px-10 md:py-14">
            <div className="max-w-4xl mx-auto">
                <div className="mb-10">
                    <Link href="/empresa" className="inline-flex items-center gap-1.5 text-sm text-zinc-500 hover:text-amber-400 transition-colors font-medium"> Voltar para Listagem</Link>
                    <div className="mt-5">
                        <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">Editar empresa {codigo}</h1>
                        <p className="mt-1.5 text-sm text-zinc-400">Preencha os dados para editar a empresa</p>
                    </div>
                </div>
                <div className="bg-white/[0.03] border border-white/10 rounded-2xl shadow-xl shadow-black/30 p-6 sm:p-8">
                    <EmpresaForm empresaExistente={empresa}/>
                </div>
            </div>
        </div>
    );
}