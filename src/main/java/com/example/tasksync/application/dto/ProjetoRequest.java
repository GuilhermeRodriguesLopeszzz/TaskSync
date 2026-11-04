
package com.example.tasksync.application.dto;

import java.time.LocalDate;
import java.util.Set;

public record ProjetoRequest(
        String nome,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataPrevistaFim
) {
}
