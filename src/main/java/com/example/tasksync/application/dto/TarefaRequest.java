package com.example.tasksync.application.dto;

import java.time.LocalDate;

public record TarefaRequest(String titulo, String descricao, LocalDate dataPrazo) {
}
