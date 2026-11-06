package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.Empresa;

public record EmpresaResponse(
        Long id,
        String cnpj,
        String nomeFantasia,
        String razaoSocial,
        EnumStatusEmpresa status
) {
    public EmpresaResponse(Empresa empresa) {
        this(
                empresa.getId(),
                // Converte o Value Object para uma String formatada
                empresa.getCnpj() != null
                        ? empresa.getCnpj().toString()
                        : null,
                empresa.getNomeFantasia(),
                empresa.getRazaoSocial(),
                empresa.getStatus()
        );
    }
}