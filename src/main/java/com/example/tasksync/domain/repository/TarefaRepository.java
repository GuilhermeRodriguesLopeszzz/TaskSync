package com.example.tasksync.domain.repository;

import com.example.tasksync.domain.entities.EnumStatusTarefa;
import com.example.tasksync.domain.entities.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
   //? boolean existsTarefaByEmailAndSenha(String email, String senha);
    Optional<List<Tarefa>> findByStatusNot(EnumStatusTarefa status);
}
