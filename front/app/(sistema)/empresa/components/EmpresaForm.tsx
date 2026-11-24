"use client"
import Link from "next/link";
import { Empresa, EmpresaFormProps } from "../../types/empresa";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { atualizarEmpresa, buscarEnderecoPorCep, salvarEmpresa } from "@/app/services/empresaService";
import { isAxiosError } from "axios";


export default function EmpresaForm({ empresaExistente }: EmpresaFormProps) {
    const router = useRouter()
    //Valor Inicial
    const [empresa, setEmpresa] = useState<Empresa>

        (empresaExistente || new Empresa(null, "", "", "", "ATIVO"));
    const [buscandoCep, setBuscandoCep] = useState(false);
    const [enderecoConsultado, setEnderecoConsultado] = useState(!!empresaExistente?.cep);
    const [erroCep, setErroCep] = useState("");
    const [salvando, setSalvando] = useState(false);

    // Atualiza somente o campo alterado, mantendo os dados do endereço.
    const handlerChange = (campo: 'cnpj' | 'nomeFantasia' | 'razaoSocial', valor: string) => {
        setEmpresa(valorAnterior => ({
            ...valorAnterior,
            [campo]: valor
        }));
    };

    const consultarCep = async (cep: string) => {
        if (cep.replace(/\D/g, "").length !== 8) {
            return;
        }

        setBuscandoCep(true);
        setErroCep("");
        setEnderecoConsultado(false);

        try {
            const endereco = await buscarEnderecoPorCep(cep);
            setEmpresa(valorAnterior => ({
                ...valorAnterior,
                cep: endereco.cep,
                logradouro: endereco.logradouro,
                bairro: endereco.bairro,
                cidade: endereco.cidade,
                uf: endereco.uf
            }));
            setEnderecoConsultado(true);
        } catch (error) {
            if (isAxiosError(error) && typeof error.response?.data === "string") {
                setErroCep(error.response.data);
            } else {
                setErroCep("Erro ao consultar CEP. Tente novamente!");
            }
        } finally {
            setBuscandoCep(false);
        }
    };

    const handlerChangeCep = (valor: string) => {
        // Remove letras e aplica a máscara do CEP.
        const numeros = valor.replace(/\D/g, "").slice(0, 8);
        const cep = numeros.replace(/^([0-9]{5})([0-9])/, "$1-$2");

        if (cep === empresa.cep) {
            return;
        }

        // Ao trocar o CEP, limpa os dados do endereço anterior.
        setEmpresa(valorAnterior => ({
            ...valorAnterior,
            cep: cep,
            logradouro: "",
            bairro: "",
            cidade: "",
            uf: ""
        }));
        setEnderecoConsultado(false);
        setErroCep("");

        // Consulta automaticamente quando o CEP estiver completo.
        if (numeros.length === 8) {
            void consultarCep(cep);
        }
    };

    const handlerSalvar = async () => {
        if (buscandoCep || salvando) {
            return;
        }

        if (!enderecoConsultado) {
            setErroCep("Consulte um CEP válido antes de salvar!");
            return;
        }

        setSalvando(true);

        try {
            if (empresaExistente) {
                await atualizarEmpresa(empresa);
            } else {
                await salvarEmpresa(empresa);
            }

            alert("Empresa foi salva com sucesso!");
            router.push("/empresa");
        } catch (error) {
            if (isAxiosError(error) && typeof error.response?.data === "string") {
                alert(error.response.data);
            } else {
                alert("Erro ao salvar Empresa!");
            }
        } finally {
            setSalvando(false);
        }
    };

    return (
        <form action={handlerSalvar}>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-5">
                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label className="text-sm font-medium text-zinc-300">Razão social</label>
                    <input name="razaoSocial" value={empresa.razaoSocial} required onChange={(e) => handlerChange('razaoSocial', e.target.value)} placeholder="Silva Tecnologia LTDA" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">Nome fantasia</label>
                    <input name="nomeFantasia" value={empresa.nomeFantasia} required onChange={(e) => handlerChange('nomeFantasia', e.target.value)} placeholder="Silva Tech" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label className="text-sm font-medium text-zinc-300">CNPJ</label>
                    <input name="cnpj" value={empresa.cnpj} required onChange={(e) => handlerChange('cnpj', e.target.value)} placeholder="00.000.000/0000-00" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all"/>
                </div>

                <div className="flex flex-col gap-1.5 sm:col-span-2">
                    <label htmlFor="cep" className="text-sm font-medium text-zinc-300">CEP</label>
                    <div className="flex gap-3">
                        <input id="cep" name="cep" value={empresa.cep ?? ""} required inputMode="numeric" maxLength={9} pattern="[0-9]{5}-?[0-9]{3}" disabled={buscandoCep || salvando} onChange={(e) => handlerChangeCep(e.target.value)} placeholder="00000-000" className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-white text-sm placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all disabled:opacity-60"/>
                        <button type="button" disabled={buscandoCep || salvando || (empresa.cep ?? "").replace(/\D/g, "").length !== 8} onClick={() => void consultarCep(empresa.cep ?? "")} className="shrink-0 px-4 py-2 rounded-lg bg-white/10 text-zinc-300 text-sm hover:bg-white/20 disabled:opacity-50 disabled:cursor-not-allowed">
                            {buscandoCep ? "Buscando..." : "Buscar CEP"}
                        </button>
                    </div>
                    {erroCep && <p role="alert" className="text-sm text-red-400">{erroCep}</p>}
                    {enderecoConsultado && <p className="text-xs text-zinc-400">Endereço preenchido pelo CEP.</p>}
                </div>
                <div className="flex flex-col gap-1.5">
                    <label htmlFor="logradouro" className="text-sm font-medium text-zinc-300">Logradouro</label>
                    <input id="logradouro" name="logradouro" value={empresa.logradouro ?? ""} readOnly className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-zinc-300 text-sm"/>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label htmlFor="bairro" className="text-sm font-medium text-zinc-300">Bairro</label>
                    <input id="bairro" name="bairro" value={empresa.bairro ?? ""} readOnly className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-zinc-300 text-sm"/>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label htmlFor="cidade" className="text-sm font-medium text-zinc-300">Cidade</label>
                    <input id="cidade" name="cidade" value={empresa.cidade ?? ""} readOnly className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-zinc-300 text-sm"/>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label htmlFor="uf" className="text-sm font-medium text-zinc-300">UF</label>
                    <input id="uf" name="uf" value={empresa.uf ?? ""} readOnly className="w-full px-4 py-2.5 bg-white/[0.04] border border-white/10 rounded-lg text-zinc-300 text-sm"/>
                </div>

                <div className="sm:col-span-2 flex items-center justify-end gap-3 mt-4 pt-5 border-t border-white/10">
                    <Link href="/empresa" className="px-4 py-2 rounded-lg text-zinc-400 hover:text-white text-sm font-medium transition-colors">Cancelar</Link>
                    <button type="submit" disabled={buscandoCep || salvando || !enderecoConsultado} className="px-5 py-2 rounded-lg bg-amber-400 hover:bg-amber-300 text-black font-semibold text-sm transition-colors disabled:opacity-50 disabled:cursor-not-allowed">{salvando ? "Salvando..." : "Salvar"}</button>
                </div>

            </div>
        </form>
    );
}