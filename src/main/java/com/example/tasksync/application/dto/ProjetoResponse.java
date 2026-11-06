
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
        Long empresaId,
        Set<Long> desenvolvedoresIds
) {

    public ProjetoResponse(Projeto projetoEntidade) {
        this(
                projetoEntidade.getId(),
                projetoEntidade.getNome(),
                projetoEntidade.getDescricao(),
                projetoEntidade.getStatus(),
                projetoEntidade.getDataInicio(),
                projetoEntidade.getDataPrevistaFim(),

                // Retorna o ID da empresa vinculada ao projeto.
                // Caso não exista empresa, retorna null.
                projetoEntidade.getEmpresa() != null
                        ? projetoEntidade.getEmpresa().getId()
                        : null,

                // Percorre os desenvolvedores vinculados ao projeto
                // e retorna somente seus IDs, sem expor os objetos completos.
                projetoEntidade.getDesenvolvedores()
                        .stream()
                        .map(Usuario::getId)
                        .collect(Collectors.toSet())
        );
    }
}
