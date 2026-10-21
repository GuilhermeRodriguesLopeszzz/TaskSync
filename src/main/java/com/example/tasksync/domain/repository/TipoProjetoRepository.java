package com.example.tasksync.domain.repository;


import com.example.tasksync.domain.entities.TipoProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TipoProjetoRepository extends JpaRepository<TipoProjeto, Long> {


}
