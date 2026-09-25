"use client"

import Link from "next/link";
import UsuarioForm from "../../components/UsuarioForm";
import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { Usuario } from "@/app/(sistema)/types/usuario";
import axios from "axios";

export default function EditarUsuario(){
    const parametro = useParams();

    const codigo = Number (parametro.codigo);

    const router = useRouter();

    const [usuario, setUsuarios] = useState<Usuario|null>(null)

    useEffect(() => {

        buscarDados();
}, []);

const buscarDados = async()=>{
    const valorUsuarioBack = await axios.get<Usuario>("http://localhost:8080/usuarios/"+codigo)

    if (valorUsuarioBack.status==200){
        setUsuarios(valorUsuarioBack.data);
    }else{
        router.push("/usuarios")
    }
    
}

if (!usuario) return (<div className="p-8">Carregando dados</div>);
    
    return(
        <div className="w-full px-6 py-10 md:px-10 md:py-14">
            <div className="max-w-4xl mx-auto">
                <div className="mb-10">
                    <Link href="/usuarios" className="inline-flex items-center gap-1.5 text-sm text-zinc-500 hover:text-amber-400 transition-colors font-medium"> Voltar para Listagem</Link>
                    <div className="mt-5">
                        <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">Editar usuario {codigo}</h1>
                        <p className="mt-1.5 text-sm text-zinc-400">Preencha os dados para editar um novo usuario</p>
                    </div>
                </div>
                <div className="bg-white/[0.03] border border-white/10 rounded-2xl shadow-xl shadow-black/30 p-6 sm:p-8">
                    <UsuarioForm usuarioExistente={usuario}/>
                </div>
            </div>
        </div>
    );
}