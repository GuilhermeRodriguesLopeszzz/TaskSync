import { Empresa } from "../(sistema)/types/empresa";
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


export async function  atualizarEmpresa (empresa:Empresa): Promise<number>{
    var dadosRetorno = await  
    api.put<number>('/empresa/'+empresa.id,empresa);
    return dadosRetorno.data;
}


export async function  salvarEmpresa (empresa:Empresa): Promise<number>{
    var dadosRetorno = await  
    api.post<number>('/empresa',empresa);
    return dadosRetorno.data;
}