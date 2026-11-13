"use client";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import axios from "axios";

import { Tarefa, TarefaFormProps } from "../../types/tarefa";
import { Projeto } from "../../types/projeto";
import { Desenvolvedor } from "../../types/desenvolvedor";

import { atualizarTarefa, salvarTarefa } from "@/app/services/tarefaService";
import { buscarListaProjetos } from "@/app/services/projetoService";
import { buscarListaDesenvolvedores } from "@/app/services/desenvolvedorService";

export default function TarefaForm({ tarefaExistente }: TarefaFormProps) {

    const router = useRouter();

    const [tarefa, setTarefa] = useState<Tarefa>(
        tarefaExistente || new Tarefa(
            null, "", "", "", "PENDENTE", null, null
        )
    );

    const [projetos, setProjetos] = useState<Projeto[]>([]);
    const [desenvolvedores, setDesenvolvedores] = useState<Desenvolvedor[]>([]);
    const [carregando, setCarregando] = useState(true);
    const [salvando, setSalvando] = useState(false);

    // Carrega os projetos e desenvolvedores ao abrir o formulário
    useEffect(() => {

        const carregarDados = async () => {
            try {
                const listaProjetos = await buscarListaProjetos();
                const listaDesenvolvedores = await buscarListaDesenvolvedores();

                setProjetos(listaProjetos);
                setDesenvolvedores(listaDesenvolvedores);

            } catch (erro) {
                alert("Erro ao carregar projetos e desenvolvedores!");
            } finally {
                setCarregando(false);
            }
        };

        carregarDados();

    }, []);

    // Atualiza os campos de texto
    const handlerChange = (
        campo: "titulo" | "descricao" | "dataPrazo",
        valor: string
    ) => {
        setTarefa(valorAnterior => ({
            ...valorAnterior,
            [campo]: valor
        }));
    };

    // Seleciona o projeto e limpa o responsável anterior
    const handlerProjeto = (id: number) => {
        setTarefa(valorAnterior => ({
            ...valorAnterior,
            projetoId: id,
            desenvolvedorId: null
        }));
    };

    // Seleciona o desenvolvedor
    const handlerDesenvolvedor = (id: number) => {
        setTarefa(valorAnterior => ({
            ...valorAnterior,
            desenvolvedorId: id
        }));
    };

    // Busca o projeto selecionado
    const projetoSelecionado = projetos.find(
        projeto => projeto.id === tarefa.projetoId
    );

    // Mostra somente desenvolvedores vinculados ao projeto
    const desenvolvedoresDisponiveis = desenvolvedores.filter(
        desenvolvedor =>
            desenvolvedor.id !== null &&
            desenvolvedor.status === "ATIVO" &&
            (projetoSelecionado?.desenvolvedoresIds ?? [])
                .includes(desenvolvedor.id)
    );

    const handlerSalvar = async (formData: FormData) => {

        if (tarefa.projetoId === null ||
            tarefa.desenvolvedorId === null) {
            alert("Selecione um projeto e um desenvolvedor!");
            return;
        }

        setSalvando(true);

        try {

            if (tarefaExistente) {
                await atualizarTarefa(tarefa);
                alert("Tarefa atualizada com sucesso!");
            } else {
                await salvarTarefa(tarefa);
                alert("Tarefa cadastrada com sucesso!");
            }

            router.push("/tarefa");

        } catch (erro) {

            if (axios.isAxiosError(erro)) {
                const mensagem = erro.response?.data;

                if (typeof mensagem === "string") {
                    alert(mensagem);
                } else {
                    alert("Erro ao salvar tarefa!");
                }

            } else {
                alert("Erro inesperado ao salvar tarefa!");
            }

        } finally {
            setSalvando(false);
        }
    };

    const estiloCampo =
        "w-full px-4 py-2.5 bg-white/[0.04] " +
        "border border-white/10 rounded-lg text-white text-sm " +
        "focus:outline-none focus:ring-2 focus:ring-amber-400/50 " +
        "focus:border-amber-400/50 transition-all";

    return (
        <form action={handlerSalvar}>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">

                {/* TÍTULO */}
                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label className="text-sm font-medium text-zinc-300">
                        Título
                    </label>
                    <input
                        name="titulo"
                        value={tarefa.titulo}
                        required
                        onChange={e => handlerChange("titulo", e.target.value)}
                        placeholder="Título da tarefa"
                        className={estiloCampo}
                    />
                </div>

                {/* DESCRIÇÃO */}
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">
                        Descrição
                    </label>
                    <input
                        name="descricao"
                        value={tarefa.descricao}
                        required
                        onChange={e => handlerChange("descricao", e.target.value)}
                        placeholder="Descrição da tarefa"
                        className={estiloCampo}
                    />
                </div>

                {/* DATA PRAZO */}
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">
                        Data Prazo
                    </label>
                    <input
                        type="date"
                        name="dataPrazo"
                        value={(tarefa.dataPrazo ?? "").substring(0, 10)}
                        required
                        onChange={e => handlerChange("dataPrazo", e.target.value)}
                        className={estiloCampo}
                    />
                </div>

                {/* PROJETO */}
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">
                        Projeto
                    </label>
                    <select
                        name="projetoId"
                        value={tarefa.projetoId ?? ""}
                        required
                        disabled={carregando || !!tarefaExistente}
                        onChange={e => handlerProjeto(Number(e.target.value))}
                        className={estiloCampo}
                    >
                        <option
                            value=""
                            disabled
                            className="bg-zinc-900 text-amber-400 font-semibold"
                        >
                            — Selecione um projeto —
                        </option>

                        {projetos.map(projeto => (
                            <option
                                key={projeto.id}
                                value={projeto.id ?? ""}
                                className="bg-zinc-900"
                            >
                                {projeto.nome}
                            </option>
                        ))}
                    </select>
                </div>

                {/* DESENVOLVEDOR */}
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">
                        Desenvolvedor responsável
                    </label>
                    <select
                        name="desenvolvedorId"
                        value={tarefa.desenvolvedorId ?? ""}
                        required
                        disabled={carregando || tarefa.projetoId === null}
                        onChange={e => handlerDesenvolvedor(Number(e.target.value))}
                        className={estiloCampo}
                    >
<option
    value=""
    disabled
    className="bg-zinc-900 text-amber-400 font-semibold"
>
    — Selecione um desenvolvedor —
</option>
                        {desenvolvedoresDisponiveis.map(dev => (
                            <option
                                key={dev.id}
                                value={dev.id ?? ""}
                                className="bg-zinc-900"
                            >
                                {dev.nome}
                            </option>
                        ))}
                    </select>

                    {tarefa.projetoId !== null &&
                        desenvolvedoresDisponiveis.length === 0 &&
                        !carregando && (
                            <p className="text-xs text-amber-400">
                                Nenhum desenvolvedor ativo vinculado a este projeto.
                            </p>
                        )}
                </div>

                {/* BOTÕES */}
                <div className="sm:col-span-2 flex items-center justify-end gap-3 mt-4 pt-5 border-t border-white/10">

                    <Link
                        href="/tarefa"
                        className="px-4 py-2 rounded-lg text-zinc-400 hover:text-white text-sm font-medium"
                    >
                        Cancelar
                    </Link>

                    <button
                        type="submit"
                        disabled={carregando || salvando}
                        className="px-5 py-2 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm disabled:opacity-50"
                    >
                        {salvando ? "Salvando..." : "Salvar"}
                    </button>

                </div>
            </div>
        </form>
    );
}
