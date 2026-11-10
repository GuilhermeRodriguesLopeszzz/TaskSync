
"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import { Tarefa } from "../types/tarefa";
import { RootState } from "@/app/redux/store";
import { alternarFavorito } from "@/app/redux/slice/favoritosSlice";

import {
  atualizarStatusTarefa,
  buscarListaTarefas,
  deletarTarefa
} from "@/app/services/tarefaService";

export default function Tarefas() {

  const [tarefas, setTarefas] = useState<Tarefa[]>([]);

  // Controla se a tela mostra todas ou somente favoritas
  const [somenteFavoritas, setSomenteFavoritas] = useState(false);

  const dispatch = useDispatch();

  // Recupera o ID do usuário logado pelo authSlice
  const usuarioId = useSelector(
    (state: RootState) => state.auth.usuario?.id
  );

  // Recupera as tarefas favoritas deste usuário
  const favoritos = useSelector((state: RootState) => {

    if (state.auth.usuario?.id == null) {
      return [];
    }

    const chave = String(state.auth.usuario.id);

    return state.favoritos.porUsuario[chave] ?? [];
  });


  // Carrega as tarefas quando a página abre
  useEffect(() => {
    carregarDados();
  }, []);


  const carregarDados = async () => {

    try {
      const dados = await buscarListaTarefas();
      setTarefas(dados);

    } catch (error) {
      alert("Erro ao carregar tarefas!");
    }
  };


  // Adiciona ou remove uma tarefa dos favoritos
  const handleFavorito = (tarefaId: number | null) => {

    if (usuarioId == null || tarefaId == null) {
      return;
    }

    dispatch(
      alternarFavorito({
        usuarioId: usuarioId,
        tarefaId: tarefaId
      })
    );
  };


  // Filtra as tarefas conforme a escolha do usuário
  const tarefasExibidas = somenteFavoritas
    ? tarefas.filter(tarefa =>
      tarefa.id !== null &&
      favoritos.includes(tarefa.id)
    )
    : tarefas;


  // Exclusão lógica da tarefa
  const handlerDeletarTarefa = async (tarefa: Tarefa) => {

    try {
      const dadosRetorno = await deletarTarefa(tarefa.id || 0);

      if (dadosRetorno == 200) {
        alert("Excluído com sucesso!");
        carregarDados();
      }

    } catch (error) {
      alert("Erro ao excluir a tarefa!");
    }
  };


  // Atualiza o status da tarefa
  const handleAlterarStatusTarefa = async (tarefa: Tarefa) => {

    try {
      await atualizarStatusTarefa(tarefa);
      carregarDados();

    } catch (error) {
      alert("Erro ao atualizar a tarefa!");
    }
  };


  return (
    <div className="min-h-screen w-full bg-[#0a0a0f] px-6 py-10 md:px-10 md:py-14">

      {/* Título e botões */}
      <div className="max-w-7xl mx-auto flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-10">

        <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
          Gestão de tarefas
        </h1>

        <div className="flex items-center gap-3 flex-wrap">

          {/* Alterna entre todas as tarefas e favoritas */}
          <button
            type="button"
            onClick={() => setSomenteFavoritas(!somenteFavoritas)}
            className="px-5 py-2.5 rounded-full border border-amber-400/40 text-amber-400 font-semibold text-sm hover:bg-amber-400/10 transition-colors"
          >
            {somenteFavoritas
              ? "Mostrar todas"
              : "★ Somente favoritas"}
          </button>

          <Link
            href="/tarefa/novo"
            className="inline-flex items-center justify-center px-5 py-2.5 rounded-full bg-gradient-to-r from-amber-400 to-orange-500 hover:from-amber-300 hover:to-orange-400 text-black font-bold text-sm transition-all"
          >
            Adicionar tarefa
          </Link>

        </div>
      </div>


      {/* Tabela de tarefas */}
      <div className="max-w-7xl mx-auto">

        <div className="rounded-3xl bg-white/[0.03] border border-white/10 backdrop-blur-xl shadow-2xl shadow-black/40 overflow-x-auto">

          <table className="w-full text-sm text-left">

            <thead>
              <tr className="border-b border-white/10 bg-white/[0.02]">

                <th className="px-6 py-4 text-zinc-400">
                  Favorito
                </th>

                <th className="px-6 py-4 text-zinc-400">
                  ID
                </th>

                <th className="px-6 py-4 text-zinc-400">
                  Título
                </th>

                <th className="px-6 py-4 text-zinc-400">
                  Descrição
                </th>

                <th className="px-6 py-4 text-zinc-400">
                  Data Prazo
                </th>

                <th className="px-6 py-4 text-zinc-400">
                  Status
                </th>

                <th className="px-6 py-4 text-zinc-400">
                  Ações
                </th>

              </tr>
            </thead>


            <tbody className="divide-y divide-white/5">

              {tarefasExibidas.map((tarefa) => (

                <tr
                  key={tarefa.id}
                  className="hover:bg-white/[0.03] transition-colors"
                >

                  {/* Estrela de favorito */}
                  <td className="px-6 py-4">

                    <button
                      type="button"
                      onClick={() => handleFavorito(tarefa.id)}
                      className="text-2xl text-amber-400 hover:scale-110 transition-transform"
                      aria-label={
                        favoritos.includes(tarefa.id ?? -1)
                          ? "Remover dos favoritos"
                          : "Adicionar aos favoritos"
                      }
                    >
                      {favoritos.includes(tarefa.id ?? -1)
                        ? "★"
                        : "☆"}
                    </button>

                  </td>

                  <td className="px-6 py-4 text-zinc-500 font-mono text-xs">
                    {tarefa.id}
                  </td>

                  <td className="px-6 py-4 font-medium text-white">
                    {tarefa.titulo}
                  </td>

                  <td className="px-6 py-4 text-zinc-400">
                    {tarefa.descricao}
                  </td>

                  <td className="px-6 py-4 text-zinc-400 font-mono text-xs">
                    {tarefa.dataPrazo}
                  </td>

                  <td className="px-6 py-4 text-emerald-400 font-semibold text-xs">
                    {tarefa.status}
                  </td>

                  {/* Ações já existentes */}
                  <td className="px-6 py-4">

                    <div className="flex items-center gap-3">

                      <Link
                        href={`/tarefa/${tarefa.id}/editar`}
                        className="inline-flex items-center justify-center w-8 h-8 rounded-lg text-zinc-400 hover:text-amber-400 hover:bg-white/5 transition-colors"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          strokeWidth="2"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          className="w-4 h-4"
                        >
                          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-7" />
                          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                        </svg>
                      </Link>

                      <button
                        onClick={() => handlerDeletarTarefa(tarefa)}
                        className="text-red-400 hover:text-red-300"
                      >
                        DELETAR
                      </button>

                      <button
                        onClick={() => handleAlterarStatusTarefa(tarefa)}
                        className="px-3 py-1.5 rounded-full border border-emerald-400/30 text-emerald-400 text-xs hover:bg-emerald-400/10"
                      >
                        Alterar status
                      </button>

                    </div>
                  </td>

                </tr>
              ))}


              {/* Mensagem quando não houver tarefas */}
              {tarefasExibidas.length === 0 && (

                <tr>
                  <td
                    colSpan={7}
                    className="px-6 py-16 text-center text-zinc-500 italic text-sm"
                  >
                    {somenteFavoritas
                      ? "Nenhuma tarefa favorita encontrada."
                      : "Nenhuma tarefa encontrada."}
                  </td>
                </tr>

              )}

            </tbody>

          </table>

        </div>
      </div>

    </div>
  );
}
