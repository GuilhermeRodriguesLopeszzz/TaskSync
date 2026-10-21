package com.example.tasksync.application.dto;

import java.time.LocalDate;

public record ProjetoRequest(String nome, String descricao, LocalDate dataInicio, LocalDate dataPrevistaFim) {
}