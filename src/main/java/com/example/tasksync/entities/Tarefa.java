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
public class Tarefa {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        public Long id;

        public String titulo;

        public String descricao;

        public String status;

        public LocalDate dataPrazo;
    }

