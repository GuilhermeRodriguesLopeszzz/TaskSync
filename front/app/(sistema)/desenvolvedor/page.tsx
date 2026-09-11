"use client"
import Link from "next/link";
import { useState, useEffect } from "react";
import { Desenvolvedor } from "../types/desenvolvedor";
import axios from "axios";

export default function Desenvolvedores() {

  const [desenvolvedores, setDesenvolvedores] = useState<Desenvolvedor[]>([]);

useEffect(() => {
  carregarDados();
}, []);

const carregarDados = async () => {
  try {
    const dados = await axios.get<Desenvolvedor[]>("http://localhost:8080/desenvolvedor");
    setDesenvolvedores(dados.data);
  } catch (error) {
    alert("Erro ao carregar dados");
  }
};
    return (
<div className="min-h-screen w-full bg-[#0a0a0f] px-6 py-10 md:px-10 md:py-14">
  <div className="max-w-7xl mx-auto flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-10">
    <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">Gestão de desenvolvedor</h1>
    <Link href="/desenvolvedor/novo" className="inline-flex items-center justify-center px-5 py-2.5 rounded-full bg-gradient-to-r from-amber-400 to-orange-500 hover:from-amber-300 hover:to-orange-400 text-black font-bold text-sm transition-all duration-200 hover:-translate-y-0.5 shadow-lg shadow-orange-500/25 hover:shadow-orange-400/40 w-fit">Adicionar novo desenvolvedor</Link>
  </div>

  <div className="max-w-7xl mx-auto">
    <div className="rounded-3xl bg-white/[0.03] border border-white/10 backdrop-blur-xl shadow-2xl shadow-black/40 overflow-x-auto">
      <table className="w-full text-sm text-left">
        <thead>
          <tr className="border-b border-white/10 bg-white/[0.02]">
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">ID</th>
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">Nome</th>
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">E-mail</th>
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">Status</th>
            <th className="px-6 py-4 font-semibold text-zinc-400 text-xs uppercase tracking-wider">CPF</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-white/5">
        {desenvolvedores.map((desenvolvedor)=> (
          <tr key={desenvolvedor.id} className="hover:bg-white/[0.03] transition-colors">
            <td className="px-6 py-4 text-zinc-500 font-mono text-xs">{desenvolvedor.id}</td>
            <td className="px-6 py-4 font-medium text-white">{desenvolvedor.nome}</td>
            <td className="px-6 py-4 text-zinc-400 font-mono text-xs">{desenvolvedor.email}</td>
            <td className="px-6 py-4 text-zinc-400">{desenvolvedor.status}</td>
            <td className="px-6 py-4 text-zinc-500">{desenvolvedor.cpf}</td>
          </tr>
          ))}
          {
  desenvolvedores.length===0 && (
    <tr><td colSpan={5} className="px-6 py-16 text-center text-zinc-500 italic text-sm">Nenhum desenvolvedor encontrado</td></tr>
  )
}
        </tbody>
      </table>
    </div>
  </div>
</div>
    );
  }