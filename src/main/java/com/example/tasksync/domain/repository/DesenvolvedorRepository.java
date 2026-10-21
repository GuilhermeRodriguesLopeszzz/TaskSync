package com.example.tasksync.domain.repository;

import com.example.tasksync.application.dto.EnumStatusDesenvolvedor;
import com.example.tasksync.domain.entities.Desenvolvedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesenvolvedorRepository extends JpaRepository<Desenvolvedor, Long> {
    //boolean existsUsuarioByEmailAndSenha(String email, String senha);
    Optional<List<Desenvolvedor>> findByStatusNot(EnumStatusDesenvolvedor status);

}
