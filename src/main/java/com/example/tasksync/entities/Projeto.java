package com.example.tasksync.entities;

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

    private String status;

    private LocalDate dataInicio;

    private LocalDate dataPrevistaFim;

}
