package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.Usuario;

public record UsuarioResponse(Long id,
                              String nome,
                              String cpf,
                              String email,
                              EnumStatusUsuario status,
                              Long empresaId,
                              EnumRole role) {

    public UsuarioResponse (Usuario usuarioEntidade){

        this(
                usuarioEntidade.getId(),
                usuarioEntidade.getNome(),
                usuarioEntidade.getCpf().toString(),
                usuarioEntidade.getEmail(),
                usuarioEntidade.getStatus(),

                // Retorna o ID da empresa ou null se não possuir empresa
                usuarioEntidade.getEmpresa() != null
                        ? usuarioEntidade.getEmpresa().getId()
                        : null,

                // Retorna o perfil do usuário
                usuarioEntidade.getRole()
        );
    }
}
