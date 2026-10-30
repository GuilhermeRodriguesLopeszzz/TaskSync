import { Tarefa } from "../(sistema)/types/tarefa";
import api from "./api";



export async function buscarListaTarefas() : Promise<Tarefa[]> {

    const dados = await api.get<Tarefa[]>("http://localhost:8080/tarefa");

    if(dados.status == 200){
        return dados.data;
    }

    return [];
    
}


export async function deletarTarefa(id:number) : Promise<number>{

 var dados = await api.delete('http://localhost:8080/tarefa/'+id+'/excluir');
    return dados.status;
}


export async function atualizarStatusTarefa(tarefa:Tarefa) :Promise<void>{
   
    var novoStatus = {};
    if(tarefa.status ==="ANDAMENTO"){
        novoStatus = {status:"PENDENTE"}
    }else{
        novoStatus = {status:"ANDAMENTO"}
    }

    var dadosRetorno = await  
    api.patch('http://localhost:8080/tarefa/'+tarefa.id+'/status',novoStatus);

    if(dadosRetorno.status==200){
        alert("Atualizado status com sucesso!");
    }else{
        alert(dadosRetorno.data);

        return;
    } 
}

export async function consultaTarefaPorId(id:number):Promise<Tarefa>{
    const valorTarefaBack = await api.get<Tarefa>('http://localhost:8080/tarefa/'+id);
    return valorTarefaBack.data;

}

export async function  atualizarTarefa (tarefa:Tarefa): Promise<number>{
    var dadosRetorno = await  
    api.put<number>('http://localhost:8080/tarefa/'+tarefa.id,tarefa);
    return dadosRetorno.data;
}


export async function  salvarTarefa (tarefa:Tarefa): Promise<number>{
    var dadosRetorno = await  
    api.post<number>('http://localhost:8080/tarefa',tarefa);
    return dadosRetorno.data;
}