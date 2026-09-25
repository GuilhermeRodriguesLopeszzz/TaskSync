"use client"
import Link from "next/link";
import { useState } from "react";
import { Usuario, UsuarioFormProps } from "../../types/usuario";
import axios from "axios";
import { useRouter } from "next/navigation";

export default function UsuarioForm({ usuarioExistente }: UsuarioFormProps) {

    const router = useRouter()
    //Valor Inicial
    const [usuario, setUsuarios] = useState<Usuario>

        (usuarioExistente || new Usuario(null, "", "", "ATIVO", "", ""));
    //Atualiação Valor
    const handlerChange = (campo: 'nome' | 'email' | 'cpf' | 'senha', valor: string) => {
        setUsuarios(valorAnterior =>
            new Usuario(
                valorAnterior.id,
                campo === 'nome' ? valor : valorAnterior.nome,
                campo === 'email' ? valor : valorAnterior.email,
                valorAnterior.status,
                campo === 'cpf' ? valor : valorAnterior.cpf,
                campo === 'senha' ? valor : valorAnterior.senha,
            )

        )
    };

    const handlerSalvar = async (formData: FormData) => {

        if (usuarioExistente) {

            var dadosRetorno = await axios.put<number>("http://localhost:8080/usuarios" + usuario.id, usuario)
            if (dadosRetorno.status == 200) {
                alert("Usuario foi salvo com sucesso!")

            } else {
                alert(dadosRetorno.data);
                return;
            }
            router.push("/usuarios")

        } else {

            var dadosRetorno = await axios.post<number>("http://localhost:8080/usuarios", usuario)
            if (dadosRetorno.status == 200) {
                alert("Usuario foi salvo com sucesso!")

            } else {
                alert(dadosRetorno.data);
                return;
            }
            router.push("/usuarios")
        }
    }
    return (
        <form action={handlerSalvar}>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">
                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label className="text-sm font-medium text-zinc-300">Nome completo</label>
                    <input name="nome" value={usuario.nome} required onChange={(e) => handlerChange('nome', e.target.value)} placeholder="João da Silva" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all" />
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">CPF</label>
                    <input name="CPF" value={usuario.cpf} required onChange={(e) => handlerChange('cpf', e.target.value)} placeholder="000.000.000-00" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all" />
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">E-mail</label>
                    <input name="email" value={usuario.email} required onChange={(e) => handlerChange('email', e.target.value)} placeholder="joao@gmail.com.br" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all" />
                </div>
                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label className="text-sm font-medium text-zinc-300">Senha</label>
                    <input name="senha" value={usuario.senha} required onChange={(e) => handlerChange('senha', e.target.value)} className="w-full sm:w-1/2 px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all" />
                </div>

                <div className="sm:col-span-2 flex items-center justify-end gap-3 mt-4 pt-5 border-t border-white/10">
                    <Link href="/usuarios" className="px-4 py-2 rounded-lg text-zinc-400 hover:text-white text-sm font-medium transition-colors">Cancelar</Link>
                    <button type="submit" className="px-5 py-2 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm transition-colors">Salvar</button>
                </div>

            </div>
        </form>
    );
}