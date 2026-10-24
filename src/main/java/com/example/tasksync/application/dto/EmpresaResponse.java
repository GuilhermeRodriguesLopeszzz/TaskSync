package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.Empresa;
import com.example.tasksync.domain.entities.Usuario;

public record EmpresaResponse(Long id, String razaoSocial, String cnpj, String nomeFantasia) {

    public EmpresaResponse (Empresa empresaEntidade){

        this(

                empresaEntidade.getId(),
                empresaEntidade.getRazaoSocial(),
                empresaEntidade.getCnpj(),
                empresaEntidade.getNomeFantasia()

        );
    }
}
