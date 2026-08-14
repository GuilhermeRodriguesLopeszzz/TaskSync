package com.example.tasksync.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Get and Setters
@Data
//Construtor sem argumentos
@NoArgsConstructor
@AllArgsConstructor

public class Usuario {

    public Long id;

    public String nome;

    public String cpf;

    public String senha;

    public String email;
}
