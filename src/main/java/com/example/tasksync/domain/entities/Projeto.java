package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.EnumStatusProjeto;
import com.example.tasksync.application.dto.ProjetoRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
//Get and Setters
@Data
//Construtor sem argumentos
@NoArgsConstructor
//Construtor com argumentos
@AllArgsConstructor
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    // troque ATIVO pelo valor inicial do seu enum
    private EnumStatusProjeto status = EnumStatusProjeto.MEDIA;

    private LocalDate dataInicio;

    private LocalDate dataPrevistaFim;

    public Projeto(ProjetoRequest projeto) {
        this.setNome(projeto.nome());
        this.setDescricao(projeto.descricao());
        this.setDataInicio(projeto.dataInicio());
        this.setDataPrevistaFim(projeto.dataPrevistaFim());
    }

}
