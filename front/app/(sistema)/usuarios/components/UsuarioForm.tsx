"use client"
import Link from "next/link";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useSelector } from "react-redux";
import { isAxiosError } from "axios";
import { RootState } from "@/app/redux/store";
import { Usuario, UsuarioFormProps } from "../../types/usuario";
import { Empresa } from "../../types/empresa";

import {
    atualizarUsuario,
    salvarUsuario
} from "@/app/services/usuarioService";

import {
    buscarListaEmpresas
} from "@/app/services/empresaService";

export default function UsuarioForm({ usuarioExistente }: UsuarioFormProps) {

    const router = useRouter();

    // Busca o usuário logado no Redux.
    const usuarioLogado = useSelector(
        (state: RootState) => state.auth.usuario
    );

    const ehAdmin = usuarioLogado?.role === "ROLE_ADMIN";

    const [empresas, setEmpresas] = useState<Empresa[]>([]);

    const [salvando, setSalvando] = useState(false);

    // Valores iniciais do formulário.
    const [usuario, setUsuarios] = useState<Usuario>(
        usuarioExistente
            ? { ...usuarioExistente, senha: "" }
            : new Usuario(
                null,
                "",
                "",
                "ATIVO",
                "",
                "",
                null,
                "ROLE_GERENTE"
            )
    );

    // Somente o administrador pode consultar todas as empresas.
    useEffect(() => {

        if (ehAdmin) {
            carregarEmpresas();
        }

    }, [ehAdmin]);

    const carregarEmpresas = async () => {

        try {

            const dados = await buscarListaEmpresas();

            // Exibe somente empresas ativas.
            setEmpresas(
                dados.filter(empresa => empresa.status === "ATIVO")
            );

        } catch (error) {
            alert("Erro ao carregar empresas!");
        }
    };

    // Atualiza os campos sem perder a empresa e o perfil.
    const handlerChange = (
        campo: "nome" | "email" | "cpf" | "senha",
        valor: string
    ) => {

        setUsuarios(valorAnterior => ({
            ...valorAnterior,
            [campo]: valor
        }));

    };

    // Atualiza a empresa selecionada.
    const handlerEmpresa = (valor: string) => {

        setUsuarios(valorAnterior => ({
            ...valorAnterior,
            empresaId: valor ? Number(valor) : null
        }));

    };

    // Atualiza o perfil selecionado.
    const handlerRole = (
        valor: "ROLE_GERENTE" | "ROLE_DEV"
    ) => {

        setUsuarios(valorAnterior => ({
            ...valorAnterior,
            role: valor
        }));

    };

    const handlerSalvar = async () => {

        if (salvando) {
            return;
        }

        if (!usuarioLogado) {
            alert("Usuário não autenticado!");
            return;
        }

        if (
            usuarioLogado.role !== "ROLE_ADMIN" &&
            usuarioLogado.role !== "ROLE_GERENTE"
        ) {
            alert("Você não possui permissão!");
            return;
        }

        let empresaId: number | null = null;
        let role: "ROLE_GERENTE" | "ROLE_DEV";

        if (ehAdmin) {

            // ADMIN escolhe a empresa e o perfil.
            empresaId = usuario.empresaId ?? null;

            if (usuario.role === "ROLE_DEV") {
                role = "ROLE_DEV";
            } else {
                role = "ROLE_GERENTE";
            }

        } else {

            // GERENTE só cadastra desenvolvedores
            // da própria empresa.
            empresaId = usuarioLogado.empresaId ?? null;
            role = "ROLE_DEV";

        }

        if (empresaId === null) {
            alert("Informe uma empresa!");
            return;
        }

        if (!usuarioExistente && !usuario.senha.trim()) {
            alert("Informe uma senha!");
            return;
        }

        // O perfil do usuário não pode ser alterado na edição.
        if (usuarioExistente?.role === "ROLE_ADMIN") {
            alert("Não é permitido editar o administrador!");
            return;
        }

        const dadosSalvar: Usuario = {
            ...usuario,
            empresaId: empresaId,
            role: usuarioExistente?.role ?? role
        };

        setSalvando(true);

        try {

            if (usuarioExistente) {

                await atualizarUsuario(dadosSalvar);

                alert("Usuário atualizado com sucesso!");

            } else {

                await salvarUsuario(dadosSalvar);

                alert("Usuário cadastrado com sucesso!");

            }

            router.push("/usuarios");

        } catch (error) {

            if (
                isAxiosError(error) &&
                typeof error.response?.data === "string"
            ) {
                alert(error.response.data);
            } else {
                alert("Erro ao salvar usuário!");
            }

        } finally {
            setSalvando(false);
        }
    };

    return (

        <form action={handlerSalvar}>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">

                {/* NOME */}

                <div className="flex flex-col gap-1.5 sm:col-span-2">

                    <label className="text-sm font-medium text-zinc-300">
                        Nome completo
                    </label>

                    <input
                        name="nome"
                        value={usuario.nome}
                        required
                        onChange={(e) =>
                            handlerChange("nome", e.target.value)
                        }
                        placeholder="João da Silva"
                        className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"
                    />

                </div>

                {/* CPF */}

                <div className="flex flex-col gap-1.5">

                    <label className="text-sm font-medium text-zinc-300">
                        CPF
                    </label>

                    <input
                        name="cpf"
                        value={usuario.cpf}
                        required
                        onChange={(e) =>
                            handlerChange("cpf", e.target.value)
                        }
                        placeholder="000.000.000-00"
                        className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"
                    />

                </div>

                {/* E-MAIL */}

                <div className="flex flex-col gap-1.5">

                    <label className="text-sm font-medium text-zinc-300">
                        E-mail
                    </label>

                    <input
                        name="email"
                        type="email"
                        value={usuario.email}
                        required
                        onChange={(e) =>
                            handlerChange("email", e.target.value)
                        }
                        placeholder="joao@gmail.com"
                        className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"
                    />

                </div>

                {/* SENHA */}

                <div className="flex flex-col gap-1.5 sm:col-span-2">

                    <label className="text-sm font-medium text-zinc-300">
                        Senha
                    </label>

                    <input
                        name="senha"
                        type="password"
                        value={usuario.senha ?? ""}
                        required={!usuarioExistente}
                        onChange={(e) =>
                            handlerChange("senha", e.target.value)
                        }
                        placeholder={
                            usuarioExistente
                                ? "Deixe vazio para manter a senha"
                                : "Informe a senha"
                        }
                        className="w-full sm:w-1/2 px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"
                    />

                </div>

                {/* EMPRESA - SOMENTE ADMIN */}

                {ehAdmin && (

                    <div className="flex flex-col gap-1.5">

                        <label className="text-sm font-medium text-zinc-300">
                            Empresa
                        </label>

                        <select
                            name="empresaId"
                            value={usuario.empresaId ?? ""}
                            required
                            onChange={(e) =>
                                handlerEmpresa(e.target.value)
                            }
                            className="w-full px-4 py-2.5 bg-zinc-900 border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-amber-400/50"
                        >

                            <option value="">
                                Selecione uma empresa
                            </option>

                            {empresas.map(empresa => (

                                <option
                                    key={empresa.id}
                                    value={empresa.id ?? ""}
                                >
                                    {empresa.nomeFantasia}
                                </option>

                            ))}

                        </select>

                        {empresas.length === 0 && (

                            <span className="text-xs text-zinc-500">
                                Nenhuma empresa ativa disponível.
                            </span>

                        )}

                    </div>

                )}

                {/* PERFIL - SOMENTE ADMIN NO CADASTRO */}

                {ehAdmin && !usuarioExistente && (

                    <div className="flex flex-col gap-1.5">

                        <label className="text-sm font-medium text-zinc-300">
                            Perfil do usuário
                        </label>

                        <select
                            name="role"
                            value={usuario.role ?? "ROLE_GERENTE"}
                            required
                            onChange={(e) =>
                                handlerRole(
                                    e.target.value as
                                    "ROLE_GERENTE" | "ROLE_DEV"
                                )
                            }
                            className="w-full px-4 py-2.5 bg-zinc-900 border border-white/10 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-amber-400/50"
                        >

                            <option value="ROLE_GERENTE">
                                Gerente
                            </option>

                            <option value="ROLE_DEV">
                                Desenvolvedor
                            </option>

                        </select>

                    </div>

                )}

                {/* PERFIL NA EDIÇÃO */}

                {usuarioExistente && (

                    <div className="flex flex-col gap-1.5">

                        <label className="text-sm font-medium text-zinc-300">
                            Perfil do usuário
                        </label>

                        <input
                            value={
                                usuario.role === "ROLE_GERENTE"
                                    ? "Gerente"
                                    : usuario.role === "ROLE_DEV"
                                        ? "Desenvolvedor"
                                        : "Administrador"
                            }
                            disabled
                            className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-zinc-400 text-sm"
                        />

                    </div>

                )}

                {/* INFORMAÇÃO PARA GERENTE */}

                {usuarioLogado?.role === "ROLE_GERENTE" &&
                    !usuarioExistente && (

                    <div className="sm:col-span-2">

                        <p className="text-sm text-zinc-400">
                            O desenvolvedor será vinculado
                            automaticamente à sua empresa.
                        </p>

                    </div>

                )}

                {/* BOTÕES */}

                <div className="sm:col-span-2 flex items-center justify-end gap-3 mt-4 pt-5 border-t border-white/10">

                    <Link
                        href="/usuarios"
                        className="px-4 py-2 rounded-lg text-zinc-400 hover:text-white text-sm font-medium transition-colors"
                    >
                        Cancelar
                    </Link>

                    <button
                        type="submit"
                        disabled={salvando}
                        className="px-5 py-2 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {salvando ? "Salvando..." : "Salvar"}
                    </button>

                </div>

            </div>

        </form>

    );

}
