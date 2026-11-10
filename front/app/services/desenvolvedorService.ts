import { Desenvolvedor, DesenvolvedorRequest } from "../(sistema)/types/desenvolvedor";
import api from "./api";


export async function buscarListaDesenvolvedores(): Promise<Desenvolvedor[]> {

    var dados = await api.get<Desenvolvedor[]>("/usuarios");

    if (dados.status == 200) {
        return dados.data.filter(usuario => usuario.role === "ROLE_DEV");
    }

    return [];
}


export async function deletarDesenvolvedor(id: number): Promise<number> {

    var dados = await api.delete("/usuarios/" + id + "/excluir");

    return dados.status;
}


export async function atualizarStatusDesenvolvedor(desenvolvedor: Desenvolvedor): Promise<void> {

    var novoStatus = {};

    if (desenvolvedor.status === "ATIVO") {
        novoStatus = { status: "BLOQUEADO" };
    } else {
        novoStatus = { status: "ATIVO" };
    }

    var dadosRetorno = await api.patch(
        "/usuarios/" + desenvolvedor.id + "/status",
        novoStatus
    );

    if (dadosRetorno.status == 200) {
        alert("Status atualizado com sucesso!");
    } else {
        alert("Erro ao atualizar o status!");
    }
}


export async function consultaDesenvolvedorPorId(id: number): Promise<Desenvolvedor> {

    var dados = await api.get<Desenvolvedor>("/usuarios/" + id);

    if (dados.data.role != "ROLE_DEV") {
        throw new Error("Este usuário não é um desenvolvedor!");
    }

    return dados.data;
}


export async function atualizarDesenvolvedor(
    id: number,
    desenvolvedor: DesenvolvedorRequest
): Promise<number> {

    var dadosRetorno = await api.put(
        "/usuarios/" + id,
        desenvolvedor
    );

    return dadosRetorno.status;
}


export async function salvarDesenvolvedor(
    desenvolvedor: DesenvolvedorRequest
): Promise<number> {

    var dadosRetorno = await api.post(
        "/usuarios",
        desenvolvedor
    );

    return dadosRetorno.status;
}