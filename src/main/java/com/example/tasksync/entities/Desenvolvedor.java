package com.example.tasksync.entities;

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
public class Desenvolvedor {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String nome;

        private String cpf;

        private String senha;

        private String email;

        private EnumStatusDesenvolvedor status;

}
