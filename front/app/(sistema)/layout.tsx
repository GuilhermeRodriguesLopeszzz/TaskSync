
"use client";

import { useSelector } from "react-redux";
import Footer from "../components/Footer";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";
import { RootState } from "../redux/store";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

export default function SistemaLayout({ children }: { children: React.ReactNode }) {
    const usuario = useSelector((state: RootState) => state.auth.usuario);
    const token = useSelector((state: RootState) => state.auth.token);
    const router = useRouter();

    useEffect(() => {
        // Redireciona para login se não estiver autenticado
        if (usuario == null || !token) {
            router.replace("/login");
        }
    }, [usuario, token, router]);

    // Impede que o painel apareça sem autenticação
    if (usuario == null || !token) {
        return null;
    }

    return (
        <div className="flex min-h-screen bg-[#0a0a0f]">
            <Sidebar />

            <div className="flex-1 flex flex-col min-w-0">
                <Header />
                <main className="flex-1">
                    {children}
                </main>
                <Footer />
            </div>
        </div>
    );
}
