package com.example.tasksync.domain.repository;

import com.example.tasksync.application.dto.EnumStatusProjeto;
import com.example.tasksync.domain.entities.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    Optional<List<Projeto>> findByStatusNot(EnumStatusProjeto status);
}
