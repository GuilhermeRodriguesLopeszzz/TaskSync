package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.EnumStatusUsuario;
import com.example.tasksync.domain.entities.Usuario;

public record UsuarioResponse(Long id, String nome, String cpf, String email, EnumStatusUsuario status) {

    public UsuarioResponse (Usuario usuarioEntidade){

        this(

                usuarioEntidade.getId(),
                usuarioEntidade.getNome(),
                usuarioEntidade.getCpf(),
                usuarioEntidade.getEmail(),
                usuarioEntidade.getStatus()

        );
    }
}
