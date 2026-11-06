package com.example.tasksync.domain.repository;


import com.example.tasksync.application.dto.EnumStatusEmpresa;
import com.example.tasksync.domain.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<List<Empresa>> findByStatusNot(EnumStatusEmpresa status);

    // Consulta o campo cnpj que está dentro do Value Object CNPJ
    boolean existsByCnpj_Cnpj(String cnpj);

}
