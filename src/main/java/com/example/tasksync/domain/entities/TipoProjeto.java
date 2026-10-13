package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.CriarAdminRequest;
import com.example.tasksync.application.dto.CriarTipoProjetoRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
//Get and Setters
@Data
//Construtor sem argumentos
@NoArgsConstructor
//Construtor com argumentos
@AllArgsConstructor
public class TipoProjeto {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    private String role = "ROLE_CRIADO_USUARIO";

    public TipoProjeto(CriarTipoProjetoRequest criarTipoProjetoRequest) {
        this.setDescricao(criarTipoProjetoRequest.descricao());
        this.setNome(criarTipoProjetoRequest.nome());

        this.setRole("ROLE_CRIADO_ADMIN");
    }

}

