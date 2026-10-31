import { Desenvolvedor } from "../(sistema)/types/desenvolvedor";
import api from "./api";



export async function buscarListaDesenvolvedores() : Promise<Desenvolvedor[]> {

    const dados = await api.get<Desenvolvedor[]>("/desenvolvedor");

    if(dados.status == 200){
        return dados.data;
    }

    return [];
    
}


export async function deletarDesenvolvedor(id:number) : Promise<number>{

 var dados = await api.delete('/desenvolvedor/'+id+'/excluir');
    return dados.status;
}


export async function atualizarStatusDesenvolvedor(desenvolvedor:Desenvolvedor) :Promise<void>{
   
    var novoStatus = {};
    if(desenvolvedor.status ==="ATIVO"){
        novoStatus = {status:"BLOQUEADO"}
    }else{
        novoStatus = {status:"ATIVO"}
    }

    var dadosRetorno = await  
    api.patch('/desenvolvedor/'+desenvolvedor.id+'/status',novoStatus);

    if(dadosRetorno.status==200){
        alert("Atualizado status com sucesso!");
    }else{
        alert(dadosRetorno.data);

        return;
    } 
}

export async function consultaDesenvolvedorPorId(id:number):Promise<Desenvolvedor>{
    const valorDesenvolvedorBack = await api.get<Desenvolvedor>('/desenvolvedor/'+id);
    return valorDesenvolvedorBack.data;

}

export async function  atualizarDesenvolvedor (desenvolvedor:Desenvolvedor): Promise<number>{
    var dadosRetorno = await  
    api.put<number>('/desenvolvedor/'+desenvolvedor.id,desenvolvedor);
    return dadosRetorno.data;
}


export async function  salvarDesenvolvedor (desenvolvedor:Desenvolvedor): Promise<number>{
    var dadosRetorno = await  
    api.post<number>('/desenvolvedor',desenvolvedor);
    return dadosRetorno.data;
}