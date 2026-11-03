package com.example.tasksync.domain.repository;

import com.example.tasksync.application.dto.EnumStatusDesenvolvedor;
import com.example.tasksync.application.dto.EnumStatusEmpresa;
import com.example.tasksync.domain.entities.Desenvolvedor;
import com.example.tasksync.domain.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<List<Empresa>> findByStatusNot(EnumStatusEmpresa status);
}
