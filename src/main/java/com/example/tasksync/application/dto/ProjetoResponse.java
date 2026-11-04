
package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.Projeto;
import com.example.tasksync.domain.entities.Usuario;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public record ProjetoResponse(
        Long id,
        String nome,
        String descricao,
        EnumStatusProjeto status,
        LocalDate dataInicio,
        LocalDate dataPrevistaFim,
        Set<Long> desenvolvedoreId
) {

    public ProjetoResponse(Projeto projetoEntidade) {
        this(
                projetoEntidade.getId(),
                projetoEntidade.getNome(),
                projetoEntidade.getDescricao(),
                projetoEntidade.getStatus(),
                projetoEntidade.getDataInicio(),
                projetoEntidade.getDataPrevistaFim(),
                projetoEntidade.getDesenvolvedores()
                        .stream()
                        .map(Usuario::getId)
                        .collect(Collectors.toSet())
        );
    }
}
