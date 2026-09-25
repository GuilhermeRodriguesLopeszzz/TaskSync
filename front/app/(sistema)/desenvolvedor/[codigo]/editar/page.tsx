"use client"

import Link from "next/link";

import { useParams, useRouter } from "next/navigation";
import DesenvolvedorForm from "../../components/DesenvolvedorForm";
import { useEffect, useState } from "react";
import { Desenvolvedor } from "@/app/(sistema)/types/desenvolvedor";
import axios from "axios";

export default function EditarDesenvolvedor(){
    const parametro = useParams();

    const codigo = Number (parametro.codigo);

     const router = useRouter();

    const [desenvolvedor, setDesenvolvedor] = useState<Desenvolvedor|null>(null)

    useEffect(() => {

        buscarDados();
}, []);

const buscarDados = async()=>{
    const valorDesenvolvedorBack = await axios.get<Desenvolvedor>("http://localhost:8080/desenvolvedor/"+codigo)

    if (valorDesenvolvedorBack.status==200){
        setDesenvolvedor(valorDesenvolvedorBack.data);
    }else{
        router.push("/desenvolvedor")
    }
    
}

if (!desenvolvedor) return (<div className="p-8">Carregando dados</div>);


    return(
        <div className="w-full px-6 py-10 md:px-10 md:py-14">
            <div className="max-w-4xl mx-auto">
                <div className="mb-10">
                    <Link href="/desenvolvedor" className="inline-flex items-center gap-1.5 text-sm text-zinc-500 hover:text-amber-400 transition-colors font-medium"> Voltar para Listagem</Link>
                    <div className="mt-5">
                        <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">Editar desenvolvedor {codigo}</h1>
                        <p className="mt-1.5 text-sm text-zinc-400">Preencha os dados para editar um novo desenvolvedor</p>
                    </div>
                </div>
                <div className="bg-white/[0.03] border border-white/10 rounded-2xl shadow-xl shadow-black/30 p-6 sm:p-8">
                    <DesenvolvedorForm desenvolvedorExistente={desenvolvedor}/>
                </div>
            </div>
        </div>
    );
}