package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.Tarefa;
import com.example.tasksync.domain.entities.Usuario;

import java.time.LocalDate;

public record TarefaResponse(Long id, String titulo, String descricao, LocalDate dataPrazo, EnumStatusTarefa status) {

    public TarefaResponse (Tarefa tarefaEntidade){

        this(

                tarefaEntidade.getId(),
                tarefaEntidade.getTitulo(),
                tarefaEntidade.getDescricao(),
                tarefaEntidade.getDataPrazo(),
                tarefaEntidade.getStatus()

        );
    }
}
