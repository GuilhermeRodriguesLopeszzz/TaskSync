import { Empresa, Endereco } from "../(sistema)/types/empresa";
import api from "./api";


export async function buscarListaEmpresas() : Promise<Empresa[]> {

    const dados = await api.get<Empresa[]>("/empresa");

    if(dados.status == 200){
        return dados.data;
    }

    return [];
    
}


export async function deletarEmpresa(id:number) : Promise<number>{

    var dados = await api.delete('/empresa/'+id+'/excluir');
    return dados.status;
}


export async function atualizarStatusEmpresa(empresa:Empresa) :Promise<void>{
   
    var novoStatus = {};
    if(empresa.status ==="ATIVO"){
        novoStatus = {status:"BLOQUEADO"}
    }else{
        novoStatus = {status:"ATIVO"}
    }

    var dadosRetorno = await  
    api.patch('/empresa/'+empresa.id+'/status',novoStatus);

    if(dadosRetorno.status==200){
        alert("Atualizado status com sucesso!");
    }else{
        alert(dadosRetorno.data);

        return;
    } 
}


export async function consultaEmpresaPorId(id:number):Promise<Empresa>{
    const valorEmpresaBack = await api.get<Empresa>('/empresa/'+id);
    return valorEmpresaBack.data;

}


// Consulta o endereço pelo backend, utilizando o Axios e o token já configurados.
export async function buscarEnderecoPorCep(cep: string): Promise<Endereco> {
    const dadosRetorno = await api.get<Endereco>('/endereco/' + cep);
    return dadosRetorno.data;
}

export async function atualizarEmpresa(empresa: Empresa): Promise<Empresa> {
    const dadosRetorno = await api.put<Empresa>('/empresa/' + empresa.id, {
        razaoSocial: empresa.razaoSocial,
        cnpj: empresa.cnpj,
        nomeFantasia: empresa.nomeFantasia,
        cep: empresa.cep
    });
    return dadosRetorno.data;
}

export async function salvarEmpresa(empresa: Empresa): Promise<Empresa> {
    const dadosRetorno = await api.post<Empresa>('/empresa', {
        razaoSocial: empresa.razaoSocial,
        cnpj: empresa.cnpj,
        nomeFantasia: empresa.nomeFantasia,
        cep: empresa.cep
    });
    return dadosRetorno.data;
}
