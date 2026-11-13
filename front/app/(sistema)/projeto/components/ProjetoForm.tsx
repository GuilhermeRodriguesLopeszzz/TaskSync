"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import axios from "axios";
import { RootState } from "@/app/redux/store";
import { Projeto, ProjetoFormProps } from "../../types/projeto";
import { Empresa } from "../../types/empresa";
import { Desenvolvedor } from "../../types/desenvolvedor";
import { buscarListaEmpresas } from "@/app/services/empresaService";
import { buscarListaDesenvolvedores } from "@/app/services/desenvolvedorService";
import { atualizarProjeto, salvarProjeto, vincularDesenvolvedorProjeto } from "@/app/services/projetoService";

export default function ProjetoForm({ projetoExistente }: ProjetoFormProps) {

    const router = useRouter();
    const usuario = useSelector((state: RootState) => state.auth.usuario);
    const ehAdmin = usuario?.role === "ROLE_ADMIN";

    const [projeto, setProjetos] = useState<Projeto>(
        projetoExistente || new Projeto(null, "", "", "", "", "BAIXA")
    );
    const [empresas, setEmpresas] = useState<Empresa[]>([]);
    const [desenvolvedores, setDesenvolvedores] = useState<Desenvolvedor[]>([]);
    const [desenvolvedorId, setDesenvolvedorId] = useState<number | null>(null);
    const [carregando, setCarregando] = useState(true);
    const [salvando, setSalvando] = useState(false);
    const [vinculando, setVinculando] = useState(false);

    // ADMIN pode escolher a empresa. No GERENTE o backend usa sua empresa.
    // A lista de desenvolvedores é necessária somente na edição do projeto.
    useEffect(() => {
        const carregarDados = async () => {
            setCarregando(true);
            try {
                if (ehAdmin && !projetoExistente) {
                    const listaEmpresas = await buscarListaEmpresas();
                    setEmpresas(listaEmpresas.filter(empresa => empresa.status === "ATIVO"));
                }
                if (projetoExistente) {
                    const listaDevs = await buscarListaDesenvolvedores();
                    setDesenvolvedores(listaDevs);
                }
            } catch (erro) {
                alert("Erro ao carregar empresas ou desenvolvedores!");
            } finally {
                setCarregando(false);
            }
        };
        carregarDados();
    }, [ehAdmin, projetoExistente]);

    // Atualiza os campos sem perder a empresa nem os desenvolvedores vinculados.
    const handlerChange = (
        campo: "nome" | "descricao" | "dataInicio" | "dataPrevistaFim",
        valor: string
    ) => {
        setProjetos(valorAnterior => ({
            ...valorAnterior,
            [campo]: valor
        }));
    };

    // Apenas o ADMIN escolhe a empresa na criação.
    const handlerEmpresa = (id: number) => {
        setProjetos(valorAnterior => ({
            ...valorAnterior,
            empresaId: id
        }));
    };

    // O projeto pertence a uma empresa, não ao ADMIN.
    const empresaProjeto = projetoExistente
        ? projeto.empresaId
        : ehAdmin ? projeto.empresaId : usuario?.empresaId;

    const desenvolvedoresDisponiveis = desenvolvedores.filter(dev =>
        dev.id !== null &&
        dev.status === "ATIVO" &&
        dev.empresaId === empresaProjeto &&
        !(projeto.desenvolvedoresIds ?? []).includes(dev.id)
    );

    const handlerSalvar = async (formData: FormData) => {
        if (!projetoExistente && ehAdmin && projeto.empresaId === null) {
            alert("Selecione a empresa do projeto!");
            return;
        }

        setSalvando(true);
        try {
            if (projetoExistente) {
                await atualizarProjeto(projeto);
                alert("Projeto atualizado com sucesso!");
                router.push("/projeto");
            } else {
                // Após cadastrar, abre a edição para permitir vincular os DEV.
                const projetoSalvo = await salvarProjeto(projeto);
                if (projetoSalvo.id !== null) {
                    router.push("/projeto/" + projetoSalvo.id + "/editar");
                    alert("Projeto cadastrado! Agora você pode vincular os desenvolvedores.");
                } else {
                    router.push("/projeto");
                }
            }
        } catch (erro) {
            if (axios.isAxiosError(erro) && typeof erro.response?.data === "string") {
                alert(erro.response.data);
            } else {
                alert("Erro ao salvar projeto!");
            }
        } finally {
            setSalvando(false);
        }
    };

    const handlerVincular = async () => {
        if (projeto.id === null || desenvolvedorId === null) {
            alert("Selecione um desenvolvedor!");
            return;
        }

        setVinculando(true);
        try {
            const projetoAtualizado = await vincularDesenvolvedorProjeto(
                projeto.id, desenvolvedorId
            );
            // A API devolve os IDs atualizados dos participantes.
            setProjetos(valorAnterior => ({
                ...valorAnterior,
                desenvolvedoresIds: Array.from(projetoAtualizado.desenvolvedoresIds ?? [])
            }));
            setDesenvolvedorId(null);
            alert("Desenvolvedor vinculado com sucesso!");
        } catch (erro) {
            if (axios.isAxiosError(erro) && typeof erro.response?.data === "string") {
                alert(erro.response.data);
            } else {
                alert("Erro ao vincular desenvolvedor!");
            }
        } finally {
            setVinculando(false);
        }
    };

    const estiloCampo = "w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all";

    return (
        <div>
            <form action={handlerSalvar}>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">
                    <div className="flex flex-col gap-1.5 sm:col-span-2">
                        <label className="text-sm font-medium text-zinc-300">Nome do projeto</label>
                        <input name="nome" value={projeto.nome} required onChange={e => handlerChange("nome", e.target.value)} className={estiloCampo} />
                    </div>

                    <div className="flex flex-col gap-1.5 sm:col-span-2">
                        <label className="text-sm font-medium text-zinc-300">Descrição do Projeto</label>
                        <input name="descricao" value={projeto.descricao} required onChange={e => handlerChange("descricao", e.target.value)} className={estiloCampo} />
                    </div>

                    {ehAdmin && !projetoExistente && (
                        <div className="flex flex-col gap-1.5 sm:col-span-2">
                            <label className="text-sm font-medium text-zinc-300">Empresa do projeto</label>
                            <select
                                name="empresaId"
                                value={projeto.empresaId ?? ""}
                                required
                                disabled={carregando || salvando}
                                onChange={e => handlerEmpresa(Number(e.target.value))}
                                className={estiloCampo}
                            >
                                <option value="" disabled className="bg-zinc-900">— Selecione a empresa —</option>
                                {empresas.map(empresa => (
                                    <option key={empresa.id} value={empresa.id ?? ""} className="bg-zinc-900">
                                        {empresa.nomeFantasia}
                                    </option>
                                ))}
                            </select>
                        </div>
                    )}

                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-zinc-300">Data Início</label>
                        <input type="date" name="dataInicio" value={(projeto.dataInicio ?? "").substring(0, 10)} required onChange={e => handlerChange("dataInicio", e.target.value)} className={estiloCampo} />
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label className="text-sm font-medium text-zinc-300">Data Prevista Fim</label>
                        <input type="date" name="dataPrevistaFim" value={(projeto.dataPrevistaFim ?? "").substring(0, 10)} required onChange={e => handlerChange("dataPrevistaFim", e.target.value)} className={estiloCampo} />
                    </div>

                    <div className="sm:col-span-2 flex items-center justify-end gap-3 mt-4 pt-5 border-t border-white/10">
                        <Link href="/projeto" className="px-4 py-2 rounded-lg text-zinc-400 hover:text-white text-sm font-medium transition-colors">Cancelar</Link>
                        <button type="submit" disabled={salvando || carregando} className="px-5 py-2 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm transition-colors disabled:opacity-50">
                            {salvando ? "Salvando..." : "Salvar"}
                        </button>
                    </div>
                </div>
            </form>

            {projetoExistente && (
                <div className="mt-8 pt-6 border-t border-white/10">
                    <h2 className="text-lg font-semibold text-white mb-2">Desenvolvedores do projeto</h2>
                    <p className="text-sm text-zinc-400 mb-4">
                        Vincule desenvolvedores da empresa para poder atribuir tarefas a eles.
                    </p>

                    <div className="flex flex-col sm:flex-row gap-3">
                        <select
                            value={desenvolvedorId ?? ""}
                            disabled={carregando || vinculando}
                            onChange={e => setDesenvolvedorId(Number(e.target.value))}
                            className={estiloCampo}
                        >
                            <option value="" disabled className="bg-zinc-900">— Selecione o desenvolvedor —</option>
                            {desenvolvedoresDisponiveis.map(dev => (
                                <option key={dev.id} value={dev.id ?? ""} className="bg-zinc-900">
                                    {dev.nome}
                                </option>
                            ))}
                        </select>
                        <button
                            type="button"
                            disabled={carregando || vinculando || desenvolvedorId === null}
                            onClick={handlerVincular}
                            className="px-5 py-2.5 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm disabled:opacity-50 whitespace-nowrap"
                        >
                            {vinculando ? "Vinculando..." : "Vincular"}
                        </button>
                    </div>

                    {desenvolvedoresDisponiveis.length === 0 && !carregando && (
                        <p className="text-xs text-amber-400 mt-2">
                            Nenhum novo desenvolvedor ativo disponível nesta empresa.
                        </p>
                    )}

                    <p className="text-sm text-zinc-400 mt-4">
                        Desenvolvedores vinculados: {(projeto.desenvolvedoresIds ?? []).length}
                    </p>
                    <div className="flex flex-wrap gap-2 mt-2">
                        {(projeto.desenvolvedoresIds ?? []).map(id => {
                            const dev = desenvolvedores.find(item => item.id === id);
                            return (
                                <span key={id} className="px-3 py-1.5 rounded-lg bg-white/5 border border-white/10 text-zinc-300 text-xs">
                                    {dev ? dev.nome : "Desenvolvedor #" + id}
                                </span>
                            );
                        })}
                    </div>
                </div>
            )}
        </div>
    );
}
