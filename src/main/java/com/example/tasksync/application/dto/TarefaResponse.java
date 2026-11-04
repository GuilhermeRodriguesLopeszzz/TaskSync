
package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.Tarefa;

import java.time.LocalDate;

public record TarefaResponse(
        Long id,
        String titulo,
        String descricao,
        LocalDate dataPrazo,
        EnumStatusTarefa status,
        Long projetoId,
        Long desenvolvedorId
) {

    public TarefaResponse(Tarefa tarefaEntidade) {
        this(
                tarefaEntidade.getId(),
                tarefaEntidade.getTitulo(),
                tarefaEntidade.getDescricao(),
                tarefaEntidade.getDataPrazo(),
                tarefaEntidade.getStatus(),
                tarefaEntidade.getProjeto() != null
                        ? tarefaEntidade.getProjeto().getId() : null,
                tarefaEntidade.getDesenvolvedor() != null
                        ? tarefaEntidade.getDesenvolvedor().getId() : null
        );
    }
}
