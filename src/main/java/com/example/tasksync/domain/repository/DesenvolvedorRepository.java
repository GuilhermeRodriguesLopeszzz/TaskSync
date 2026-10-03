package com.example.tasksync.domain.repository;

import com.example.tasksync.domain.entities.Desenvolvedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesenvolvedorRepository extends JpaRepository<Desenvolvedor, Long> {
}
