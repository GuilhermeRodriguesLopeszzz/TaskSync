"use client"
import Link from "next/link";
import { useEffect, useState } from "react";
import axios from "axios";
import { Tarefa } from "../types/tarefa";

export default function Tarefas() {

  const [tarefas, setTarefas] = useState<Tarefa[]>([]);

useEffect(() => {
  carregarDados();
}, []);

const carregarDados = async () => {
  try {
    const dados = await axios.get<Tarefa[]>("http://localhost:8080/tarefa");
    setTarefas(dados.data);
  } catch (error) {
    alert("Erro ao carregar dados");
  }
};

const handlerDeletarTarefa = async (tarefa: Tarefa) => {
    var dadosRetorno = await
      axios.delete('http://localhost:8080/tarefa/' + tarefa.id + '/excluir');

    if (dadosRetorno.status == 200) {
      alert("Excluido com sucesso!");
    } else {
      alert(dadosRetorno.data);

      return;
    }

    carregarDados();

  }

  const handleAlterarStatusTarefa = async (tarefa: Tarefa) => {
    var novoStatus = {};
    if (tarefa.status === "ANDAMENTO") {
      novoStatus = { status: "INTERROMPIDO" }
    } else {
      novoStatus = { status: "ANDAMENTO" }
    }

    var dadosRetorno = await
      axios.patch('http://localhost:8080/tarefa/' + tarefa.id + '/status', novoStatus);

    if (dadosRetorno.status == 200) {
      alert("Atulizado status com sucesso!");
    } else {
      alert(dadosRetorno.data);

      return;
    }

    carregarDados();

  }
    return (
<div className="min-h-screen w-full bg-[#0a0a0f] px-6 py-10 md:px-10 md:py-14">
  <div className="max-w-7xl mx-auto flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-10">
    <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">Gestão de tarefas</h1>
    <Link href="/tarefas/novo" className="inline-flex items-center justify-center px-5 py-2.5 rounded-full bg-gradient-to-r from-amber-400 to-orange-500 hover:from-amber-300 hover:to-orange-400 text-black font-bold text-sm transition-all duration-200 hover:-translate-y-0.5 shadow-lg shadow-orange-500/25 hover:shadow-orange-400/40 w-fit"></Link>
  </div>

  <div className="max-w-7xl mx-auto">
    <div className="rounded-3xl bg-white/[0.03] border border-white/10 backdrop-blur-xl shadow-2xl shadow-black/40 overflow-x-auto">
      <table className="w-full text-sm text-left">
        <thead>
          <tr className="border-b border-white/10 bg-white/[0.02]">
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">ID</th>
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">Título</th>
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">Descrição</th>
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">Data Prazo</th>
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">Status</th>
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">Ações</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-white/5">
{tarefas.map((tarefa)=> (
  <tr key={tarefa.id} className="hover:bg-white/[0.03] transition-colors">
    <td className="px-6 py-4 text-zinc-500 font-mono text-xs">{tarefa.id}</td>
    <td className="px-6 py-4 font-medium text-white">{tarefa.titulo}</td>
    <td className="px-6 py-4 text-zinc-400">{tarefa.descricao}</td>
    <td className="px-6 py-4 text-zinc-400 font-mono text-xs">{tarefa.dataPrazo}</td>
    <td className="px-6 py-4 text-emerald-400 font-semibold text-xs">{tarefa.status}</td>
    <td className="px-6 py-4">
      <div className="flex items-center gap-2">
        <Link href={`/tarefa/${tarefa.id}/editar`} className="inline-flex items-center justify-center w-8 h-8 rounded-lg text-zinc-400 hover:text-amber-400 hover:bg-white/5 transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-4 h-4">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
          </svg>
        </Link>

        <button
          onClick={() => handlerDeletarTarefa(tarefa)}
          className="px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors text-red-400 hover:text-white hover:bg-red-500/10">
          DELETAR
        </button>

        <button onClick={() => handleAlterarStatusTarefa(tarefa)}
          className={`px-3 py-1.5 rounded-full text-xs font-semibold transition-colors border ${tarefa.status === 'INTERROMPIDO'
            ? 'text-red-400 border-red-400/30 bg-red-400/10 hover:bg-red-400/20'
            : 'text-emerald-400 border-emerald-400/30 bg-emerald-400/10 hover:bg-emerald-400/20'}`
          }>
          {tarefa.status}
        </button>
      </div>
    </td>
  </tr>
))}

          {
  tarefas.length===0 && (
    <tr><td colSpan={5} className="px-6 py-16 text-center text-zinc-500 italic text-sm">Nenhuma tarefa encontrada</td></tr>
  )
}
        </tbody>
      </table>
    </div>
  </div>
</div>
    );
  }