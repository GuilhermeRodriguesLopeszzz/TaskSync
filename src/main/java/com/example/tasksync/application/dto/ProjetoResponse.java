package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.Projeto;

import java.time.LocalDate;

public record ProjetoResponse(Long id, String nome, String descricao, EnumStatusProjeto status,
                              LocalDate dataInicio, LocalDate dataPrevistaFim) {

    public ProjetoResponse(Projeto projetoEntidade) {

        this(
                projetoEntidade.getId(),
                projetoEntidade.getNome(),
                projetoEntidade.getDescricao(),
                projetoEntidade.getStatus(),
                projetoEntidade.getDataInicio(),
                projetoEntidade.getDataPrevistaFim()
        );
    }
}
