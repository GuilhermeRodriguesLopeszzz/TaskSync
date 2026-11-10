"use client";

import Link from "next/link";
import { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { RootState } from "@/app/redux/store";

import { Desenvolvedor } from "../types/desenvolvedor";
import {
  atualizarStatusDesenvolvedor,
  buscarListaDesenvolvedores,
  deletarDesenvolvedor
} from "@/app/services/desenvolvedorService";


export default function Desenvolvedores() {

  const [desenvolvedores, setDesenvolvedores] = useState<Desenvolvedor[]>([]);
  const [erro, setErro] = useState("");

  const usuario = useSelector((state: RootState) => state.auth.usuario);

  const podeGerenciar = usuario?.role === "ROLE_ADMIN" ||
    usuario?.role === "ROLE_GERENTE";


  useEffect(() => {

    if (podeGerenciar) {
      carregarDados();
    }

  }, [podeGerenciar]);


  const carregarDados = async () => {

    try {
      var dados = await buscarListaDesenvolvedores();
      setDesenvolvedores(dados);
      setErro("");

    } catch (error) {
      setErro("Erro ao carregar desenvolvedores!");
    }
  };


  const handlerDeletarDesenvolvedor = async (desenvolvedor: Desenvolvedor) => {

    if (!confirm("Deseja excluir este desenvolvedor?")) {
      return;
    }

    try {

      if (desenvolvedor.id == null) {
        alert("Desenvolvedor sem ID!");
        return;
      }

      var dadosRetorno = await deletarDesenvolvedor(desenvolvedor.id);

      if (dadosRetorno == 200) {
        alert("Excluído com sucesso!");
        carregarDados();
      } else {
        alert("Erro ao excluir desenvolvedor!");
      }

    } catch (error) {
      alert("Não foi possível excluir o desenvolvedor!");
    }
  };


  const handleAlterarStatusDesenvolvedor = async (desenvolvedor: Desenvolvedor) => {

    try {

      await atualizarStatusDesenvolvedor(desenvolvedor);
      carregarDados();

    } catch (error) {
      alert("Erro ao atualizar status!");
    }
  };


  if (!podeGerenciar) {
    return (
      <div className="p-10 text-zinc-300">
        Você não possui permissão para gerenciar desenvolvedores!
      </div>
    );
  }


  return (

    <div className="min-h-screen w-full bg-[#0a0a0f] px-6 py-10 md:px-10 md:py-14">

      <div className="max-w-7xl mx-auto flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-10">

        <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
          Gestão de desenvolvedores
        </h1>

        <Link
          href="/desenvolvedor/novo"
          className="inline-flex items-center justify-center px-5 py-2.5 rounded-full bg-gradient-to-r from-amber-400 to-orange-500 hover:from-amber-300 hover:to-orange-400 text-black font-bold text-sm transition-all duration-200"
        >
          Adicionar novo desenvolvedor
        </Link>

      </div>


      <div className="max-w-7xl mx-auto">

        <div className="rounded-3xl bg-white/[0.03] border border-white/10 backdrop-blur-xl shadow-2xl shadow-black/40 overflow-x-auto">

          <table className="w-full text-sm text-left">

            <thead>
              <tr className="border-b border-white/10 bg-white/[0.02]">

                <th className="px-6 py-4 text-zinc-400">ID</th>
                <th className="px-6 py-4 text-zinc-400">Nome</th>
                <th className="px-6 py-4 text-zinc-400">E-mail</th>
                <th className="px-6 py-4 text-zinc-400">CPF</th>
                <th className="px-6 py-4 text-zinc-400">Empresa</th>
                <th className="px-6 py-4 text-zinc-400">Status</th>
                <th className="px-6 py-4 text-zinc-400">Ações</th>

              </tr>
            </thead>


            <tbody className="divide-y divide-white/5">

              {desenvolvedores.map((desenvolvedor) => (

                <tr
                  key={desenvolvedor.id}
                  className="hover:bg-white/[0.03] transition-colors"
                >

                  <td className="px-6 py-4 text-zinc-500 font-mono text-xs">
                    {desenvolvedor.id}
                  </td>

                  <td className="px-6 py-4 font-medium text-white">
                    {desenvolvedor.nome}
                  </td>

                  <td className="px-6 py-4 text-zinc-400">
                    {desenvolvedor.email}
                  </td>

                  <td className="px-6 py-4 text-zinc-400">
                    {desenvolvedor.cpf}
                  </td>

                  <td className="px-6 py-4 text-zinc-400">
                    {desenvolvedor.empresaId}
                  </td>

                  <td className="px-6 py-4 text-zinc-400">
                    {desenvolvedor.status}
                  </td>


                  <td className="px-6 py-4">

                    <Link
                      href={`/desenvolvedor/${desenvolvedor.id}/editar`}
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
                      onClick={() => handlerDeletarDesenvolvedor(desenvolvedor)}
                      className="px-3 py-1.5 rounded-lg text-xs font-semibold text-red-400 hover:text-white hover:bg-red-500/10"
                    >
                      DELETAR
                    </button>


                    <button
                      onClick={() => handleAlterarStatusDesenvolvedor(desenvolvedor)}
                      className={`px-3 py-1.5 rounded-full text-xs font-semibold border ${desenvolvedor.status === "BLOQUEADO"
                          ? "text-red-400 border-red-400/30 bg-red-400/10"
                          : "text-emerald-400 border-emerald-400/30 bg-emerald-400/10"
                        }`}
                    >

                      {desenvolvedor.status}

                    </button>

                  </td>

                </tr>

              ))}


              {erro && (
                <tr>
                  <td colSpan={7} className="px-6 py-16 text-center text-red-400">
                    {erro}
                  </td>
                </tr>
              )}


              {!erro && desenvolvedores.length === 0 && (
                <tr>
                  <td colSpan={7} className="px-6 py-16 text-center text-zinc-500">
                    Nenhum desenvolvedor encontrado
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