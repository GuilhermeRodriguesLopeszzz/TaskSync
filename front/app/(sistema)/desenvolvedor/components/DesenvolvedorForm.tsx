"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";

import { RootState } from "@/app/redux/store";
import { Desenvolvedor, DesenvolvedorRequest, DesenvolvedorFormProps } from "../../types/desenvolvedor";
import { Empresa } from "../../types/empresa";

import { buscarListaEmpresas } from "@/app/services/empresaService";
import { atualizarDesenvolvedor, salvarDesenvolvedor } from "@/app/services/desenvolvedorService";


export default function DesenvolvedorForm({ desenvolvedorExistente }: DesenvolvedorFormProps) {

    const router = useRouter();

    const usuario = useSelector((state: RootState) => state.auth.usuario);

    const [desenvolvedor, setDesenvolvedor] = useState<Desenvolvedor>(
        new Desenvolvedor(
            desenvolvedorExistente?.id ?? null,
            desenvolvedorExistente?.nome ?? "",
            desenvolvedorExistente?.cpf ?? "",
            "",
            desenvolvedorExistente?.email ?? "",
            desenvolvedorExistente?.status ?? "ATIVO",
            desenvolvedorExistente?.empresaId ?? null,
            "ROLE_DEV"
        )
    );

    const [empresas, setEmpresas] = useState<Empresa[]>([]);

    const [empresaId, setEmpresaId] = useState<number | null>(
        desenvolvedorExistente?.empresaId ?? null
    );


    useEffect(() => {

        if (usuario?.role === "ROLE_ADMIN") {
            carregarEmpresas();
        }

    }, [usuario?.role]);


    const carregarEmpresas = async () => {

        try {

            var dados = await buscarListaEmpresas();
            setEmpresas(dados);

        } catch (error) {
            alert("Erro ao carregar empresas!");
        }
    };


    const handlerChange = (
        campo: "nome" | "cpf" | "email" | "senha",
        valor: string
    ) => {

        setDesenvolvedor(valorAnterior =>

            new Desenvolvedor(
                valorAnterior.id,
                campo === "nome" ? valor : valorAnterior.nome,
                campo === "cpf" ? valor : valorAnterior.cpf,
                campo === "senha" ? valor : valorAnterior.senha,
                campo === "email" ? valor : valorAnterior.email,
                valorAnterior.status,
                valorAnterior.empresaId,
                valorAnterior.role
            )
        );
    };



const handlerSalvar = async (formData: FormData) => {

    if (usuario?.role !== "ROLE_ADMIN" && usuario?.role !== "ROLE_GERENTE") {
        alert("Você não possui permissão!");
        return;
    }

    var empresaSalvar: number | null = null;

    if (usuario.role === "ROLE_ADMIN") {
        empresaSalvar = empresaId;
    } else {
        empresaSalvar = usuario.empresaId ?? null;
    }

    if (usuario.role === "ROLE_ADMIN" && empresaSalvar == null) {
        alert("Selecione uma empresa!");
        return;
    }

    if (!desenvolvedorExistente && !desenvolvedor.senha.trim()) {
        alert("Informe uma senha!");
        return;
    }

    var dados = new DesenvolvedorRequest(
        desenvolvedor.nome,
        desenvolvedor.cpf,
        desenvolvedor.email,
        desenvolvedor.senha,
        empresaSalvar,
        "ROLE_DEV"
    );

    try {

        if (desenvolvedorExistente) {

            if (desenvolvedorExistente.id == null) {
                alert("Desenvolvedor sem ID!");
                return;
            }

            var dadosRetorno = await atualizarDesenvolvedor(
                desenvolvedorExistente.id,
                dados
            );

            if (dadosRetorno == 200) {
                alert("Desenvolvedor atualizado com sucesso!");
                router.push("/desenvolvedor");
            } else {
                alert("Erro ao atualizar desenvolvedor!");
            }

        } else {

            var dadosRetorno = await salvarDesenvolvedor(dados);

            if (dadosRetorno == 201) {
                alert("Desenvolvedor cadastrado com sucesso!");
                router.push("/desenvolvedor");
            } else {
                alert("Erro ao salvar desenvolvedor!");
            }
        }

    } catch (error) {
        alert("Erro ao salvar desenvolvedor! Verifique os dados informados.");
    }
};


    return (

        <form action={handlerSalvar}>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">

                <div className="flex flex-col gap-1.5 sm:col-span-2">

                    <label className="text-sm font-medium text-zinc-300">
                        Nome completo
                    </label>

                    <input
                        name="nome"
                        value={desenvolvedor.nome}
                        required
                        onChange={(e) => handlerChange("nome", e.target.value)}
                        placeholder="João da Silva"
                        className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-amber-400/50"
                    />

                </div>


                <div className="flex flex-col gap-1.5">

                    <label className="text-sm font-medium text-zinc-300">
                        CPF
                    </label>

                    <input
                        name="cpf"
                        value={desenvolvedor.cpf}
                        required
                        onChange={(e) => handlerChange("cpf", e.target.value)}
                        placeholder="000.000.000-00"
                        className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-amber-400/50"
                    />

                </div>


                <div className="flex flex-col gap-1.5">

                    <label className="text-sm font-medium text-zinc-300">
                        E-mail
                    </label>

                    <input
                        type="email"
                        name="email"
                        value={desenvolvedor.email}
                        required
                        onChange={(e) => handlerChange("email", e.target.value)}
                        placeholder="joao@gmail.com.br"
                        className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-amber-400/50"
                    />

                </div>


                <div className="flex flex-col gap-1.5">

                    <label className="text-sm font-medium text-zinc-300">
                        {desenvolvedorExistente ? "Nova senha (opcional)" : "Senha"}
                    </label>

                    <input
                        type="password"
                        name="senha"
                        value={desenvolvedor.senha}
                        required={!desenvolvedorExistente}
                        onChange={(e) => handlerChange("senha", e.target.value)}
                        placeholder={desenvolvedorExistente ? "Deixe vazio para manter" : "Digite uma senha"}
                        className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-amber-400/50"
                    />

                </div>


                {usuario?.role === "ROLE_ADMIN" && (

                    <div className="flex flex-col gap-1.5">

                        <label className="text-sm font-medium text-zinc-300">
                            Empresa
                        </label>

                        <select
                            value={empresaId ?? ""}
                            required
                            onChange={(e) => setEmpresaId(Number(e.target.value))}
                            className="w-full px-4 py-2.5 bg-[#17171c] border border-white/10 rounded-lg text-white text-sm"
                        >

                            <option value="">Selecione uma empresa</option>

                            {empresas.filter(empresa =>
                                empresa.status === "ATIVO" ||
                                empresa.id === desenvolvedorExistente?.empresaId
                            ).map((empresa) => (

                                <option key={empresa.id} value={empresa.id ?? ""}>
                                    {empresa.nomeFantasia}
                                </option>

                            ))}

                        </select>

                    </div>

                )}


                {usuario?.role === "ROLE_GERENTE" &&  (

                    <div className="flex flex-col gap-1.5">

                        <label className="text-sm font-medium text-zinc-300">
                            Empresa
                        </label>

                        <input
                            value={"Empresa vinculada ao gerente"}
                            disabled
                            className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-zinc-500 text-sm"
                        />

                    </div>

                )}


                <div className="sm:col-span-2 flex items-center justify-end gap-3 mt-4 pt-5 border-t border-white/10">

                    <Link
                        href="/desenvolvedor"
                        className="px-4 py-2 rounded-lg text-zinc-400 hover:text-white text-sm font-medium transition-colors"
                    >
                        Cancelar
                    </Link>

                    <button
                        type="submit"
                        className="px-5 py-2 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm transition-colors"
                    >
                        Salvar
                    </button>

                </div>

            </div>

        </form>
    );
}