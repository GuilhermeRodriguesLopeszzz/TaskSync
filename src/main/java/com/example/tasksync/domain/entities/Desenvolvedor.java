package com.example.tasksync.domain.entities;

import com.example.tasksync.application.dto.DesenvolvedorRequest;
import com.example.tasksync.application.dto.EnumStatusDesenvolvedor;
import com.example.tasksync.domain.valueobject.CPF;
import jakarta.persistence.*;
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

        @Embedded
        private CPF cpf;

        private String senha;

        private String email;

        private EnumStatusDesenvolvedor status = EnumStatusDesenvolvedor.ATIVO;

        public void setEmail(String email) {
                if (email != null && !email.isEmpty() && !email.contains("@")) {
                        throw new IllegalArgumentException("Email Invalido!");
                }
                this.email = email;
        }

        public Desenvolvedor(DesenvolvedorRequest desenvolvedor) {
                this.setCpf(new CPF(desenvolvedor.cpf()));
                this.setNome(desenvolvedor.nome());
                this.setSenha(desenvolvedor.senha());
                this.setEmail(desenvolvedor.email());
        }

}
