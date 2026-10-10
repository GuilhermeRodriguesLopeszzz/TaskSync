package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.CriarAdminRequest;
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
// Gera um construtor vazio (sem parâmetros), necessário para frameworks como JPA/Hibernate e Jackson instanciarem a classe
@NoArgsConstructor
//Construtor com argumentos
// Gera um construtor com todos os atributos da classe como parâmetros, facilitando criar o objeto já preenchido
@AllArgsConstructor




public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cpf;

    private String senha;

    private String email;

    private String role = "ROLE_USER";

    private EnumStatusUsuario status = EnumStatusUsuario.ATIVO;

    public Usuario(CriarAdminRequest criarAdminRequest) {
        this.setCpf(cpf);
        this.setNome(nome);
        this.setSenha(senha);
        this.setEmail(email);

        this.setRole("ROLE_ADMIN");
    }
}
