"use client";

import { useSelector } from "react-redux";
import Footer from "../components/Footer";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import { RootState } from "../redux/store";
import { useRouter } from "next/navigation";
import { useEffect } from "react";
import type { ReactNode } from "react";

export default function SistemaLayout({ children }: { children: ReactNode }) {
  const usuario = useSelector((state: RootState) => state.auth.usuario);
  const token = useSelector((state: RootState) => state.auth.token);
  const router = useRouter();

  const autenticado = Boolean(usuario && token);

  useEffect(() => {
    if (!autenticado) {
      router.replace("/login");
    }
  }, [autenticado, router]);

  // Não mostra o painel enquanto o usuário não estiver autenticado.
  if (!autenticado) {
    return null;
  }

  return (
    <div className="flex min-h-screen bg-[#0a0a0f]">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <Header />
        <main className="flex-1">{children}</main>
        <Footer />
      </div>
    </div>
  );
}
