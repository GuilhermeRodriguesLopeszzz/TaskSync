package com.example.tasksync.application.dto;

import com.example.tasksync.domain.entities.Usuario;
import com.example.tasksync.domain.valueobject.CPF;

public record UsuarioLogado(Long id,
                            String email,
                            CPF cpf,
                            Long empresaId,
                            EnumRole role,
                            java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities) {

    public UsuarioLogado(Usuario usuario){
        this(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getEmpresa()!=null? usuario.getEmpresa().getId(): null,
                usuario.getRole(),
                usuario.getAuthorities()
        );
    }
}
