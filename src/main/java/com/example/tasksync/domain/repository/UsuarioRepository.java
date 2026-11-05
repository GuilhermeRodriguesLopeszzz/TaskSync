package com.example.tasksync.domain.repository;

import com.example.tasksync.application.dto.EnumStatusUsuario;
import com.example.tasksync.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository

// JpaRepository<Usuario, Long> fornece operações de banco para
// a entidade Usuario, cujo ID é do tipo Long
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Verifica se existe um usuário com o email e a senha informados
    // Retorna true se existir e false se não existir
    boolean existsUsuarioByEmailAndSenha(String email, String senha);

    // Busca todos os usuários cujo status seja diferente do informado
    // Optional representa a possibilidade de não existir um resultado
    Optional<List<Usuario>> findByStatusNot(EnumStatusUsuario status);

    // Busca todos os usuários que pertencem à empresa informada
    // Empresa_Id acessa o ID da empresa relacionada ao usuário
    // Retorna uma lista vazia caso não encontre nenhum usuário
    List<Usuario> getUsuariosByEmpresa_Id(Long empresa);

}
