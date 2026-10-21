package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.Desenvolvedor;

public record DesenvolvedorResponse(Long id, String nome, String cpf, String email, EnumStatusDesenvolvedor status) {

    public DesenvolvedorResponse(Desenvolvedor desenvolvedorEntidade) {

        this(
                desenvolvedorEntidade.getId(),
                desenvolvedorEntidade.getNome(),
                desenvolvedorEntidade.getCpf().toString(),
                desenvolvedorEntidade.getEmail(),
                desenvolvedorEntidade.getStatus()
        );
    }
}