package com.example.tasksync.repository;

import com.example.tasksync.entities.EnumStatusTarefa;
import com.example.tasksync.entities.EnumStatusUsuario;
import com.example.tasksync.entities.Tarefa;
import com.example.tasksync.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
   //? boolean existsTarefaByEmailAndSenha(String email, String senha);
    Optional<List<Tarefa>> findByStatusNot(EnumStatusTarefa status);
}
