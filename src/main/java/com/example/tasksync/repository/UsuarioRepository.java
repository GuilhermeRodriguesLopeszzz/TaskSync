package com.example.tasksync.repository;

import com.example.tasksync.entities.EnumStatusUsuario;
import com.example.tasksync.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsUsuarioByEmailAndSenha(String email, String senha);
    Optional<List<Usuario>> findByStatusNot(EnumStatusUsuario status);

}
