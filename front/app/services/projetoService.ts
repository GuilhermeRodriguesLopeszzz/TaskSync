import { Projeto } from "../(sistema)/types/projeto";
import api from "./api";



export async function buscarListaProjetos() : Promise<Projeto[]> {

    const dados = await api.get<Projeto[]>("http://localhost:8080/projetos");

    if(dados.status == 200){
        return dados.data;
    }

    return [];
    
}


export async function deletarProjeto(id:number) : Promise<number>{

 var dados = await api.delete('http://localhost:8080/projetos/'+id+'/excluir');
    return dados.status;
}


export async function atualizarStatusProjeto(projeto:Projeto) :Promise<void>{
   
    var novoStatus = {};
    if(projeto.status === "BAIXA"){
        novoStatus = {status:"MEDIA"}
    }else if(projeto.status === "MEDIA"){
        novoStatus = {status:"ALTA"}
    }else if(projeto.status === "ALTA"){
        novoStatus = {status:"URGENTE"}
    }else{
        novoStatus = {status:"BAIXA"}
    }

    var dadosRetorno = await  
    api.patch('http://localhost:8080/projetos/'+projeto.id+'/status',novoStatus);

    if(dadosRetorno.status==200){
        alert("Atualizado status com sucesso!");
    }else{
        alert(dadosRetorno.data);

        return;
    } 
}

export async function consultaProjetoPorId(id:number):Promise<Projeto>{
    const valorProjetoBack = await api.get<Projeto>('http://localhost:8080/projetos/'+id);
    return valorProjetoBack.data;

}

export async function  atualizarProjeto (projeto:Projeto): Promise<number>{
    var dadosRetorno = await  
    api.put<number>('http://localhost:8080/projetos/'+projeto.id,projeto);
    return dadosRetorno.data;
}


export async function  salvarProjeto (projeto:Projeto): Promise<number>{
    var dadosRetorno = await  
    api.post<number>('http://localhost:8080/projetos',projeto);
    return dadosRetorno.data;
}